package dash;

/**
 * Represents one task and its completion state.
 */
public class Task {
    /** The text describing what needs to be done. */
    private final String description;

    /** Whether this task has been completed. */
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

    /**
     * Returns this task in the format shown by the chatbot.
     *
     * @return The status icon and description of this task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
