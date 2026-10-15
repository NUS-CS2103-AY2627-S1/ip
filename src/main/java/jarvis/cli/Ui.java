package jarvis.cli;

import java.util.List;
import java.util.Scanner;

import jarvis.classes.Task;

/** Handles terminal input and output for the Jarvis cli. */
public class Ui {
    // String BANNER art for Jarvis
    private static final String BANNER =
            "     ██╗ █████╗ ██████╗ ██╗   ██╗██╗███████╗\n"
            + "     ██║██╔══██╗██╔══██╗██║   ██║██║██╔════╝\n"
            + "     ██║███████║██████╔╝██║   ██║██║███████╗\n"
            + "██   ██║██╔══██║██╔══██╗╚██╗ ██╔╝██║╚════██║\n"
            + "╚█████╔╝██║  ██║██║  ██║ ╚████╔╝ ██║███████║\n"
            + " ╚════╝ ╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚═╝╚══════╝\n";
    /** String message that creates a border. */
    private static final String BORDER_LINE = "---------------------------------------------------------------------\n";
    /** Preset message to greet the user when the program starts. */
    private static final String INITIAL_MESSAGE = BORDER_LINE + BANNER
            + "Good day Sir/Ma' am, I am Jarvis, your friendly AI assistant\n"
            + "How may I be of service to you today ?\n" + BORDER_LINE;
    /** Preset String message to say goodbye to the user when 'bye' is inputted into terminal. */
    private static final String EXIT_MESSAGE = "Goodbye Sir/Ma' am. I hope to be of service to you again next time\n"
            + BORDER_LINE;
    /** Preset message referencing a famous Jarvis meme. */
    private static final String CLIP_THAT_MESSAGE = "Clipped and Ready to ship Sir\n" + BORDER_LINE;
    /** Preset String message in response to any unknown commands that user inputs. */
    private static final String UNKNOWN_COMMAND_MESSAGE = "Apologies Sir/Ma' am.\n"
            + "I do not understand your command, please retry with a valid command\n" + BORDER_LINE;
    /** Creates a new Scanner instance to allow program to read user input from the terminal. */
    private final Scanner scanner;

    /** Creates a UI instance. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return User command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Print the welcome message. */
    public void showWelcome() {
        System.out.println(INITIAL_MESSAGE);
    }

    /** Print the goodbye message. */
    public void sayGoodbye() {
        System.out.println(EXIT_MESSAGE);
    }

    /** Print all tasks. */
    public void listAllTasks(List<Task> taskList) {
        System.out.println("\nHere are the list of things you had wished to do earlier Sir/Ma' am\n");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, taskList.get(i).toString());
        }
        System.out.println(BORDER_LINE);
    }

    /** Prints the response for the special clip-that command. */
    public void showClipThat() {
        System.out.println(CLIP_THAT_MESSAGE);
    }

    /** Prints the response for an unrecognized command. */
    public void showUnknownCommand() {
        System.out.println(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Prints an error message.
     *
     * @param message Error message to print.
     */
    public void showError(String message) {
        System.err.println(message);
    }
}
