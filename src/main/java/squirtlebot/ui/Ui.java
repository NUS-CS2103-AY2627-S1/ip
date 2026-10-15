package squirtlebot.ui;

import java.util.Scanner;

/**
 * Handles user interaction such as reading input and printing messages.
 */
public class Ui {
    private static final String HORIZONTAL_LINE = "\t_____________________________________________________________";
    private String savedMessage;


    /**
     * Constructs a new Ui.
     */
    public Ui() {
        savedMessage = "";
    }

    /**
     * Prints welcome banner for SquirtleBot.
     * Intended for use when operating in CLI-mode.
     */
    public void printBanner() {
        String banner = """
                                           16600000661                    \s
                                    4009000000000000000000001             \s
                                7001   60000800000000000001               \s
                              90  0000000  0000000800000 1000000006       \s
                            60 700000000000 00800000006 0000000000000     \s
                           00 00  000000000 1080000000 000000000   000    \s
                          80 00   0000000004 080000880 0000000000  0000   \s
                         869 00000000000000 7080000000 0000000000000000   \s
                        6660 00000000000000 00800000000 000000000000000   \s
                        06661 00000000000  0000000000000 0000000000000    \s
                       6666668   0000    00000000000000005 0000000000     \s
                       666666666660800000000   780005    000         06   \s
                       66666666656008880000               0800000000002   \s
                       19666666628000000800               0808000000801   \s
                        06466666560800000000 3333333337  0000000000000    \s
                         066666664000000000007  73337   00000000000806    \s
                          0666666560080000000000096600000000000000000     \s
                           69666664400000000008000000088000000000006      \s
                             0866664300000000000888880000000080000        \s
                       16886    08966660000000000000000000000006          \s
                    90966666606     769860000000000000000003              \s
                   0666666666660  6006                      00            \s
                  79666669666660 90006  900 888888883888888  001          \s
                  16656  1  168  0000 1 00 88888888838888888 000          \s
                   066666660 79 0008   80  08888888338888888 4000         \s
                   186666660  0     73 00 333333333333333388  0           \s
                     080800  00000  33 00 000000000858000000              \s
                """;

        System.out.println(Ui.HORIZONTAL_LINE);
        System.out.println(banner);
        System.out.println("Hello! I'm SquirtleBot :)");
        System.out.println(Ui.HORIZONTAL_LINE);
        System.out.println("\tWhat can I do for you? ");
    }

    /**
     * Updates saved message.
     *
     * @param savedMessage new message to be saved.
     */
    public void setSavedMessage(String savedMessage) {
        this.savedMessage = savedMessage;
    }

    /**
     * Displays the saved message between 2 horizontal lines.
     */
    public void printSavedMessage() {
        System.out.println(Ui.HORIZONTAL_LINE);
        System.out.println(savedMessage);
        System.out.println(Ui.HORIZONTAL_LINE);
    }

    /**
     * Returns saved message with leading and trailing whitespace removed.
     *
     * @return previously saved message.
     */
    public String getSavedMessage() {
        return savedMessage.trim();
    }

    /**
     * Reads the next line of user input.
     *
     * @return string containing user's input.
     */
    public String readInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }


    /**
     * Returns the welcome message used for GUI mode of operation.
     */
    public String getGuiWelcomeMessage() {
        return ("Hello! I'm SquirtleBot :) \nWhat can I do for you?");
    }
}
