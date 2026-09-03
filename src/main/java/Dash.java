import java.util.Scanner;

/**
 * A chatbot that echoes commands until the user says goodbye.
 */
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
        while (true) {
            String command = scanner.nextLine();
            System.out.println(line);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            System.out.println(" " + command);
            System.out.println(line);
        }
    }
}
