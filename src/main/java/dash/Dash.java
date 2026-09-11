package dash;

import java.util.Scanner;

/**
 * Runs the Dash command-line chatbot.
 */
public class Dash {
    private static final String LIST_COMMAND = "list";
    private static final String BYE_COMMAND = "bye";
    private static final String TODO_COMMAND_PREFIX = "todo ";
    private static final String DEADLINE_COMMAND_PREFIX = "deadline ";
    private static final String EVENT_COMMAND_PREFIX = "event ";
    private static final String MARK_COMMAND_PREFIX = "mark ";
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";
    private static final int TASK_LIMIT = 100;
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
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

            taskCount = handleCommand(command, tasks, taskCount);
            System.out.println(DIVIDER);
        }
    }

    private static int handleCommand(String command, Task[] tasks, int taskCount) {
        if (command.equals(LIST_COMMAND)) {
            printTaskList(tasks, taskCount);
        } else if (command.startsWith(TODO_COMMAND_PREFIX)) {
            taskCount = addTask(tasks, taskCount,
                    new Todo(getArgument(command, TODO_COMMAND_PREFIX)));
        } else if (command.startsWith(DEADLINE_COMMAND_PREFIX)) {
            taskCount = addTask(tasks, taskCount, createDeadline(command));
        } else if (command.startsWith(EVENT_COMMAND_PREFIX)) {
            taskCount = addTask(tasks, taskCount, createEvent(command));
        } else if (command.startsWith(MARK_COMMAND_PREFIX)) {
            markTask(tasks, command, MARK_COMMAND_PREFIX, true);
        } else if (command.startsWith(UNMARK_COMMAND_PREFIX)) {
            markTask(tasks, command, UNMARK_COMMAND_PREFIX, false);
        }
        return taskCount;
    }

    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        printTaskAdded(task, newTaskCount);
        return newTaskCount;
    }

    private static void markTask(Task[] tasks, String command, String commandPrefix, boolean isDone) {
        int taskIndex = getTaskIndex(command, commandPrefix);
        if (isDone) {
            tasks[taskIndex].markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + tasks[taskIndex]);
        } else {
            tasks[taskIndex].markAsNotDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + tasks[taskIndex]);
        }
    }

    private static int getTaskIndex(String command, String commandPrefix) {
        int taskNumber = Integer.parseInt(command.substring(commandPrefix.length()));
        return taskNumber - 1;
    }

    private static String getArgument(String command, String commandPrefix) {
        return command.substring(commandPrefix.length());
    }

    private static Deadline createDeadline(String command) {
        int byIndex = command.indexOf(BY_SEPARATOR);
        String description = command.substring(DEADLINE_COMMAND_PREFIX.length(), byIndex);
        String by = command.substring(byIndex + BY_SEPARATOR.length());
        return new Deadline(description, by);
    }

    private static Event createEvent(String command) {
        int fromIndex = command.indexOf(FROM_SEPARATOR);
        int toIndex = command.indexOf(TO_SEPARATOR);
        String description = command.substring(EVENT_COMMAND_PREFIX.length(), fromIndex);
        String from = command.substring(fromIndex + FROM_SEPARATOR.length(), toIndex);
        String to = command.substring(toIndex + TO_SEPARATOR.length());
        return new Event(description, from, to);
    }

    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
