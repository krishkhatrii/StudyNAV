import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class Goal {
    private String title;
    private int target;
    private int progress;
    private String status;
    private Date deadline;

    Goal(String title, int target, Date deadline) {
        this.title = title;
        this.target = target;
        this.progress = 0;
        this.status = "In Progress";
        this.deadline = deadline;
    }

    public void markComplete() {
        status = "Completed";
    }

    public boolean isAchieved() {
        return (progress >= target);
    }

    public String getTitle() {
        return title;
    }

    public int getTarget() {
        return target;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public Date getDeadline() {
        return deadline;
    }
}

class GoalManage {
    private ArrayList<Goal> goals = new ArrayList<>();

    public void addGoal(String title, int target, Date deadline) {
        goals.add(new Goal(title, target, deadline));
    }

    public Goal getGoalByTitle(String title) {
        for (Goal g : goals) {
            if (g.getTitle().equals(title)) {
                return g;
            }
        }
        return null;
    }

    public String updateProgress(String title, int newProgress) {
        Goal goal = getGoalByTitle(title);
        if (goal != null) {
            goal.setProgress(newProgress);
            if (goal.isAchieved()) {
                goal.markComplete();
                return "Goal '" + title + "' is achieved.";
            }
            return "Progress updated.";
        } else {
            return "Goal with title '" + title + "' not found.";
        }
    }
}

public class GoalManagerApp extends JFrame {
    private GoalManage goalManager;
    private JTextField addTitleField, addTargetField, addDeadlineField;
    private JTextField updateTitleField, updateProgressField;
    private JTextField checkTitleField;
    private JTextArea outputArea;

    public GoalManagerApp() {
        goalManager = new GoalManage();

        setTitle("Goal Manager");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

       
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

       
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Goal", createAddGoalPanel());
        tabbedPane.addTab("Update Progress", createUpdateProgressPanel());
        tabbedPane.addTab("Check Status", createCheckStatusPanel());

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createAddGoalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        
        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Goal Title:"), c);

        c.gridx = 1;
        addTitleField = new JTextField(15);
        panel.add(addTitleField, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Target:"), c);

        c.gridx = 1;
        addTargetField = new JTextField(5);
        panel.add(addTargetField, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Deadline (yyyy-MM-dd):"), c);

        c.gridx = 1;
        addDeadlineField = new JTextField(10);
        panel.add(addDeadlineField, c);

        JButton addGoalButton = new JButton("Add Goal");
        addGoalButton.addActionListener(e -> addGoal());

        c.gridx = 1;
        c.gridy = 3;
        panel.add(addGoalButton, c);

        return panel;
    }

    private JPanel createUpdateProgressPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Goal Title:"), c);

        c.gridx = 1;
        updateTitleField = new JTextField(15);
        panel.add(updateTitleField, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("New Progress:"), c);

        c.gridx = 1;
        updateProgressField = new JTextField(5);
        panel.add(updateProgressField, c);

        JButton updateProgressButton = new JButton("Update Progress");
        updateProgressButton.addActionListener(e -> updateProgress());

        c.gridx = 1;
        c.gridy = 2;
        panel.add(updateProgressButton, c);

        return panel;
    }

    private JPanel createCheckStatusPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Goal Title:"), c);

        c.gridx = 1;
        checkTitleField = new JTextField(15);
        panel.add(checkTitleField, c);

        JButton checkStatusButton = new JButton("Check Status");
        checkStatusButton.addActionListener(e -> checkGoalStatus());

        c.gridx = 1;
        c.gridy = 1;
        panel.add(checkStatusButton, c);

        return panel;
    }

    private void addGoal() {
        String title = addTitleField.getText();
        int target;
        Date deadline;

        try {
            target = Integer.parseInt(addTargetField.getText());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            deadline = dateFormat.parse(addDeadlineField.getText());
            goalManager.addGoal(title, target, deadline);
            outputArea.append("Goal '" + title + "' added successfully.\n");
        } catch (NumberFormatException e) {
            outputArea.append("Error: Target must be a number.\n");
        } catch (ParseException e) {
            outputArea.append("Error: Invalid date format. Use yyyy-MM-dd.\n");
        }
    }

    private void updateProgress() {
        String title = updateTitleField.getText();
        int newProgress;

        try {
            newProgress = Integer.parseInt(updateProgressField.getText());
            String message = goalManager.updateProgress(title, newProgress);
            outputArea.append(message + "\n");
        } catch (NumberFormatException e) {
            outputArea.append("Error: Progress must be a number.\n");
        }
    }

    private void checkGoalStatus() {
        String title = checkTitleField.getText();
        Goal goal = goalManager.getGoalByTitle(title);

        if (goal != null) {
            outputArea.append("Goal Status: " + goal.getStatus() + "\n");
        } else {
            outputArea.append("Goal with title '" + title + "' not found.\n");
        }
    }

    public static void main(String[] args) {
        new GoalManagerApp();
    }
}