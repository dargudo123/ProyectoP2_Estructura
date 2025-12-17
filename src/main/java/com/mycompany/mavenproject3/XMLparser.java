/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

import java.util.Stack;

public class XMLparser {

    public XMLtree parse(String xml) {
        xml = xml.replaceAll("<\\?.*?\\?>", "").trim(); // eliminar cabecera XML

        XMLtree tree = new XMLtree();
        Stack<XMLnode> stack = new Stack<>();
        int i = 0;

        while (i < xml.length()) {
            if (Character.isWhitespace(xml.charAt(i))) {
                i++;
                continue;
            }

            if (xml.charAt(i) == '<') {
                // Cierre de etiqueta
                if (i + 1 < xml.length() && xml.charAt(i + 1) == '/') {
                    int end = xml.indexOf('>', i);
                    String closingTag = xml.substring(i + 2, end).trim();

                    if (stack.isEmpty()) {
                        throw new RuntimeException("Cierre inesperado: </" + closingTag + ">");
                    }

                    XMLnode top = stack.pop();
                    if (!top.getTag().equals(closingTag)) {
                        throw new RuntimeException("Etiqueta mal cerrada: se esperaba </" + top.getTag() + "> pero se encontró </" + closingTag + ">");
                    }

                    i = end + 1;
                }

                // Apertura de etiqueta
                else {
                    int end = xml.indexOf('>', i);
                    if (end == -1) break;

                    String rawTag = xml.substring(i + 1, end).trim();
                    String[] parts = rawTag.split("\\s+", 2);
                    String tagName = parts[0];
                    XMLnode node = new XMLnode(tagName);

                    // Procesar atributos si existen
                    if (parts.length > 1) {
                        String attrString = parts[1];
                        String[] attrPairs = attrString.split("\\s+");
                        for (String pair : attrPairs) {
                            if (pair.contains("=")) {
                                String[] kv = pair.split("=");
                                if (kv.length == 2) {
                                    String key = kv[0];
                                    String value = kv[1].replaceAll("\"", "");
                                    node.addAttribute(key, value);
                                }
                            }
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
                    stack.peek().setText(text);
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
