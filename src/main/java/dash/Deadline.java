package dash;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The due date or time entered by the user. */
    private final String by;

    /**
     * Creates an incomplete deadline with its description and due date or time.
     *
     * @param description The text describing the task.
     * @param by The due date or time.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
