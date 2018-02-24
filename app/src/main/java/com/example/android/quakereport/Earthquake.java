package com.example.android.quakereport;

/**
 * Created by test-pc on 20-Jan-18.
 */

public class Earthquake {

    private double magnitude;
    private String city;
    private String time;
    private long timeInMilliseconds;
    private String url;

    /**
     * Constructs a new {@link Earthquake} object.
     *
     * @param magnitude is the magnitude (size) of the earthquake
     * @param city is the location where the earthquake happened
     * @param timeInMilliseconds is the time in milliseconds (from the Epoch) when the
     *                           earthquake happened
     * @param url is the website URL to find more details about the earthquake
     */
    public Earthquake(double magnitude, String city, long timeInMilliseconds, String url)
        {
            this.magnitude = magnitude;
            this.city = city;
            this.timeInMilliseconds = timeInMilliseconds;
            this.url = url;
        }

    public double getMagnitude()
        {
            return this.magnitude;
        }

    public String getCity()
        {
            return city;
        }

    public long getTimeInMilliseconds()
        {
            return timeInMilliseconds;
        }

    public String getUrl()
        {
            return url;
        }
}
