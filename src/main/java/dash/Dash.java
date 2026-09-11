package dash;

import java.util.Scanner;

/**
 * Runs the Dash command-line chatbot.
 */
public class Dash {
    private static final String LIST_COMMAND = "list";
    private static final String BYE_COMMAND = "bye";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
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

            try {
                taskCount = handleCommand(command, tasks, taskCount);
            } catch (DashException exception) {
                System.out.println(" " + exception.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }

    private static int handleCommand(String command, Task[] tasks, int taskCount) throws DashException {
        String[] parts = command.split(" ", 2);
        String keyword = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";

        if (keyword.equals(LIST_COMMAND)) {
            printTaskList(tasks, taskCount);
        } else if (keyword.equals(TODO_COMMAND)) {
            taskCount = addTask(tasks, taskCount, createTodo(arguments));
        } else if (keyword.equals(DEADLINE_COMMAND)) {
            taskCount = addTask(tasks, taskCount, createDeadline(arguments));
        } else if (keyword.equals(EVENT_COMMAND)) {
            taskCount = addTask(tasks, taskCount, createEvent(arguments));
        } else if (keyword.equals(MARK_COMMAND)) {
            markTask(tasks, taskCount, arguments, true);
        } else if (keyword.equals(UNMARK_COMMAND)) {
            markTask(tasks, taskCount, arguments, false);
        } else {
            throw new DashException(
                    "I don't recognize that command. Try list, todo, deadline, event, mark, unmark, or bye.");
        }
        return taskCount;
    }

    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) throws DashException {
        if (taskCount >= TASK_LIMIT) {
            throw new DashException("The task list is full. I can only keep " + TASK_LIMIT + " tasks.");
        }
        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        printTaskAdded(task, newTaskCount);
        return newTaskCount;
    }

    private static void markTask(Task[] tasks, int taskCount, String arguments, boolean isDone) throws DashException {
        int taskIndex = getTaskIndex(arguments, taskCount);
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

    private static int getTaskIndex(String arguments, int taskCount) throws DashException {
        if (arguments.isBlank()) {
            throw new DashException("Please give the task number to mark or unmark. Try: mark 1");
        }
        try {
            int taskNumber = Integer.parseInt(arguments.trim());
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new DashException("There is no task numbered " + taskNumber + ".");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new DashException("That is not a valid task number. Try something like: mark 1");
        }
    }

    private static Todo createTodo(String arguments) throws DashException {
        if (arguments.isBlank()) {
            throw new DashException("A to-do needs a description. Try: todo borrow book");
        }
        return new Todo(arguments);
    }

    private static Deadline createDeadline(String arguments) throws DashException {
        int byIndex = arguments.indexOf(BY_SEPARATOR);
        if (byIndex == -1) {
            if (arguments.startsWith("/by")) {
                throw new DashException("A deadline needs a description before /by.");
            }
            throw new DashException("A deadline needs a /by time. Try: deadline return book /by Sunday");
        }
        String description = arguments.substring(0, byIndex);
        String by = arguments.substring(byIndex + BY_SEPARATOR.length());
        if (description.isBlank()) {
            throw new DashException("A deadline needs a description before /by.");
        }
        if (by.isBlank()) {
            throw new DashException("A deadline needs a /by time. Try: deadline return book /by Sunday");
        }
        return new Deadline(description, by);
    }

    private static Event createEvent(String arguments) throws DashException {
        int fromIndex = arguments.indexOf(FROM_SEPARATOR);
        int toIndex = arguments.indexOf(TO_SEPARATOR);
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new DashException(
                    "An event needs /from and /to times. Try: event meeting /from Mon 2pm /to 4pm");
        }
        String description = arguments.substring(0, fromIndex);
        String from = arguments.substring(fromIndex + FROM_SEPARATOR.length(), toIndex);
        String to = arguments.substring(toIndex + TO_SEPARATOR.length());
        if (description.isBlank()) {
            throw new DashException("An event needs a description before /from.");
        }
        if (from.isBlank() || to.isBlank()) {
            throw new DashException(
                    "An event needs /from and /to times. Try: event meeting /from Mon 2pm /to 4pm");
        }
        return new Event(description, from, to);
    }

    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
