/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.bookanalyzer;

/**
 *
 * @author HENSEL
 */
public class Main {

    final static boolean DEBUG = true;
    final static String url = "https://en.wikisource.org/wiki/War_and_Peace/Book_One";

    public static void main(String[] args) {

        NLP.init();

        Book book = new Book(url);

        book.fetch();

        book.analyze();
        
        System.out.println(book.toString());
    }
}