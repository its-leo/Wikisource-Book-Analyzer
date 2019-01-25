package com.leo.bookanalyzer;

import java.util.ArrayList;

/**
 *
 * @author HENSEL
 */
public class Chapter {

    private Book book;
    private String title;

    private ArrayList<String> paragraphs;

    private int sentiment = 0;

    public Chapter(Book book, String title) {
        this.book = book;
        this.title = title;
        this.paragraphs = new ArrayList<String>();

    }

    public void addParagraph(String paragraph) {
        paragraphs.add(paragraph);
    }

    public String getParagraph(int i) {
        return paragraphs.get(i);
    }

    public ArrayList<String> getParagraphs() {
        return paragraphs;
    }

    public String getTitle() {
        return title;
    }

    public void changeSentimentBy(int change) {
        this.sentiment += change;
    }    
    
    public int getSentiment() {
        return sentiment;
    }

}
