package src;
import java.util.ArrayList;
import java.util.List;

public class TaskNode {
    private String name;         // Örn: "Sabah_kalkma"
    private String displayName;  // Örn: "Sabah kalkma" (UI'da gösterilecek)
    private String day;          // 'R' veya '1'..'7'
    private int indentLevel;     // Girinti seviyesi
    private List<TaskNode> children = new ArrayList<>();
    private TaskNode parent;

    public TaskNode(String name, String day, int indentLevel) {
        this.name = name;
        this.day = day;
        this.indentLevel = indentLevel;
        // Başlangıçta displayName = name. Ayarlamak istersek setDisplayName kullanabiliriz.
        this.displayName = name;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDay() {
        return day;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public List<TaskNode> getChildren() {
        return children;
    }

    public TaskNode getParent() {
        return parent;
    }

    public void setParent(TaskNode parent) {
        this.parent = parent;
    }

    public void addChild(TaskNode child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Dosyaya yazarken orijinal name'i ("Sabah_kalkma" vb.) kullanıyoruz.
     */
    @Override
    public String toString() {
        return name + ":" + day + ":x";
    }
}
