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
    private TreeView<XMLnode> treeView;
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

        HBox topControls = new HBox(10, loadBtn, preordenBtn, estructuraBtn, saveBtn);
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
        
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(XMLnode node, boolean empty) {
                super.updateItem(node, empty);

                if (empty || node == null) {
                    setText(null);
                } else {
                    if (node.getText().isEmpty()) {
                        setText(node.getTag());
                    } else {
                        setText(node.getTag() + " : " + node.getText());
                    }
                }
            }
        });
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
        output.setText(sb.length() == 0
                ? "No se encontraron etiquetas <" + tag + ">."
                : sb.toString());
    }

    private void buscarRec(XMLnode node, String tag, StringBuilder sb) {
        if (node == null) return;
        if (node.getTag().equals(tag)) {
            sb.append("<").append(node.getTag()).append(">\n");
            if (!node.getText().isEmpty()) {
                sb.append("  Texto: ").append(node.getText()).append("\n");
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

        TreeItem<XMLnode> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            output.setText("Seleccione un nodo en la vista de árbol.");
            return;
        }

        String tag = newNodeTag.getText().trim();
        String text = newNodeText.getText().trim();
        if (tag.isEmpty()) {
            output.setText("Ingrese la etiqueta del nuevo nodo.");
            return;
        }

        XMLnode parentNode = selected.getValue();

        XMLnode nuevo = new XMLnode(tag);
        nuevo.setText(text);
        parentNode.addChild(nuevo);

        output.setText("Nodo agregado correctamente.");
        actualizarTreeView();
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
            output.setText("XML guardado correctamente.");
        } catch (Exception e) {
            output.setText("Error al guardar XML: " + e.getMessage());
        }
    }

    private void actualizarTreeView() {
        TreeItem<XMLnode> rootItem = crearTreeItem(tree.getRoot());
        treeView.setRoot(rootItem);
        treeView.setShowRoot(true);
        rootItem.setExpanded(true);
    }

    private TreeItem<XMLnode> crearTreeItem(XMLnode node) {
        TreeItem<XMLnode> item = new TreeItem<>(node);
        item.setExpanded(true);
        for (XMLnode child : node.getChildren()) {
            item.getChildren().add(crearTreeItem(child));
        }
        return item;
    }

    public static void main(String[] args) {
        launch(args);
    }
}




