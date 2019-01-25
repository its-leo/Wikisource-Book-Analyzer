/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leo.textanalyzer.entities;

import com.leo.textanalyzer.Helper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author HENSEL
 */
public class Character extends AbstractData {

    private HashMap<String, Integer> titles;
    private HashMap<String, Integer> children;
    private HashMap<String, Integer> parents;

    public String cityOfDeath;

    public Character(String name) {
        super(name);

        System.out.println("- New Character named " + name + "!");

        similarityThreshold = 0.78;
    }

    public HashMap<String, Integer> getTitles() {
        return titles;
    }

    public void addTitle(String title) {
        //Some characters are just referenced by their title, so name and title would be identical
        if (!title.equals(primaryName)) {
            if (titles == null) {
                titles = new HashMap<String, Integer>();
            }
            if (!titles.containsKey(title)) {
                titles.put(title, 1);
            } else {
                titles.put(title, titles.get(title) + 1);
            }
            String out = "- " + primaryName + " is " + Helper.getIndefiniteArticle(title) + " " + title + "!";
            System.out.println(out);
        }
    }

    public ArrayList<String> getChildren() {
        ArrayList filteredChildren = new ArrayList<String>();
        if (children != null) {
            for (Map.Entry<String, Integer> child : children.entrySet()) {
                if (child.getValue() > 1) {
                    filteredChildren.add(child.getKey());
                }
            }
        }
        return filteredChildren;
    }

    public void addChildren(String childrenName) {
        if (children == null) {
            children = new HashMap<String, Integer>();
        }
        if (!children.containsKey(childrenName)) {
            children.put(childrenName, 1);
        } else {
            children.put(childrenName, children.get(childrenName) + 1);
        }
        System.out.println("- " + childrenName + " is " + primaryName + "'s child");
    }

    public ArrayList<String> getParents() {
        ArrayList filteredParents = new ArrayList<String>();
        if (parents != null) {
            for (Map.Entry<String, Integer> parent : parents.entrySet()) {
                if (parent.getValue() > 1) {
                    filteredParents.add(parent.getKey());
                }
            }
        }
        return filteredParents;
    }

    public void addParents(String parentsName) {
        if (parents == null) {
            parents = new HashMap<String, Integer>();
        }
        if (!parents.containsKey(parentsName)) {
            parents.put(parentsName, 1);
        } else {
            parents.put(parentsName, parents.get(parentsName) + 1);
        }
        System.out.println("- " + parentsName + " is " + primaryName + "'s parent");
    }

    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public void setCityOfDeath(String cityOfDeath) {
        if (this.cityOfDeath == null) {
            this.cityOfDeath = cityOfDeath;
            System.out.println("- " + primaryName + " tragically died in " + cityOfDeath);
        }
    }

    public String getPrimaryTitle() {
        int maxOccurences = 0;
        String primaryTitle = null;
        for (Map.Entry<String, Integer> title : titles.entrySet()) {
            if (title.getValue() > maxOccurences) {
                maxOccurences = title.getValue();
                primaryTitle = title.getKey();
            }
        }
        return primaryTitle;
    }

    @Override
    public String toString() {
        String out = super.toString();
        if (titles != null) {
            out += " , Titles: " + titles.toString();
        }
        if (cityOfDeath != null) {
            out += " , City of Death: " + cityOfDeath;
        }
        if (parents != null) {
            out += " , Parents: " + parents.toString();
        }
        if (children != null) {
            out += " , Children: " + children.toString();
        }
        return out;
    }

}
