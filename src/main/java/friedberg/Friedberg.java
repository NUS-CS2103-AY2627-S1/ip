package friedberg;

import java.util.Scanner;

import exception.FriedbergException;
import parser.ArchiveCommand;
import parser.Command;
import parser.CommandContext;
import parser.CommandParser;
import parser.DeadlineCommand;
import parser.DeleteCommand;
import parser.EventCommand;
import parser.MarkCommand;
import parser.TodoCommand;
import parser.UnMarkCommand;
import parser.UnarchiveCommand;

/**
 * Main Friedberg Chatbot.
 */
public class Friedberg {
    private static final String NAME = "Friedberg";
    private static final String TERMINAL_SEPARATOR = "____________________________________________________________";
    private static final String BANNER = "______     _          _ _                   \n"
            + "|  ___|   (_)        | | |                  \n"
            + "| |_ _ __ _  ___  __| | |__   ___ _ __ __ _ \n"
            + "|  _| '__| |/ _ \\/ _` | '_ \\ / _ \\ '__/ _` |\n"
            + "| | | |   | |  __/ (_| | |_) |  __/ | | (_| |\n"
            + "\\_| |_|   |_|\\___|\\__,_|_.__/ \\___|_|  \\__, |\n"
            + "                                        __/ |\n"
            + "                                       |___/ \n";

    private final CommandContext commandContext;

    /**
     * Creates a Friedberg chatbot with its session-long command context.
     *
     * @throws FriedbergException if stored task data cannot be loaded
     */
    public Friedberg() throws FriedbergException {
        this.commandContext = new CommandContext();
    }

    /**
     * Creates a Friedberg chatbot with an injected command context.
     * This keeps tests away from the real data directory.
     *
     * @param commandContext command context to use for this session
     */
    public Friedberg(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    /**
     * Runs the chatbot Friedberg in the CLI.
     */
    public void run() {
        this.greet();
        this.command();
    }

    /**
     * Returns the greeting text shared by frontends.
     *
     * @return greeting text without terminal decoration
     */
    public String getGreeting() {
        return "Hello! I'm " + Friedberg.NAME + ".\nI am a chatbot beep boop, what can I do for you?\n"
                + "Type help to show all available commands.";
    }

    /**
     * Prints the startup banner and greeting.
     */
    public void greet() {
        System.out.println(BANNER);
        System.out.println(this.getGreeting());
        System.out.println(TERMINAL_SEPARATOR);
        System.out.println();
    }

    /**
     * Processes one user input and returns a displayable result for any frontend.
     * This method does not read stdin, print output, or update UI controls.
     *
     * @param input raw input entered by the user
     * @return command message and whether the frontend should end the session
     */
    public CommandResult processInput(String input) {
        String userInput = input;
        try {
            Command command = CommandParser.parse(userInput);
            String message = command.execute(userInput, this.commandContext);
            assert message != null : "A successful command must return a response";
            assert !message.isBlank() : "A successful command must return a nonblank response";
            return new CommandResult(message, command.isBye(), this.classifyResponse(command));
        } catch (FriedbergException e) {
            return new CommandResult(String.format("User Error using Friedberg: %s", e.getMessage()), false,
                    ResponseType.ERROR);
        }
    }

    /**
     * Classifies a command result for GUI styling without changing command behavior.
     *
     * @param command successfully parsed and executed command
     * @return semantic response type for the command
     */
    private ResponseType classifyResponse(Command command) {
        if (command instanceof TodoCommand || command instanceof DeadlineCommand || command instanceof EventCommand) {
            return ResponseType.ADD;
        }
        if (command instanceof MarkCommand || command instanceof UnMarkCommand) {
            return ResponseType.STATUS;
        }
        boolean isRemoveCommand = command instanceof DeleteCommand || command instanceof ArchiveCommand
                || command instanceof UnarchiveCommand;
        if (isRemoveCommand) {
            return ResponseType.REMOVE;
        }
        return ResponseType.DEFAULT;
    }

    /**
     * Reads and executes user commands until an exit command is received.
     */
    public void command() {
        Scanner stdin = new Scanner(System.in);
        boolean isBye = false;
        while (!isBye) {
            String userInput = stdin.nextLine();
            System.out.println(TERMINAL_SEPARATOR);
            CommandResult result = this.processInput(userInput);
            System.out.println(result.message());
            isBye = result.shouldExit();
            if (!isBye) {
                System.out.println(TERMINAL_SEPARATOR);
                System.out.println();
            }
        }
        stdin.close();
    }
}
