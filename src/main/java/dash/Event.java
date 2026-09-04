package dash;

/**
 * Represents a task with a specified start and end date or time.
 */
public class Event extends Task {
    /** The start date or time entered by the user. */
    private final String from;

    /** The end date or time entered by the user. */
    private final String to;

    /**
     * Creates an incomplete event with its description, start, and end times.
     *
     * @param description The text describing the task.
     * @param from The start date or time.
     * @param to The end date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
