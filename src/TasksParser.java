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
     * "tasks.txt" gibi dosya yolunu alır, satır satır okur,
     * her satırı TaskReader.parseLine ile TaskNode'a çevirir,
     * sonra indent değerlerine göre parent-child ilişkisini oluşturur.
     */
    public static List<TaskNode> parseTasks(String filePath) throws IOException {
        // Dosyadaki tüm satırları okuyalım
        List<String> lines = readAllLines(filePath);

        // Bu satırları TaskNode listesine çevirelim
        List<TaskNode> allNodes = TaskReader.parseLines(lines);

        // Hiyerarşiyi kurmak için stack yaklaşımı
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
