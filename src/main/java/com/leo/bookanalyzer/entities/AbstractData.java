/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.bookanalyzer.entities;

import java.util.HashMap;

/**
 *
 * @author HENSEL
 */
public abstract class AbstractData {

    //SimilarityThreshold needs extensive testing!
    public double similarityThreshold = 0.9;

    public String primaryName;

    //Which name,location,... variants with how many occurences?
    private HashMap<String, Integer> names;

    //How many occurence in which chapter?
    private HashMap<String, Integer> chapters;

    private int occurences;

    public AbstractData(String name) {
        this.primaryName = name;
        this.occurences = 1;
        this.names = new HashMap();
        this.chapters = new HashMap();
    }

    public void addOtherName(String name) {
        if (!primaryName.equals(name) && name.length() > 3) {
            if (!names.containsKey(name)) {
                System.out.println("- " + primaryName + " is also known as " + name);
                names.put(name, 1);
            } else {
                names.put(name, names.get(name) + 1);
                System.out.println("- " + primaryName + " was called " + name + " once again");
            }
        }
    }

    public HashMap<String, Integer> getOtherNames() {
        return names;
    }

    public void addChapterOccurence(String chapterName) {
        if (!chapters.containsKey(chapterName)) {
            chapters.put(chapterName, 1);
        } else {
            chapters.put(chapterName, chapters.get(chapterName) + 1);
        }
    }

    public void addOccurence() {
        this.occurences++;
    }

    public int getOccurences() {
        return occurences;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setName(String name) {
        this.primaryName = name;
    }

    public String toString() {
        return "Primary Name: " + primaryName + " , Other Names: " + names.toString() + " Occurences: " + occurences;
    }
}
