/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.textanalyzer;

import com.leo.textanalyzer.entities.AbstractData;
import com.leo.textanalyzer.entities.CauseOfDeath;
import com.leo.textanalyzer.entities.Character;
import com.leo.textanalyzer.entities.Place;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author HENSEL
 */
public class Book {

    private String url;
    //-----------------------------------
    private ArrayList<Chapter> chapters;
    //Bookmark to know which chapters have already been analyzed.
    private int chapterBookmark;
    //-----------------------------------
    private ArrayList<Character> characters;
    private ArrayList<Place> places;
    private ArrayList<CauseOfDeath> causesOfDeath;
    //-----------------------------------
    private int tokenCount, sentenceCount;
    //-----------------------------------

    public Book(String url) {
        this.url = url;
        //-----------------------------------
        this.chapters = new ArrayList<Chapter>();

        this.chapterBookmark = 0;
        //-----------------------------------
        this.characters = new ArrayList<Character>();
        this.places = new ArrayList<Place>();
        this.causesOfDeath = new ArrayList<CauseOfDeath>();
        //-----------------------------------
        tokenCount = 0;
        sentenceCount = 0;
        //-----------------------------------

    }

    public void fetch() {
        System.out.print("Fetching Book... ");
        try {
            Document document;
            document = Jsoup.connect(url).get();

            Element content = document.select("div#mw-content-text").first();
            Elements elements = content.select("span.mw-headline, h2 ~ div, h2 ~ p, h2 ~ ul");

            for (Element elem : elements) {
                if (elem.hasClass("mw-headline")) {
                    chapters.add(new Chapter(this, elem.text()));
                } else {
                    chapters.get(chapters.size() - 1).addParagraph(elem.text());
                }
            }
            System.out.println("Done");
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void analyze() {
        if (chapters.size() > 0) {
            NLP.analyze(this);
        }
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void addCharacter(String characterName) {
        //The threshold needs adjustment!
        if (searchData(characters, characterName) == -1) {
            characters.add(new Character(characterName));
        }
    }

    public void addPlace(String placeName, Place.Type type) {
        if (searchData(places, placeName) == -1) {
            places.add(new Place(placeName, type));
        }
    }

    public void addCauseOfDeath(String causeOfDeathName) {
        if (searchData(causesOfDeath, causeOfDeathName) == -1) {
            causesOfDeath.add(new CauseOfDeath(causeOfDeathName));
        }
    }

    /**
     * Returns an Interger which states whether and where a "name" exists in a
     * given ArrayList of a child of AbstractData (characters, places,
     * causesOfDeath...).
     *
     * @param list ArrayList of Type Character, Place...
     * @param name Search term
     */
    private <T extends AbstractData> int searchData(ArrayList<T> list, String name) {
        double nearestDistance = 0;
        int indexOfNearest = -1;

        for (int i = 0; i < list.size(); i++) {
            String primaryName = list.get(i).getPrimaryName();

            if (primaryName.contains(name)) {
                nearestDistance = 1;
                indexOfNearest = i;
            }
            double distance = StringUtils.getJaroWinklerDistance(primaryName, name);
            if (distance > nearestDistance && distance > list.get(i).similarityThreshold) {
                nearestDistance = distance;
                indexOfNearest = i;
            }
        }
        if (indexOfNearest > -1) {
            list.get(indexOfNearest).addOtherName(name);
            list.get(indexOfNearest).addChapterOccurence(chapters.get(chapterBookmark).getTitle());
            list.get(indexOfNearest).addOccurence();
        }
        return indexOfNearest;
    }

    //Get a character by name
    public Character getCharacter(String characterName) {
        int indexOfNearest = searchData(characters, characterName);
        if (indexOfNearest > -1) {
            return characters.get(indexOfNearest);
        }
        return null;
    }

    public void addToTokenCount(int wordCount) {
        this.tokenCount += wordCount;
    }

    public void addToSentenceCount(int sentenceCount) {
        this.sentenceCount += sentenceCount;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getChapterBookmark() {
        return chapterBookmark;
    }

    public void setChapterBookmark(int chapterBookmark) {
        this.chapterBookmark = chapterBookmark;
    }

    //------------------------------------------------------------------
    //The following code mess needs to be cleaned up and just serves the purpose to show off the data
    public String toString() {
        String out = "\n";
        if (chapters.size() > 0) {
            if (Main.DEBUG) {
                System.out.println("\nCHARACTERS");
                for (int i = 0; i < characters.size(); i++) {
                    System.out.println(characters.get(i).toString());
                }
                System.out.println("\nPLACES");
                for (int i = 0; i < places.size(); i++) {
                    System.out.println(places.get(i).toString());
                }
                System.out.println("\nCAUSES OF DEATH");
                for (int i = 0; i < causesOfDeath.size(); i++) {
                    System.out.println(causesOfDeath.get(i).toString());
                }
                System.out.printf("%n%-15s|%s%n", "CHAPTER", "SENTIMENT");
                for (int i = 0; i < chapterBookmark; i++) {
                    System.out.printf("%-15s|%s%n", chapters.get(i).getTitle(), chapters.get(i).getSentiment());
                }
            }

            out += "We have analyzed " + tokenCount + " tokens (words, punctuation) and " + sentenceCount + " sentences in total.\n";

            int mostCharacterOccurences = 0;
            int mostCharacterOccurencesIndex = 0;
            for (int i = 0; i < characters.size(); i++) {
                if (characters.get(i).getOccurences() > mostCharacterOccurences) {
                    mostCharacterOccurences = characters.get(i).getOccurences();
                    mostCharacterOccurencesIndex = i;
                }
            }
            Character protagonist = characters.get(mostCharacterOccurencesIndex);

            out += "The main character is";
            if (protagonist.getPrimaryTitle() != null) {
                out += " " + Helper.getIndefiniteArticle(protagonist.getPrimaryTitle()) + " " + protagonist.getPrimaryTitle();
            }
            out += " named " + protagonist.getPrimaryName();

            if (!protagonist.getOtherNames().isEmpty()) {
                out += " - also known as " + Collections.max(protagonist.getOtherNames().entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            }

            if (protagonist.getCityOfDeath() != null) {
                out += " who died in " + protagonist.getCityOfDeath();
            }
            out += ".\n";

            if (protagonist.getParents() != null) {
                if (!protagonist.getParents().isEmpty()) {
                    out += "The characters parents are " + protagonist.getParents().toString() + ".\n";
                }
            }
            if (protagonist.getChildren() != null) {
                if (!protagonist.getChildren().isEmpty()) {
                    out += "The characters children are " + protagonist.getChildren().toString() + ".\n";
                }
            }

            int mostPlaceOccurences = 0;
            int mostPlaceOccurencesIndex = 0;
            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).getOccurences() > mostPlaceOccurences) {
                    mostPlaceOccurences = places.get(i).getOccurences();
                    mostPlaceOccurencesIndex = i;
                }
            }

            if (mostPlaceOccurences > 0) {
                Place mostPopularPlace = places.get(mostPlaceOccurencesIndex);
                out += "The most frequently mentioned place is";
                if (mostPopularPlace.getType() != Place.Type.UNSPECIFIC) {
                    out += " a " + mostPopularPlace.getType().toString().toLowerCase();
                }
                out += " called " + mostPopularPlace.getPrimaryName() + ".\n";
            }

            int maxCauseOfDeathOccurences = 0;
            int maxCauseOfDeathOccurencesIndex = 0;
            for (int i = 0; i < causesOfDeath.size(); i++) {
                if (causesOfDeath.get(i).getOccurences() > maxCauseOfDeathOccurences) {
                    maxCauseOfDeathOccurences = causesOfDeath.get(i).getOccurences();
                    maxCauseOfDeathOccurencesIndex = i;
                }
            }
            CauseOfDeath mostCommonCauseOfDeath = causesOfDeath.get(maxCauseOfDeathOccurencesIndex);
            out += "The most common cause of death is " + mostCommonCauseOfDeath.primaryName + ".\n";

            int lowestSentiment = 0;
            int lowestSentimentChapterIndex = -1;
            int highestSentiment = 0;
            int highestSentimentChapterIndex = -1;
            int sentimentSum = 0;
            for (int i = 0; i < chapters.size(); i++) {
                int currentSentiment = chapters.get(i).getSentiment();
                if (currentSentiment > highestSentiment) {
                    highestSentiment = currentSentiment;
                    highestSentimentChapterIndex = i;
                }
                if (currentSentiment < lowestSentiment) {
                    lowestSentiment = currentSentiment;
                    lowestSentimentChapterIndex = i;
                }
                sentimentSum += currentSentiment;

            }
            double averageSentiment = sentimentSum / chapterBookmark;

            if (highestSentimentChapterIndex > -1) {
                out += chapters.get(highestSentimentChapterIndex).getTitle()
                        + " has the most positive sentiment with " + highestSentiment + " .\n";
            }

            if (lowestSentimentChapterIndex > -1) {
                out += chapters.get(lowestSentimentChapterIndex).getTitle()
                        + " has the most negative sentiment with " + lowestSentiment + " .\n";
            }
            out += "The average sentiment per chapter is " + averageSentiment + " , it's such a ";

            if (averageSentiment < 0) {
                out += "dark";
            } else {
                out += "nice";
            }
            out += " story.";

            out += "\n\n";
        }
        return out;
    }

}
