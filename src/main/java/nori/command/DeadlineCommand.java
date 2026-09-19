package nori.command;

import java.util.Optional;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.Deadline;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Adds a deadline task.
 */
class DeadlineCommand extends AddTaskCommand {
    /** Shown alongside every complaint, so a correction is always in view. */
    private static final String DEADLINE_EXAMPLE = "\"deadline submit report /by 2019-10-15\"";

    /**
     * Creates a command that adds a deadline.
     *
     * @param details the description and due date after the {@code deadline} keyword.
     */
    DeadlineCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        CommandOptions options = CommandOptions.parse(details);
        Optional<String> formatError = findFormatError(options);
        if (formatError.isPresent()) {
            ui.showError(formatError.get());
            return;
        }
        addTask(tasks, ui, storage, new Deadline(options.getDescription(),
                Deadline.parseInput(options.getValue(CommandOptions.OPTION_BY))));
    }

    /**
     * Reports the first fault in this command's details.
     *
     * @param options the options read from the details.
     * @return the correction to show the user, or empty when a deadline can be built.
     */
    private Optional<String> findFormatError(CommandOptions options) {
        Optional<String> unexpectedOption = options.findUnexpectedOption(CommandOptions.OPTION_BY);
        if (unexpectedOption.isPresent()) {
            return Optional.of("NOOT?! A deadline does not use \"" + unexpectedOption.get() + "\"."
                    + " Try " + DEADLINE_EXAMPLE + ".");
        }
        Optional<String> repeatedOption = options.findRepeatedOption();
        if (repeatedOption.isPresent()) {
            return Optional.of("NOOT?! A deadline takes only one \"/by\"."
                    + " Try " + DEADLINE_EXAMPLE + ".");
        }
        if (!options.hasOption(CommandOptions.OPTION_BY)) {
            return Optional.of("NOOT?! I cannot find the \"/by\" part of that deadline."
                    + " Use " + DEADLINE_EXAMPLE + ".");
        }
        if (options.getDescription().isEmpty()) {
            return Optional.of("NOOT?! A deadline needs a description before \"/by\"."
                    + " Try " + DEADLINE_EXAMPLE + ".");
        }
        if (options.getValue(CommandOptions.OPTION_BY).isEmpty()) {
            return Optional.of("NOOT?! A deadline needs a due date after \"/by\"."
                    + " Try " + DEADLINE_EXAMPLE + ".");
        }
        return Optional.empty();
    }
}
