package com.mycompany.mavenproject3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

public class App extends Application {

    private XMLtree tree;
    private TextArea output;
    private TextField inputTag;
    private TreeView<String> treeView;
    private TextField newNodeTag, newNodeText;

    @Override
    public void start(Stage stage) {

        Button loadBtn = new Button("Cargar XML");
        Button searchBtn = new Button("Buscar etiqueta");
        Button preordenBtn = new Button("Recorrido Preorden");
        Button estructuraBtn = new Button("Mostrar estructura");
        Button addNodeBtn = new Button("Agregar nodo");
        Button saveBtn = new Button("Guardar XML");

        inputTag = new TextField();
        inputTag.setPromptText("Nombre de etiqueta");

        newNodeTag = new TextField();
        newNodeTag.setPromptText("Etiqueta del nuevo nodo");

        newNodeText = new TextField();
        newNodeText.setPromptText("Texto del nuevo nodo");

        output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        treeView = new TreeView<>();
        treeView.setPrefHeight(200);

        loadBtn.setOnAction(e -> cargarXML());
        searchBtn.setOnAction(e -> buscarEtiqueta());
        preordenBtn.setOnAction(e -> mostrarPreorden());
        estructuraBtn.setOnAction(e -> mostrarEstructura());
        addNodeBtn.setOnAction(e -> agregarNodo());
        saveBtn.setOnAction(e -> guardarXML());

        // ----------------- LAYOUT -----------------
        HBox topControls = new HBox(10, loadBtn, preordenBtn, estructuraBtn, saveBtn);
        topControls.setSpacing(10);

        HBox searchBox = new HBox(10, new Label("Buscar:"), inputTag, searchBtn);
        HBox addNodeBox = new HBox(10, new Label("Agregar nodo:"), newNodeTag, newNodeText, addNodeBtn);

        VBox mainLayout = new VBox(10,
                topControls,
                searchBox,
                addNodeBox,
                new Label("Vista de árbol:"),
                treeView,
                new Label("Salida:"),
                output
        );
        mainLayout.setPadding(new javafx.geometry.Insets(10));

        stage.setScene(new Scene(mainLayout, 700, 600));
        stage.setTitle("XML Parser");
        stage.show();
    }

    // ----------------- LÓGICA -----------------
    private void cargarXML() {
        try (InputStream is = new FileInputStream("sample.xml")) {
            String xml = new String(is.readAllBytes());
            XMLparser parser = new XMLparser();
            tree = parser.parse(xml);

            output.setText("XML cargado correctamente.");
            actualizarTreeView();

        } catch (Exception e) {
            output.setText("Error al cargar XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarPreorden() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        tree.preorden(tree.getRoot(), sb);
        output.setText(sb.toString());
    }

    private void mostrarEstructura() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML.");
            return;
        }
        output.setText(tree.getEstructura());
    }

    private void buscarEtiqueta() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML.");
            return;
        }
        String tag = inputTag.getText().trim();
        if (tag.isEmpty()) {
            output.setText("Ingrese una etiqueta.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        buscarRec(tree.getRoot(), tag, sb);
        if (sb.length() == 0) {
            output.setText("No se encontraron etiquetas <" + tag + ">.");
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
                node.getAttributes().forEach((k, v) -> sb.append("  ").append(k).append(" = ").append(v).append("\n"));
            }
            sb.append("\n");
        }
        for (XMLnode child : node.getChildren()) {
            buscarRec(child, tag, sb);
        }
    }

    private void agregarNodo() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML.");
            return;
        }
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            output.setText("Seleccione un nodo en la vista de árbol para agregar un hijo.");
            return;
        }

        String tag = newNodeTag.getText().trim();
        String text = newNodeText.getText().trim();
        if (tag.isEmpty()) {
            output.setText("Ingrese la etiqueta del nuevo nodo.");
            return;
        }

        // Buscar nodo en el árbol XMLnode
        XMLnode parentNode = buscarNodoPorRuta(tree.getRoot(), selected);
        if (parentNode == null) {
            output.setText("Error: no se pudo encontrar el nodo seleccionado.");
            return;
        }

        XMLnode nuevo = new XMLnode(tag);
        nuevo.setText(text);
        parentNode.addChild(nuevo);

        output.setText("Nodo agregado correctamente.");
        actualizarTreeView();
    }

    private XMLnode buscarNodoPorRuta(XMLnode current, TreeItem<String> selectedItem) {
        if (current == null) return null;
        if (current.getTag().equals(selectedItem.getValue())) return current;
        for (XMLnode child : current.getChildren()) {
            XMLnode res = buscarNodoPorRuta(child, selectedItem);
            if (res != null) return res;
        }
        return null;
    }

    private void guardarXML() {
        if (tree == null || tree.isEmpty()) {
            output.setText("Primero carga un XML.");
            return;
        }
        try (FileWriter writer = new FileWriter("modificado.xml")) {
            StringBuilder sb = new StringBuilder();
            XMLwriter.write(tree.getRoot(), sb, 0);
            writer.write(sb.toString());
            output.setText("XML guardado en modificado.xml correctamente.");
        } catch (Exception e) {
            output.setText("Error al guardar XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarTreeView() {
        if (tree == null || tree.isEmpty()) {
            treeView.setRoot(null);
            return;
        }
        TreeItem<String> rootItem = crearTreeItem(tree.getRoot());
        treeView.setRoot(rootItem);
        treeView.setShowRoot(true);
        rootItem.setExpanded(true);
    }

    private TreeItem<String> crearTreeItem(XMLnode node) {
        String display = node.getTag();
        if (!node.getText().isEmpty()) {
            display += " : " + node.getText();
        }
        TreeItem<String> item = new TreeItem<>(display);
        for (XMLnode child : node.getChildren()) {
            item.getChildren().add(crearTreeItem(child));
        }
        return item;
    }

    public static void main(String[] args) {
        launch(args);
    }
}



