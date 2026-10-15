package clover.command;

import java.time.LocalDate;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.parser.TaskArguments;
import clover.storage.Storage;
import clover.task.Deadline;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Adds a deadline task with a description and due date.
 */
public class DeadlineCommand extends Command {
    private static final String DEADLINE_MARKER = "/by";

    private final String arguments;

    /**
     * Creates a command from the text after the {@code deadline} keyword.
     */
    public DeadlineCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Validates, adds, and saves the deadline task.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException {
        Parser.rejectUnknownParameters(arguments, "/by", "/for");
        TaskArguments parsedArguments = Parser.parseTaskArguments(arguments);
        String taskArguments = parsedArguments.getTaskArguments();
        int markerIndex = Parser.findSingleMarker(taskArguments, DEADLINE_MARKER);
        if (markerIndex <= 0) {
            throw invalidFormat();
        }

        String description = taskArguments.substring(0, markerIndex).trim();
        String dueDate = taskArguments.substring(markerIndex + DEADLINE_MARKER.length()).trim();
        if (description.isEmpty() || dueDate.isEmpty()) {
            throw invalidFormat();
        }

        LocalDate date = Parser.parseDate(dueDate);
        String tutoreeName = validateTutoreeName(parsedArguments.getTutoreeName(), tutorees);
        Deadline task = new Deadline(description, date, tutoreeName);
        if (tasks.containsEquivalent(task)) {
            throw new CloverException("That study quest is already in the grove.");
        }
        tasks.add(task);
        try {
            saveTasks(tasks, storage);
        } catch (CloverException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public CommandResponseStyle getResponseStyle() {
        return CommandResponseStyle.TASK_ADDED;
    }

    /** Creates the shared message for malformed deadline commands. */
    private CloverException invalidFormat() {
        return new CloverException("To set a deadline, use: deadline DESCRIPTION /by DUE DATE. Optional: add "
                + "/for TUTOREE NAME.");
    }
}
