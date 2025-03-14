package src;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskReader parses each line of a file into a TaskNode.
 * It calculates the indentation level (4 spaces = 1 indent), splits the line,
 * replaces underscores with spaces in the display name, and sets the completed status.
 */
public class TaskReader {

    /**
     * Parses a single line (e.g., "    Math207:1:x") into a TaskNode.
     *
     * @param line The line to parse.
     * @return A TaskNode representing the task, or null if the line is invalid.
     */
    public static TaskNode parseLine(String line) {
        int indentCount = countLeadingIndent(line);
        String trimmed = line.trim();
        String[] parts = trimmed.split(":");
        if (parts.length < 3) {
            return null; // Invalid line
        }
        String name = parts[0];
        String day = parts[1];
        String accomplish = parts[2];

        // Create TaskNode and set displayName (replace underscores with spaces)
        TaskNode node = new TaskNode(name, day, indentCount);
        node.setDisplayName(name.replace('_', ' '));

        // Set completed status: "a" means done, "x" means not done
        if (accomplish.equals("a")) {
            node.setCompleted(true);
        } else {
            node.setCompleted(false);
        }
        return node;
    }

    /**
     * Parses a list of lines into TaskNodes.
     *
     * @param lines The lines to parse.
     * @return A list of TaskNodes.
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
     * Counts the leading indentation level in a line.
     * 4 spaces are considered as 1 indent level.
     *
     * @param line The line to examine.
     * @return The calculated indent level.
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
        return spaceCount / 4;
    }
}
