package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TasksParser reads the main tasks file (tasks.txt) and converts it into a hierarchy of TaskNodes.
 * It uses TaskReader to parse each line and then uses a stack-based approach to establish parent-child relationships.
 */
public class TasksParser {

    /**
     * Parses the given file into a hierarchical list of TaskNodes.
     *
     * @param filePath The path to the tasks file.
     * @return A list of root TaskNodes.
     * @throws IOException If reading the file fails.
     */
    public static List<TaskNode> parseTasks(String filePath) throws IOException {
        List<String> lines = readAllLines(filePath);
        List<TaskNode> allNodes = TaskReader.parseLines(lines);
        List<TaskNode> hierarchy = new ArrayList<>();
        Deque<TaskNode> stack = new ArrayDeque<>();

        // Build hierarchy: attach each node as a child of the last node with a lower indent level.
        for (TaskNode current : allNodes) {
            while (!stack.isEmpty() && current.getIndentLevel() <= stack.peek().getIndentLevel()) {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                stack.peek().addChild(current);
            } else {
                hierarchy.add(current);
            }
            stack.push(current);
        }
        return hierarchy;
    }

    /**
     * Reads all non-empty lines from the specified file.
     *
     * @param filePath The path to the file.
     * @return A list of non-empty lines.
     * @throws IOException If reading the file fails.
     */
    private static List<String> readAllLines(String filePath) throws IOException {
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
