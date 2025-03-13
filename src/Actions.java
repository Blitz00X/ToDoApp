package src;

import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;
import java.util.List;


public class Actions {
    public static List<TaskNode> rootTasks;
    


    public static void ToggleTask(TreeView<TaskNode> treeView){
        TreeItem<TaskNode> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            TaskNode selectedTask = selectedItem.getValue();
            selectedTask.setCompleted(!selectedTask.isCompleted());
            for(TreeItem<TaskNode> item : selectedItem.getChildren()){
                item.getValue().setCompleted(selectedTask.isCompleted());
            }

            try{
                TaskFileWriter.writeTasksByDay(rootTasks);
            }catch(IOException e){
                e.printStackTrace();
            }
            treeView.refresh();
        }

    }
    public static void registerShortcuts(Scene scene, TreeView<TaskNode> treeView){
        KeyCodeCombination comboSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(comboSave, () -> {
            ToggleTask(treeView);
        });
    }



}
