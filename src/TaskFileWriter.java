package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskFileWriter {

    /**
     * Root TaskNode listesini, day değerine göre 1..7.txt'ye (veya R ise hepsine) yazar.
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
        // day = 'R' -> 1..7, yoksa int parse
        List<Integer> targetDays = new ArrayList<>();
        if (node.getDay().equalsIgnoreCase("R")) {
            targetDays.addAll(Arrays.asList(1,2,3,4,5,6,7));
        } else {
            try {
                int d = Integer.parseInt(node.getDay());
                targetDays.add(d);
            } catch (NumberFormatException e) {
                // geçersiz ise atla
                return;
            }
        }

        // Indent'li satır oluştur
        String line = createLineWithIndent(node, indent);

        // Hedef günlere yaz
        for (Integer d : targetDays) {
            if (d >= 1 && d <= 7) {
                BufferedWriter bw = writers[d - 1];
                bw.write(line);
                bw.newLine();
            }
        }

        // Çocuklar
        for (TaskNode child : node.getChildren()) {
            writeNodeRecursive(child, indent + 1, writers);
        }
    }

    /**
     * "görev_adi:day:x" formatını (girintili) yazar.
     * Burada node.getName() orijinal metindir ("Sabah_kalkma"), alt tireli.
     */
    private static String createLineWithIndent(TaskNode node, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t"); // her seviye için 1 tab
        }
        sb.append(node.getName())
          .append(":")
          .append(node.getDay())
          .append(":x");
        return sb.toString();
    }
}
