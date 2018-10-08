package com.example.android.newsapp;

public class News {

    /**
     * Title of the news article
     */
    private String strTitle;

    /**
     * Name of the section of the news article
     */
    private String strSection;

    /**
     * Name of the author of the news article
     */
    private String strAuthor;

    /**
     * Published date of the news article
     */
    private String strPublishedDate;

    /**
     * URL of the news article
     */
    private String strURL;

    /**
     * Constructs a new {@link News} object
     *
     * @param title         is the title of the news article
     * @param section       is the section the news article belongs to
     * @param author        is the author of the news article (where available)
     * @param publishedDate is the date the article was published
     * @param URL           is the URL of the article
     */
    public News(String title, String section, String author, String publishedDate, String URL) {
        strTitle = title;
        strSection = section;
        strAuthor = author;
        strPublishedDate = publishedDate;
        strURL = URL;
    }

    /**
     * Returns the title
     */
    public String getTitle() {
        return strTitle;
    }

    /**
     * Returns the section
     */
    public String getSection() {
        return strSection;
    }

    /**
     * Returns the author
     */
    public String getAuthor() {
        return strAuthor;
    }

    /**
     * Returns the date
     */
    public String getPublishedDate() {
        return strPublishedDate;
    }

    /**
     * Returns the URL
     */
    public String getURL() {
        return strURL;
    }
}
