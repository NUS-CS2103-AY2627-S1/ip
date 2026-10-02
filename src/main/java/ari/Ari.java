package ari;

import java.io.IOException;

import ari.exception.EmptyArgumentException;
import ari.exception.TaskNotFoundException;
import ari.storage.Storage;
import ari.task.Task;
import ari.task.TaskList;
import ari.ui.Ui;

/**
 * Runs the Ari task manager.
 */
public class Ari {
    private static final String PROTECTED_MESSAGE =
            "Storage is protected: tasks were not loaded. Repair the data file and restart Ari.";

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private boolean hasStarted;
    private boolean isStorageReady;

    /**
     * Creates Ari with the components needed to run the task manager.
     *
     * @param filePath Path of the task data file.
     */
    public Ari(String filePath) {
        this(new Storage(filePath));
    }

    /**
     * Creates Ari with replaceable storage for deterministic persistence tests.
     *
     * @param storage Persistence component used for loading and committing changes.
     */
    public Ari(Storage storage) {
        this.storage = storage;
        this.tasks = new TaskList();
        this.ui = new Ui();
        this.hasStarted = false;
    }

    /**
     * Starts Ari and processes commands until the user exits.
     */
    public void run() {
        this.ui.showWelcome();
        this.ui.showMessage(start());

        while (true) {
            String input = this.ui.readCommand();
            if (input == null) {
                break;
            }
            CommandType command = Parser.parseCommandType(input);
            CommandResult result = processCommand(input);
            if (!result.isError() && shouldShowDivider(command)) {
                this.ui.showMessageWithLines(result.message());
            } else {
                this.ui.showMessage(result.message());
            }
            if (result.shouldExit()) {
                break;
            }
        }

        this.ui.close();
    }

    /**
     * Initializes storage and loads saved tasks once.
     *
     * @return Message describing the result of loading saved tasks.
     */
    public String start() {
        if (this.hasStarted) {
            return this.isStorageReady ? "Ari is ready!" : PROTECTED_MESSAGE;
        }
        this.hasStarted = true;

        try {
            this.storage.start();
        } catch (IOException e) {
            return "Sorry, I couldn't initialize the storage file: " + e.getMessage() + "\n" + PROTECTED_MESSAGE;
        }

        try {
            boolean isDataFilePresent = this.storage.loadInto(this.tasks);
            this.isStorageReady = true;
            if (isDataFilePresent) {
                return "All tasks were loaded!";
            }
            return "There are no saved tasks yet!";
        } catch (IOException | IllegalArgumentException e) {
            return "Sorry, I couldn't load your tasks: " + e.getMessage() + "\n" + PROTECTED_MESSAGE;
        }
    }

    /**
     * Processes one user command and returns Ari's response.
     *
     * @param input Full command entered by the user.
     * @return Ari's response to the command.
     */
    public String getResponse(String input) {
        return processCommand(input).message().stripTrailing();
    }

    /** Returns whether startup failed and the session must not change stored data. */
    public boolean isStorageProtected() {
        return this.hasStarted && !this.isStorageReady;
    }

    /**
     * Validates and processes a command. Mutations become visible only after persistence succeeds.
     *
     * @param input Full command entered by the user.
     * @return Explicit success, error and exit information for either user interface.
     */
    public CommandResult processCommand(String input) {
        if (!this.hasStarted) {
            start();
        }
        CommandType command = Parser.parseCommandType(input);
        try {
            Parser.validateCommand(input, command);
            if (command == CommandType.EXIT || command == CommandType.BYE) {
                String message = this.isStorageReady
                        ? "All accepted changes are saved."
                        : "The original data file was left unchanged.";
                return new CommandResult(message + "\n" + command.getDescription(), false, true);
            }
            if (!this.isStorageReady && command != CommandType.UNKNOWN) {
                return new CommandResult(PROTECTED_MESSAGE, true, false);
            }
            if (isMutation(command)) {
                TaskList stagedTasks = this.tasks.copy();
                String response = executeCommand(input, command, stagedTasks);
                this.storage.saveFrom(stagedTasks);
                this.tasks = stagedTasks;
                return new CommandResult(response, false, false);
            }
            return new CommandResult(executeCommand(input, command, this.tasks), command == CommandType.UNKNOWN, false);
        } catch (EmptyArgumentException | TaskNotFoundException e) {
            return new CommandResult(e.getMessage(), true, false);
        } catch (NumberFormatException e) {
            return new CommandResult("Oops! You can only enter integer IDs. Try again!", true, false);
        } catch (IllegalArgumentException e) {
            return new CommandResult(e.getMessage(), true, false);
        } catch (IOException e) {
            return new CommandResult("I couldn't save this change. Nothing was changed. Check the storage location "
                    + "and try again. Details: " + e.getMessage(), true, false);
        }
    }

    /** Returns whether a command must commit a new persisted task list. */
    private boolean isMutation(CommandType command) {
        return command == CommandType.TODO || command == CommandType.DEADLINE || command == CommandType.EVENT
                || command == CommandType.MARK || command == CommandType.UNMARK || command == CommandType.DELETE
                || command == CommandType.SORT;
    }

    /**
     * Carries out a non-exit command against the task list.
     *
     * @param input Full command entered by the user.
     * @param command Parsed command type.
     * @param targetTasks Live list for reads, or an independent staged list for mutations.
     * @return Response describing the command result.
     * @throws EmptyArgumentException If a required command argument is missing.
     * @throws TaskNotFoundException If a requested task does not exist.
     */
    private String executeCommand(String input, CommandType command, TaskList targetTasks)
            throws EmptyArgumentException, TaskNotFoundException {
        switch (command) {
            case SORT:
                targetTasks.sortByDescription();
                return String.format("%s\n%s", command.getDescription(), targetTasks.toString());

            case LIST:
                return String.format(
                        "%s\n%s",
                        command.getDescription(),
                        targetTasks.toString()
                );

            case FIND:
                String keyword = Parser.parseFindKeyword(input);
                TaskList matchingTasks = targetTasks.findMatchingTasks(keyword);
                String matchingTasksText = (matchingTasks.getLength() == 0)
                        ? "No matching tasks found."
                        : matchingTasks.toString().stripTrailing();

                return String.format(
                        "%s\n%s",
                        command.getDescription(),
                        matchingTasksText
                );

            case MARK:
            case UNMARK:
                int index = Parser.parseTaskId(input);
                String changedTask = (command.equals(CommandType.MARK))
                        ? targetTasks.markTask(index)
                        : targetTasks.unmarkTask(index);

                return String.format(
                        "%s\n %s",
                        command.getDescription(),
                        changedTask
                );

            case TODO:
            case DEADLINE:
            case EVENT:
                Task addedTask = Parser.parseTask(input, command);
                targetTasks.addTask(addedTask);

                return String.format(
                        "%s\n %s\n%s",
                        command.getDescription(),
                        addedTask,
                        targetTasks.getLengthText()
                );

            case DELETE:
                int taskId = Parser.parseTaskId(input);
                String startingText = command.getDescription();
                String middleText = targetTasks.deleteTask(taskId);
                String endingText = targetTasks.getLengthText();

                if (middleText.equals("None")) {
                    startingText = "Fortunately, there was nothing to delete.";
                    middleText = "Because you've completed all your tasks!";
                    endingText = "Good job! Keep this up!";
                }

                return String.format(
                        "%s\n %s\n%s",
                        startingText,
                        middleText,
                        endingText
                );

            case UNKNOWN:
                return "Sorry, I didn't get that... Could you say something else? ^.^";

            default:
                throw new IllegalStateException("Unexpected command: " + command);
        }
    }

    /**
     * Returns whether console output for a command should be placed between dividers.
     *
     * @param command Parsed command type.
     * @return True for commands whose successful responses use dividers.
     */
    private boolean shouldShowDivider(CommandType command) {
        return command.equals(CommandType.FIND)
                || command.equals(CommandType.MARK)
                || command.equals(CommandType.UNMARK)
                || command.equals(CommandType.TODO)
                || command.equals(CommandType.DEADLINE)
                || command.equals(CommandType.EVENT)
                || command.equals(CommandType.DELETE);
    }

    /**
     * Creates and runs Ari using the default task data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Ari("data/ari.txt").run();
    }
}
