package se.irl.pixicle;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PixicleAsyncTask extends AsyncTask<String, Void, Boolean> {

    public static String FUNCTION_APPLY_CONFIG = "ApplyConfig";
    public static String FUNCTION_SET_PIXEL_COUNT = "SetPixelCnt";
    public static String FUNCTION_SCHEDULE_CONFIG = "ScheduleCfg";

    /***
     * Parameters are [access_token],[device_id],[cloud_function],[arguments]
     * @param params
     * @return
     */
    @Override
    protected Boolean doInBackground(String... params) {

        if(params.length != 4)
        {
            Log.wtf("PixicleConfigActivity", "Incorrect number of params (" + params.length + ")");
        }

        String access_token = params[0];
        String device_id = params[1];
        String cloudFunction = params[2];
        String arguments = params[3];

        try {
            String myUrl = "https://api.spark.io/v1/devices/"+device_id.toLowerCase()+"/"+cloudFunction+"?access_token="+access_token;

            URL url = new URL(myUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write("args="+arguments);
            writer.flush();
            writer.close();
            os.close();

            connection.connect();
            int res = connection.getResponseCode();
            if (res != 200) {
                Log.d("PixicleAsyncTask", "Failed to fetch stuff: " + res);
                // TODO: SHOW DIALOG OR SOMETHING!!!!
                return false;
            }
        } catch(Exception e)
        {
            Log.wtf("PixicleAsyncTask", e.toString());
            // TODO: SHOW DIALOG OR SOMETHING!!!!
            return false;
        }

        return true;
    }
}
