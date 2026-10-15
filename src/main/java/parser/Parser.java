package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import app.DestroyerOfWorlds;
import command.Command;
import task.DeadlineTask;
import task.EventTask;
import task.Task;
import task.TaskList;
import util.Util;

/**
 * Converts user commands and persisted task signatures into the appropriate program objects.
 * Keeps parsing and command recognition separated from file I/O and task management.
 */
public class Parser {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Identifies the supported command formats. */
    private enum CommandType {
        TODO_SIGNATURE,
        TODO_COMMAND,
        DEADLINE_SIGNATURE,
        DEADLINE_COMMAND,
        EVENT_SIGNATURE,
        EVENT_COMMAND,
        EXIT,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        FIND,
        NONE
    }

    /**
     * Parses a task signature string and returns the corresponding Task object.
     * Supported formats:
     *   T|0/1|description
     *   D|0/1|description|dd/MM/yyyy HH:mm
     *   E|0/1|description|dd/MM/yyyy HH:mm|dd/MM/yyyy HH:mm
     * @param taskSignature the task signature string
     * @return the parsed Task object, or null if parsing fails
     */
    public static Task parseTaskSignature(String taskSignature) {
        try {
            ArrayList<String> words = Util.toArrayList(taskSignature, '|');

            if (words.isEmpty()) {
                return null;
            }

            String taskType = words.get(0);
            boolean done = words.get(1).equals("1");
            String description = words.get(2);

            if ("T".equals(taskType)) {
                return new Task(description, done);
            } else if ("D".equals(taskType)) {
                LocalDateTime byDate = LocalDateTime.parse(words.get(3).trim(), TIME_FORMAT);
                return new DeadlineTask(description, done, byDate);
            } else if ("E".equals(taskType)) {
                LocalDateTime fromDate = LocalDateTime.parse(words.get(3).trim(), TIME_FORMAT);
                LocalDateTime toDate = LocalDateTime.parse(words.get(4).trim(), TIME_FORMAT);
                return new EventTask(description, done, fromDate, toDate);
            }
        } catch (Exception e) {
            // Ignore malformed entries while loading persisted data
            return null;
        }
        return null;
    }

    /**
     * Interprets a user command and returns the corresponding status/output payload.
     * @param text raw user input
     * @param tasks the current task list to operate on
     * @return command result payload, with the status string as the first element
     */
    public static ArrayList<Object> parseCommand(String text, TaskList tasks) {

        assert text != null : "Command text must not be null";
        assert tasks != null : "Command parsing requires a task list";
        CommandType commandType = findCommandType(text);

        if (isAddCommandType(commandType)) {
            return handleAddCommand(text, tasks, commandType);
        } else if (isIndexedTaskCommand(commandType)) {
            return handleIndexedTaskCommand(text, tasks, commandType);
        } else if (commandType == CommandType.FIND) {
            String keyword = text.substring(5).trim();
            return new ArrayList<>(List.of(tasks.findTasks(keyword)));
        } else if (isSimpleCommand(commandType)) {
            return handleSimpleCommand(tasks, commandType);
        } else {
            return new ArrayList<>(List.of("REEEEAAAUUU!!!"));
        }
    }

    /**
     * Checks whether a command takes no arguments.
     *
     * @param commandType command type to check
     * @return true for exit and list commands
     */
    private static boolean isSimpleCommand(CommandType commandType) {
        return commandType == CommandType.EXIT || commandType == CommandType.LIST;
    }

    /**
     * Handles commands that take no arguments and do not select a task by index.
     *
     * @param tasks the current task list
     * @param commandType the recognized command type
     * @return command result payload
     */
    private static ArrayList<Object> handleSimpleCommand(TaskList tasks, CommandType commandType) {
        if (commandType == CommandType.EXIT) {
            return new ArrayList<>(List.of(DestroyerOfWorlds.exit()));
        } else if (commandType == CommandType.LIST) {
            return new ArrayList<>(List.of(tasks.toString()));
        } else {
            throw new IllegalArgumentException("Unsupported simple command type");
        }
    }

    /**
     * Checks whether a command operates on a task selected by its list index.
     *
     * @param commandType command type to check
     * @return true for mark, unmark, and delete commands
     */
    private static boolean isIndexedTaskCommand(CommandType commandType) {
        return commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE;
    }

    /**
     * Executes a command that operates on a task selected by its list index.
     *
     * @param text raw user input
     * @param tasks the current task list
     * @param commandType the recognized indexed-task command
     * @return command result payload
     */
    private static ArrayList<Object> handleIndexedTaskCommand(
            String text, TaskList tasks, CommandType commandType) {
        try {
            int commandNameLength;
            if (commandType == CommandType.MARK) {
                commandNameLength = 4;
            } else if (commandType == CommandType.UNMARK) {
                commandNameLength = 6;
            } else if (commandType == CommandType.DELETE) {
                commandNameLength = 6;
            } else {
                throw new IllegalArgumentException("REEEEAAAUUU: INDEX OUT OF BOUNDS!!!");
            }
            int taskNumber = Util.trimAndExtractInteger(text, commandNameLength);
            String result;
            if (commandType == CommandType.MARK) {
                result = tasks.setTask(taskNumber - 1, true);
            } else if (commandType == CommandType.UNMARK) {
                result = tasks.setTask(taskNumber - 1, false);
            } else if (commandType == CommandType.DELETE) {
                result = tasks.removeTask(taskNumber - 1);
            } else {
                throw new IllegalArgumentException("REEEEAAAUUU: INDEX OUT OF BOUNDS!!!");
            }
            return new ArrayList<>(List.of(result));
        } catch (NumberFormatException e) {
            return new ArrayList<>(List.of("REEEEAAAUUU: " + e.getMessage()));
        }
    }

    /**
     * Checks whether a command result string represents an error rather than a normal status
     * update, based on the app's error-message convention (messages beginning with the
     * "REEEEAAAUUU"/"RIEEEEAAAU" prefixes used throughout the parser and task list).
     *
     * @param message the result string returned by {@link #parseCommand}
     * @return true if the message signals an error to the user
     */
    public static boolean isErrorMessage(String message) {
        return message != null && (message.contains("REEEEAAAUUU") || message.contains("RIEEEEAAAU"));
    }

    /**
     * Checks whether a command type creates a task.
     *
     * @param commandType command type to check
     * @return true if the command type is an add command type
     */
    private static boolean isAddCommandType(CommandType commandType) {
        return commandType == CommandType.TODO_SIGNATURE
                || commandType == CommandType.TODO_COMMAND
                || commandType == CommandType.DEADLINE_SIGNATURE
                || commandType == CommandType.DEADLINE_COMMAND
                || commandType == CommandType.EVENT_SIGNATURE
                || commandType == CommandType.EVENT_COMMAND;
    }

    /**
     * Parses a recognized add-task command handles it accordingly.
     *
     * @param text raw user input
     * @param tasks the current task list
     * @param commandType the recognized add-command format
     * @return command result payload, with the status string as the first element
     */
    private static ArrayList<Object> handleAddCommand(
            String text, TaskList tasks, CommandType commandType) {
        if (commandType == CommandType.TODO_SIGNATURE) {
            ArrayList<String> words = Util.toArrayList(text, '|');
            assert words.size() == 3 : "Matched todo signature must have three fields";
            boolean done = words.get(1).equals("1");
            String description = words.get(2);
            Task out = new Task(description, done);
            return new ArrayList<>(List.of("Added: " + out.toString(), out));
        } else if (commandType == CommandType.TODO_COMMAND) {
            ArrayList<String> words = Util.toArrayList(text, new ArrayList<>(List.of("todo ")));
            assert words.size() == 2 : "Matched todo command must have two fields";
            boolean done = false;
            String description = words.get(1);
            Task out = new Task(description, done);
            return new ArrayList<>(List.of(addTaskIfUnique(tasks, out)));
        } else if (commandType == CommandType.DEADLINE_SIGNATURE) {
            try {
                ArrayList<String> words = Util.toArrayList(text, '|');
                assert words.size() == 4 : "Matched deadline signature must have four fields";
                boolean done = words.get(1).equals("1");
                String description = words.get(2);
                LocalDateTime byDate = LocalDateTime.parse(words.get(3).trim(), TIME_FORMAT);
                Task out = new DeadlineTask(description, done, byDate);
                return new ArrayList<>(List.of("Added: " + out.toString(), out));
            } catch (DateTimeParseException e) {
                return new ArrayList<>(List.of("REEEEAAAUUU: WRONG TIME FORMAT!!!"));
            }
        } else if (commandType == CommandType.DEADLINE_COMMAND) {
            try {
                ArrayList<String> words = Util.toArrayList(
                        text,
                        new ArrayList<>(List.of("deadline ", " /by "))
                );
                assert words.size() == 3 : "Matched deadline command must have three fields";
                boolean done = false;
                String description = words.get(1);
                LocalDateTime byDate = LocalDateTime.parse(words.get(2).trim(), TIME_FORMAT);
                Task out = new DeadlineTask(description, done, byDate);
                return new ArrayList<>(List.of(addTaskIfUnique(tasks, out)));
            } catch (DateTimeParseException e) {
                return new ArrayList<>(List.of("REEEEAAAUUU: WRONG TIME FORMAT!!!"));
            }
        } else if (commandType == CommandType.EVENT_SIGNATURE) {
            try {
                ArrayList<String> words = Util.toArrayList(text, '|');
                assert words.size() == 5 : "Matched event signature must have five fields";
                boolean done = words.get(1).equals("1");
                String description = words.get(2);
                LocalDateTime fromDate = LocalDateTime.parse(words.get(3).trim(), TIME_FORMAT);
                LocalDateTime toDate = LocalDateTime.parse(words.get(4).trim(), TIME_FORMAT);
                Task out = new EventTask(description, done, fromDate, toDate);
                return new ArrayList<>(List.of("Added: " + out.toString(), out));
            } catch (DateTimeParseException e) {
                return new ArrayList<>(List.of("REEEEAAAUUU: WRONG TIME FORMAT!!!"));
            }
        } else if (commandType == CommandType.EVENT_COMMAND) {
            try {
                ArrayList<String> words = Util.toArrayList(
                        text,
                        new ArrayList<>(List.of("event ", " /from ", " /to "))
                );
                assert words.size() == 4 : "Matched event command must have four fields";
                boolean done = false;
                String description = words.get(1);
                LocalDateTime fromDate = LocalDateTime.parse(words.get(2).trim(), TIME_FORMAT);
                LocalDateTime toDate = LocalDateTime.parse(words.get(3).trim(), TIME_FORMAT);
                Task out = new EventTask(description, done, fromDate, toDate);
                return new ArrayList<>(List.of(addTaskIfUnique(tasks, out)));
            } catch (DateTimeParseException e) {
                return new ArrayList<>(List.of("REEEEAAAUUU: WRONG TIME FORMAT!!!"));
            }
        } else {
            throw new IllegalArgumentException("Unsupported add command type");
        }
    }

    /**
     * Adds a user-created task only when an equivalent task is not already present.
     *
     * @param tasks the current task list
     * @param task candidate task to add
     * @return success or duplicate-task status message
     */
    private static String addTaskIfUnique(TaskList tasks, Task task) {
        if (tasks.containsDuplicate(task)) {
            return "REEEEAAAUUU: TASK ALREADY EXISTS!!!!";
        }
        return tasks.addTask(task);
    }

    /**
     * Identifies the command type represented by the input text.
     *
     * @param text raw user input
     * @return the matching command type, or {@code NONE} when no command matches
     */
    private static CommandType findCommandType(String text) {
        String todoSignatureRegex = "\\AT\\|[01]\\|"
                + "\\s*[\\S&&[^\\|]][\\s\\S&&[^\\|]]*\\z";
        if (new Command(todoSignatureRegex).find(0, text)) {
            return CommandType.TODO_SIGNATURE;
        }

        String todoCommandRegex = "\\Atodo\\s"
                + "\\s*[\\S&&[^\\|]][\\s\\S&&[^\\|]]*\\z";
        if (new Command(todoCommandRegex).find(0, text)) {
            return CommandType.TODO_COMMAND;
        }

        String deadlineSignatureRegex = "\\AD\\|[01]\\|"
                + "\\s*[\\S&&[^\\|]][\\s\\S&&[^\\|]]*\\|"
                + "\\s*\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s*\\z";
        if (new Command(deadlineSignatureRegex).find(0, text)) {
            return CommandType.DEADLINE_SIGNATURE;
        }

        String deadlineCommandRegex = "\\Adeadline\\s"
                + "\\s*[\\S&&[^\\|]][\\s\\S&&[^\\|]]*\\s/by\\s"
                + "\\s*\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s*\\z";
        if (new Command(deadlineCommandRegex).find(0, text)) {
            return CommandType.DEADLINE_COMMAND;
        }

        String eventSignatureRegex = "\\AE\\|[01]\\|"
                + "\\s*[\\S&&[^\\|]][\\s\\S&&[^\\|]]*\\|"
                + "\\s*\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s*\\|"
                + "\\s*\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s*\\z";
        if (new Command(eventSignatureRegex).find(0, text)) {
            return CommandType.EVENT_SIGNATURE;
        }

        String eventCommandRegex = "\\Aevent\\s"
                + "\\s*[\\S&&[^\\|]][\\s\\S&&[^\\|]]*\\s/from\\s"
                + "\\s*\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s*\\s/to\\s"
                + "\\s*\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}\\s*\\z";
        if (new Command(eventCommandRegex).find(0, text)) {
            return CommandType.EVENT_COMMAND;
        }

        if (new Command("\\Abye\\s*\\z").find(0, text)) {
            return CommandType.EXIT;
        }
        if (new Command("\\Alist\\s*\\z").find(0, text)) {
            return CommandType.LIST;
        }
        if (new Command("\\Amark\\s+-?\\d+\\s*\\z").find(0, text)) {
            return CommandType.MARK;
        }
        if (new Command("\\Aunmark\\s+-?\\d+\\s*\\z").find(0, text)) {
            return CommandType.UNMARK;
        }
        if (new Command("\\Adelete\\s+-?\\d+\\s*\\z").find(0, text)) {
            return CommandType.DELETE;
        }
        if (new Command("\\Afind\\s+\\S[\\s\\S]*\\z").find(0, text)) {
            return CommandType.FIND;
        }
        return CommandType.NONE;
    }
}
