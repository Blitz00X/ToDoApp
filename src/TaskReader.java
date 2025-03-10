package src;

import java.util.ArrayList;
import java.util.List;

public class TaskReader {

    /**
     * Bir satır ("    Math207:1:x" gibi) okuyup TaskNode oluşturan metod.
     *  - Girinti sayısını belirler (tab ve boşlukları 4-space = 1-tab şeklinde sayıyoruz).
     *  - "görev_adi:gün:x" formatını parçalayıp TaskNode oluşturur.
     *  - displayName'i alt tireleri boşluk yaparak ayarlar.
     */
    public static TaskNode parseLine(String line) {
        int indentCount = countLeadingIndent(line);
        String trimmed = line.trim();

        // "Görev_Adi:gün:x" formatındaysa parts[0]=Görev_Adi, parts[1]=gün
        String[] parts = trimmed.split(":");
        if (parts.length < 2) {
            return null; // geçersiz satır
        }
        String name = parts[0];
        String day = parts[1];

        // TaskNode oluştur
        TaskNode node = new TaskNode(name, day, indentCount);
        // displayName'de alt tireleri boşluk yapıyoruz
        node.setDisplayName(name.replace('_', ' '));

        return node;
    }

    /**
     * Dosyadaki tüm satırları parseLine ile okuyup TaskNode listesi döndürür
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
     * Hem tab hem boşluk karakterlerini sayarak indent değeri hesaplayan metod.
     * 4 boşluk = 1 tab eşdeğerliği.
     */
    private static int countLeadingIndent(String line) {
        int spaceCount = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                spaceCount++;
            } else if (c == '\t') {
                spaceCount += 4; // 1 tab = 4 space
            } else {
                break;
            }
        }
        // 4 boşluk = 1 indent
        return spaceCount / 4;
    }
}
