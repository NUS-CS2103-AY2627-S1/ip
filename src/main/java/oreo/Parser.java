package oreo;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oreo.exception.OreoException;
import oreo.task.Deadline;
import oreo.task.Event;
import oreo.task.Task;
import oreo.task.Todo;

/** Interprets user commands and constructs the tasks described by them. */
public class Parser {
    /** Message displayed when a tag does not follow the supported format. */
    private static final String INVALID_TAG_MESSAGE = "A tag must start with # and contain only letters, numbers, "
            + "hyphens, or underscores.";
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("(.+?) /by (\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "(.+?) /from (\\d{4}-\\d{2}-\\d{2}) /to (\\d{4}-\\d{2}-\\d{2})");

    /** The command categories understood by the application. */
    public enum Command {
        BYE, LIST, LIST_TAGS, FIND, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE, TAG, UNTAG, UNKNOWN
    }

    /** A validated task number and the tags to add or remove. */
    public record TagUpdate(int taskNumber, List<String> tags) {
    }

    /** A task description with its separately parsed trailing tags. */
    private record TaggedDescription(String description, List<String> tags) {
    }

    /** Identifies the command category for a line of user input. */
    public Command parseCommand(String input) {
        String commandWord = input.split(" ", 2)[0];
        if (input.startsWith("untag")) {
            return Command.UNTAG;
        } else if (input.startsWith("tag")) {
            return Command.TAG;
        } else if (commandWord.equals("bye")) {
            return Command.BYE;
        } else if (commandWord.equals("list")) {
            return input.startsWith("list tags") ? Command.LIST_TAGS : Command.LIST;
        } else if (commandWord.equals("find")) {
            return Command.FIND;
        } else if (commandWord.equals("mark")) {
            return Command.MARK;
        } else if (commandWord.equals("unmark")) {
            return Command.UNMARK;
        } else if (commandWord.equals("todo")) {
            return Command.TODO;
        } else if (commandWord.equals("deadline")) {
            return Command.DEADLINE;
        } else if (commandWord.equals("event")) {
            return Command.EVENT;
        } else if (commandWord.equals("delete")) {
            return Command.DELETE;
        } else {
            return Command.UNKNOWN;
        }
    }

    /** Extracts the keyword supplied to a find command. */
    public String parseFindKeyword(String command) {
        if (!command.startsWith("find ")) {
            throw new OreoException("Use: find <keyword>.");
        }
        String keyword = command.substring("find ".length()).trim();
        if (keyword.isEmpty()) {
            throw new OreoException("Use: find <keyword>.");
        }
        return keyword;
    }

    /** Creates a to-do task from a command. */
    public Todo parseTodo(String command) {
        if (!command.startsWith("todo ")) {
            throw new OreoException("Use: todo <description> [#tag]...");
        }
        TaggedDescription taskDetails = parseTaggedDescription(command.substring("todo ".length()),
                "Use: todo <description> [#tag]...");
        Todo todo = new Todo(taskDetails.description());
        todo.addTags(taskDetails.tags());
        return todo;
    }

    /** Creates a deadline from a command with a description and {@code /by} value. */
    public Deadline parseDeadline(String command) {
        if (!command.startsWith("deadline ")) {
            throw new OreoException("Use: deadline <description> /by <year>-<month>-<date> [#tag]...");
        }
        try {
            TaggedDescription taskDetails = parseTaggedDescription(command.substring("deadline ".length()),
                    "Use: deadline <description> /by <year>-<month>-<date> [#tag]...");
            if (countOccurrences(taskDetails.description(), " /by ") != 1) {
                throw new OreoException("Use: deadline <description> /by <year>-<month>-<date> [#tag]...");
            }
            Matcher matcher = DEADLINE_PATTERN.matcher(taskDetails.description());
            if (!matcher.matches()) {
                throw new OreoException("Use: deadline <description> /by <year>-<month>-<date> [#tag]...");
            }
            Deadline deadline = new Deadline(matcher.group(1), matcher.group(2));
            deadline.addTags(taskDetails.tags());
            return deadline;
        } catch (DateTimeParseException e) {
            throw new OreoException("Use: deadline <description> /by <year>-<month>-<date> [#tag]...");
        }
    }

    /** Creates an event from a command with description, {@code /from}, and {@code /to} values. */
    public Event parseEvent(String command) {
        if (!command.startsWith("event ")) {
            throw new OreoException("Use: event <description> /from <year>-<month>-<date> "
                    + "/to <year>-<month>-<date> [#tag]...");
        }
        try {
            TaggedDescription taskDetails = parseTaggedDescription(command.substring("event ".length()),
                    "Use: event <description> /from <year>-<month>-<date> /to <year>-<month>-<date> [#tag]...");
            if (countOccurrences(taskDetails.description(), " /from ") != 1
                    || countOccurrences(taskDetails.description(), " /to ") != 1) {
                throw new OreoException("Use: event <description> /from <year>-<month>-<date> "
                        + "/to <year>-<month>-<date> [#tag]...");
            }
            Matcher matcher = EVENT_PATTERN.matcher(taskDetails.description());
            if (!matcher.matches()) {
                throw new OreoException("Use: event <description> /from <year>-<month>-<date> "
                        + "/to <year>-<month>-<date> [#tag]...");
            }
            Event event = new Event(matcher.group(1), matcher.group(2), matcher.group(3));
            event.addTags(taskDetails.tags());
            return event;
        } catch (DateTimeParseException e) {
            throw new OreoException("Use: event <description> /from <year>-<month>-<date> "
                    + "/to <year>-<month>-<date> [#tag]...");
        }
    }

    /** Validates the exact {@code list tags} command. */
    public void parseListTags(String command) {
        if (!command.equals("list tags")) {
            throw new OreoException("Use: list tags.");
        }
    }

    /** Parses a tag update command for an existing task. */
    public TagUpdate parseTagUpdate(String command, String commandWord) {
        String usage = "Use: " + commandWord + " <task number> <#tag>...";
        if (!command.startsWith(commandWord + " ")) {
            throw new OreoException(usage);
        }

        String[] arguments = command.substring(commandWord.length()).trim().split("\\s+");
        if (arguments.length < 2) {
            throw new OreoException(usage);
        }

        try {
            int taskNumber = Integer.parseInt(arguments[0]);
            List<String> tags = parseTags(List.of(arguments).subList(1, arguments.length));
            return new TagUpdate(taskNumber, tags);
        } catch (NumberFormatException e) {
            throw new OreoException(usage);
        }
    }

    /** Separates a task description from its trailing tags. */
    private TaggedDescription parseTaggedDescription(String text, String usage) {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            return new TaggedDescription("", List.of());
        }

        String[] words = trimmedText.split("\\s+");
        int firstTagIndex = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].startsWith("#")) {
                firstTagIndex = i;
                break;
            }
        }
        if (firstTagIndex == -1) {
            return new TaggedDescription(trimmedText, List.of());
        }

        List<String> tags = parseTags(List.of(words).subList(firstTagIndex, words.length));
        String description = String.join(" ", List.of(words).subList(0, firstTagIndex));
        if (description.isEmpty()) {
            throw new OreoException(usage);
        }
        return new TaggedDescription(description, tags);
    }

    /** Validates and normalizes a collection of tag tokens. */
    private List<String> parseTags(List<String> tagTokens) {
        List<String> tags = new ArrayList<>();
        for (String tag : tagTokens) {
            if (!Task.isValidTag(tag)) {
                throw new OreoException(INVALID_TAG_MESSAGE);
            }
            String normalizedTag = tag.toLowerCase(Locale.ROOT);
            if (!tags.contains(normalizedTag)) {
                tags.add(normalizedTag);
            }
        }
        return tags;
    }

    /** Counts non-overlapping appearances of a command marker. */
    private int countOccurrences(String text, String marker) {
        int count = 0;
        int markerIndex = text.indexOf(marker);
        while (markerIndex >= 0) {
            count++;
            markerIndex = text.indexOf(marker, markerIndex + marker.length());
        }
        return count;
    }
}
