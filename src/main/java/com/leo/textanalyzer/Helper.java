/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.textanalyzer;

/**
 *
 * @author HENSEL
 */
public class Helper {

    public static String getIndefiniteArticle(String word) {
        String article;
        if (word.matches("^[aeiouAEIOU]\\w+")) {
            article = "an";
        } else {
            article = "a";
        }
        return article;
    }
}
