import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

class AttendanceTrackerGUI extends JPanel {
    private JComboBox<String> subjectComboBox;
    private JLabel attendanceStatusLabel;
    private JLabel detailedStatusLabel;
    private JButton markAttendanceButton;
    private JButton unmarkAttendanceButton;
    private JButton checkAttendanceButton;
    private JButton endOfSemesterButton;

    private final String CSV_FILE = "subjects.csv";
    private Map<String, SubjectData> subjectsMap;

    public AttendanceTrackerGUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel selectSubjectLabel = new JLabel("Select Subject:");
        subjectComboBox = new JComboBox<>();
        subjectComboBox.setPreferredSize(new Dimension(200, 30));
        topPanel.add(selectSubjectLabel);
        topPanel.add(subjectComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        markAttendanceButton = new JButton("Mark Attendance");
        unmarkAttendanceButton = new JButton("Unmark Attendance");
        checkAttendanceButton = new JButton("Check Attendance Status");
        endOfSemesterButton = new JButton("End of Semester Total");

        markAttendanceButton.setPreferredSize(new Dimension(180, 30));
        unmarkAttendanceButton.setPreferredSize(new Dimension(180, 30));
        checkAttendanceButton.setPreferredSize(new Dimension(180, 30));
        endOfSemesterButton.setPreferredSize(new Dimension(180, 30));

        buttonPanel.add(markAttendanceButton);
        buttonPanel.add(unmarkAttendanceButton);
        buttonPanel.add(checkAttendanceButton);
        buttonPanel.add(endOfSemesterButton);

        JPanel statusPanel = new JPanel(new BorderLayout(10, 10));
        attendanceStatusLabel = new JLabel("Select a subject to begin.", SwingConstants.CENTER);
        detailedStatusLabel = new JLabel("", SwingConstants.CENTER);
        detailedStatusLabel.setVerticalAlignment(SwingConstants.TOP);
        detailedStatusLabel.setPreferredSize(new Dimension(380, 100));
        statusPanel.add(attendanceStatusLabel, BorderLayout.NORTH);
        statusPanel.add(detailedStatusLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        markAttendanceButton.setEnabled(false);
        unmarkAttendanceButton.setEnabled(false);
        checkAttendanceButton.setEnabled(false);
        endOfSemesterButton.setEnabled(false);
        subjectsMap = loadSubjectsFromCSV();
        subjectsMap.keySet().forEach(subjectComboBox::addItem);

        subjectComboBox.addActionListener(e -> {
            if (subjectComboBox.getSelectedItem() != null) {
                markAttendanceButton.setEnabled(true);
                unmarkAttendanceButton.setEnabled(true);
                checkAttendanceButton.setEnabled(true);
                endOfSemesterButton.setEnabled(true);
                attendanceStatusLabel.setText("Selected: " + subjectComboBox.getSelectedItem());
                detailedStatusLabel.setText(""); // Clear previous status
            }
        });

        markAttendanceButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedSubject != null) {
                SubjectData data = subjectsMap.get(selectedSubject);
                if (data != null) {
                    data.incrementClassesAttended();
                    saveSubjectsToCSV();
                    attendanceStatusLabel.setText("Attendance marked for " + selectedSubject);
                    detailedStatusLabel.setText(""); 
                }
            }
        });

        unmarkAttendanceButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedSubject != null) {
                SubjectData data = subjectsMap.get(selectedSubject);
                if (data != null) {
                    data.decrementClassesAttended();
                    saveSubjectsToCSV();
                    attendanceStatusLabel.setText("Attendance unmarked for " + selectedSubject);
                    detailedStatusLabel.setText(""); 
                }
            }
        });

        checkAttendanceButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedSubject != null) {
                SubjectData data = subjectsMap.get(selectedSubject);
                if (data != null) {
                    float currentPercentage = data.calculateAttendancePercentage();
                    int requiredClasses = data.calculateClassesNeeded();
                    String statusMessage = String.format(
                        "<html>Classes Attended: %d<br>Total Classes: %d<br>Current Attendance: %.2f%%<br>Required Attendance: %d%%<br>More Classes Needed: %d</html>",
                        data.getClassesAttended(), data.getTotalClasses(), currentPercentage, data.getPercentageGoal(), Math.max(0, requiredClasses)
                    );
                    detailedStatusLabel.setText(statusMessage);
                }
            }
        });

        endOfSemesterButton.addActionListener(e -> {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedSubject != null) {
                SubjectData data = subjectsMap.get(selectedSubject);
                if (data != null) {
                    float currentPercentage = data.calculateAttendancePercentage();
                    if (currentPercentage >= data.getPercentageGoal()) {
                        attendanceStatusLabel.setText("Attendance Satisfied");
                    } else {
                        attendanceStatusLabel.setText("RC");
                    }
                }
            }
        });
    }

    private Map<String, SubjectData> loadSubjectsFromCSV() {
        Map<String, SubjectData> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String subjectName = parts[0];
                    int totalClasses = Integer.parseInt(parts[1]);
                    int classesAttended = Integer.parseInt(parts[2]);
                    int percentageGoal = Integer.parseInt(parts[3]);
                    map.put(subjectName, new SubjectData(totalClasses, classesAttended, percentageGoal));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void saveSubjectsToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (Map.Entry<String, SubjectData> entry : subjectsMap.entrySet()) {
                String subjectName = entry.getKey();
                SubjectData data = entry.getValue();
                bw.write(subjectName + "," + data.getTotalClasses() + "," + data.getClassesAttended() + "," + data.getPercentageGoal());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        subjectsMap = loadSubjectsFromCSV();
        subjectComboBox.removeAllItems();
        subjectsMap.keySet().forEach(subjectComboBox::addItem);
        attendanceStatusLabel.setText("Select a subject to begin.");
        detailedStatusLabel.setText("");
        markAttendanceButton.setEnabled(false);
        unmarkAttendanceButton.setEnabled(false);
        checkAttendanceButton.setEnabled(false);
        endOfSemesterButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Attendance Tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLocationRelativeTo(null);
            frame.add(new AttendanceTrackerGUI());
            frame.setVisible(true);
        });
    }
}

class SubjectData {
    private final int totalClasses;
    private int classesAttended;
    private final int percentageGoal;

    public SubjectData(int totalClasses, int classesAttended, int percentageGoal) {
        this.totalClasses = totalClasses;
        this.classesAttended = classesAttended;
        this.percentageGoal = percentageGoal;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public int getPercentageGoal() {
        return percentageGoal;
    }

    public void incrementClassesAttended() {
        classesAttended++; 
    }

    public void decrementClassesAttended() {
        if (classesAttended > 0) {
            classesAttended--; 
        }
    }

    public float calculateAttendancePercentage() {
        return ((float) classesAttended / totalClasses) * 100;
    }

    public int calculateClassesNeeded() {
        float requiredAttendance = (percentageGoal / 100f) * totalClasses;
        return (int) Math.ceil(requiredAttendance) - classesAttended;
    }
}
