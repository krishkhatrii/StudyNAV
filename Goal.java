import java.time.LocalDate;
import java.util.ArrayList;

class Goal {
    private String name;
    private int target; 
    private int currentProgress;
    private LocalDate deadline;
    private String status; 
    private ArrayList<Task> tasks;

    public Goal(String name, int target, LocalDate deadline) {
        this.name = name;
        this.target = target;
        this.currentProgress = 0;
        this.deadline = deadline;
        this.status = "In Progress";
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
        updateProgress(); 
    }
    public void removeTask(Task task) {
        tasks.remove(task);
        updateProgress(); 
    }
    

    public void updateProgress() {
        int completedTasks = (int) tasks.stream().filter(Task::isComplete).count();
        currentProgress = (int) ((double) completedTasks / tasks.size() * 100);

        if (currentProgress >= target) {
            status = "Completed";
        } else if (LocalDate.now().isAfter(deadline)) {
            status = "Missed";
        } else {
            status = "In Progress";
        }
    }

    public String getName() { return name; }
    public int getTargetProgress() { return target; }
    public int getCurrentProgress() { return currentProgress; }
    public String getStatus() { return status; }
    public LocalDate getDeadline() { return deadline; }
    public ArrayList<Task> getTasks() { return tasks; }

    public void setCurrentProgress (int currentProgress) {
        this.currentProgress = currentProgress;
    }
    public void setStatus (String status) {
        this.status = status;
    }
}