/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author danie
 */

public class XMLnode {

    private String tag;
    private String text;

    private Map<String, String> attributes;
    private List<XMLnode> children;
    private XMLnode parent;

    public XMLnode(String tag) {
        this.tag = tag;
        this.text = "";
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public String getTag() { return tag; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public void addAttribute(String k, String v) {
        attributes.put(k, v);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void addChild(XMLnode child) {
        child.parent = this;
        children.add(child);
    }

    public List<XMLnode> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}

