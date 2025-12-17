package com.mycompany.mavenproject3;

import java.io.FileInputStream;
import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application {

    private XMLtree tree;
    private TextArea output;
    private TextField inputTag;

    @Override
    public void start(Stage stage) {

        Button loadBtn = new Button("Cargar XML");
        Button searchBtn = new Button("Buscar etiqueta");
        Button preordenBtn = new Button("Recorrido Preorden");
        Button estructuraBtn = new Button("Mostrar estructura");

        inputTag = new TextField();
        inputTag.setPromptText("Nombre de etiqueta");

        output = new TextArea();
        output.setEditable(false);

        loadBtn.setOnAction(e -> cargarXML());
        searchBtn.setOnAction(e -> buscarEtiqueta());
        preordenBtn.setOnAction(e -> mostrarPreorden());
        estructuraBtn.setOnAction(e -> mostrarEstructura());

        VBox root = new VBox(10,
                loadBtn,
                inputTag,
                searchBtn,
                preordenBtn,
                estructuraBtn,
                output
        );

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Parser XML - JavaFX");
        stage.show();
    }

    // ------------------ LÓGICA ------------------

    private void cargarXML() {
        try (InputStream is = new FileInputStream("sample.xml")) {
            if (is == null) {
                throw new RuntimeException("No se encontró sample.xml.");
            }

            String xml = new String(is.readAllBytes());
            XMLparser parser = new XMLparser();
            tree = parser.parse(xml);

            output.setText("XML cargado correctamente.");

        } catch (Exception e) {
            output.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarPreorden() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML");
            return;
        }

        StringBuilder sb = new StringBuilder();
        tree.preorden(tree.getRoot(), sb);
        output.setText(sb.toString());
    }

    private void mostrarEstructura() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML");
            return;
        }

        output.setText(tree.getEstructura());
    }

    private void buscarEtiqueta() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML");
            return;
        }

        String tag = inputTag.getText().trim();
        if (tag.isEmpty()) {
            output.setText("Ingrese una etiqueta");
            return;
        }

        StringBuilder sb = new StringBuilder();
        buscarRec(tree.getRoot(), tag, sb);

        if (sb.length() == 0) {
            output.setText("No se encontraron etiquetas <" + tag + ">");
        } else {
            output.setText(sb.toString());
        }
    }

    private void buscarRec(XMLnode node, String tag, StringBuilder sb) {
        if (node == null) return;

        if (node.getTag().equals(tag)) {
            sb.append("Encontrado: <").append(node.getTag()).append(">\n");

            if (!node.getAttributes().isEmpty()) {
                sb.append("Atributos:\n");
                node.getAttributes().forEach(
                        (k, v) -> sb.append("  ").append(k).append(" = ").append(v).append("\n")
                );
            }
            sb.append("\n");
        }

        for (XMLnode child : node.getChildren()) {
            buscarRec(child, tag, sb);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


