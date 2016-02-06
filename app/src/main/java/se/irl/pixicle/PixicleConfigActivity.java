package se.irl.pixicle;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

public class PixicleConfigActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PIXICLE_LOADER_ID = 17;
    private static final int CONFIGURATIONS_LOADER_ID = 18;

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
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException e) {
            Log.wtf("Pixicle", e.toString());
        }

        mViewPager = (ViewPager) findViewById(R.id.container);

        // Load values from the database
        // TODO: This should show loading while loading...

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mSoftwareVersion);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(20);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getLoaderManager().initLoader(PIXICLE_LOADER_ID, null, this);
        getLoaderManager().initLoader(CONFIGURATIONS_LOADER_ID, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = mViewPager.getCurrentItem();
                SectionsPagerAdapter pagerAdapter = (SectionsPagerAdapter) mViewPager.getAdapter();

                PixicleConfigFragmentBase configFragment = (PixicleConfigFragmentBase) pagerAdapter.getItem(pos);

                String plugin = configFragment.getPluginName();
                String args = configFragment.getPixicleConfigArgs();

                new PixicleAsyncTask().execute(mAccessToken, mDeviceIdentifier, PixicleAsyncTask.FUNCTION_APPLY_CONFIG, plugin+":"+args);

                // Save to database
                ContentValues values = new ContentValues();
                values.put(PixicleContentProvider.CONFIGURATION_PIXICLE_ID_COLUMN, mPixicleId);
                values.put(PixicleContentProvider.DEVICE_ID_COLUMN, mDeviceIdentifier);
                values.put(PixicleContentProvider.PLUGIN_NAME_COLUMN, plugin);
                values.put(PixicleContentProvider.CONFIGURATION_COLUMN, args);
                values.put(PixicleContentProvider.CREATED_TIME_COLUMN, Constants.StdDateFormat.format(new Date()));
                values.put(PixicleContentProvider.TIMESTAMP_COLUMN, Constants.StdDateFormat.format(new Date()));

                ContentResolver contentResolver = getContentResolver();

                Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(PixicleContentProvider.CONFIGURATION_URI, Integer.toString(mPixicleId)), plugin);

                if(contentResolver.update(uri, values, null, null) == 0)
                    contentResolver.insert(PixicleContentProvider.CONFIGURATION_URI, values);
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
                String plugin = configFragment.getPluginName();
                String configuration = configFragment.getPixicleConfigArgs();

                Intent intent = new Intent(getApplicationContext(), ScheduleConfigurationActivity.class);
                intent.putExtra(Constants.ARG_PIXICLE_IDENTITY, mPixicleId);
                intent.putExtra(Constants.ARG_DEVICE_IDENTITY, mDeviceIdentifier);
                intent.putExtra(Constants.ARG_ACCESS_TOKEN, mAccessToken);
                intent.putExtra(Constants.ARG_CONFIGURATION, plugin+":"+configuration);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mPixicleId == -1)
            return null;

        Uri uri;

        switch(id) {
            case PIXICLE_LOADER_ID:
                uri = Uri.withAppendedPath(PixicleContentProvider.PIXICLE_URI, Integer.toString(mPixicleId));
                return new CursorLoader(this, uri, null, null, null, null);
            case CONFIGURATIONS_LOADER_ID:
                uri = Uri.withAppendedPath(PixicleContentProvider.CONFIGURATION_URI, Integer.toString(mPixicleId));
                return new CursorLoader(this, uri, null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch(loader.getId()) {
            case PIXICLE_LOADER_ID:
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
                if (data.isNull(pinNumberColumn))
                    mPinNumber = 0;
                else
                    mPinNumber = data.getInt(pinNumberColumn);
                if (data.isNull(accessTokenColumn))
                    mAccessToken = null;
                else
                    mAccessToken = data.getString(accessTokenColumn);
                mNumberOfPixels = data.getInt(numberOfNeoPixelsColumn);

                // TODO: Show the name of the Pixicle in the header, or somewhere?
                break;
            case CONFIGURATIONS_LOADER_ID:




                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment fragments[] = new Fragment[8];

        public SectionsPagerAdapter(FragmentManager fm, int softwareVersion)
        {
            super(fm);
            mSoftwareVersion = softwareVersion;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments[position] = fragment;
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {

            if(fragments[position] != null)
                return fragments[position];

            switch (position) {
                case 0:
                    return JuggleFragment.newInstance();
                case 1:
                    return FireFragment.newInstance();
                case 2:
                    return TwinkleFragment.newInstance();
                case 3:
                    return ProgressFragment.newInstance();
                case 4:
                    return RainbowFragment.newInstance();
                case 5:
                    return GradientFragment.newInstance();
                case 6:
                    return DashFragment.newInstance();
                case 7:
                    return SolidFragment.newInstance();
                case 8:
                    return CustomFragment.newInstance();
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
                    return "Gradient";
                case 6:
                    return "Dash";
                case 7:
                    return "Solid";
                case 8:
                    return "Custom";
            }
            return null;
        }
    }
}
