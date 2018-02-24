package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Created by test-pc on 24-Feb-18.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /*Request Url*/
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public EarthquakeLoader(Context context, String url)
        {
            super(context);
            this.mUrl = url;
        }

    @Override
    protected void onStartLoading()
        {
            Log.i(LOG_TAG, "TEST: onStartLoading() called ... ");

            forceLoad();
        }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Earthquake> loadInBackground()
        {
            Log.i(LOG_TAG, "TEST: loadInBackground() called ... ");

            if(this.mUrl == null)
                {
                    return null;
                }

            List<Earthquake> result = QueryUtils.fetchEarthquakeData(mUrl);
            return result;
        }
}
