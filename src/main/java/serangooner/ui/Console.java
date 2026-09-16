package serangooner.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Deals with everything the user reads and types on the command line.
 * The words themselves come from {@link Ui}; this class only decides how they
 * reach a terminal, which is why the banner and the dividers that frame an
 * exchange live here rather than there.
 */
public class Console {
    private static final String DIVIDER = "━━━ . °‧ 𓆝 𓆟 𓆞 ·｡";
    private static final String BANNER =
            "  ____   U _____ u   ____        _      _   _     ____"
                    + "    U  ___ u   U  ___ u  _   _   U _____ u   ____     \n"
                    + " / __\"| u\\| ___\"|/U |  _\"\\ u U  /\"\\  u | \\ |\"| U /\"___"
                    + "|u   \\/\"_ \\/    \\/\"_ \\/ | \\ |\"|  \\| ___\"|/U |  _\"\\ u  \n"
                    + "<\\___ \\/  |  _|\"   \\| |_) |/  \\/ _ \\/ <|  \\| |>\\| |  _"
                    + " /   | | | |    | | | |<|  \\| |>  |  _|\"   \\| |_) |/  \n"
                    + " u___) |  | |___    |  _ <    / ___ \\ U| |\\  |u | |_| "
                    + "|.-,_| |_| |.-,_| |_| |U| |\\  |u  | |___    |  _ <    \n"
                    + " |____/>> |_____|   |_| \\_\\  /_/   \\_\\ |_| \\_|   \\____"
                    + "| \\_)-\\___/  \\_)-\\___/  |_| \\_|   |_____|   |_| \\_\\   \n"
                    + "  )(  (__)<<   >>   //   \\\\_  \\\\    >> ||   \\\\,-._)(|_"
                    + "       \\\\         \\\\    ||   \\\\,-.<<   >>   //   \\\\_  \n"
                    + " (__)    (__) (__) (__)  (__)(__)  (__)(_\")  (_/(__)__"
                    + ")     (__)       (__)   (_\")  (_/(__) (__) (__)  (__)  \n";

    private final Scanner scanner;
    private final PrintStream out;
    private final SoundPlayer soundPlayer;

    /**
     * Constructs a console that reads from and writes to the standard streams.
     */
    public Console() {
        this(System.in, System.out, true);
    }

    /**
     * Constructs a console that reads from and writes to the given streams,
     * without the sound that accompanies an error. Intended for tests, which
     * have no one listening and no terminal to read from.
     *
     * @param in Stream that commands are read from.
     * @param out Stream that messages are written to.
     */
    public Console(InputStream in, PrintStream out) {
        this(in, out, false);
    }

    private Console(InputStream in, PrintStream out, boolean isSoundEnabled) {
        this.scanner = new Scanner(in);
        this.out = out;
        this.soundPlayer = new SoundPlayer(isSoundEnabled);
    }

    /**
     * Returns whether the user has entered another command.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the name the chatbot opens with.
     */
    public void showBanner() {
        out.println(BANNER);
        showDivider();
    }

    /**
     * Shows the given message on its own.
     *
     * @param message Words to show.
     */
    public void show(String message) {
        out.println(message);
    }

    /**
     * Shows the given message followed by a divider, and shows nothing at all
     * when there is nothing worth saying.
     *
     * @param message Words to show, which may be empty.
     */
    public void showBlock(String message) {
        if (message.isEmpty()) {
            return;
        }
        show(message);
        showDivider();
    }

    /**
     * Shows the given error to the user, with a sound to match.
     *
     * @param message Explanation of what went wrong.
     */
    public void showError(String message) {
        soundPlayer.play();
        show(message);
    }

    /**
     * Draws the line that separates one exchange from the next.
     */
    public void showDivider() {
        out.println(DIVIDER);
    }
}
