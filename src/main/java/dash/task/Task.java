package dash.task;

/**
 * Represents one task and its completion state.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description The text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the icon used to show this task's completion state.
     *
     * @return {@code X} when complete; otherwise a space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
