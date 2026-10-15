package bob.parser;

import static bob.parser.MarkedArguments.BY_KEYWORD;
import static bob.parser.MarkedArguments.DESC_KEYWORD;
import static bob.parser.MarkedArguments.FROM_KEYWORD;
import static bob.parser.MarkedArguments.TO_KEYWORD;

import java.util.Optional;

import bob.BobException;
import bob.command.CommandWord;
import bob.command.EditCommand;
import bob.task.TaskDateTime;
import bob.task.TaskEdit;

/**
 * Parses the text following {@link CommandWord#EDIT} into the {@link EditCommand}
 * it asks for.
 *
 * <p>An edit uses the same markers as a new task, plus
 * {@value MarkedArguments#DESC_KEYWORD}, but lets them come in any order and lets
 * any of them be left out. Finding the markers is left to {@link MarkedArguments},
 * which {@link TaskParser} uses too, so a marker is recognized by the same rules in
 * both. What this class adds is which values may be missing, and which missing
 * value to report first.
 */
class EditParser {

    /**
     * Returns the {@link EditCommand} described by the text following
     * {@link CommandWord#EDIT}: a task number, then one or more markers, each
     * followed by the new value of one detail of that task.
     *
     * <p>Each value runs from its marker to whichever marker comes next in the text,
     * so the order carries no meaning that a rule about it would protect. The price,
     * shared with {@link TaskParser}, is that a new description cannot contain a
     * marker as a word of its own, though text such as {@code /tokyo} is fine.
     *
     * <p>Only what can be judged from the text is checked here. Whether the task has
     * the dates being changed, and whether an event would still end after it starts,
     * depend on the task, and are left to {@link bob.task.Task#withEdit Task.withEdit}.
     *
     * @param arguments the text following the command word.
     * @throws BobException if the task number is missing or is not a number, if the
     *                      text after it does not begin with a marker, if a marker
     *                      is written twice, in the wrong case or with nothing after
     *                      it, or if a new date is not a date.
     */
    static EditCommand parseEdit(String arguments) throws BobException {
        // The task number is everything up to the first space; the changes are the rest.
        String[] parts = arguments.split("\\s+", 2);
        int taskNumber = ArgumentParser.parseTaskNumber(parts[0], CommandWord.EDIT);
        String changes = (parts.length == 2) ? parts[1] : "";
        MarkedArguments marked = MarkedArguments.parse(changes);
        if (marked.getKeywords().isEmpty() || !marked.getPreamble().isEmpty()) {
            throw BobException.withExample("What should I change about task " + taskNumber + "?"
                    + "\nUse " + DESC_KEYWORD + ", " + BY_KEYWORD + ", " + FROM_KEYWORD
                    + " or " + TO_KEYWORD + ".",
                    CommandWord.EDIT.getKeyword() + " " + taskNumber + " " + DESC_KEYWORD + " read book");
        }

        // Every marker is checked for a value before any date is read, so a missing
        // value is reported ahead of a date that could not be read.
        for (String keyword : MarkedArguments.KEYWORDS) {
            if (marked.getValue(keyword).filter(String::isEmpty).isPresent()) {
                throw new BobException("You wrote " + keyword + " but nothing after it.");
            }
        }
        return new EditCommand(taskNumber, new TaskEdit(
                marked.getValue(DESC_KEYWORD).orElse(null),
                parseEditedDate(marked, BY_KEYWORD),
                parseEditedDate(marked, FROM_KEYWORD),
                parseEditedDate(marked, TO_KEYWORD)));
    }

    /**
     * Returns the new date following {@code keyword} in the changes of an edit, or
     * {@code null} if that marker was not used and the date is to be kept.
     *
     * <p>{@code null} rather than an empty {@link Optional}, because the answer goes
     * straight into a {@link TaskEdit}, where a detail left unchanged is {@code null}.
     *
     * @param marked  the changes of the edit, split at their markers.
     * @param keyword the marker whose date to read.
     * @throws BobException if the text following the marker is not a date.
     */
    private static TaskDateTime parseEditedDate(MarkedArguments marked, String keyword) throws BobException {
        Optional<String> dateText = marked.getValue(keyword);
        if (dateText.isEmpty()) {
            return null;
        }
        return TaskDateTime.parse(dateText.get());
    }
}
