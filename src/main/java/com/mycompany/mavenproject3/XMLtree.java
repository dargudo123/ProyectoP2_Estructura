/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;


public class XMLtree {

    private XMLnode root;

    public XMLtree() {
        root = null;
    }

    public XMLnode getRoot() {
        return root;
    }

    public void setRoot(XMLnode root) {
        this.root = root;
    }

    public boolean isEmpty() {
        return root == null;
    }
    
    public void preorden(XMLnode nodo, StringBuilder sb) {
        if (nodo == null) return;

        sb.append("<").append(nodo.getTag()).append(">\n");

        for (XMLnode hijo : nodo.getChildren()) {
            preorden(hijo, sb);
        }
    }
    
    private void mostrarEstructura(XMLnode nodo, StringBuilder sb, String prefix, boolean esUltimo) {
        if (nodo == null) return;

        sb.append(prefix);

        if (esUltimo) {
            sb.append("└─ ");
            prefix += "   ";
        } else {
            sb.append("├─ ");
            prefix += "│  ";
        }

        sb.append(nodo.getTag()).append("\n");

        var hijos = nodo.getChildren();
        for (int i = 0; i < hijos.size(); i++) {
            mostrarEstructura(
                    hijos.get(i),
                    sb,
                    prefix,
                    i == hijos.size() - 1
            );
        }
    }

    
    public String getEstructura() {
        if (root == null) return "Árbol vacío";

        StringBuilder sb = new StringBuilder();
        sb.append(root.getTag()).append("\n");

        var hijos = root.getChildren();
        for (int i = 0; i < hijos.size(); i++) {
            mostrarEstructura(hijos.get(i), sb, "", i == hijos.size() - 1);
        }
        return sb.toString();
    }

}



