import java.util.Scanner;

public class Mona {
    private final static String separator = "____________________________________________________________";
    private final static String banner = " __  __  ___  _   _    _ \n"
            + "|  \\/  |/ _ \\| \\ | |  / \\\n"
            + "| |\\/| | | | |  \\| | / _ \\\n"
            + "| |  | | |_| | |\\  |/ ___ \\\n"
            + "|_|  |_|\\___/|_| \\_/_/   \\_\\\n";

    private static void printFormatted(String text) {
        System.out.println(separator);
        System.out.println(text);
        System.out.println(separator);
    }

    public static void main(String[] args) {
        printFormatted(banner + "\n" + "Hello! I'm Mona.\nWhat can I do for you?");

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("Mona > ");
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "bye":
                    printFormatted("Bye. Hope to see you again soon!");
                    exit = true;
                    break;
                default:
                    printFormatted(userInput);
            }
        }
    }
}
