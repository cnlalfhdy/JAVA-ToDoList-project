import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class FinalTodoApp {

    private static ArrayList<String> todoList = new ArrayList<>();
    private static ArrayList<Boolean> doneList = new ArrayList<>();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static final String FILE_NAME = "todo_data.txt";

    public static void main(String[] args) {

        loadFromFile();

        JFrame frame = new JFrame("To-Do 리스트");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JList<String> todoJList = new JList<>(listModel);

        todoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // ⭐ 체크박스 렌더러 적용
        todoJList.setCellRenderer(new CheckBoxRenderer());

        JScrollPane scrollPane = new JScrollPane(todoJList);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addBtn = new JButton("추가");
        JButton deleteBtn = new JButton("삭제");

        panel.add(addBtn);
        panel.add(deleteBtn);
        frame.add(panel, BorderLayout.SOUTH);

        // ✅ 추가
        addBtn.addActionListener(e -> {
            String todo = JOptionPane.showInputDialog(frame, "할 일 입력:");
            if (todo != null && !todo.isEmpty()) {
                todoList.add(todo);
                doneList.add(false);
                updateList();
                saveToFile();
            }
        });

        // ✅ 삭제
    deleteBtn.addActionListener(e -> {

        boolean hasChecked = false;

            for (int i = doneList.size() - 1; i >= 0; i--) {
                if (doneList.get(i)) {
                    todoList.remove(i);
                    doneList.remove(i);
                    hasChecked = true;
                }
            }

            if (!hasChecked) {
                JOptionPane.showMessageDialog(frame, "삭제할 항목을 체크하세요!");
            }

            updateList();
            saveToFile();
    });

        // ⭐ 체크박스 클릭 이벤트
        todoJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = todoJList.locationToIndex(e.getPoint());

            if (index >= 0) {
                todoJList.setSelectedIndex(index); // ⭐ 선택 추가
                doneList.set(index, !doneList.get(index)); // 체크 토글
                updateList();
                saveToFile();
            }
        }
    });

        updateList();
        frame.setVisible(true);
    }

    // 리스트 갱신
    private static void updateList() {
        listModel.clear();
        for (int i = 0; i < todoList.size(); i++) {
            String status = doneList.get(i) ? "[O]" : "[ ]";
            listModel.addElement(status + " " + todoList.get(i));
        }
    }

    // 파일 저장
    private static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < todoList.size(); i++) {
                bw.write(todoList.get(i) + "|" + doneList.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 불러오기
    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    todoList.add(parts[0]);
                    doneList.add(Boolean.parseBoolean(parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ⭐ 체크박스 렌더러
class CheckBoxRenderer extends JCheckBox implements ListCellRenderer<String> {

    @Override
    public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setText(value);

        // 체크 여부 판단
        if (value.startsWith("[O]")) {
            setSelected(true);
        } else {
            setSelected(false);
        }

        return this;
    }
}