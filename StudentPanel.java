import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class StudentPanel extends JPanel {
    private ArrayList<Subject> subjects = new ArrayList<>();
    private JTextArea displayArea;
    private final String FILE_NAME = "subjects.csv"; 

    public StudentPanel() {
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Subject");
        JButton removeButton = new JButton("Remove Subject");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);
  
        addButton.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog("Enter Subject Name:");
            if (subjectName != null && !subjectName.isEmpty()) {
                try {
                    int totalClass = Integer.parseInt(JOptionPane.showInputDialog("Enter Total Classes:"));
                    int attended = Integer.parseInt(JOptionPane.showInputDialog("Enter Classes Attended:"));
                    int targetAttendance = Integer.parseInt(JOptionPane.showInputDialog("Enter Target Attendance Percentage:"));
                    addSub(subjectName, totalClass, attended, targetAttendance);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter numeric values.");
                }
            }
        });

        removeButton.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog("Enter Subject Name to Remove:");
            if (subjectName != null && !subjectName.isEmpty()) {
                remove(subjectName);
            }
        });

        loadFromFile();

        Timer timer = new Timer(1000, e -> refreshData());
        timer.start();
    }

    void addSub(String subName, int totalClass, int classAttended, int targetedAttendance) {
        subjects.add(new Subject(subName, totalClass, classAttended, targetedAttendance));
        updateDisplay();
        saveToFile(); 
    }

    void remove(String subName) {
        boolean removed = subjects.removeIf(subject -> subject.getSubName().equals(subName));
        if (removed) {
            updateDisplay();
            saveToFile(); 
        } else {
            JOptionPane.showMessageDialog(this, "Subject not found.");
        }
    }

    private void updateDisplay() {
        displayArea.setText("");
        for (Subject subject : subjects) {
            displayArea.append(subject.toString() + "\n");
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Subject subject : subjects) {
                writer.println(subject.getSubName() + "," 
                        + subject.getTotalClass() + "," 
                        + subject.getClassAttended() + "," 
                        + subject.getTargetedAttendance());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            subjects.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String subName = parts[0];
                    int totalClass = Integer.parseInt(parts[1]);
                    int classAttended = Integer.parseInt(parts[2]);
                    int targetedAttendance = Integer.parseInt(parts[3]);
                    subjects.add(new Subject(subName, totalClass, classAttended, targetedAttendance));
                }
            }
            updateDisplay();
        } catch (FileNotFoundException e) {
            
            System.out.println("No data file found. Starting with an empty subject list.");
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    public void refreshData() {
        loadFromFile();
        updateDisplay();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Panel");
        StudentPanel panel = new StudentPanel();
     
        frame.add(panel, BorderLayout.CENTER);
  
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.saveToFile();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

class Subject {
    private String subName;
    private int totalClass;
    private int classAttended;
    private int targetedAttendance;

    public Subject(String subName, int totalClass, int classAttended, int targetedAttendance) {
        this.subName = subName;
        this.totalClass = totalClass;
        this.classAttended = classAttended;
        this.targetedAttendance = targetedAttendance;
    }
    

    public String getSubName() { return subName; }
    public void setSubName(String subName) { this.subName = subName; }
    public int getTotalClass() { return totalClass; }
    public void setTotalClass(int totalClass) { this.totalClass = totalClass; }
    public int getClassAttended() { return classAttended; }
    public void setClassAttended(int classAttended) { this.classAttended = classAttended; }
    public int getTargetedAttendance() { return targetedAttendance; }
    public void setTargetedAttendance(int targetedAttendance) { this.targetedAttendance = targetedAttendance; }

    @Override
    public String toString() {
        return "Subject{" +
                "SubName='" + subName + '\'' +
                ", TotalClass=" + totalClass +
                ", ClassAttended=" + classAttended +
                ", TargetedAttendance=" + targetedAttendance + "%" +
                '}';
    }
}
