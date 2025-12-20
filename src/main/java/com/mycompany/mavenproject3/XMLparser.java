/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLparser {

    public XMLtree parse(String xml) {
        xml = xml.replaceAll("<\\?.*?\\?>", "").trim(); 

        XMLtree tree = new XMLtree();
        Stack<XMLnode> stack = new Stack<>();
        int i = 0;

        while (i < xml.length()) {
            if (Character.isWhitespace(xml.charAt(i))) {
                i++;
                continue;
            }

            if (xml.charAt(i) == '<') {
                if (i + 1 < xml.length() && xml.charAt(i + 1) == '/') {
                    int end = xml.indexOf('>', i);
                    String closingTag = xml.substring(i + 2, end).trim();

                    if (stack.isEmpty()) {
                        throw new RuntimeException("Cierre incorrecto: </" + closingTag + ">");
                    }

                    XMLnode top = stack.pop();
                    if (!top.getTag().equals(closingTag)) {
                        throw new RuntimeException("Etiqueta mal cerrada.");
                    }

                    i = end + 1;
                }

                else {
                    int end = xml.indexOf('>', i);
                    if (end == -1) break;

                    String rawTag = xml.substring(i + 1, end).trim();
                    String[] parts = rawTag.split("\\s+", 2);
                    String tagName = parts[0];
                    XMLnode node = new XMLnode(tagName);

                    if (parts.length > 1) {
                        String attrString = parts[1];
                        Pattern attrPattern = Pattern.compile("(\\w+)\\s*=\\s*\"([^\"]*)\"");
                        Matcher matcher = attrPattern.matcher(attrString);
                        while (matcher.find()) {
                            String key = matcher.group(1);
                            String value = matcher.group(2);
                            node.addAttribute(key, value);
                        }
                    }

                    if (stack.isEmpty()) {
                        tree.setRoot(node);
                    } else {
                        stack.peek().addChild(node);
                    }

                    stack.push(node);
                    i = end + 1;
                }
            }

            else {
                int end = xml.indexOf('<', i);
                if (end == -1) end = xml.length();

                String text = xml.substring(i, end).trim();
                if (!text.isEmpty() && !stack.isEmpty()) {
                    XMLnode current = stack.peek();
                    if (!current.getText().isEmpty()) {
                        current.setText(current.getText() + " " + text);
                    } else {
                        current.setText(text);
                    }
                }

                i = end;
            }
        }

        if (!stack.isEmpty()) {
            throw new RuntimeException("Documento incompleto. Etiquetas abiertas sin cerrar.");
        }

        return tree;
    }
}

