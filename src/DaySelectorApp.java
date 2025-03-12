package src;

/**
 * DaySelectorApp is a JavaFX application that allows users to select a day
 * and view tasks for that day in a hierarchical TreeView.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DaySelectorApp extends Application {

    private Map<Integer, TreeView<String>> dayTreeViews = new HashMap<>();

    /**
     * Main method:
     * 1) Parses tasks.txt and creates separate files for each day (1..7.txt)
     * 2) Starts the JavaFX UI
     */
    public static void main(String[] args) {
        // 1) Convert tasks.txt to 1..7.txt
        try {
            List<TaskNode> rootTasks = TasksParser.parseTasks("tasks.txt");
            TaskFileWriter.writeTasksByDay(rootTasks);
            System.out.println("tasks.txt converted to 1...7.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2) Launch JavaFX UI
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Dropdown to select the day
        ComboBox<Integer> dayCombo = new ComboBox<>();
        for (int i = 1; i <= 7; i++) {
            dayCombo.getItems().add(i);
        }
        dayCombo.setValue(1);

        BorderPane root = new BorderPane();
        root.setTop(dayCombo);

        // Parse 1..7.txt files and create TreeView for each day
        for (int day = 1; day <= 7; day++) {
            TreeView<String> treeView = buildTreeViewFromFile(day + ".txt");
            dayTreeViews.put(day, treeView);
        }

        // Display corresponding TreeView when day selection changes
        dayCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            root.setCenter(dayTreeViews.get(newVal));
        });
        
        // Default view: Day 1
        root.setCenter(dayTreeViews.get(1));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Daily Tasks");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Reads and parses files (1.txt to 7.txt) and converts them into TreeView.
     */
    private TreeView<String> buildTreeViewFromFile(String fileName) {
        
        List<TaskNode> rootTasks = parseDayFile(fileName);

        TreeItem<String> invisibleRoot = new TreeItem<>("ROOT");
        invisibleRoot.setExpanded(true);

        // Add each root task as a TreeItem
        for (TaskNode node : rootTasks) {
            TreeItem<String> item = createTreeItem(node);
            invisibleRoot.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(invisibleRoot);
        treeView.setShowRoot(false); // Hide "ROOT"
        return treeView;
    }

    /**
     * Reads a file (e.g., 1.txt) and converts it into a hierarchical structure using a stack method.
     */
    private List<TaskNode> parseDayFile(String fileName) {
        List<TaskNode> dayRootNodes = new ArrayList<>();
        try {
            List<String> lines = readAllLines(fileName);

            // Convert individual lines into TaskNode objects
            List<TaskNode> allNodes = TaskReader.parseLines(lines);

            // Use stack to establish parent-child relationships
            Deque<TaskNode> stack = new ArrayDeque<>();
            for (TaskNode current : allNodes) {
                while (!stack.isEmpty() && current.getIndentLevel() <= stack.peek().getIndentLevel()) {
                    stack.pop();
                }
                if (!stack.isEmpty()) {
                    stack.peek().addChild(current);
                } else {
                    dayRootNodes.add(current);
                }
                stack.push(current);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dayRootNodes;
    }

    /**
     * Converts a TaskNode (and its children) into a JavaFX TreeItem.
     * Uses displayName (replacing underscores with spaces) for UI display.
     */
    private TreeItem<String> createTreeItem(TaskNode node) {
        TreeItem<String> item = new TreeItem<>(node.getDisplayName());
        for (TaskNode child : node.getChildren()) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }

    /**
     * Reads all non-empty lines from a file and returns them as a list.
     */
    private List<String> readAllLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }
}
