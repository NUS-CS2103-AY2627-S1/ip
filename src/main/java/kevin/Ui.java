package kevin;

import java.util.Scanner;

public class Ui {
    private String BANNER_START = "What's up? I'm Kevin, your chatbot for task management.\n\n";
    private String loadingTasksMessage = "";
    private String BANNER_END = "What would you like me to help you with?\n";

    private Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads next line as a String.
     */
    public String readNextLine() {
        return scanner.nextLine();
    }

    /**
     * Prints an output.
     */
    public void print(String output) {
        System.out.println(output);
    }

    /**
     * Stores loadingTasksMessage.
     */
    public void storeLoadingTasksMessage(String loadingTasksMessage) {
        this.loadingTasksMessage = loadingTasksMessage;
    }

    /**
     * Creates Banner to output on start.
     */
    public String createBanner() {
        String banner = BANNER_START + loadingTasksMessage;
        if (!loadingTasksMessage.isEmpty()) {
            banner += '\n';
        }
        return(banner + BANNER_END);

    }
    /**
     * Initializes UI, prints banner and reads first input.
     */
    public String start() {
        print(createBanner());
        return readNextLine();
    }
}
