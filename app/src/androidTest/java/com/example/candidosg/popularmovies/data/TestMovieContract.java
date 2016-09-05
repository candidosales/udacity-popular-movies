package com.example.candidosg.popularmovies.data;

import android.test.AndroidTestCase;

/**
 * Created by brasilct on 18/08/16.
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_MOVIE_ID = "21321321";  // December 20th, 2014

    /*
        Students: Uncomment this out to test your weather location function.
     */
//    public void testBuildWeatherLocation() {
//        Uri locationUri = WeatherContract.WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);
//        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
//                        "WeatherContract.",
//                locationUri);
//        assertEquals("Error: Weather location not properly appended to the end of the Uri",
//                TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
//        assertEquals("Error: Weather location Uri doesn't match our expected result",
//                locationUri.toString(),
//                "content://com.example.android.sunshine.app/weather/%2FNorth%20Pole");
//    }

}
