/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.textanalyzer.entities;

/**
 *
 * @author HENSEL
 */
public class Place extends AbstractData {

    public enum Type {
        UNSPECIFIC,
        COUNTRY,
        CITY
    }

    public Type type;

    public Place(String name, Type type) {
        super(name);
        this.type = type;
        String out = "- ";
        if (type != type.UNSPECIFIC) {
            out += "The " + type.toString().toLowerCase() + " ";
        }
        out += primaryName + " is a new place of action!";
        System.out.println(out);
    }

    public Type getType() {
        return type;
    }
}
