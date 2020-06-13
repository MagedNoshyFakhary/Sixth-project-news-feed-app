package com.maged.elmagdnews;

import java.util.ArrayList;

/**
 * An {@link News} object contains information related to a single news article.
 */

public class News{
    /** Title of the news article */
    private String mArticleTitle;
    /** Author of the news article */
    private  ArrayList<String> mArticleAuthors;
    /** Date the news article was published */
    private String mArticlePublishDate;
    /** Website URL of the news article */
    private String mArticleUrl;
    /** Website URL of the news article Thumbnail*/
    private String mThumbnail;
    /** Section of the news article */
    private String section;
    /**
     * Constructs a new {@link News} object.
     */
    public News(String mArticleTitle,  ArrayList<String> mArticleAuthor, String mArticlePublishDate, String mArticleUrl, String mThumbnail,  String section) {
        this.mArticleTitle = mArticleTitle;
        this.mArticleAuthors = mArticleAuthor;
        this.mArticlePublishDate = mArticlePublishDate;
        this.mArticleUrl = mArticleUrl;
        this.mThumbnail = mThumbnail;
        this.section = section;
    }

    public String getSection() {
        return section;
    }

    // Returns the title of the news article.
    public String getmArticleTitle() {
        return mArticleTitle;
    }
    // Returns the title of the news article Author.
    public  ArrayList<String> getmArticleAuthor() {
        return mArticleAuthors;
    }
    // Returns the title of the news article Dare.
    public String getmArticlePublishDate() {
        return mArticlePublishDate;
    }
    // Returns the title of the news article Url.
    public String getmArticleUrl() {
        return mArticleUrl;
    }
    // Returns the title of the news article Thumbnail.
    public String getmThumbnail() { return mThumbnail; }
}
