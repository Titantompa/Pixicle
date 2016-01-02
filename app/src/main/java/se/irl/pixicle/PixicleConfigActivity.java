package se.irl.pixicle;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class PixicleConfigActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1786523;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int mPixicleId = -1;

    private String mPixicleName;
    private String mDeviceIdentifier;
    private String mAccessToken;
    private int mSoftwareVersion = -1;
    private int mNumberOfPixels = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPixicleId = intent.getIntExtra(Constants.ARG_DEVICE_IDENTITY, -1);

        setContentView(R.layout.activity_pixicle_config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load values from the database
        // TODO: This should show loading while loading...

        getLoaderManager().initLoader(LOADER_ID, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = mViewPager.getCurrentItem();
                SectionsPagerAdapter pagerAdapter = (SectionsPagerAdapter) mViewPager.getAdapter();

                PixicleConfigFragmentBase configFragment = (PixicleConfigFragmentBase) pagerAdapter.getItem(pos);

                String args = configFragment.getPixicleConfigArgs();

                new PixicleAsyncTask().execute(mAccessToken, mDeviceIdentifier, PixicleAsyncTask.FUNCTION_APPLY_CONFIG, args);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pixicle_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.turn_off_pixicle:
                // Send the off command to the Pixicle
                new PixicleAsyncTask().execute(mAccessToken, mDeviceIdentifier, PixicleAsyncTask.FUNCTION_APPLY_CONFIG, "Off");
                return true;

            case R.id.send_pixicle_hwconfig:
                // Send the off command to the Pixicle
                new PixicleAsyncTask().execute(mAccessToken, mDeviceIdentifier, PixicleAsyncTask.FUNCTION_SET_PIXEL_COUNT, new Integer(mNumberOfPixels).toString());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mPixicleId == -1)
            return null;

        switch(id) {
            case LOADER_ID:

                Uri uri = Uri.withAppendedPath(PixicleContentProvider.PIXICLE_URI, new Integer(mPixicleId).toString());

                CursorLoader loader = new CursorLoader(
                        this,
                        uri,
                        null, // projection
                        null, // selection
                        null, // selectionArgs
                        null);

                return loader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int idColumn = data.getColumnIndex(PixicleContentProvider.PIXICLE_ID_COLUMN);
        int nameColumn = data.getColumnIndex(PixicleContentProvider.NAME_COLUMN);
        int deviceIdColumn = data.getColumnIndex(PixicleContentProvider.DEVICE_ID_COLUMN);
        int softwareVersionColumn = data.getColumnIndex(PixicleContentProvider.SOFTWARE_VERSION_COLUMN);
        int accessTokenColumn = data.getColumnIndex(PixicleContentProvider.ACCESS_TOKEN_COLUMN);
        int numberOfNeoPixelsColumn = data.getColumnIndex(PixicleContentProvider.NUMBER_OF_NEOPIXELS_COLUMN);

        data.moveToFirst();

        mDeviceIdentifier = data.getString(deviceIdColumn);
        mPixicleId = data.getInt(idColumn);
        mPixicleName = data.getString(nameColumn);
        mSoftwareVersion = data.getInt(softwareVersionColumn);
        if(data.isNull(accessTokenColumn))
            mAccessToken = null;
        else
            mAccessToken = data.getString(accessTokenColumn);
        mNumberOfPixels = data.getInt(numberOfNeoPixelsColumn);

        // TODO: Show the name of the Pixicle in the header, or somewhere

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mSoftwareVersion);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(20);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private DashFragment mDashFragment = null;
        private JuggleFragment mJuggleFragment = null;
        private ProgressFragment mProgressFragment = null;
        private RainbowFragment mRainbowFragment = null;
        private SolidFragment mSolidFragment = null;
        private TwinkleFragment mTwinkleFragment = null;
        private CustomFragment mCustomFragment = null;

        public SectionsPagerAdapter(FragmentManager fm, int softwareVersion)
        {
            super(fm);
            mSoftwareVersion = softwareVersion;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return mDashFragment == null ? mDashFragment = DashFragment.newInstance() : mDashFragment;
                case 1:
                    return mJuggleFragment == null ? mJuggleFragment = JuggleFragment.newInstance() : mJuggleFragment;
                case 2:
                    return mProgressFragment == null ? mProgressFragment = ProgressFragment.newInstance() : mProgressFragment;
                case 3:
                    return mRainbowFragment == null ? mRainbowFragment =  RainbowFragment.newInstance() : mRainbowFragment;
                case 4:
                    return mSolidFragment == null ? mSolidFragment = SolidFragment.newInstance() : mSolidFragment;
                case 5:
                    return mTwinkleFragment == null ? mTwinkleFragment =  TwinkleFragment.newInstance(): mTwinkleFragment;
                case 6:
                    return mCustomFragment == null ? mCustomFragment = CustomFragment.newInstance() : mCustomFragment;
            }

            return null;
        }

        @Override
        public int getCount() { return 7; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Dash";
                case 1:
                    return "Juggle";
                case 2:
                    return "Progress";
                case 3:
                    return "Rainbow";
                case 4:
                    return "Solid";
                case 5:
                    return "Twinkle";
                case 6:
                    return "Custom";
            }
            return null;
        }
    }
}
