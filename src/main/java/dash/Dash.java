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

    /** The command prefix that marks a task as complete. */
    private static final String MARK_COMMAND_PREFIX = "mark ";

    /** The command prefix that marks a task as incomplete. */
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";

    /** The maximum number of tasks that can be stored in memory. */
    private static final int TASK_LIMIT = 100;

    /** A divider used to separate chatbot messages. */
    private static final String DIVIDER = "____________________________________________________________";

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
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
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
}
