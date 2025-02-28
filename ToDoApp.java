import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ToDoApp {
    private JFrame frame;
    private DefaultListModel<String> taskModel;
    private JList<String> taskList;
    private JTextField taskField;
    private static final String FILE_NAME = "tasks.txt";

    public ToDoApp() {
        frame = new JFrame("To-Do List");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        taskModel = new DefaultListModel<>();
        taskList = new JList<>(taskModel);
        loadTasksFromFile();

        taskField = new JTextField();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Remove");
        JButton completeButton = new JButton("Accomplished");

        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> completeTask());

        // Klavye kısayollarını ekle
        setupKeyboardShortcuts();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 3));
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(completeButton);

        frame.add(new JScrollPane(taskList), BorderLayout.CENTER);
        frame.add(taskField, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addTask() {
        String task = taskField.getText().trim();
        if (!task.isEmpty()) {
            taskModel.addElement(task);
            taskField.setText("");
            saveTasksToFile();
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskModel.remove(selectedIndex);
            saveTasksToFile();
        }
    }

    private void completeTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String task = taskModel.getElementAt(selectedIndex);
            taskModel.set(selectedIndex, "✅ " + task);
            saveTasksToFile();
        }
    }

    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < taskModel.size(); i++) {
                writer.write(taskModel.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                taskModel.addElement(line);
            }
        } catch (IOException e) {
            System.out.println("Önceki görevler bulunamadı.");
        }
    }

    private void setupKeyboardShortcuts() {
        // Add Task için Ctrl + Space
        taskField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK), "addTask");
        taskField.getActionMap().put("addTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        // Remove Task için Ctrl + X
        taskList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "deleteTask");
        taskList.getActionMap().put("deleteTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });

        // Complete Task için Ctrl + S
        taskList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "completeTask");
        taskList.getActionMap().put("completeTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeTask();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoApp::new);
    }

    
}