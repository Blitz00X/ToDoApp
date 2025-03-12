package src;

import java.util.ArrayList;
import java.util.List;

public class TaskNode {
    private String name;         // Example: "Morning_wakeup"
    private String displayName;  // Example: "Morning wakeup" (Displayed in UI)
    private String day;          // 'R' or '1'..'7'
    private int indentLevel;     // Indentation level
    private List<TaskNode> children = new ArrayList<>();
    private TaskNode parent;

    public TaskNode(String name, String day, int indentLevel) {
        this.name = name;
        this.day = day;
        this.indentLevel = indentLevel;
        this.displayName = name;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDay() {
        return day;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public List<TaskNode> getChildren() {
        return children;
    }

    public TaskNode getParent() {
        return parent;
    }

    public void setParent(TaskNode parent) {
        this.parent = parent;
    }

    public void addChild(TaskNode child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Uses the original name (e.g., "Morning_wakeup") when writing to a file.
     */
    @Override
    public String toString() {
        return name + ":" + day + ":x";
    }
}
