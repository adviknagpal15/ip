package dash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dash.task.Deadline;
import dash.task.Event;
import dash.task.Task;
import dash.task.Todo;

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
    private static final String DATA_FILE_PATH = "./data/dash.txt";

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
        int taskCount = loadTasks(tasks);

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
        saveTasks(tasks, newTaskCount);
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
        saveTasks(tasks, taskCount);
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
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new DashException("A to-do needs a description. Try: todo borrow book");
        }
        return new Todo(description);
    }

    private static Deadline createDeadline(String arguments) throws DashException {
        int byIndex = arguments.indexOf(BY_SEPARATOR);
        if (byIndex == -1) {
            if (arguments.startsWith("/by ") || arguments.equals("/by")) {
                throw new DashException("A deadline needs a description before /by.");
            }
            throw new DashException("A deadline needs a /by time. Try: deadline return book /by Sunday");
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new DashException("A deadline needs a description before /by.");
        }
        if (by.isEmpty()) {
            throw new DashException("A deadline needs a /by time. Try: deadline return book /by Sunday");
        }
        return new Deadline(description, by);
    }

    private static Event createEvent(String arguments) throws DashException {
        int fromIndex = arguments.indexOf(FROM_SEPARATOR);
        int toIndex = arguments.indexOf(TO_SEPARATOR);
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            if (arguments.startsWith("/from ") || arguments.equals("/from")) {
                throw new DashException("An event needs a description before /from.");
            }
            throw new DashException(
                    "An event needs /from and /to times. Try: event meeting /from Mon 2pm /to 4pm");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new DashException("An event needs a description before /from.");
        }
        if (from.isEmpty() || to.isEmpty()) {
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

    private static int loadTasks(Task[] tasks) {
        Path path = Paths.get(DATA_FILE_PATH);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return 0;
        }

        int count = 0;
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                if (count >= TASK_LIMIT) {
                    break;
                }
                Task task = parseTaskFromFile(line);
                if (task != null) {
                    tasks[count] = task;
                    count++;
                }
            }
        } catch (IOException exception) {
            System.out.println(" Error reading tasks from file: " + exception.getMessage());
        }
        return count;
    }

    private static Task parseTaskFromFile(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0].trim();
        String status = parts[1].trim();
        if (!status.equals("0") && !status.equals("1")) {
            return null;
        }
        boolean isDone = status.equals("1");
        String description = parts[2].trim();
        if (description.isEmpty()) {
            return null;
        }

        Task task;
        switch (type) {
        case "T":
            if (parts.length != 3) {
                return null;
            }
            task = new Todo(description);
            break;
        case "D":
            if (parts.length != 4) {
                return null;
            }
            String by = parts[3].trim();
            if (by.isEmpty()) {
                return null;
            }
            task = new Deadline(description, by);
            break;
        case "E":
            if (parts.length != 5) {
                return null;
            }
            String from = parts[3].trim();
            String to = parts[4].trim();
            if (from.isEmpty() || to.isEmpty()) {
                return null;
            }
            task = new Event(description, from, to);
            break;
        default:
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static void saveTasks(Task[] tasks, int taskCount) {
        try {
            Path path = Paths.get(DATA_FILE_PATH);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                lines.add(tasks[i].toFileFormat());
            }
            Files.write(path, lines);
        } catch (IOException exception) {
            System.out.println(" Error saving tasks to file: " + exception.getMessage());
        }
    }
}
