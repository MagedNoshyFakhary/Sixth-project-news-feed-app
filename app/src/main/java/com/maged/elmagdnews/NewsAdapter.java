package com.maged.elmagdnews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * {@link NewsAdapter} knows how to create a list item layout for each article
 * in the data source (a list of {@link News} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAdapter extends ArrayAdapter<News> {

    /** Tag for log messages **/
    private static final String LOG_TAG = NewsAdapter.class.getName();
    private Context context;

     public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    /**
     * Constructor for News Adapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_single_list_item, parent, false);
        }
        News currentNews = getItem(position);
        if (currentNews != null) {
            ImageView contentImage = listItemView.findViewById(R.id.thumbnail);
            loadImageFromUrl(currentNews.getmThumbnail(), contentImage);

            TextView contentTitle = listItemView.findViewById(R.id.title);
            String title = currentNews.getmArticleTitle();
            if(title != null) {
                contentTitle.setText(title);
            }else{
                contentTitle.setText(context.getString(R.string.no_title_found));
            }

            TextView contentSection = listItemView.findViewById(R.id.section);
            String section = currentNews.getSection();
            if(section != null) {
                contentSection.setText(section);
            }else{
                contentSection.setText(context.getString(R.string.no_section_found));
            }

            TextView contentPublishedDate = listItemView.findViewById(R.id.date);
            String currentDateAndTime = currentNews.getmArticlePublishDate();
            if(currentDateAndTime != null) {
                try {
                    String date = getNewsPublicationDate(currentDateAndTime);
                    contentPublishedDate.setText(date);
                    contentPublishedDate.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, context.getString(R.string.problem_passing_date_and_time), e);
                }
            }else{
                contentPublishedDate.setText(context.getString(R.string.no_date_found));
            }

            TextView authorsTextView = listItemView.findViewById(R.id.authors);
            ArrayList<String> authorsArray = currentNews.getmArticleAuthor();
            if(authorsArray == null ){
                authorsTextView.setText(context.getString(R.string.no_author_found));
            }else{
                StringBuilder authorString = new StringBuilder();
                for(int i=0;i<authorsArray.size(); i++){
                    authorString.append(authorsArray.get(i));
                    if((i + 1) < authorsArray.size()){
                        authorString.append(", ");
                    }
                }
                authorsTextView.setText(authorString.toString());
                authorsTextView.setVisibility(View.VISIBLE);
            }
        }

        return listItemView;
    }
/*getNewsPublicationDate*/
    private String getNewsPublicationDate(String currentDateAndTime) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.received_date_and_time_format));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat(context.getString(R.string.displayed_date_format));
        return sdfDate.format(sdf.parse(currentDateAndTime));
    }
// loadImageFromUrl
    private void loadImageFromUrl(String url, ImageView contentImage) {
        if(url != null) {
            Picasso.with(context).load(url).placeholder(R.drawable.loading)
                    .error(R.drawable.no_image_available)
                    .into(contentImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }else{
            contentImage.setImageResource(R.drawable.no_image_available);
        }
    }
}
