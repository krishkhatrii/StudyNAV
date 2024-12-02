import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class GoalTrackingApp extends JPanel {
    private GoalManager goalManager;
    private JPanel goalPanelContainer;

    private static final String GOALS_FILE = "Goals.csv";
    private static final String TASKS_FILE = "Tasks.csv";

    public GoalTrackingApp() {
        goalManager = new GoalManager();
        loadGoalsFromCSV();
        loadTasksFromCSV();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        goalPanelContainer = new JPanel();
        goalPanelContainer.setLayout(new BoxLayout(goalPanelContainer, BoxLayout.Y_AXIS));


        JButton addGoalButton = new JButton("Add New Goal");
        addGoalButton.addActionListener(e -> showAddGoalDialog());

        add(addGoalButton, BorderLayout.NORTH);
        add(new JScrollPane(goalPanelContainer), BorderLayout.CENTER);

        refreshGoalsDisplay();
    }

    private void showAddGoalDialog() {
        JTextField goalNameField = new JTextField();
        JTextField targetField = new JTextField();
        JTextField deadlineField = new JTextField();

        JPanel dialogPanel = new JPanel(new GridLayout(3, 2));
        dialogPanel.add(new JLabel("Goal Name:"));
        dialogPanel.add(goalNameField);
        dialogPanel.add(new JLabel("Target Progress (%):"));
        dialogPanel.add(targetField);
        dialogPanel.add(new JLabel("Deadline (YYYY-MM-DD):"));
        dialogPanel.add(deadlineField);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Add New Goal", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String goalName = goalNameField.getText();
                int target = Integer.parseInt(targetField.getText());
                LocalDate deadline = LocalDate.parse(deadlineField.getText());

                Goal newGoal = new Goal(goalName, target, deadline);
                goalManager.addGoal(newGoal);
                saveGoalsToCSV();
                refreshGoalsDisplay();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check your entries.");
            }
        }
    }

    private void refreshGoalsDisplay() {
        goalPanelContainer.removeAll();
        for (Goal goal : goalManager.getGoals()) {
            JPanel goalPanel = createGoalPanel(goal);
            goalPanelContainer.add(goalPanel);
        }
        goalPanelContainer.revalidate();
        goalPanelContainer.repaint();
    }

    private JPanel createGoalPanel(Goal goal) {
        JPanel goalPanel = new JPanel(new BorderLayout());
        goalPanel.setBorder(BorderFactory.createTitledBorder(goal.getName()));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(goal.getCurrentProgress());
        progressBar.setStringPainted(true);

        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        for (Task task : goal.getTasks()) {
            JPanel taskItemPanel = createTaskPanel(goal, task, progressBar);
            taskPanel.add(taskItemPanel);
        }

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> showAddTaskDialog(goal, progressBar));

JButton deleteGoalButton = new JButton("Delete Goal");
deleteGoalButton.addActionListener(e -> {
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete the goal '" + goal.getName() + "'?", 
        "Delete Goal", 
        JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        goalManager.removeGoal(goal.getName());
        saveGoalsToCSV(); 
        refreshGoalsDisplay(); 
    }
});

goalPanel.add(deleteGoalButton, BorderLayout.WEST);

        JLabel statusLabel = new JLabel("Status: " + goal.getStatus());
        goalPanel.add(progressBar, BorderLayout.NORTH);
        goalPanel.add(new JScrollPane(taskPanel), BorderLayout.CENTER);
        goalPanel.add(statusLabel, BorderLayout.SOUTH);
        goalPanel.add(addTaskButton, BorderLayout.EAST);

        return goalPanel;
    }

    private void showAddTaskDialog(Goal goal, JProgressBar progressBar) {
        String taskDescription = JOptionPane.showInputDialog(this, "Enter Task Description:");
        if (taskDescription != null && !taskDescription.isEmpty()) {
            Task newTask = new Task(taskDescription);
            goal.addTask(newTask);
            saveTasksToCSV();
            goal.updateProgress();
            refreshGoalsDisplay();
        }
    }

    private JPanel createTaskPanel(Goal goal, Task task, JProgressBar progressBar) {
        JPanel taskPanel = new JPanel(new BorderLayout());

        JCheckBox taskCheckBox = new JCheckBox(task.getDescription(), task.isComplete());
        taskCheckBox.addActionListener(e -> {
            task.markComplete(taskCheckBox.isSelected());
            goal.updateProgress();
            progressBar.setValue(goal.getCurrentProgress());
            saveTasksToCSV();
            refreshGoalsDisplay();
        });

            JButton deleteTaskButton = new JButton("Delete");
            deleteTaskButton.setPreferredSize(new Dimension(80, 25)); 
            deleteTaskButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete the task '" + task.getDescription() + "'?", 
                    "Delete Task", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    goal.removeTask(task);
                    saveTasksToCSV(); 
                    refreshGoalsDisplay(); 
                }
            });

        taskPanel.add(taskCheckBox, BorderLayout.CENTER);
        taskPanel.add(deleteTaskButton, BorderLayout.EAST);
        return taskPanel;
    }

    private void saveGoalsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GOALS_FILE))) {
            for (Goal goal : goalManager.getGoals()) {
                writer.write(String.join(",", goal.getName(), String.valueOf(goal.getTargetProgress()),
                        goal.getDeadline().toString(), String.valueOf(goal.getCurrentProgress()), goal.getStatus()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGoalsFromCSV() {
        File file = new File(GOALS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                int targetProgress = Integer.parseInt(parts[1]);
                LocalDate deadline = LocalDate.parse(parts[2]);
                int currentProgress = Integer.parseInt(parts[3]);
                String status = parts[4];

                Goal goal = new Goal(name, targetProgress, deadline);
                goal.setCurrentProgress(currentProgress);
                goal.setStatus(status);
                goalManager.addGoal(goal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasksToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASKS_FILE))) {
            for (Goal goal : goalManager.getGoals()) {
                for (Task task : goal.getTasks()) {
                    writer.write(String.join(",", goal.getName(), task.getDescription(), String.valueOf(task.isComplete())));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromCSV() {
        File file = new File(TASKS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String goalName = parts[0];
                String description = parts[1];
                boolean isComplete = Boolean.parseBoolean(parts[2]);

                Goal goal = goalManager.findGoal(goalName);
                if (goal != null) {
                    Task task = new Task(description);
                    if (isComplete) task.markComplete(true);
                    goal.addTask(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Goal Tracking App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);

            GoalTrackingApp appPanel = new GoalTrackingApp();
            frame.add(appPanel);

            frame.setVisible(true);
        });
    }
}