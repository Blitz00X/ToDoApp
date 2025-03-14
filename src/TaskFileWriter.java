package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * TaskFileWriter writes the tasks (the model) to files.
 * It writes to daily files (1.txt to 7.txt) based on each task's day.
 * For tasks with day 'R', the task is written to all files.
 * It uses each TaskNode's toString() (which appends ":a" if completed, ":x" otherwise)
 * and applies proper indentation.
 */
public class TaskFileWriter {

    /**
     * Writes the given list of root TaskNodes to daily files (1.txt to 7.txt).
     *
     * @param rootNodes The list of root TaskNodes.
     * @throws IOException If writing fails.
     */
    public static void writeTasksByDay(List<TaskNode> rootNodes) throws IOException {
        System.out.println("Writing tasks to 1..7.txt");
        String[] dayFiles = {"1.txt", "2.txt", "3.txt", "4.txt", "5.txt", "6.txt", "7.txt"};
        BufferedWriter[] writers = new BufferedWriter[7];
        for (int i = 0; i < 7; i++) {
            writers[i] = new BufferedWriter(new FileWriter(dayFiles[i]));
        }
        for (TaskNode root : rootNodes) {
            writeNodeRecursive(root, 0, writers);
        }
        for (BufferedWriter bw : writers) {
            bw.close();
        }
    }

    /**
     * Recursively writes a TaskNode and its children with proper indentation to the given writers.
     *
     * @param node    The task node.
     * @param indent  The current indentation level.
     * @param writers An array of BufferedWriters corresponding to the daily files.
     * @throws IOException If writing fails.
     */
    private static void writeNodeRecursive(TaskNode node, int indent, BufferedWriter[] writers) throws IOException {
        List<Integer> targetDays = new ArrayList<>();
        // If the task's day is 'R', it goes to all files; otherwise, parse the day as an integer.
        if (node.getDay().equalsIgnoreCase("R")) {
            targetDays.addAll(Arrays.asList(1,2,3,4,5,6,7));
        } else {
            try {
                int d = Integer.parseInt(node.getDay());
                targetDays.add(d);
            } catch (NumberFormatException e) {
                return; // Skip invalid day values.
            }
        }

        // Create the indented line using the node's toString() method.
        String line = createLineWithIndent(node, indent);

        // Write the line to each of the target files.
        for (Integer d : targetDays) {
            if (d >= 1 && d <= 7) {
                BufferedWriter bw = writers[d - 1];
                bw.write(line);
                bw.newLine();
            }
        }
        // Process children recursively with increased indentation.
        for (TaskNode child : node.getChildren()) {
            writeNodeRecursive(child, indent + 1, writers);
        }
    }

    /**
     * Creates a string for the given TaskNode with proper indentation.
     *
     * @param node   The task node.
     * @param indent The current indentation level.
     * @return A formatted string, e.g. "\tMorning_wakeup:R:a".
     */
    private static String createLineWithIndent(TaskNode node, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        sb.append(node.toString());
        return sb.toString();
    }
    
    /**
     * Writes the current state of the tasks back to the main tasks.txt file.
     * This ensures that toggle changes persist across application restarts.
     *
     * @param rootNodes The list of root TaskNodes.
     * @param filePath  The path to the main tasks file.
     * @throws IOException If writing fails.
     */
    public static void writeTasksToMainFile(List<TaskNode> rootNodes, String filePath) throws IOException {
        System.out.println("Writing tasks to tasks.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (TaskNode root : rootNodes) {
                writeNodeToMainFile(root, 0, bw);
            }
        }
    }
    
    private static void writeNodeToMainFile(TaskNode node, int indent, BufferedWriter bw) throws IOException {
        System.out.println("Writing node to main file: " + node.getName());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        sb.append(node.toString());
        bw.write(sb.toString());
        bw.newLine();
        for (TaskNode child : node.getChildren()) {
            writeNodeToMainFile(child, indent + 1, bw);
        }
    }
}
