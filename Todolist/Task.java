package Todolist;

// Class to hold task data
public class Task {
    private String title;
    private String deadline;
    private String content;
    private boolean completed;

    public Task(String title, String deadline, String content) {
        this.title = title;
        this.deadline = deadline;
        this.content = content;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return title + " (Due: " + deadline + ")";
    }
}
