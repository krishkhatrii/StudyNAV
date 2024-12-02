class Task {
    private String description;
    private boolean isComplete;

    public Task(String description) {
        this.description = description;
        this.isComplete = false;
    }

    
    public void markComplete(boolean complete) {
        this.isComplete = complete;
    }


    public boolean isComplete() { return isComplete; }
    public String getDescription() { return description; }
}
