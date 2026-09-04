package dash;

import java.util.Scanner;

/**
 * Runs the Dash command-line chatbot.
 */
public class Dash {
    /** The command that displays all stored tasks. */
    private static final String LIST_COMMAND = "list";

    /** The command that ends the chatbot session. */
    private static final String BYE_COMMAND = "bye";

    /** The command prefix that adds a task without date or time information. */
    private static final String TODO_COMMAND_PREFIX = "todo ";

    /** The command prefix that adds a task with a due date or time. */
    private static final String DEADLINE_COMMAND_PREFIX = "deadline ";

    /** The command prefix that adds a task with a start and end time. */
    private static final String EVENT_COMMAND_PREFIX = "event ";

    /** The command prefix that marks a task as complete. */
    private static final String MARK_COMMAND_PREFIX = "mark ";

    /** The command prefix that marks a task as incomplete. */
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";

    /** The maximum number of tasks that can be stored in memory. */
    private static final int TASK_LIMIT = 100;

    /** A divider used to separate chatbot messages. */
    private static final String DIVIDER = "____________________________________________________________";

    /** The separator before a deadline's due date or time. */
    private static final String BY_SEPARATOR = " /by ";

    /** The separator before an event's start date or time. */
    private static final String FROM_SEPARATOR = " /from ";

    /** The separator before an event's end date or time. */
    private static final String TO_SEPARATOR = " /to ";

    /**
     * Starts the chatbot and processes commands until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        String banner = " ____              _     \n"
                + "|  _ \\  __ _ ___| |__  \n"
                + "| | | |/ _` / __| '_ \\ \n"
                + "| |_| | (_| \\__ \\ | | |\n"
                + "|____/ \\__,_|___/_| |_|\n";
        Task[] tasks = new Task[TASK_LIMIT];
        int taskCount = 0;

        System.out.println(banner);
        System.out.println(DIVIDER);
        System.out.println("Hello! I'm Dash.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            if (command.equals(BYE_COMMAND)) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            if (command.equals(LIST_COMMAND)) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith(TODO_COMMAND_PREFIX)) {
                tasks[taskCount] = new Todo(getArgument(command, TODO_COMMAND_PREFIX));
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith(DEADLINE_COMMAND_PREFIX)) {
                tasks[taskCount] = createDeadline(command);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith(EVENT_COMMAND_PREFIX)) {
                tasks[taskCount] = createEvent(command);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith(MARK_COMMAND_PREFIX)) {
                int taskIndex = getTaskIndex(command, MARK_COMMAND_PREFIX);
                tasks[taskIndex].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskIndex]);
            } else if (command.startsWith(UNMARK_COMMAND_PREFIX)) {
                int taskIndex = getTaskIndex(command, UNMARK_COMMAND_PREFIX);
                tasks[taskIndex].markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskIndex]);
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Returns the zero-based task index specified by a task command.
     *
     * @param command The complete command entered by the user.
     * @param commandPrefix The command text preceding the task number.
     * @return The zero-based index of the requested task.
     */
    private static int getTaskIndex(String command, String commandPrefix) {
        int taskNumber = Integer.parseInt(command.substring(commandPrefix.length()));
        return taskNumber - 1;
    }

    /**
     * Returns the text after a command prefix.
     *
     * @param command The complete command entered by the user.
     * @param commandPrefix The command text preceding its argument.
     * @return The argument supplied after the command prefix.
     */
    private static String getArgument(String command, String commandPrefix) {
        return command.substring(commandPrefix.length());
    }

    /**
     * Returns a deadline parsed from a deadline command.
     *
     * @param command The complete deadline command entered by the user.
     * @return A task with its description and due date or time.
     */
    private static Deadline createDeadline(String command) {
        int byIndex = command.indexOf(BY_SEPARATOR);
        String description = command.substring(DEADLINE_COMMAND_PREFIX.length(), byIndex);
        String by = command.substring(byIndex + BY_SEPARATOR.length());
        return new Deadline(description, by);
    }

    /**
     * Returns an event parsed from an event command.
     *
     * @param command The complete event command entered by the user.
     * @return A task with its description, start date or time, and end date or time.
     */
    private static Event createEvent(String command) {
        int fromIndex = command.indexOf(FROM_SEPARATOR);
        int toIndex = command.indexOf(TO_SEPARATOR);
        String description = command.substring(EVENT_COMMAND_PREFIX.length(), fromIndex);
        String from = command.substring(fromIndex + FROM_SEPARATOR.length(), toIndex);
        String to = command.substring(toIndex + TO_SEPARATOR.length());
        return new Event(description, from, to);
    }

    /**
     * Prints the confirmation shown after a task has been added.
     *
     * @param task The task that was added.
     * @param taskCount The number of tasks now stored.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
