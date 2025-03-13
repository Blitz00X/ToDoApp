package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * DaySelectorApp is a JavaFX application that allows users to select a day
 * (1..7) and view tasks for that day in a hierarchical TreeView.
 * There's also a toggle button + a Ctrl+S shortcut that toggles the selected task
 * (:x <-> :a) for whichever day is currently displayed.
 */
public class DaySelectorApp extends Application {

    // day -> that day's TreeView
    private Map<Integer, TreeView<TaskNode>> dayTreeViews = new HashMap<>();

    public static void main(String[] args) {
        // 1) Parse tasks.txt into rootTasks, write them to 1..7.txt
        try {
            List<TaskNode> rootTasks = TasksParser.parseTasks("tasks.txt");
            Actions.rootTasks = rootTasks; // so we can do toggling and rewriting
            TaskFileWriter.writeTasksByDay(rootTasks);
            System.out.println("tasks.txt converted to 1..7.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2) JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // A combo to pick day=1..7
        ComboBox<Integer> dayCombo = new ComboBox<>();
        for (int i = 1; i <= 7; i++) {
            dayCombo.getItems().add(i);
        }
        dayCombo.setValue(1); // default day = 1

        BorderPane root = new BorderPane();
        root.setTop(dayCombo);

        // Build TreeView<TaskNode> for each day
        for (int d = 1; d <= 7; d++) {
            TreeView<TaskNode> tv = buildTreeViewFromFile(d + ".txt");
            dayTreeViews.put(d, tv);
        }

        // By default, show day=1 in center
        root.setCenter(dayTreeViews.get(1));

        // "Toggle Done" button
        Button toggleButton = new Button("Toggle");
        toggleButton.setOnAction(e -> {
            // get selected day from combo
            int selectedDay = dayCombo.getValue();
            TreeView<TaskNode> currentTree = dayTreeViews.get(selectedDay);
            Actions.ToggleTask(currentTree);
        });
        root.setBottom(toggleButton);

        // Build scene
        Scene scene = new Scene(root, 600, 400);

        // 1) Initially, bind Ctrl+S to day=1's tree
        scene.getAccelerators().clear();
        Actions.registerShortcuts(scene, dayTreeViews.get(1));

        // 2) If user picks a new day from the combo, re-bind the kısayol
        dayCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            // show that day's tree
            root.setCenter(dayTreeViews.get(newVal));

            // re-register the Ctrl+S => toggle for this new day's tree
            scene.getAccelerators().clear();
            Actions.registerShortcuts(scene, dayTreeViews.get(newVal));
        });

        primaryStage.setTitle("Daily Tasks with per-day Shortcut");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Build a TreeView<TaskNode> from a given day file (e.g. "1.txt").
     * This sets a cellFactory to show "(Done)" if node.isCompleted() is true.
     */
    private TreeView<TaskNode> buildTreeViewFromFile(String fileName) {
        // parse the day file -> root tasks
        List<TaskNode> rootTasks = parseDayFile(fileName);

        // invisible root
        TreeItem<TaskNode> invisibleRoot = new TreeItem<>(null);
        invisibleRoot.setExpanded(true);

        // add each root TaskNode
        for (TaskNode node : rootTasks) {
            invisibleRoot.getChildren().add(createTreeItem(node));
        }

        // make a TreeView with that root
        TreeView<TaskNode> treeView = new TreeView<>(invisibleRoot);
        treeView.setShowRoot(false);

        // custom cell factory to show "✅" if node.isCompleted()
        treeView.setCellFactory(tv -> new TreeCell<TaskNode>() {
            @Override
            protected void updateItem(TaskNode node, boolean empty) {
                super.updateItem(node, empty);
                if (empty || node == null) {
                    setText(null);
                } else {
                    String text = node.getDisplayName();
                    if (node.isCompleted()) {
                        text = "✅" + text ;
                    }
                    setText(text);
                }
                
            }
            
        });

        return treeView;
    }

    /**
     * Parse e.g. "1.txt" into a list of root TaskNodes.
     */
    private List<TaskNode> parseDayFile(String fileName) {
        List<TaskNode> dayRootNodes = new ArrayList<>();
        try {
            List<String> lines = readAllLines(fileName);
            List<TaskNode> allNodes = TaskReader.parseLines(lines);

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
     * Recursively build TreeItem<TaskNode>
     */
    private TreeItem<TaskNode> createTreeItem(TaskNode node) {
        TreeItem<TaskNode> item = new TreeItem<>(node);
        for (TaskNode child : node.getChildren()) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }

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
