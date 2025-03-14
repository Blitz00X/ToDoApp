package src;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskNode represents a single task.
 * It stores the original name (with underscores), a display name for the UI,
 * the day value ("R" or "1".."7"), the indentation level (for hierarchy),
 * its child tasks, and its completed state.
 * The toString() method returns the string for file writing, appending ":a" if completed and ":x" if not.
 */
public class TaskNode {
    private String name;         // Original name (e.g., "Morning_wakeup")
    private String displayName;  // Display name for UI (e.g., "Morning wakeup")
    private String day;          // Day value: 'R' or "1".."7"
    private int indentLevel;     // Indentation level used for hierarchy
    private List<TaskNode> children = new ArrayList<>();
    private TaskNode parent;
    private boolean isCompleted = false; // Completed state: false = not done, true = done

    public TaskNode(String name, String day, int indentLevel) {
        this.name = name;
        this.day = day;
        this.indentLevel = indentLevel;
        // Initially, displayName is the same as name. We later replace underscores with spaces.
        this.displayName = name;
    }

    // Getters and setters
    public String getName() { return name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDay() { return day; }

    public int getIndentLevel() { return indentLevel; }

    public List<TaskNode> getChildren() { return children; }

    public TaskNode getParent() { return parent; }
    public void setParent(TaskNode parent) { this.parent = parent; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    /**
     * Adds a child to this task and sets the child's parent.
     */
    public void addChild(TaskNode child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Returns the string used for file writing.
     * It appends ":a" if the task is completed, or ":x" if not.
     */
    @Override
    public String toString() {
        String isCompletedStr = isCompleted ? ":a" : ":x";
        // Debug print: this helps verify the state during file writing.
        System.out.println(name + isCompletedStr);
        return name + ":" + day + isCompletedStr;
    }
}
