package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Created by test-pc on 20-Jan-18.
 */

public final class QueryUtils {

    /** Sample JSON response for a USGS query */
    //private static final String SAMPLE_JSON_RESPONSE =
    //        "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createURL(String stringURL)
        {
            URL url = null;
            try
                {
                    url = new URL(stringURL);
                }
            catch (MalformedURLException e)
                {
                    Log.e(LOG_TAG, "Error with creating url", e);
                }
            return url;
        }

    private static String makeHttpRequest(URL url) throws IOException
        {
            String jsonResponse = " ";

            //if the url is null, then return early
            if(url == null)
                {
                    return jsonResponse;
                }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try
                {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(10000 /* milliseconds */);
                    urlConnection.setConnectTimeout(15000 /* milliseconds */);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //If the request was successful (response code 200)
                    //then read the input stream and parse the response
                    if(urlConnection.getResponseCode() == 200)
                        {
                            inputStream = urlConnection.getInputStream();
                            jsonResponse = readFromStream(inputStream);
                        }
                    else
                        {
                            Log.e(LOG_TAG,"Error response code: " + urlConnection.getResponseCode());
                        }
                }
            catch (IOException e)
                {
                    Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results", e);
                }
            finally
                {
                    if(urlConnection != null)
                        {
                            urlConnection.disconnect();
                        }
                    if(inputStream != null)
                        {
                            inputStream.close();
                        }
                }
            return jsonResponse;
        }

    private static String readFromStream(InputStream inputStream) throws IOException
        {
            StringBuilder output = new StringBuilder();
            if(inputStream != null)
                {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    while (line != null)
                        {
                            output.append(line);
                            line = reader.readLine();
                        }
                }
            return output.toString();
        }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractFeatureFromJson(String earthquakeJson)
        {
            //If the JSON is empty, return early
            if(TextUtils.isEmpty(earthquakeJson))
                {
                    return null;
                }

            // Create an empty ArrayList that we can start adding earthquakes to
            ArrayList<Earthquake> earthquakes = new ArrayList<>();

            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try
                {
                    //Create a JSON object from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(earthquakeJson);

                    //Extract the JSONArray associated with the key called "features",
                    //which represents a list of features (earthquakes)
                    JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

                    //For each earthquake in the earthquake array, create an {@link Earthquake} object
                    for (int i=0; i<earthquakeArray.length(); i++)
                        {
                            //Get a single earthquake at position i within the list of earthquake array
                            JSONObject earthquakeObject = earthquakeArray.getJSONObject(i);

                            // For a given earthquake, extract the JSONObject associated with the
                            // key called "properties", which represents a list of all properties
                            // for that earthquake.
                            JSONObject properties = earthquakeObject.getJSONObject("properties");

                            //Extract the value for the key called "mag"
                            double magnitude = properties.optDouble("mag");

                            //Extract the value for the key called "place"
                            String place = properties.optString("place");

                            //Extract the value for the key called "time"
                            long date = properties.getLong("time");

                            //Extract the value for the key called "url"
                            String url = properties.getString("url");

                            //Create a new {@link Earthquake} object with magnitude, location,
                            //time and url from the JSON response
                            Earthquake earthquake = new Earthquake(magnitude, place, date, url);

                            //Add the new {@link Earthquake} to the list of earthquakes
                            earthquakes.add(earthquake);
                        }
                }
            catch (JSONException e)
                {
                    // If an error is thrown when executing any of the above statements in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
                }

            // Return the list of earthquakes
            return earthquakes;
        }

    public static List<Earthquake> fetchEarthquakeData(String resultUrl)
        {
            Log.i(LOG_TAG, "TEST: fetchEarthquakeData() called ... ");

            // A simulation of a slow network with a delay time of 2 seconds
            try
                {
                   Thread.sleep(2000);
                }
            catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            //Create URL object
            URL url = createURL(resultUrl);

            //Perform HTTP request to the URL and recieve a json response
            String jsonResponse = null;
            try
                {
                    jsonResponse = makeHttpRequest(url);
                }
            catch (IOException e)
                {
                    Log.e(LOG_TAG, "Problem making HTTP Request", e);
                }

            //Extract relevant fields from the JSON response and create a list of {@link Earthquake}
            List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);

            return earthquakes;
        }
}
