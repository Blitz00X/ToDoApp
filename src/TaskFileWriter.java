package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskFileWriter {

    /**
     * Writes a list of root TaskNodes to files based on their day values (1..7.txt),
     * or to all files if the day value is 'R'.
     */
    public static void writeTasksByDay(List<TaskNode> rootNodes) throws IOException {
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

    private static void writeNodeRecursive(TaskNode node, int indent, BufferedWriter[] writers) throws IOException {
        // If day = 'R', write to all files; otherwise, parse the integer value.
        List<Integer> targetDays = new ArrayList<>();
        if (node.getDay().equalsIgnoreCase("R")) {
            targetDays.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        } else {
            try {
                int d = Integer.parseInt(node.getDay());
                targetDays.add(d);
            } catch (NumberFormatException e) {
                // Skip invalid day values
                return;
            }
        }

        // Create the indented line
        String line = createLineWithIndent(node, indent);

        // Write to the target day files
        for (Integer d : targetDays) {
            if (d >= 1 && d <= 7) {
                BufferedWriter bw = writers[d - 1];
                bw.write(line);
                bw.newLine();
            }
        }

        // Process child nodes
        for (TaskNode child : node.getChildren()) {
            writeNodeRecursive(child, indent + 1, writers);
        }
    }

    /**
     * Writes a line in the format "task_name:day:x" with proper indentation.
     * The node.getName() method retains the original format (e.g., "Morning_wakeup").
     */
    private static String createLineWithIndent(TaskNode node, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t"); // Use one tab per level of indentation
        }
        sb.append(node.toString());
        return sb.toString();
    }
}
