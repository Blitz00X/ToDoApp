package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * DaySelectorApp is a JavaFX application that allows the user to select a day (1..7)
 * and view tasks for that day in a hierarchical TreeView.
 * The app uses a single data source (loaded from tasks.txt) and builds the UI by filtering tasks by day.
 * A Toggle button and Ctrl+S shortcut toggle the selected task's completed status,
 * and updates are saved back to tasks.txt and the daily files.
 */
public class App extends Application {

    // Map: day number -> TreeView for that day, built from the same data source
    private Map<Integer, TreeView<TaskNode>> dayTreeViews = new HashMap<>();

    public static void main(String[] args) {
        try {
            // Parse tasks.txt to build the task model
            List<TaskNode> rootTasks = TasksParser.parseTasks("tasks.txt");
            // Set the main data source in Actions
            Actions.rootTasks = rootTasks;
            // Write tasks to daily files (1.txt to 7.txt)
            TaskFileWriter.writeTasksByDay(rootTasks);
            System.out.println("tasks.txt converted to 1..7.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Launch the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a ComboBox for day selection (1 to 7)
        ComboBox<Integer> dayCombo = new ComboBox<>();
        for (int i = 1; i <= 7; i++) {
            dayCombo.getItems().add(i);
        }
        dayCombo.setValue(1); // Default day is 1

        BorderPane root = new BorderPane();
        root.setTop(dayCombo);

        // Build TreeViews for each day by filtering the main data source (Actions.rootTasks)
        for (int d = 1; d <= 7; d++) {
            TreeView<TaskNode> tv = buildTreeViewForDay(d);
            dayTreeViews.put(d, tv);
        }

        // Show the TreeView for day 1 by default
        root.setCenter(dayTreeViews.get(1));

        // Create a Toggle button at the bottom that toggles the completed state of the selected task
        Button toggleButton = new Button("Toggle");
        toggleButton.setOnAction(e -> {
            int selectedDay = dayCombo.getValue();
            TreeView<TaskNode> currentTree = dayTreeViews.get(selectedDay);
            Actions.toggleTask(currentTree);
        });
        root.setBottom(toggleButton);

        Scene scene = new Scene(root, 600, 400);

        // Register the Ctrl+S shortcut for the currently selected day's TreeView (initially day 1)
        scene.getAccelerators().clear();
        Actions.registerShortcuts(scene, dayTreeViews.get(1));

        // When the selected day changes, update the center and rebind the shortcut to the new day's TreeView
        dayCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            root.setCenter(dayTreeViews.get(newVal));
            scene.getAccelerators().clear();
            Actions.registerShortcuts(scene, dayTreeViews.get(newVal));
        });

        primaryStage.setTitle("Daily Tasks with Persistent Toggle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Builds a TreeView for a given day by filtering the main data source (Actions.rootTasks).
     * A task is included if its day is "R" (routine) or equals the given day.
     *
     * @param day The day number.
     * @return A TreeView containing the filtered task hierarchy.
     */
    private TreeView<TaskNode> buildTreeViewForDay(int day) {
        // Filter the root tasks from the main data source
        List<TaskNode> filteredRoots = new ArrayList<>();
        for (TaskNode task : Actions.rootTasks) {
            if (task.getDay().equalsIgnoreCase("R") || task.getDay().equals(String.valueOf(day))) {
                filteredRoots.add(task);
            }
        }

        // Create an invisible root for the TreeView
        TreeItem<TaskNode> invisibleRoot = new TreeItem<>(null);
        invisibleRoot.setExpanded(true);
        // For each filtered task, add a TreeItem (recursively filtering children)
        for (TaskNode node : filteredRoots) {
            invisibleRoot.getChildren().add(createTreeItem(node, day));
        }

        TreeView<TaskNode> treeView = new TreeView<>(invisibleRoot);
        treeView.setShowRoot(false);

        // Set a custom cell factory to display the task's displayName and show " (Done)" if completed.
        treeView.setCellFactory(tv -> new TreeCell<TaskNode>() {
            @Override
            protected void updateItem(TaskNode node, boolean empty) {
                super.updateItem(node, empty);
                if (empty || node == null) {
                    setText(null);
                } else {
                    String text = node.getDisplayName();
                    if (node.isCompleted()) {
                        text += " (Done)";
                    }
                    setText(text);
                }
            }
        });
        return treeView;
    }

    /**
     * Recursively creates a TreeItem for a TaskNode, filtering children by the given day.
     *
     * @param node The TaskNode.
     * @param day  The day number.
     * @return A TreeItem containing the task and its filtered children.
     */
    private TreeItem<TaskNode> createTreeItem(TaskNode node, int day) {
        TreeItem<TaskNode> item = new TreeItem<>(node);
        for (TaskNode child : node.getChildren()) {
            // Include a child if its day is "R" or equals the given day.
            if (child.getDay().equalsIgnoreCase("R") || child.getDay().equals(String.valueOf(day))) {
                item.getChildren().add(createTreeItem(child, day));
            }
        }
        return item;
    }
}
