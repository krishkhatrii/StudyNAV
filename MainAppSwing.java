
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainAppSwing extends JPanel {

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final Map<String, JComboBox<String>> timetableMap = new HashMap<>();
    private static final DefaultTableModel examModel = new DefaultTableModel(new String[]{"Subject", "Date", "Priority"}, 0);
    JComboBox<String> subjectField;

    private static final Color CLASS_COLOR = new Color(173, 216, 230);
    private static final Color STUDY_COLOR = new Color(144, 238, 144);
    private static final Color PLAY_COLOR = new Color(255, 182, 193);
    private static final Color REST_COLOR = new Color(255, 255, 224);

    private static final String TIMETABLE_FILE = "Timetable.csv";
    private static final String EXAMS_FILE = "Exams.csv";
    private static final String SUBJECTS_FILE = "Subjects.csv";

    private static List<String> subjectsList = new ArrayList<>();

    public MainAppSwing() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadSubjectsFromCSV(); 

        JPanel timetableGrid = createTimetableGrid();
        JPanel examSection = createExamSection();

        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(timetableGrid);
        add(examSection);

        loadTimetableFromCSV();
        loadExamsFromCSV();
    }

    private JPanel createTimetableGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < DAYS.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            grid.add(new JLabel(DAYS[i]), gbc);
        }

        for (int row = 1; row <= 9; row++) {
            gbc.gridx = 0;
            gbc.gridy = row;
            grid.add(new JLabel("Period " + row), gbc);
            for (int col = 0; col < DAYS.length; col++) {
                JComboBox<String> cell = createEditableCell();
                gbc.gridx = col + 1;
                grid.add(cell, gbc);
                String cellKey = DAYS[col] + row;
                timetableMap.put(cellKey, cell);
            }
        }

        JButton saveButton = new JButton("Save Timetable");
        saveButton.addActionListener(e -> saveTimetableToCSV());
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = DAYS.length + 1;
        grid.add(saveButton, gbc);

        return grid;
    }

    private JComboBox<String> createEditableCell() {
        JComboBox<String> cell = new JComboBox<>(new String[]{"Play", "Study", "Rest"});
        for (String subject : subjectsList) {
            cell.addItem(subject);
        }
        cell.setPreferredSize(new Dimension(150, 30));  
        cell.addActionListener(e -> updateCellColor((JComboBox<String>) e.getSource()));
        return cell;
    }

    private void updateCellColor(JComboBox<String> cell) {
        String text = (String) cell.getSelectedItem();
        if (text == null) return;
        text = text.toLowerCase();
        if (text.contains("study")) {
            cell.setBackground(STUDY_COLOR);
        } else if (text.contains("play")) {
            cell.setBackground(PLAY_COLOR);
        } else if (text.contains("rest")) {
            cell.setBackground(REST_COLOR);
        } else {
            cell.setBackground(CLASS_COLOR);
        }
    }

    private JPanel createExamSection() {
        JPanel examPanel = new JPanel();
        examPanel.setLayout(new BoxLayout(examPanel, BoxLayout.Y_AXIS));

        JTable examTable = new JTable(examModel);
        JScrollPane tableScrollPane = new JScrollPane(examTable);

        subjectField = new JComboBox<>(subjectsList.toArray(new String[0]));
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");  
        dateSpinner.setEditor(dateEditor);
        JSlider prioritySlider = new JSlider(1, 10);

        JButton addExamButton = new JButton("Add Exam");
        addExamButton.addActionListener((ActionEvent e) -> {
            String subject = (String) subjectField.getSelectedItem();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(dateSpinner.getValue());  
            String priority = String.valueOf(prioritySlider.getValue());

            if (subject != null && !date.isEmpty()) {
                examModel.addRow(new String[]{subject, date, priority});
                saveExamsToCSV();
            }
        });

        JButton removeExamButton = new JButton("Remove Selected Exam");
        removeExamButton.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow != -1) {
                examModel.removeRow(selectedRow);
                saveExamsToCSV();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Subject:"));
        inputPanel.add(subjectField);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateSpinner);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(prioritySlider);
        inputPanel.add(addExamButton);
        inputPanel.add(removeExamButton);

        examPanel.add(tableScrollPane);
        examPanel.add(inputPanel);

        return examPanel;
    }

    private void loadSubjectsFromCSV() {
        subjectsList.clear();
        File file = new File(SUBJECTS_FILE);
        if (!file.exists()) return;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                subjectsList.add(values[0].trim()); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void saveTimetableToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TIMETABLE_FILE))) {
            for (int row = 1; row <= 9; row++) {
                for (int col = 0; col < DAYS.length; col++) {
                    String cellKey = DAYS[col] + row;
                    String value = (String) timetableMap.get(cellKey).getSelectedItem();
                    writer.write(value == null || value.isEmpty() ? "Rest" : value);
                    if (col < DAYS.length - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTimetableFromCSV() {
        File file = new File(TIMETABLE_FILE);
        if (!file.exists()) return;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (int row = 1; row <= 9; row++) {
                String line = reader.readLine();
                if (line != null) {
                    String[] values = line.split(",");
                    for (int col = 0; col < DAYS.length; col++) {
                        String cellKey = DAYS[col] + row;
                        JComboBox<String> cell = timetableMap.get(cellKey);
                        if (cell != null) {
                            cell.setSelectedItem(values[col].trim());
                            updateCellColor(cell);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void saveExamsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXAMS_FILE))) {
            for (int i = 0; i < examModel.getRowCount(); i++) {
                for (int j = 0; j < examModel.getColumnCount(); j++) {
                    writer.write((String) examModel.getValueAt(i, j));
                    if (j < examModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExamsFromCSV() {
        File file = new File(EXAMS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                examModel.addRow(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshData() {
        loadSubjectsFromCSV();
    
        for (JComboBox<String> cell : timetableMap.values()) {
            String selected = (String) cell.getSelectedItem();
            cell.removeAllItems();
    
            cell.addItem("Play");   
            cell.addItem("Study");
            cell.addItem("Rest");
    
            for (String subject : subjectsList) {
                cell.addItem(subject);
            }
    
            if (selected != null) {
                cell.setSelectedItem(selected);
            }
        }
        subjectField.removeAllItems();
        for (String subject : subjectsList) {
            subjectField.addItem(subject);
        }
        loadTimetableFromCSV();
    }
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Student Timetable and Exam Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            MainAppSwing mainPanel = new MainAppSwing();
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}
