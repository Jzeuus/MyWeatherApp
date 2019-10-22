package edu.ncc.jsonactivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "REST";
    private static final String JTAG = "JSON";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_CITY = "name";
    public static final String TAG_DAY = "day";
    public static final String TAG_MIN = "min";
    public static final String TAG_MAX = "max";
    public static final String TAG_DESCRIPTION = "desc";
    ArrayList<HashMap<String, String>> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationList = new ArrayList<HashMap<String, String>>();
        Log.d(TAG, "calling get locations");
        new GetLocations().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * inner class that will access the rest API and process the JSON returned
     */
    private class GetLocations extends AsyncTask<Void, Void, Void> {
        String result = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "on pre execute");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpURLConnection urlConnection;
            BufferedReader reader;

            try {
                // set the URL for the API call - substitute your APPID in the statement below
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=5118226&units=imperial&APPID=c59c8be06eb651401da3f4e32aa4371e");
                // connect to the site to read information
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // store the data retrieved by the request
                InputStream inputStream = urlConnection.getInputStream();
                // no data returned by the request, so terminate the method
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                // store the data into a BufferedReader so it can be stored into a string
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String s;
                while ((s = reader.readLine()) != null) {
                    result += s;
                }
            } catch (Exception e) {
                Log.i("HttpAsyncTask", "EXCEPTION: " + e.getMessage());
            }

            Log.d(TAG, "Returned string: " + result);
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            super.onPostExecute(r);

            if (result != null) {
                Log.d(TAG, "about to start the JSON parsing" + result);
                try {
                    JSONObject jsonObj = new JSONObject(result);

                    // code to parse the JSON objects here
                    String cityName = jsonObj.getString("name");
                    Log.d(TAG, "The city name is " + cityName);

                    JSONObject coordinates = jsonObj.getJSONObject("coord");
                    String latitude = coordinates.getString("lat");
                    String longitude = coordinates.getString("lon");
                    Log.d(TAG, "latitude: " + latitude + " " +
                            " longitude: " + longitude);

                    JSONObject mainObj = jsonObj.getJSONObject("main");
                    String dayTemp = mainObj.getString("temp");
                    String minTemp = mainObj.getString("temp_min");
                    String maxTemp = mainObj.getString("temp_max");
                    Log.d(TAG, "day temp: " + dayTemp + " " +
                            " min temp: " + minTemp +
                            " max temp: " + maxTemp);

                    JSONArray weatherArray = jsonObj.getJSONArray("weather");
                    JSONObject item1 = weatherArray.getJSONObject(0);
                    String description = item1.getString("description");
                    Log.d(TAG, "Today's weather is " + description);





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

        }
    }
}
