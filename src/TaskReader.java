package src;

import java.util.ArrayList;
import java.util.List;

public class TaskReader {

    /**
     * Parses a line (e.g., "    Math207:1:x") and creates a TaskNode.
     *  - Determines indentation count (counts tabs and spaces, considering 4 spaces = 1 tab).
     *  - Splits the format "task_name:day:x" into TaskNode components.
     *  - Sets displayName by replacing underscores with spaces.
     */
    public static TaskNode parseLine(String line) {
        int indentCount = countLeadingIndent(line);
        String trimmed = line.trim();

        // If the format is "Task_Name:day:x", then parts[0]=Task_Name, parts[1]=day
        String[] parts = trimmed.split(":");
        if (parts.length < 2) {
            return null; // Invalid line
        }
        String name = parts[0];
        String day = parts[1];
        String accomplish = parts[2];
        

        // Create TaskNode
        TaskNode node = new TaskNode(name, day, indentCount);
        // Replace underscores with spaces in displayName
        node.setDisplayName(name.replace('_', ' '));


        if (accomplish.equals("a")){
            node.setCompleted(true);
        }else{node.setCompleted(false);}

        return node;
    }

    /**
     * Reads all lines in a file and converts them into a list of TaskNodes using parseLine.
     */
    public static List<TaskNode> parseLines(List<String> lines) {
        List<TaskNode> nodeList = new ArrayList<>();
        for (String line : lines) {
            TaskNode node = parseLine(line);
            if (node != null) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    /**
     * Calculates the indentation level by counting both tabs and spaces.
     * Assumes 4 spaces = 1 tab.
     */
    private static int countLeadingIndent(String line) {
        int spaceCount = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                spaceCount++;
            } else if (c == '\t') {
                spaceCount += 4; // 1 tab = 4 spaces
            } else {
                break;
            }
        }
        // Convert spaces to indentation level (4 spaces = 1 indent level)
        return spaceCount / 4;
    }
}
