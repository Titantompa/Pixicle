package se.irl.pixicle;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.Date;

// TODO: Add a menu to send number of neopixels to the device

public class PixiclePropertiesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int mPixicleId = -1;

    private ContentResolver mContentResolver = null;

    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixicle_properties);

        Intent intent = getIntent();
        mPixicleId = intent.getIntExtra(Constants.ARG_PIXICLE_IDENTITY, -1);

        addItemsToSpinner();
        setButtonText();

        // Load values from the database
        // TODO: This should show loading while loading...

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void setButtonText() {
        Button button = (Button) findViewById(R.id.save_button);

        if(mPixicleId == -1)
            button.setText(R.string.props_create);
        else
            button.setText(R.string.props_save);
    }

    private void addItemsToSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.software_version);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.props_software_versions_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void onDeleteButton(View view)
    {
        if(mPixicleId != -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder = builder.setTitle("Delete Pixicle");
            builder = builder.setMessage("Are you sure you want to remove the configuration of the current Pixicle?");
            builder = builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (mContentResolver == null)
                        mContentResolver = getContentResolver();

                    mContentResolver.delete(Uri.withAppendedPath(PixicleContentProvider.PIXICLE_URI, Integer.toString(mPixicleId)), null, null);

                    finish();
                }
            });
            builder = builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder = builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.show();
        }

    }

    public void onSaveButton(View view) {

        if(mContentResolver == null)
            mContentResolver = getContentResolver();

        EditText name = (EditText) findViewById(R.id.device_name);
        EditText deviceId = (EditText) findViewById(R.id.device_id);
        EditText numberOfNeoPixels = (EditText) findViewById(R.id.number_of_leds);
        EditText pinNumber = (EditText) findViewById(R.id.pin_number);
        EditText accessToken = (EditText) findViewById(R.id.access_token);
        Spinner softwareVersion = (Spinner) findViewById(R.id.software_version);

        ContentValues values = new ContentValues();
        values.put(PixicleContentProvider.NAME_COLUMN, name.getText().toString());
        values.put(PixicleContentProvider.DEVICE_ID_COLUMN, deviceId.getText().toString().toLowerCase());
        values.put(PixicleContentProvider.NUMBER_OF_NEOPIXELS_COLUMN, numberOfNeoPixels.getText().toString());
        values.put(PixicleContentProvider.NEOPIXEL_ARRANGEMENT_COLUMN, Constants.ARRAGEMENT_ARRAY);
        if(pinNumber.getText().length() == 0)
            values.putNull(PixicleContentProvider.PIN_NUMBER_COLUMN);
        else
            values.put(PixicleContentProvider.PIN_NUMBER_COLUMN, pinNumber.getText().toString());
        if(accessToken.getText().length() == 0)
            values.putNull(PixicleContentProvider.ACCESS_TOKEN_COLUMN);
        else
            values.put(PixicleContentProvider.ACCESS_TOKEN_COLUMN, accessToken.getText().toString());
        values.put(PixicleContentProvider.SOFTWARE_VERSION_COLUMN, softwareVersion.getSelectedItemPosition()+1);
        values.put(PixicleContentProvider.NEOPIXEL_ARRANGEMENT_COLUMN, Constants.ARRAGEMENT_ARRAY);
        values.put(PixicleContentProvider.CREATED_TIME_COLUMN, Constants.StdDateFormat.format(new Date()));
        values.put(PixicleContentProvider.TIMESTAMP_COLUMN, Constants.StdDateFormat.format(new Date()));

        if(mPixicleId == -1) {

            Uri result = mContentResolver.insert(PixicleContentProvider.PIXICLE_URI, values);

            if(result != null)
                mPixicleId = Integer.parseInt(result.getLastPathSegment());
        } else {
            mContentResolver.update(Uri.withAppendedPath(PixicleContentProvider.PIXICLE_URI, Integer.toString(mPixicleId)), values, null, null);
        }

        finish();
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

        data.moveToFirst();

        if(data.isAfterLast())
            return;

        // Fill values into the columns
        EditText name = (EditText) findViewById(R.id.device_name);
        EditText deviceId = (EditText) findViewById(R.id.device_id);
        EditText numberOfNeoPixels = (EditText) findViewById(R.id.number_of_leds);
        EditText pinNumber = (EditText) findViewById(R.id.pin_number);
        EditText accessToken = (EditText) findViewById(R.id.access_token);
        Spinner softwareVersion = (Spinner) findViewById(R.id.software_version);

        int nameColumn = data.getColumnIndex(PixicleContentProvider.NAME_COLUMN);
        int deviceIdColumn = data.getColumnIndex(PixicleContentProvider.DEVICE_ID_COLUMN);
        int softwareVersionColumn = data.getColumnIndex(PixicleContentProvider.SOFTWARE_VERSION_COLUMN);
        int numberOfNeoPixelsColumn = data.getColumnIndex(PixicleContentProvider.NUMBER_OF_NEOPIXELS_COLUMN);
        int accessTokenColumn = data.getColumnIndex(PixicleContentProvider.ACCESS_TOKEN_COLUMN);
        int pinNumberColumn = data.getColumnIndex(PixicleContentProvider.PIN_NUMBER_COLUMN);

        try {
            name.setText(data.getString(nameColumn));
            deviceId.setText(data.getString(deviceIdColumn));
            softwareVersion.setSelection(data.getInt(softwareVersionColumn) - 1, true);
            if(!data.isNull(pinNumberColumn))
                pinNumber.setText(data.getString(pinNumberColumn));
            if(!data.isNull(accessTokenColumn))
                accessToken.setText(data.getString(accessTokenColumn));
            numberOfNeoPixels.setText(data.getString(numberOfNeoPixelsColumn));
        } catch(Exception e)
        {
            Log.wtf("PixiclePropertiesActivity", e);
            // TODO: This would probably be better if it shows an error message?
        }
        data.setNotificationUri(getContentResolver(), PixicleContentProvider.PIXICLE_URI);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO: Don't know what this is for.
    }
}
