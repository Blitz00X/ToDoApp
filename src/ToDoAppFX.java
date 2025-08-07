package src;
// Import necessary JavaFX and IO classes
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
import java.util.ArrayList;
import java.util.List;

public class ToDoAppFX extends Application {
    // UI components and constants
    private TreeView<String> taskTree;
    private TreeItem<String> root;
    private TextField taskField;
    private ComboBox<String> listSelector;
    private Button newListButton;
    private Button addButton;
    private Button deleteButton;
    private Button completeButton;
    private static final String FILE_EXTENSION = ".txt";
    private String currentList = "default"; // Default list
    private static final String LISTS_FILE = "lists.txt"; // File to store lists

    // Special views and day labels
    private static final String WEEK_VIEW = "week";
    private static final String[] DAYS_OF_WEEK = {
            "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"
    };

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Doer");

        // Root item for the tree
        root = new TreeItem<>("Tasks");
        root.setExpanded(true);
        taskTree = new TreeView<>(root);
        taskTree.setShowRoot(true);

        // Prevent `TreeView` from gaining focus
        taskTree.setFocusTraversable(false);

        // Input field and buttons
        taskField = new TextField();
        addButton = new Button("Add Task");
        deleteButton = new Button("Remove");
        completeButton = new Button("Toggle");

        // Set button actions
        addButton.setOnAction(e -> addTask());
        deleteButton.setOnAction(e -> deleteTask());
        completeButton.setOnAction(e -> completeTask());

        // ComboBox for list selection
        listSelector = new ComboBox<>();
        listSelector.setPromptText("Select List");
        loadLists(); // Load existing lists
        listSelector.setOnAction(e -> {
            String selectedList = listSelector.getValue();
            if (selectedList != null) {
                switchList(selectedList);
                disableTaskActions(false);
            }
        });

        // Button to create a new list
        newListButton = new Button("New List");
        newListButton.setOnAction(e -> createNewList());

        // Layout setup
        HBox inputPanel = new HBox(10, addButton, deleteButton, completeButton);
        HBox topPanel = new HBox(10, listSelector, newListButton, taskField);
        BorderPane layout = new BorderPane();
        layout.setCenter(taskTree);
        layout.setTop(topPanel);
        layout.setBottom(inputPanel);

        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Disable list selection and task actions if no lists exist
        if (listSelector.getItems().isEmpty()) {
            listSelector.setDisable(true);
            disableTaskActions(true);
            
        } else {
            // Disable task actions if no list is selected
            disableTaskActions(true);
        }

        // Refocus on taskField if it loses focus
        scene.setOnMouseClicked(event -> {
            if (!taskField.isFocused()) {
                Platform.runLater(taskField::requestFocus);
            }
        });

        // Setup keyboard shortcuts
        setupKeyboardShortcuts(scene);
    }

    // Enable or disable task-related actions
    private void disableTaskActions(boolean disable) {
        taskField.setDisable(disable);
        taskTree.setDisable(disable);
        addButton.setDisable(disable);
        deleteButton.setDisable(disable);
        completeButton.setDisable(disable);
    }

    // Create a new list
    private void createNewList() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New List");
        dialog.setHeaderText("Enter List Name:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty() && !listSelector.getItems().contains(name)) {
                listSelector.getItems().add(name);
                saveLists();
                listSelector.getSelectionModel().select(name); // Select the new list
                switchList(name);

                // Enable task actions if a list is created for the first time
                disableTaskActions(false);
                listSelector.setDisable(false);

                Platform.runLater(taskField::requestFocus);
            }
        });
    }

    // Switch to a different list
    private void switchList(String listName) {
        if (listName == null || listName.isEmpty()) return;
        currentList = listName;
        root.getChildren().clear(); // Clear previous list

        if (WEEK_VIEW.equalsIgnoreCase(listName)) {
            showWeekView();
        } else {
            loadTasksFromFile(); // Load new list
        }
    }

    // Display all tasks grouped by day in the week view
    private void showWeekView() {
        for (String day : DAYS_OF_WEEK) {
            TreeItem<String> dayItem = new TreeItem<>(day);
            dayItem.setExpanded(true);
            root.getChildren().add(dayItem);
            loadTasksFromFile(dayItem, day);
        }
    }

    // Load lists from file
    private void loadLists() {
        File file = new File(LISTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                listSelector.getItems().add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save lists to file
    private void saveLists() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LISTS_FILE))) {
            for (String list : listSelector.getItems()) {
                writer.write(list);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a new task
    private void addTask() {
        String task = taskField.getText().trim();
        if (!task.isEmpty()) {
            TreeItem<String> selectedItem = taskTree.getSelectionModel().getSelectedItem();
            TreeItem<String> newItem = new TreeItem<>(task);

            if (selectedItem != null && selectedItem != root) {
                // If the selected item is completed, mark the new task as completed
                if (selectedItem.getValue().startsWith("✅")) {
                    newItem.setValue("✅ " + task);
                }
                selectedItem.getChildren().add(newItem);
                selectedItem.setExpanded(true);
            } else {
                root.getChildren().add(newItem);
            }

            taskField.clear();
            saveTasksToFile();
        }
    }

    // Delete a selected task
    private void deleteTask() {
        TreeItem<String> selectedItem = taskTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem == root) {
                // If the root item "Tasks" is deleted, delete the list
                deleteCurrentList();
            } else {
                selectedItem.getParent().getChildren().remove(selectedItem);
                saveTasksToFile();
            }
        }
    }

    // Delete the current list
    private void deleteCurrentList() {
        listSelector.getItems().remove(currentList);
        saveLists();
        File file = new File(currentList + FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
        }
        if (!listSelector.getItems().isEmpty()) {
            listSelector.getSelectionModel().selectFirst();
            switchList(listSelector.getValue());
            
            
        } else {
            disableTaskActions(true);
            listSelector.setDisable(true);
        }
        
    }

    // Mark a task and its subtasks as complete or incomplete
    private void completeTask() {
        TreeItem<String> selectedItem = taskTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem != root) {
            toggleTaskCompletion(selectedItem);
            saveTasksToFile();
        }
    }

    // Toggle the completion status of a task and its subtasks
    private void toggleTaskCompletion(TreeItem<String> taskItem) {
        String taskText = taskItem.getValue();
        if (!taskText.startsWith("✅")) {
            taskItem.setValue("✅ " + taskText);
        } else {
            taskItem.setValue(taskText.substring(2));
        }
        for (TreeItem<String> child : taskItem.getChildren()) {
            toggleTaskCompletion(child);
        }
    }

    // Save tasks to file, including nested subtasks
    private void saveTasksToFile() {
        if (WEEK_VIEW.equalsIgnoreCase(currentList)) {
            return; // Week view is a summary and has no backing file
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentList + FILE_EXTENSION))) {
            for (TreeItem<String> child : root.getChildren()) {
                saveTaskHierarchy(writer, child, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Recursively persist a task and its children with indentation to preserve hierarchy
    private void saveTaskHierarchy(BufferedWriter writer, TreeItem<String> item, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            writer.write("\t");
        }
        writer.write(item.getValue());
        writer.newLine();
        for (TreeItem<String> child : item.getChildren()) {
            saveTaskHierarchy(writer, child, depth + 1);
        }
    }

    // Load tasks from file
    private void loadTasksFromFile() {
        loadTasksFromFile(root, currentList);
    }

    // Overloaded loader that populates a parent item with tasks from the given list
    private void loadTasksFromFile(TreeItem<String> parent, String listName) {
        File file = new File(listName + FILE_EXTENSION);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<TreeItem<String>> stack = new ArrayList<>();
            stack.add(parent); // Depth 0
            while ((line = reader.readLine()) != null) {
                int depth = 0;
                while (depth < line.length() && line.charAt(depth) == '\t') {
                    depth++;
                }
                String value = line.substring(depth);
                TreeItem<String> item = new TreeItem<>(value);
                while (stack.size() > depth + 1) {
                    stack.remove(stack.size() - 1);
                }
                stack.get(depth).getChildren().add(item);
                stack.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Setup keyboard shortcuts
    private void setupKeyboardShortcuts(Scene scene) {
        // Add new task: Ctrl + Space
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN).match(event)) {
                addTask();
                event.consume();
            }
        });

        // Delete task: Ctrl + X
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN).match(event)) {
                deleteTask();
                event.consume();
            }
        });

        // Mark task as complete: Ctrl + S
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                completeTask();
                event.consume();
            }
        });

        // Create new list: Ctrl + N
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN).match(event)) {
                createNewList();
                event.consume();
            }
        });

        // Switch between lists: Ctrl + Tab
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN).match(event)) {
                switchToNextList();
                event.consume();
            }
        });
    }

    // Switch to the next list in the ComboBox
    private void switchToNextList() {
        int currentIndex = listSelector.getSelectionModel().getSelectedIndex();
        int nextIndex = (currentIndex + 1) % listSelector.getItems().size(); // Circular switch
        listSelector.getSelectionModel().select(nextIndex);
        switchList(listSelector.getItems().get(nextIndex));
    }

    public static void main(String[] args) {
        launch(args);
    }
}