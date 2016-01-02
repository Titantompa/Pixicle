package se.irl.pixicle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Date;
import java.util.GregorianCalendar;

public class ScheduleConfigurationActivity extends AppCompatActivity {

    private int mPixicleId;
    private String mDeviceIdentifier;
    private String mAccessToken;
    private String mConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPixicleId = intent.getIntExtra(Constants.ARG_PIXICLE_IDENTITY, -1);
        mDeviceIdentifier = intent.getStringExtra(Constants.ARG_DEVICE_IDENTITY);
        mAccessToken = intent.getStringExtra(Constants.ARG_ACCESS_TOKEN);
        mConfiguration = intent.getStringExtra(Constants.ARG_CONFIGURATION);

        setContentView(R.layout.activity_schedule_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TimePicker timePicker = (TimePicker) findViewById(R.id.schedule_time);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) findViewById(R.id.schedule_date);
                TimePicker timePicker = (TimePicker) findViewById(R.id.schedule_time);

                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                GregorianCalendar gregDate = new GregorianCalendar(year, month, dayOfMonth, hour, minute, 0);
                Long gregTime = gregDate.getTimeInMillis()/1000;
                Date debugDate = new Date();
                Long debugTime = debugDate.getTime()/1000;

                Log.d("SchedCfgActivity", "Current time is "+debugTime+" schedule time is "+gregTime+" which is "+(gregTime-debugTime)+" seconds into the future.");

                String config = gregTime.toString() +":"+mConfiguration;

                new PixicleAsyncTask().execute(mAccessToken, mDeviceIdentifier, PixicleAsyncTask.FUNCTION_SCHEDULE_CONFIG, config);

                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
