/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

public class XMLwriter {

    public static void write(XMLnode node, StringBuilder sb, int indent) {
        String pad = " ".repeat(indent);

        StringBuilder openTag = new StringBuilder("<" + node.getTag());
        for (var entry : node.getAttributes().entrySet()) {
            openTag.append(" ").append(entry.getKey())
                   .append("=\"").append(entry.getValue()).append("\"");
        }
        openTag.append(">");

        sb.append(pad).append(openTag).append("\n");

        if (!node.getText().isEmpty()) {
            sb.append(" ".repeat(indent + 2)).append(node.getText()).append("\n");
        }

        for (XMLnode child : node.getChildren()) {
            write(child, sb, indent + 2);
        }

        sb.append(pad).append("</").append(node.getTag()).append(">\n");
    }
}
