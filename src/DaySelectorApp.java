package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DaySelectorApp extends Application {

    private Map<Integer, TreeView<String>> dayTreeViews = new HashMap<>();

    /**
     * Tek seferde:
     * 1) tasks.txt'yi parse -> 1..7.txt oluştur,
     * 2) JavaFX arayüzünü başlat.
     */
    public static void main(String[] args) {
        // 1) tasks.txt -> 1..7.txt
        try {
            List<TaskNode> rootTasks = TasksParser.parseTasks("tasks.txt");
            TaskFileWriter.writeTasksByDay(rootTasks);
            System.out.println("tasks.txt'den 1..7.txt dosyaları oluşturuldu.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2) JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ComboBox<Integer> dayCombo = new ComboBox<>();
        for (int i = 1; i <= 7; i++) {
            dayCombo.getItems().add(i);
        }
        dayCombo.setValue(1);

        BorderPane root = new BorderPane();
        root.setTop(dayCombo);

        // 1..7.txt dosyalarını parse edip TreeView oluştur
        for (int day = 1; day <= 7; day++) {
            TreeView<String> treeView = buildTreeViewFromFile(day + ".txt");
            dayTreeViews.put(day, treeView);
        }

        // ComboBox değiştiğinde ekrana o TreeView'ı koyalım
        dayCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            root.setCenter(dayTreeViews.get(newVal));
        });
        // Varsayılan görünüm: 1.gün
        root.setCenter(dayTreeViews.get(1));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Günlük Görevler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Verilen gün dosyasını (ör: "1.txt") parse edip
     * TreeView'e dönüştüren metod.
     */
    private TreeView<String> buildTreeViewFromFile(String fileName) {
        // Dosya satırlarını parse edip hiyerarşik TaskNode listesi al
        List<TaskNode> rootTasks = parseDayFile(fileName);

        TreeItem<String> invisibleRoot = new TreeItem<>("ROOT");
        invisibleRoot.setExpanded(true);

        // Her rootTask'ı TreeItem'a ekle
        for (TaskNode node : rootTasks) {
            TreeItem<String> item = createTreeItem(node);
            invisibleRoot.getChildren().add(item);
        }

        TreeView<String> treeView = new TreeView<>(invisibleRoot);
        treeView.setShowRoot(false); // "ROOT" görünmesin
        return treeView;
    }

    /**
     * 1.txt gibi bir dosyayı, TaskReader+stack yöntemiyle hiyerarşiye dönüştürür.
     */
    private List<TaskNode> parseDayFile(String fileName) {
        List<TaskNode> dayRootNodes = new ArrayList<>();
        try {
            List<String> lines = readAllLines(fileName);

            // Tek tek satırlardan TaskNode listesi
            List<TaskNode> allNodes = TaskReader.parseLines(lines);

            // Stack ile parent-child ilişkisi
            Deque<TaskNode> stack = new ArrayDeque<>();
            for (TaskNode current : allNodes) {
                while (!stack.isEmpty() && current.getIndentLevel() <= stack.peek().getIndentLevel()) {
                    stack.pop();
                }
                if (!stack.isEmpty()) {
                    stack.peek().addChild(current);
                } else {
                    dayRootNodes.add(current);
                }
                stack.push(current);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dayRootNodes;
    }

    /**
     * Bir TaskNode'u (ve alt çocuklarını) JavaFX TreeItem'a dönüştürür.
     * Burada alt tire yerine boşluk gösterilen displayName'i kullanıyoruz.
     */
    private TreeItem<String> createTreeItem(TaskNode node) {
        // UI'da displayName -> "görev adi"
        TreeItem<String> item = new TreeItem<>(node.getDisplayName());
        for (TaskNode child : node.getChildren()) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }

    private List<String> readAllLines(String filePath) throws IOException {
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
