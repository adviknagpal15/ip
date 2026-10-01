package dash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
    private static final String DELETE_COMMAND = "delete";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";
    private static final String DATA_FILE_PATH = "./data/dash.txt";

    private final Ui ui;
    private final ArrayList<Task> tasks;

    /**
     * Initializes the chatbot and loads existing tasks from storage.
     */
    public Dash() {
        this.ui = new Ui();
        this.tasks = loadTasks();
    }

    /**
     * Starts the chatbot and processes commands until the user enters {@code bye}.
     */
    public void run() {
        ui.showWelcome();

        while (true) {
            String command = ui.readCommand();
            ui.showLine();

            if (command.equals(BYE_COMMAND)) {
                ui.showGoodbye();
                ui.showLine();
                break;
            }

            try {
                handleCommand(command);
            } catch (DashException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Starts the application.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Dash().run();
    }

    private void handleCommand(String command) throws DashException {
        String[] parts = command.split(" ", 2);
        String keyword = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";

        if (keyword.equals(LIST_COMMAND)) {
            ui.showTaskList(tasks);
        } else if (keyword.equals(TODO_COMMAND)) {
            addTask(createTodo(arguments));
        } else if (keyword.equals(DEADLINE_COMMAND)) {
            addTask(createDeadline(arguments));
        } else if (keyword.equals(EVENT_COMMAND)) {
            addTask(createEvent(arguments));
        } else if (keyword.equals(MARK_COMMAND)) {
            markTask(arguments, true);
        } else if (keyword.equals(UNMARK_COMMAND)) {
            markTask(arguments, false);
        } else if (keyword.equals(DELETE_COMMAND)) {
            deleteTask(arguments);
        } else {
            throw new DashException(
                    "I don't recognize that command. Try list, todo, deadline, event, mark, unmark, delete, or bye.");
        }
    }

    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        saveTasks();
    }

    private void deleteTask(String arguments) throws DashException {
        int taskIndex = getTaskIndex(arguments, tasks.size(), DELETE_COMMAND);
        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        saveTasks();
    }

    private void markTask(String arguments, boolean isDone) throws DashException {
        int taskIndex = getTaskIndex(arguments, tasks.size(), isDone ? MARK_COMMAND : UNMARK_COMMAND);
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
            ui.showTaskMarked(task);
        } else {
            task.markAsNotDone();
            ui.showTaskUnmarked(task);
        }
        saveTasks();
    }

    private int getTaskIndex(String arguments, int taskCount, String command) throws DashException {
        if (arguments.isBlank()) {
            if (command.equals(DELETE_COMMAND)) {
                throw new DashException("Please give the task number to delete. Try: delete 1");
            }
            throw new DashException("Please give the task number to mark or unmark. Try: mark 1");
        }
        try {
            int taskNumber = Integer.parseInt(arguments.trim());
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new DashException("There is no task numbered " + taskNumber + ".");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            if (command.equals(DELETE_COMMAND)) {
                throw new DashException("That is not a valid task number. Try something like: delete 1");
            }
            throw new DashException("That is not a valid task number. Try something like: mark 1");
        }
    }

    private Todo createTodo(String arguments) throws DashException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new DashException("A to-do needs a description. Try: todo borrow book");
        }
        return new Todo(description);
    }

    private Deadline createDeadline(String arguments) throws DashException {
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

    private Event createEvent(String arguments) throws DashException {
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

    private ArrayList<Task> loadTasks() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        Path path = Paths.get(DATA_FILE_PATH);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTaskFromFile(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException exception) {
            ui.showLoadingError(exception.getMessage());
        }
        return loadedTasks;
    }

    private Task parseTaskFromFile(String line) {
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

    private void saveTasks() {
        try {
            Path path = Paths.get(DATA_FILE_PATH);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileFormat());
            }
            Files.write(path, lines);
        } catch (IOException exception) {
            ui.showSavingError(exception.getMessage());
        }
    }
}
