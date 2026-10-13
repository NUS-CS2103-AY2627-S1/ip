package jimbo;

import jimbo.command.*;
import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parses user input and matches them to commands.
 */
public class Parser {
    /**
     * Parses user input into commands.
     *
     * @param input Raw string input from user.
     * @return Command encapsulated as an object.
     * @throws JimboException When command or arguments are illegal.
     */
    public Command parse(String input) throws JimboException {
        String[] inputFragments = input.split(" ");
        List<String> commandSections = splitCommandIntoSections(
                Arrays.copyOfRange(inputFragments, 1, inputFragments.length));
        // make it case-insensitive
        String commandType = inputFragments[0].toLowerCase();
        return switch (commandType) {
            case "bye" -> getByeCommand();
            case "list" -> getListCommand();
            case "save" -> getSaveCommand();
            case "mark" -> getMarkCommand(inputFragments);
            case "unmark" -> getUnmarkCommand(inputFragments);
            case "delete" -> getDeleteCommand(inputFragments);
            case "todo" -> getTodoCommand(inputFragments);
            case "deadline" -> getDeadlineCommand(commandSections);
            case "event" -> getEventCommand(commandSections);
            case "find" -> getFindCommand(commandSections);
            case "tag" -> getTagCommand(commandSections);
            case "untag" -> getUntagCommand(commandSections);
            default -> throw new JimboException("i don't understand this command");
        };
    }

    private static ByeCommand getByeCommand() {
        return new ByeCommand();
    }

    private static ListCommand getListCommand() {
        return new ListCommand();
    }

    private static SaveCommand getSaveCommand() {
        return new SaveCommand();
    }

    private static TaskCommand getTodoCommand(String[] inputFragments) {
        return new TaskCommand(new Todo(String.join(" ",
                Arrays.copyOfRange(inputFragments, 1, inputFragments.length))));
    }

    private static FindCommand getFindCommand(List<String> commandSections) throws JimboException {
        if (commandSections.size() != 1) {
            throw new JimboException("find should not have any flags");
        }
        return new FindCommand(commandSections.get(0));
    }

    private static TaskCommand getEventCommand(List<String> commandSections) throws JimboException {
        if (commandSections.size() < 3) {
            throw new JimboException("expected 3 parameters for commands");
        }
        if (commandSections.size() > 3) {
            throw new JimboException("too many parameters given");
        }

        assert commandSections.size() == 3 : "Number of command parameters should have been checked";

        String fromFlag = "/from ";
        String fromParam = commandSections.get(1);
        if (fromParam.indexOf(fromFlag) == -1) {
            throw new JimboException("no /from flag found");
        }
        String fromDate = fromParam.substring(fromParam.indexOf(fromFlag) + fromFlag.length());

        String toFlag = "/to ";
        String toParam = commandSections.get(2);
        if (toParam.indexOf(toFlag) == -1) {
            throw new JimboException("no /to flag found");
        }
        String toDate = toParam.substring(toParam.indexOf(toFlag) + toFlag.length());
        return new TaskCommand(new Event(commandSections.get(0), fromDate, toDate));
    }

    private static TaskCommand getDeadlineCommand(List<String> commandSections) throws JimboException {
        if (commandSections.size() < 2) {
            throw new JimboException("expected 2 parameters for commands");
        }
        if (commandSections.size() > 2) {
            throw new JimboException("too many parameters given");
        }

        assert commandSections.size() == 2 : "Number of command parameters should have been checked";

        String byFlag = "/by ";
        String byParam = commandSections.get(1);
        if (!byParam.contains(byFlag)) {
            throw new JimboException("no /by flag found");
        }
        String byDate = byParam.substring(byParam.indexOf(byFlag) + byFlag.length());
        return new TaskCommand(new Deadline(commandSections.get(0), byDate));
    }

    private static DeleteCommand getDeleteCommand(String[] inputFragments) throws JimboException {
        try {
            var taskIndex = Integer.parseInt(inputFragments[1]);
            // Since the list displayed to the user is 1-indexed, we need to
            // change it back to 0-indexing
            return new DeleteCommand(taskIndex - 1);
        } catch (NumberFormatException e) {
            throw new JimboException("please provide a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new JimboException("please provide a valid index");
        }
    }

    private static UnmarkCommand getUnmarkCommand(String[] inputFragments) throws JimboException {
        try {
            var taskIndex = Integer.parseInt(inputFragments[1]);
            // Since the list displayed to the user is 1-indexed, we need to
            // change it back to 0-indexing
            return new UnmarkCommand(taskIndex - 1);
        } catch (NumberFormatException e) {
            throw new JimboException("please provide a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new JimboException("please provide a valid index");
        }
    }

    private static MarkCommand getMarkCommand(String[] inputFragments) throws JimboException {
        try {
            var taskIndex = Integer.parseInt(inputFragments[1]);
            // Since the list displayed to the user is 1-indexed, we need to
            // change it back to 0-indexing
            return new MarkCommand(taskIndex - 1);
        } catch (NumberFormatException e) {
            throw new JimboException("please provide a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new JimboException("please provide a valid index");
        }
    }

    private static TagCommand getTagCommand(List<String> commandSections) throws JimboException {
        if (commandSections.size() < 2) {
            throw new JimboException("expected 2 parameters for commands");
        }
        if (commandSections.size() > 2) {
            throw new JimboException("too many parameters given");
        }

        assert commandSections.size() == 2 : "Number of command parameters should have been checked";

        String tagFlag = "/tag ";
        String tagParam = commandSections.get(1);
        if (!tagParam.contains(tagFlag)) {
            throw new JimboException("no /tag flag found");
        }
        String tag = tagParam.substring(tagParam.indexOf(tagFlag) + tagFlag.length());
        int index;
        try {
            index = Integer.parseInt(commandSections.get(0));
        } catch (NumberFormatException e) {
            throw new JimboException("not a valid index");
        }
        // Since the list displayed to the user is 1-indexed, we need to
        // change the task index back to 0-indexing
        return new TagCommand(index - 1, tag);
    }

    private static UntagCommand getUntagCommand(List<String> commandSections) throws JimboException {
        if (commandSections.size() < 2) {
            throw new JimboException("expected 2 parameters for commands");
        }
        if (commandSections.size() > 2) {
            throw new JimboException("too many parameters given");
        }

        assert commandSections.size() == 2 : "Number of command parameters should have been checked";

        String tagFlag = "/tag ";
        String tagParam = commandSections.get(1);
        if (!tagParam.contains(tagFlag)) {
            throw new JimboException("no /tag flag found" + tagParam);
        }
        String tag = tagParam.substring(tagParam.indexOf(tagFlag) + tagFlag.length());
        int index;
        try {
            index = Integer.parseInt(commandSections.get(0));
        } catch (NumberFormatException e) {
            throw new JimboException("not a valid index");
        }
        // Since the list displayed to the user is 1-indexed, we need to
        // change the task index back to 0-indexing
        return new UntagCommand(index - 1, tag);
    }

    private List<String> splitCommandIntoSections(String[] commandFragments) {
        List<String> commandSections = new ArrayList<>();
        String section = "";
        boolean isFirstInSection = true;
        for (String fragment : commandFragments) {
            if (!fragment.isEmpty() && fragment.charAt(0) != '/') {
                if (isFirstInSection) {
                    section += fragment;
                    isFirstInSection = false;
                    continue;
                }
                section += " " + fragment;
            } else {
                commandSections.add(section);
                section = fragment;
                isFirstInSection = false;
            }
        }
        commandSections.add(section);
        return commandSections;
    }

    
}
