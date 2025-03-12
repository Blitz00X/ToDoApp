package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TasksParser {

    /**
     * Takes a file path like "tasks.txt", reads it line by line,
     * converts each line into a TaskNode using TaskReader.parseLine,
     * and then establishes parent-child relationships based on indentation levels.
     */
    public static List<TaskNode> parseTasks(String filePath) throws IOException {
        // Read all lines from the file
        List<String> lines = readAllLines(filePath);

        // Convert lines into a list of TaskNodes
        List<TaskNode> allNodes = TaskReader.parseLines(lines);

        // Use stack approach to build the hierarchy
        List<TaskNode> hierarchy = new ArrayList<>();
        Deque<TaskNode> stack = new ArrayDeque<>();

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
     * Reads all non-empty lines from a file and returns them as a list.
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
