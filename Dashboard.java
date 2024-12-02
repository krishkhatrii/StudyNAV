import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends JPanel {
    private List<String[]> goalsData = new ArrayList<>();
    private List<String[]> subjectsData = new ArrayList<>();
    private List<String[]> tasksData = new ArrayList<>();
    private List<String[]> timetableData = new ArrayList<>();

    private JLabel clockLabel;
    private JLabel dayLabel;
    private JPanel classesPanel;
    private JLabel tasksLabel;
    private JLabel attendanceLabel;

    public Dashboard() {
        
        this.setLayout(new GridLayout(4, 1));

        Border border = BorderFactory.createLineBorder(Color.BLACK);

        JPanel currentDateTimePanel = new JPanel(new BorderLayout());
        dayLabel = new JLabel();
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dayLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentDateTimePanel.add(dayLabel, BorderLayout.NORTH);

        clockLabel = new JLabel();
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        currentDateTimePanel.add(clockLabel, BorderLayout.CENTER);

        currentDateTimePanel.setBorder(border);
        this.add(currentDateTimePanel);

        JPanel todayClassesPanel = new JPanel(new BorderLayout());
        classesPanel = new JPanel(new GridLayout(1, 9));
        todayClassesPanel.add(classesPanel, BorderLayout.CENTER);
        todayClassesPanel.setBorder(border);
        this.add(todayClassesPanel);

        JPanel tasksPanel = new JPanel(new BorderLayout());
        tasksLabel = new JLabel();
        tasksLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tasksLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        tasksPanel.add(tasksLabel, BorderLayout.CENTER);
        tasksPanel.setBorder(border);
        this.add(tasksPanel);

        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendanceLabel = new JLabel();
        attendanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        attendanceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        attendancePanel.add(attendanceLabel, BorderLayout.CENTER);
        attendancePanel.setBorder(border);
        this.add(attendancePanel);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock();
                updateData();
            }
        });
        timer.start();

        updateData();
    }

     private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        clockLabel.setText(now.format(DateTimeFormatter.ofPattern("hh:mm a"))); 
        dayLabel.setText(now.format(DateTimeFormatter.ofPattern("EEEE")));
    }

    private void updateData() {
        readCSV("Goals.csv", goalsData);
        readCSV("subjects.csv", subjectsData);
        readCSV("Tasks.csv", tasksData);
        readCSV("Timetable.csv", timetableData);
        updateTodaysClasses();
        tasksLabel.setText(getTasks());
        attendanceLabel.setText(getAttendance());
    }

    private void readCSV(String filePath, List<String[]> dataList) {
        dataList.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataList.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTodaysClasses() {
        classesPanel.removeAll();
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() - 1; 

        boolean hasClasses = false;
        for (String[] row : timetableData) {
            if (row.length > dayOfWeek) {
                String className = row[dayOfWeek].trim();
                JLabel classLabel = new JLabel(className, SwingConstants.CENTER);
                classLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                classLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                classLabel.setOpaque(true); 
                switch (className.toLowerCase()) {
                    case "play":
                        classLabel.setBackground(new Color(255, 182, 193)); 
                        break;
                    case "study":
                        classLabel.setBackground(new Color(144, 238, 144)); 
                        break;
                    case "rest":
                        classLabel.setBackground(new Color(255, 255, 224)); 
                        break;
                    default:
                        classLabel.setBackground(new Color(173, 216, 230)); 
                        break;
                }
                classesPanel.add(classLabel);
                hasClasses = true;
            }
        }
        if (!hasClasses) {
            JLabel noClassesLabel = new JLabel("No classes", SwingConstants.CENTER);
            noClassesLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            noClassesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            classesPanel.add(noClassesLabel);
        }
        classesPanel.revalidate();
        classesPanel.repaint();
    }

    private String getTasks() {
        StringBuilder tasks = new StringBuilder("<html>");
        for (String[] row : tasksData) {
            if (row.length > 2) {
                boolean isDone = Boolean.parseBoolean(row[2]);
                String status = isDone ? "Done" : "Not Done";
                tasks.append(row[0]).append(": ").append(row[1]).append(" - ").append(status).append("<br>");
            }
        }
        tasks.append("</html>");
        return tasks.toString();
    }

    private String getAttendance() {
        StringBuilder attendance = new StringBuilder("<html>");
        for (String[] row : subjectsData) {
            if (row.length > 3) {
                attendance.append(row[0]).append(": ").append(row[2]).append("/").append(row[3]).append("<br>");
            }
        }
        attendance.append("</html>");
        return attendance.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Dashboard");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.add(new Dashboard());
                frame.setVisible(true);
            }
        });
    }
}
