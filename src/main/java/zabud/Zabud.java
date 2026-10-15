package zabud;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import zabud.commands.Parser;
import zabud.session.Session;

/**
 * Runs Zabud, a command-line assistant that stores tasks for the current session.
 */
public class Zabud {
    /**
     * The line printed between sections of the command-line interface.
     */
    private static final String SEPARATOR = "____________________________________________________________";

    /**
     * Session shared by commands submitted through the GUI.
     */
    private final Session session;

    /**
     * Creates a Zabud instance using the default session storage path.
     */
    public Zabud() {
        this(new Session());
    }

    /**
     * Creates a Zabud instance backed by the supplied session.
     *
     * @param session session used to execute commands.
     */
    Zabud(Session session) {
        this.session = session;
    }

    /**
     * Starts Zabud and delegates each line of input to the command dispatcher.
     *
     * @param args command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Session session = new Session();
        printWelcome();

        if (System.console() == null) {
            runPipedInput(session);
        } else {
            runInteractiveInput(session);
        }
    }

    /**
     * Executes one GUI command and returns the text that the existing command layer prints.
     *
     * @param input command entered by the user.
     * @return command output and whether the application should remain open.
     */
    public synchronized Response getResponse(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        boolean shouldContinue;
        boolean isError = !Parser.isValid(Parser.parse(input), session);
        // ponytail: single-threaded GUI adapter; return messages directly if command execution becomes concurrent.
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            session.recordCommand(input);
            shouldContinue = Parser.invoke(input, session);
        } finally {
            System.setOut(originalOutput);
        }
        return new Response(output.toString(StandardCharsets.UTF_8).stripTrailing(), shouldContinue, isError);
    }

    /**
     * Processes line-oriented input used by tests, pipes, and redirected input.
     */
    private static void runPipedInput(Session session) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                session.recordCommand(line);
                if (!Parser.invoke(line, session)) {
                    break;
                }
            }
        } catch (IOException exception) {
            System.out.println(" I could not read input: " + exception.getMessage());
        }
    }

    /**
     * Runs a small raw-mode line editor with history and escape-key controls.
     */
    private static void runInteractiveInput(Session session) {
        setTerminalMode(false);
        try {
            Reader reader = new InputStreamReader(System.in);
            StringBuilder current = new StringBuilder();
            System.out.print("> ");
            int character;
            while ((character = reader.read()) != -1) {
                if (character == '\n' || character == '\r') {
                    String line = current.toString();
                    System.out.print("\n" + SEPARATOR + "\n");
                    session.recordCommand(line);
                    if (!Parser.invoke(line, session)) {
                        break;
                    }
                    current.setLength(0);
                    System.out.print("> ");
                } else if (character == 27) {
                    handleEscape(reader, current, session);
                } else if (character == 127 || character == 8) {
                    if (current.length() > 0) {
                        current.deleteCharAt(current.length() - 1);
                        redraw(current);
                    }
                } else if (!Character.isISOControl(character)) {
                    current.append((char) character);
                    System.out.print((char) character);
                }
            }
        } catch (IOException exception) {
            System.out.println("\n I could not read input: " + exception.getMessage());
        } finally {
            setTerminalMode(true);
        }
    }

    /**
     * Interprets arrow-key history navigation or clears the line for a bare escape.
     */
    private static void handleEscape(Reader reader, StringBuilder current, Session session) throws IOException {
        if (!reader.ready()) {
            current.setLength(0);
            session.resetHistoryNavigation();
            redraw(current);
            return;
        }
        int bracket = reader.read();
        if (bracket != '[' || !reader.ready()) {
            current.setLength(0);
            session.resetHistoryNavigation();
            redraw(current);
            return;
        }
        int direction = reader.read();
        if (direction == 'A') {
            current.replace(0, current.length(), session.previousCommand());
        } else if (direction == 'B') {
            current.replace(0, current.length(), session.nextCommand());
        } else {
            current.setLength(0);
            session.resetHistoryNavigation();
        }
        redraw(current);
    }

    /**
     * Replaces the visible input line with the current editor value.
     */
    private static void redraw(StringBuilder current) {
        System.out.print("\r\033[2K> " + current);
    }

    /**
     * Switches the terminal between canonical and raw input modes when available.
     */
    private static void setTerminalMode(boolean shouldRestore) {
        try {
            new ProcessBuilder("sh", "-c", shouldRestore ? "stty sane" : "stty -icanon -echo")
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (IOException exception) {
            // Raw terminal mode is optional; piped and GUI input remain usable.
        }
    }

    /**
     * Prints the welcome banner and greeting.
     */
    private static void printWelcome() {
        String banner = "███████╗  █████╗  ██████╗  ██╗   ██╗ ██████╗\n"
                + "╚══███╔╝ ██╔══██╗ ██╔══██╗ ██║   ██║ ██╔══██╗\n"
                + "  ███╔╝  ███████║ ██████╔╝ ██║   ██║ ██║  ██║\n"
                + " ███╔╝   ██╔══██║ ██╔══██╗ ██║   ██║ ██║  ██║\n"
                + "███████╗ ██║  ██║ ██████╔╝ ╚██████╔╝ ██████╔╝\n"
                + "╚══════╝ ╚═╝  ╚═╝ ╚═════╝   ╚═════╝  ╚═════╝";
        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.println("Hello, King Solomon! I'm Zabud, your royal assistant.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Result returned to the GUI after executing a command.
     *
     * @param message text generated by the command.
     * @param shouldContinue whether the application should continue accepting commands.
     * @param isError whether the user's command was invalid.
     */
    public record Response(String message, boolean shouldContinue, boolean isError) {
    }
}
