package com.example.android.quakereport;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by test-pc on 20-Jan-18.
 */

public class EarthquakeAdapter extends ArrayAdapter {

    private String fullLocation;
    private String offsetLocation;
    private String primaryLocation;
    private static final String LOCATION_SEPARATOR = " of ";

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param earthquake List of AndroidFlavor objects to display in a list
     */
    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquake)
        {
            // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
            // the second argument is used when the ArrayAdapter is populating a single TextView.
            // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
            // going to use this second argument, so it can be any value. Here, we used 0.
            super(context, 0, earthquake);
        }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            View listItemView = convertView;
            if(listItemView == null)
                {
                    listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                }

            // Get the {@link Earthquake} object located at this position in the list
            Earthquake currentEarthquake = (Earthquake) getItem(position);

            DecimalFormat formatter = new DecimalFormat("0.0");

            // Find the TextView in the list_item.xml layout with the ID version_name
            TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude);

            // Get the version name from the current AndroidFlavor object and
            // set this text on the name TextView
            magnitudeTextView.setText(String.valueOf(formatter.format(currentEarthquake.getMagnitude())));

            // Set the proper background color on the magnitude circle.
            // Fetch the background from the TextView, which is a GradientDrawable.
            GradientDrawable magnitudeCircle = (GradientDrawable) listItemView.findViewById(R.id.magnitude).getBackground();

            // Get the appropriate background color based on the current earthquake magnitude
            int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

            // Set the color on the magnitude circle
            magnitudeCircle.setColor(magnitudeColor);

            //Get the complete location as is from the JSON data that was used
            fullLocation = currentEarthquake.getCity();

            //If the complete location contains a " of "
            if (fullLocation.contains(LOCATION_SEPARATOR))
                {
                    //Splits the original location into an array at the index of the string " of "
                    String[] parts = fullLocation.split(LOCATION_SEPARATOR);

                    //Index 0 is used as the offset location with the " of " added to it
                    offsetLocation = parts[0] + LOCATION_SEPARATOR;

                    //Index 1 is used as the primary location, which is the rest of the original
                    //String after the " of "
                    primaryLocation = parts[1];
                }
            else
                {
                    //Set the value of offsetLoaction to be the value that is
                    //used in the String values directory, named "near_the"
                    offsetLocation = getContext().getString(R.string.near_the);

                    //The primary location value is set to be the original value that was queried
                    //from JSON
                    primaryLocation = fullLocation;
                }

            TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_Location);
            primaryLocationView.setText(primaryLocation);

            TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.offset_Location);
            locationOffsetView.setText(offsetLocation);

            /*
            //Alternative code that i made by myself
            if(fullLocation.contains("of"))
                {
                    int length = fullLocation.length();
                    int index = fullLocation.indexOf("of") + 3;
                    offsetLocation = fullLocation.substring(0, index);
                    primaryLocation = fullLocation.substring(index, length);

                    TextView offsetLocationTextView = (TextView) listItemView.findViewById(R.id.offset_Location);
                    TextView primaryLocationTextView = (TextView) listItemView.findViewById(R.id.primary_Location);

                    offsetLocationTextView.setText(offsetLocation);
                    primaryLocationTextView.setText(primaryLocation);
                }
            else
                {
                    offsetLocation = "Near the";

                    TextView offsetLocationTextView = (TextView) listItemView.findViewById(R.id.offset_Location);
                    TextView primaryLocationTextView = (TextView) listItemView.findViewById(R.id.primary_Location);

                    offsetLocationTextView.setText(offsetLocation);
                    primaryLocationTextView.setText(fullLocation);
                }
            */

            //Get the millisecond value (from the JSON source) of the date where the earthquake happened
            Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

            //Cast a TextView that uses the id "date" from the list_item.xml layout
            TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);

            //Convert the millisecond into a "Month Date, Year" format based on the formatDate()
            //method below
            String formattedDate = formatDate(dateObject);

            //Set the value of the new Date format into the TextView that was created
            dateTextView.setText(formattedDate);

            //Cast a TextView that uses the id "time" from the list_item.xml layout
            TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);

            //Convert the millisecond into a "hh:mm AM/PN" format based on the formatTime()
            //method below
            String formattedTime = formatTime(dateObject);

            //Set the value of the new Date format into the TextView that was created
            timeTextView.setText(formattedTime);

            // Return the whole list item layout (containing 2 TextViews and an ImageView)
            // so that it can be shown in the ListView
            return listItemView;
        }

    /**
     * Converts the data of time (in milliseconds) from when the Earthquake happened that we got
     * from JSON into a "Month Date, Year" format
     *
     * @param dateObject the date when the Earthquake happened (in milliseconds)
     */
    private String formatDate(Date dateObject)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyy");
            return dateFormat.format(dateObject);
        }

    /**
     * Converts the data of time (in milliseconds) from when the Earthquake happened that we got
     * from JSON into a "hh:mm AM/PM" format
     *
     * @param dateObject the date when the Earthquake happened (in milliseconds)
     */
    private String formatTime(Date dateObject)
        {
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            return timeFormat.format(dateObject);
        }

    /**
     * Return the color for the magnitude circle based on the intensity of the earthquake.
     *
     * @param magnitude of the earthquake
     */
    private int getMagnitudeColor(double magnitude)
        {
            int magnitudeColorResourceID;
            int magnitudeFloor = (int) Math.floor(magnitude);
            switch (magnitudeFloor)
                {
                    case 0:
                    case 1:
                        magnitudeColorResourceID = R.color.magnitude1;
                        break;
                    case 2:
                        magnitudeColorResourceID = R.color.magnitude2;
                        break;
                    case 3:
                        magnitudeColorResourceID = R.color.magnitude3;
                        break;
                    case 4:
                        magnitudeColorResourceID = R.color.magnitude4;
                        break;
                    case 5:
                        magnitudeColorResourceID = R.color.magnitude5;
                        break;
                    case 6:
                        magnitudeColorResourceID = R.color.magnitude6;
                        break;
                    case 7:
                        magnitudeColorResourceID = R.color.magnitude7;
                        break;
                    case 8:
                        magnitudeColorResourceID = R.color.magnitude8;
                        break;
                    case 9:
                        magnitudeColorResourceID = R.color.magnitude9;
                        break;
                    default:
                        magnitudeColorResourceID = R.color.magnitude10plus;
                        break;
                }
            return ContextCompat.getColor(getContext(), magnitudeColorResourceID);
        }
}
