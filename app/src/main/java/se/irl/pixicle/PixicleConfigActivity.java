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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
    private int mPinNumber = 0; // 0 is the default pin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPixicleId = intent.getIntExtra(Constants.ARG_PIXICLE_IDENTITY, -1);

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
                new PixicleAsyncTask().execute(mAccessToken, mDeviceIdentifier, PixicleAsyncTask.FUNCTION_SET_HARDWARE_CONFIG, Integer.toString(mNumberOfPixels)+","+Integer.toString(mPinNumber));
                return true;

            case R.id.schedule_configuration:

                int pos = mViewPager.getCurrentItem();
                SectionsPagerAdapter pagerAdapter = (SectionsPagerAdapter) mViewPager.getAdapter();
                PixicleConfigFragmentBase configFragment = (PixicleConfigFragmentBase) pagerAdapter.getItem(pos);
                String configuration = configFragment.getPixicleConfigArgs();

                Intent intent = new Intent(getApplicationContext(), ScheduleConfigurationActivity.class);
                intent.putExtra(Constants.ARG_PIXICLE_IDENTITY, mPixicleId);
                intent.putExtra(Constants.ARG_DEVICE_IDENTITY, mDeviceIdentifier);
                intent.putExtra(Constants.ARG_ACCESS_TOKEN, mAccessToken);
                intent.putExtra(Constants.ARG_CONFIGURATION, configuration);
                startActivity(intent);
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

                Uri uri = Uri.withAppendedPath(PixicleContentProvider.PIXICLE_URI, Integer.toString(mPixicleId));

                return new CursorLoader(
                        this,
                        uri,
                        null, // projection
                        null, // selection
                        null, // selectionArgs
                        null);
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
        int pinNumberColumn = data.getColumnIndex(PixicleContentProvider.PIN_NUMBER_COLUMN);

        data.moveToFirst();

        mDeviceIdentifier = data.getString(deviceIdColumn);
        mPixicleId = data.getInt(idColumn);
        mPixicleName = data.getString(nameColumn);
        mSoftwareVersion = data.getInt(softwareVersionColumn);
        if(data.isNull(pinNumberColumn))
            mPinNumber = 0;
        else
            mPinNumber = data.getInt(pinNumberColumn);
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
        private FireFragment mFireFragment = null;

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
                    return mJuggleFragment == null ? mJuggleFragment = JuggleFragment.newInstance() : mJuggleFragment;
                case 1:
                    return mFireFragment == null ? mFireFragment = FireFragment.newInstance() : mFireFragment;
                case 2:
                    return mTwinkleFragment == null ? mTwinkleFragment =  TwinkleFragment.newInstance(): mTwinkleFragment;
                case 3:
                    return mProgressFragment == null ? mProgressFragment = ProgressFragment.newInstance() : mProgressFragment;
                case 4:
                    return mRainbowFragment == null ? mRainbowFragment =  RainbowFragment.newInstance() : mRainbowFragment;
                case 5:
                    return mDashFragment == null ? mDashFragment = DashFragment.newInstance() : mDashFragment;
                case 6:
                    return mSolidFragment == null ? mSolidFragment = SolidFragment.newInstance() : mSolidFragment;
                case 7:
                    return mCustomFragment == null ? mCustomFragment = CustomFragment.newInstance() : mCustomFragment;
            }

            return null;
        }

        @Override
        public int getCount() { return 8; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Juggle";
                case 1:
                    return "Fire";
                case 2:
                    return "Twinkle";
                case 3:
                    return "Progress";
                case 4:
                    return "Rainbow";
                case 5:
                    return "Dash";
                case 6:
                    return "Solid";
                case 7:
                    return "Custom";
            }
            return null;
        }
    }
}
