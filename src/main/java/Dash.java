import java.util.Scanner;

public class Dash {
    public static void main(String[] args) {
        String banner =
                " ____              _     \n"
                        + "|  _ \\  __ _ ___| |__  \n"
                        + "| | | |/ _` / __| '_ \\ \n"
                        + "| |_| | (_| \\__ \\ | | |\n"
                        + "|____/ \\__,_|___/_| |_|\n";

        System.out.println(banner);

        String line = "____________________________________________________________";

        System.out.println(line);
        System.out.println("Hello! I'm Dash.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (true) {
            String command = scanner.nextLine();
            System.out.println(line);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = isDone[i] ? "[X]" : "[ ]";
                    System.out.println(" " + (i + 1) + "." + status + " " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;
                isDone[taskIndex] = true;
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   [X] " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(line);
        }
    }
}
