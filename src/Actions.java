package src;

import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;
import java.util.List;

/**
 * The Actions class contains methods for actions such as toggling the completed state
 * of a task and registering keyboard shortcuts.
 */
public class Actions {
    // This static list holds the root tasks parsed from the main data file (tasks.txt)
    public static List<TaskNode> rootTasks;

    /**
     * Toggles the "completed" status of the selected task in the provided TreeView.
     * After toggling, it writes the updated tasks to both the daily files (1.txt to 7.txt)
     * and the main tasks.txt file, then refreshes the TreeView.
     *
     * @param treeView The TreeView containing the tasks.
     */
    public static void toggleTask(TreeView<TaskNode> treeView) {
        // Retrieve the selected task
        TreeItem<TaskNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TaskNode selectedTask = selectedItem.getValue();
            // Toggle the completed state
            selectedTask.setCompleted(!selectedTask.isCompleted());
            System.out.println("Toggled task: " + selectedTask.getDisplayName() + " to " + selectedTask.isCompleted());
            
            // Optionally, propagate to children if desired
            for (TreeItem<TaskNode> childItem : selectedItem.getChildren()) {
                if(childItem.getValue() != null) {
                    childItem.getValue().setCompleted(selectedTask.isCompleted());
                }
            }
            try {
                // Write the updated tasks to daily files (1.txt to 7.txt)
                TaskFileWriter.writeTasksByDay(rootTasks);
                System.out.println("Edited task saved to 1..7.txt");
                // Write the updated tasks to the main tasks.txt file
                TaskFileWriter.writeTasksToMainFile(rootTasks, "tasks.txt");
                System.out.println("Edited task saved to tasks.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Refresh the TreeView to update the UI
            treeView.refresh();
        } else {
            System.out.println("No item selected!");
        }
    }

    /**
     * Registers the Ctrl+S keyboard shortcut on the given Scene.
     * When pressed, it toggles the completed state of the selected task in the specified TreeView.
     *
     * @param scene    The Scene to register the shortcut on.
     * @param treeView The TreeView on which the shortcut acts.
     */
    public static void registerShortcuts(Scene scene, TreeView<TaskNode> treeView) {
        KeyCodeCombination comboSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(comboSave, () -> toggleTask(treeView));
    }
}
