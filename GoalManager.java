import java.util.ArrayList;

class GoalManager {
    private ArrayList<Goal> goals;

    public GoalManager() {
        this.goals = new ArrayList<>();
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }
    public boolean removeGoal(String goalName) {
        return goals.removeIf(goal -> goal.getName().equalsIgnoreCase(goalName));
    }
    
    public void updateGoalProgress() {
        for (Goal goal : goals) {
            goal.updateProgress();
        }
    }

    public Goal findGoal(String goalName) {
        for (Goal goal : goals) { 
            if (goal.getName().equals(goalName)) {
                return goal; 
            }
        }
        return null; 
    }

    public ArrayList<Goal> getGoals() { return goals; }
}
