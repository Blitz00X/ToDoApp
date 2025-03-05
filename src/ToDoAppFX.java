package src;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.StringTokenizer;   

public class ToDoAppFX extends Application {
    private TreeView<String> taskTree;
    private TreeItem<String> root;
    private TextField taskField;
    private static final String FILE_NAME = "tasks.txt";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Doer");

        // Root item for the tree
        root = new TreeItem<>("Tasks");
        root.setExpanded(true);
        taskTree = new TreeView<>(root);
        taskTree.setShowRoot(true);

        // Input field and buttons
        taskField = new TextField();
        Button addButton = new Button("Add Task");
        Button deleteButton = new Button("Remove");
        Button completeButton = new Button("Complete");

        addButton.setOnAction(e -> addTask());
        deleteButton.setOnAction(e -> deleteTask());
        completeButton.setOnAction(e -> completeTask());

        // Layout
        HBox inputPanel = new HBox(10, addButton, deleteButton, completeButton);
        BorderPane layout = new BorderPane();
        layout.setCenter(taskTree);
        layout.setTop(taskField);
        layout.setBottom(inputPanel);

        Scene scene = new Scene(layout, 500, 500);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.show();

        // Load tasks from file
        loadTasksFromFile();

        // Kullanıcı bir öğeye tıkladığında eğer `taskField` odaklanmamışsa odaklan
        taskTree.setOnMouseClicked(event -> {
            if (!taskField.isFocused()) {
                Platform.runLater(() -> taskField.requestFocus());
            }
        });

        // Setup keyboard shortcuts
        setupKeyboardShortcuts(scene);
    }


    private void addTask() {
        String task = taskField.getText().trim();
        if (!task.isEmpty()) {
            TreeItem<String> selectedItem = taskTree.getSelectionModel().getSelectedItem();
            TreeItem<String> newItem = new TreeItem<>(task);

            if (selectedItem != null && selectedItem != root) {
                selectedItem.getChildren().add(newItem);
                selectedItem.setExpanded(true);
            } else {
                root.getChildren().add(newItem);
            }

            taskField.clear();
            saveTasksToFile();
        }
    }

    private void deleteTask() {
        TreeItem<String> selectedItem = taskTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem != root) {
            selectedItem.getParent().getChildren().remove(selectedItem);
            saveTasksToFile();
        }
    }

    private void completeTask() {
        TreeItem<String> selectedItem = taskTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem != root) {
            String taskText = selectedItem.getValue();
            if (!taskText.startsWith("✅")) {
                selectedItem.setValue("✅ " + taskText);
                if(selectedItem.getChildren().size() > 0){
                    for(TreeItem<String> child : selectedItem.getChildren()){
                        child.setValue("✅ " + child.getValue());
                    }
                }
                
            }else{
                selectedItem.setValue(taskText.substring(2));
                if(selectedItem.getChildren().size() > 0){
                    for(TreeItem<String> child : selectedItem.getChildren()){
                        child.setValue(child.getValue().substring(2));
                    }
                }
                
            }
            saveTasksToFile();
        }
    }

    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            saveNode(root, writer, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNode(TreeItem<String> node, BufferedWriter writer, int depth) throws IOException {
        if (node != root) {
            for (int i = 0; i < depth; i++) {
                writer.write("\t");
            }
            writer.write(node.getValue());
            writer.newLine();
        }
        for (TreeItem<String> child : node.getChildren()) {
            saveNode(child, writer, depth + 1);
        }
    }

    private void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            TreeItem<String> currentParent = root;
            int previousDepth = 0;

            while ((line = reader.readLine()) != null) {
                int depth = line.indexOf(line.trim());
                line = line.trim();
                TreeItem<String> newItem = new TreeItem<>(line);

                if (depth == previousDepth) {
                    currentParent.getChildren().add(newItem);
                } else if (depth > previousDepth) {
                    if (!currentParent.getChildren().isEmpty()) {
                        currentParent = currentParent.getChildren().get(currentParent.getChildren().size() - 1);
                    }
                    currentParent.getChildren().add(newItem);
                } else {
                    while (previousDepth > depth) {
                        currentParent = currentParent.getParent();
                        previousDepth--;
                    }
                    currentParent.getChildren().add(newItem);
                }
                previousDepth = depth;
            }
        } catch (IOException e) {
            System.out.println("Önceki görevler bulunamadı.");
        }
    }

    private void setupKeyboardShortcuts(Scene scene) {
        // Add Task: Ctrl + Space
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN).match(event)) {
                addTask();
                event.consume();
            }
        });

        // Delete Task: Ctrl + X
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN).match(event)) {
                deleteTask();
                event.consume();
            }
        });

        // Complete Task: Ctrl + S
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                completeTask();
                event.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}