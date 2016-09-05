package com.example.candidosg.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by brasilct on 18/08/16.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String MOVIE_QUERY = "123123";
    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
//    public void testUriMatcher() {
//        UriMatcher testMatcher = WeatherProvider.buildUriMatcher();
//
//        assertEquals("Error: The WEATHER URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_DIR), WeatherProvider.WEATHER);
//        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_LOCATION);
//        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
//                testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
//        assertEquals("Error: The LOCATION URI was matched incorrectly.",
//                testMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
//    }
}
