package src;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 1) tasks.txt dosyasından tüm görevleri parse et
            List<TaskNode> roots = TasksParser.parseTasks("tasks.txt");

            // 2) Her günü temsil eden 1.txt..7.txt dosyalarına yaz
            TaskFileWriter.writeTasksByDay(roots);

            System.out.println("Görevler ilgili gün dosyalarına başarıyla yazıldı.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}