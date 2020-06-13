package com.maged.elmagdnews;

import android.content.Context;
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
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving news data from The Guardian API.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int OK_RESPONSE_CODE = 200;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }
/*fetchNewsData*/
    static List<News> fetchNewsData(String queryUrl, Context context) {
        URL url = createUrl(queryUrl, context);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.http_request_error_message), e);
        }
        return extractNews(jsonResponse, context);
    }

    private static URL createUrl(String queryUrl, Context context) {
        URL url = null;
        try {
            url = new URL(queryUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_building_url), e);
        }
        return url;
    }
/*makeHttpRequest*/
    private static String makeHttpRequest(URL url, Context context) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod(context.getString(R.string.method_GET));
            urlConnection.connect();

            if (urlConnection.getResponseCode() == OK_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream, context);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.error_response_code_message) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_retrieving_json_result), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
/*readFromStream*/
    private static String readFromStream(InputStream inputStream, Context context) throws IOException {
        StringBuilder outputString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader isr = new InputStreamReader(inputStream, Charset.forName(context.getString(R.string.utf_8)));
            BufferedReader br = new BufferedReader(isr);
            String currentLine = br.readLine();
            while (currentLine != null) {
                outputString.append(currentLine);
                currentLine = br.readLine();
            }
        }
        return outputString.toString();
    }
/*extractNews*/
    private static List<News> extractNews(String jsonResponse, Context context) {
        List<News> news = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.has(context.getString(R.string.response_object))){
                JSONObject responseJSONObject = jsonObject.getJSONObject(context.getString(R.string.response_object));
                if(responseJSONObject.has(context.getString(R.string.results_array))) {
                    JSONArray resultsJSONArray = responseJSONObject.getJSONArray(context.getString(R.string.results_array));
                    for (int i = 0; i < resultsJSONArray.length(); i++) {
                        JSONObject currentJSONObject = resultsJSONArray.getJSONObject(i);
                        String title;
                        if(currentJSONObject.has(context.getString(R.string.title))) {
                            title = currentJSONObject.getString(context.getString(R.string.title));
                        }else{
                            title = null;
                        }
                        String section;
                        if(currentJSONObject.has(context.getString(R.string.section_name))) {
                            section = currentJSONObject.getString(context.getString(R.string.section_name));
                        }else{
                            section = null;
                        }
                        String publicationDateTime;
                        if(currentJSONObject.has(context.getString(R.string.web_publication_date_and_time))) {
                            publicationDateTime = currentJSONObject.getString(context.getString(R.string.web_publication_date_and_time));
                        }else{
                            publicationDateTime = null;
                        }
                        String webUrl;
                        if(currentJSONObject.has(context.getString(R.string.web_url))) {
                            webUrl = currentJSONObject.getString(context.getString(R.string.web_url));
                        }else{
                            webUrl = null;
                        }
                        String thumbnail;
                        if(currentJSONObject.has(context.getString(R.string.fields_object))) {
                            JSONObject currentFieldsObject = currentJSONObject.getJSONObject(context.getString(R.string.fields_object));
                            if(currentFieldsObject.has(context.getString(R.string.thumbnail))) {
                                thumbnail = currentFieldsObject.getString(context.getString(R.string.thumbnail));
                            }else{
                                thumbnail = null;
                            }
                        }
                        else{
                            thumbnail = null;
                        }
                        ArrayList<String> authors = new ArrayList<>();
                        if(currentJSONObject.has(context.getString(R.string.tags_array))) {
                            JSONArray currentTagsArray = currentJSONObject.getJSONArray(context.getString(R.string.tags_array));
                            if (currentTagsArray == null || currentTagsArray.length() == 0) {
                                authors = null;
                            } else {
                                for (int j = 0; j < currentTagsArray.length(); j++) {
                                    JSONObject currentObjectInTags = currentTagsArray.getJSONObject(j);
                                    authors.add(currentObjectInTags.getString(context.getString(R.string.web_title_in_tags_array)));
                                }
                            }
                        }else{
                            authors = null;
                        }
                        news.add(new News(title,authors, publicationDateTime, webUrl, thumbnail, section));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_parsing_news_json_result), e);
        }
        return news;
    }
}
