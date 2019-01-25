/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.bookanalyzer;

import com.leo.bookanalyzer.entities.Place;
import com.leo.bookanalyzer.entities.Character;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author HENSEL
 */
public class NLP {

    static StanfordCoreNLP pipeline;
    static CoreDocument document;
    Properties props = new Properties();

    public static void init() {
        System.out.println("Initializing NLP... ");
        Properties props = new Properties();

        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, coref, kbp, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public static void analyze(Book book) {
        ArrayList<Chapter> chapters = book.getChapters();

        for (int i = 0; i < chapters.size(); i++) {            
            ArrayList<String> paragraphs = chapters.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                String text = paragraphs.get(j);
                if (text != null && text.length() > 0) {
                    //Whole paragraph as document
                    document = new CoreDocument(text);
                    //Splitting and annotating the paragraph
                    pipeline.annotate(document);

                    for (CoreSentence sentence : document.sentences()) {
                        book.addToSentenceCount(1);
                        book.addToTokenCount(sentence.tokens().size());

                        //-----------------------------------
                        //What's the sentiment for sentences of the paragraph
                        int change = 0;
                        switch (sentence.sentiment()) {
                            case "Very negative":
                                change = -2;
                                break;
                            case "Negative":
                                change = -1;
                                break;
                            case "Positive":
                                change = 1;
                                break;
                            case "Very positive":
                                change = 2;
                                break;
                        }
                        chapters.get(i).changeSentimentBy(change);
                        System.out.printf("%-15s|%s%n", sentence.sentiment(), sentence);

                        //-----------------------------------
                        //Are there any characters or places or deaths??
                        List<CoreEntityMention> entityMentions = sentence.entityMentions();
                        for (CoreEntityMention em : entityMentions) {
                            String chapterTitle = chapters.get(i).getTitle();

                            switch (em.entityType()) {
                                case "PERSON":
                                    if (em.tokens().get(0).ner().equals("PERSON")) {
                                        book.addCharacter(em.text());
                                    }
                                    break;
                                case "LOCATION":
                                    book.addPlace(em.text(), Place.Type.UNSPECIFIC);
                                    break;
                                case "COUNTRY":
                                    book.addPlace(em.text(), Place.Type.COUNTRY);
                                    break;
                                case "CITY":
                                    book.addPlace(em.text(), Place.Type.CITY);
                                    break;
                                case "CAUSE_OF_DEATH":
                                    book.addCauseOfDeath(em.text());
                                    break;
                            }
                        }

                        for (RelationTriple rt : sentence.relations()) {
                            Character c = book.getCharacter(rt.subjectLink());
                            if (c != null && rt.objectLink().length() > 3) {
                                switch (rt.relationGloss()) {
                                    case "per:title":
                                        c.addTitle(rt.objectLink());
                                        break;

                                    case "per:city_of_death":
                                        c.setCityOfDeath(rt.objectLink());
                                        break;

                                    case "per:children":
                                        c.addChildren(rt.objectLink());
                                        break;

                                    case "per:parents":
                                        c.addParents(rt.objectLink());
                                        break;
                                }
                            }
                        }
                    }
                }
            }
            book.setChapterBookmark(i);
        }
        book.setChapters(chapters);
    }
}
