package com.example.android.newsapp;

import android.net.Uri;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtility {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtility.class.getSimpleName();

    private QueryUtility() {

    }

    /**
     * Query the Gaurdian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link news}
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link news}
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news items to
        List<News> newsArrayList = new ArrayList<>();


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONObject associated with the key called "response",
            // which represents a responses
            JSONObject newsJSONObject = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or news articles).
            JSONArray newsArray = newsJSONObject.getJSONArray("results");

            // For each newsItem in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news item at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentNews.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String section = currentNews.getString("sectionName");

                // Extract the value for the key called "webUrl"
                String URL = currentNews.getString("webUrl");

                /**
                 * Check first if the tags are empty
                 * Extract the value for the key called "webTitle"
                 */
                String author = "";
                JSONArray tagsArray = currentNews.getJSONArray("tags");
                if (tagsArray.length() > 0) {
                    JSONObject tagObject = tagsArray.getJSONObject(0);
                    author = tagObject.getString("webTitle");

                }

                // Extract the value for the key called "webPublicationDate"
                String date = dateFormatter(currentNews.getString("webPublicationDate"));

                // Create a new {@link News} object with the title, section, author,
                // date and url from the JSON response.
                News news = new News(title, section, author, date, URL);

                newsArrayList.add(news);
            }

        } catch (JSONException e) {

            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }

        // Return the list of earthquakes
        return newsArrayList;
    }

    /**
     * @param date the date received from the api
     * @return a formatted version of the date
     */
    private static String dateFormatter(String date) {
        String pattern = "dd-MM-yyyy";
        Date newDate = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat(pattern);
        try {
            newDate = inputFormat.parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing the date", e);
        }

        String formattedDate = outputFormat.format(newDate);

        return formattedDate;
    }

}
