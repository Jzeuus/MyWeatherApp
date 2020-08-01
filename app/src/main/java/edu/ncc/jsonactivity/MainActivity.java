package edu.ncc.jsonactivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
                // URL url = new URL("https://api.openweathermap.org/data/2.5/weather?id=5118226&units=imperial&APPID=c59c8be06eb651401da3f4e32aa4371e");
                URL url = new URL("https://api.openweathermap.org/data/2.5/forecast/daily?id=5118226&units=imperial&cnt=5&APPID=c59c8be06eb651401da3f4e32aa4371e");
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

                    /*
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
                    */

                    // retrieve the city name
                    JSONObject cityObj = jsonObj.getJSONObject("city");
                    String cityName = cityObj.getString("name");
                    ((TextView)findViewById(R.id.name)).setText(cityName);

                    // retrieve the coordinates
                    JSONObject coordObj = cityObj.getJSONObject("coord");
                    String latitude = coordObj.getString("lat");
                    String longitude = coordObj.getString("lon");
                    ((TextView)findViewById(R.id.longitude)).setText(longitude);
                    ((TextView)findViewById(R.id.latitude)).setText(latitude);

                    // retrieve the day, min and max temperatures
                    JSONArray listArray = jsonObj.getJSONArray("list");

                    // retrieve the length of the array from the JSON
                    int count = jsonObj.getInt("cnt");
                    Log.d(TAG, "The count is " + count);
                    for (int i=0; i<count; i++) {
                        JSONObject dayObj = listArray.getJSONObject(i);
                        JSONObject tempObj = dayObj.getJSONObject("temp");
                        String dayTemp = tempObj.getString("day");
                        String minTemp = tempObj.getString("min");
                        String maxTemp = tempObj.getString("max");

                        // retrieve the description
                        JSONArray weatherArray = dayObj.getJSONArray("weather");
                        JSONObject weatherObj = weatherArray.getJSONObject(0);
                        String description = weatherObj.getString("description");

                        Log.d(TAG, "city: " + cityName + " lat/lon: " + latitude
                                + " " + longitude + " day temp: " + dayTemp + " min/max: " +
                                minTemp + " " + maxTemp + " description: "
                                + description);

                        // create a HashMap
                        HashMap<String, String> location = new HashMap<>();
                        // put the information into the HashMap
                        //location.put(TAG_CITY, cityName);
                        //location.put(TAG_LAT, latitude);
                        //location.put(TAG_LON, longitude);
                        location.put(TAG_DAY, dayTemp);
                        location.put(TAG_MAX, maxTemp);
                        location.put(TAG_MIN, minTemp);
                        location.put(TAG_DESCRIPTION, description);
                        // add the HashMap to the ArrayList
                        locationList.add(location);
                    }
                    // create a ListAdapter object that maps the data to the
                    // views in the list_item layout
                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this, locationList,
                            R.layout.list_item,
                            new String[] {TAG_DAY, TAG_MAX, TAG_MIN, TAG_DESCRIPTION},
                            new int[] {R.id.temperature, R.id.max, R.id.min, R.id.description});

                    // get access to the list view in the layout file
                    ListView myList = (ListView)findViewById(R.id.list);
                    // set the adapter to be displayed in the list view
                    myList.setAdapter(adapter);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

        }
    }
}
