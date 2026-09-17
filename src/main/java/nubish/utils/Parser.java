package nubish.utils;

import java.time.format.DateTimeParseException;

import nubish.tasks.Deadline;
import nubish.tasks.Event;
import nubish.tasks.Task;
import nubish.tasks.Todo;

/**
 * Parses user commands and applies the requested changes to the task list.
 */
public class Parser {
    private static final String TASK_NUMBER_MESSAGE = "Hrmmm... Please put a valid task number.";
    private static final String TODO_DESCRIPTION_MESSAGE = "Hrmmm... The description of a todo cannot be empty.";
    private static final String DEADLINE_FORMAT_MESSAGE = "Hrmmm... Please use the proper format for deadlines: "
            + "deadline {taskname} /by {deadline}";
    private static final String EVENT_FORMAT_MESSAGE = "Hrmmm... Please use the proper format for events: "
            + "event {eventName} /from {startDate} /to {enddate}";
    private static final String EVENT_DATE_TIME_MESSAGE = EVENT_FORMAT_MESSAGE
            + ". Start times must be earlier than end times.";
    private static final String STORAGE_DELIMITER_MESSAGE = "Hrmmm... Please do not use | in task details.";

    private final Storage storage;
    private final UI ui;
    private final TaskList taskList;

    /**
     * Creates a parser that coordinates storage, UI output, and task list updates.
     *
     * @param storage storage used by the application
     * @param ui user interface used to display command results
     * @param taskList task list modified by parsed commands
     */
    public Parser(Storage storage, UI ui, TaskList taskList) {
        this.storage = storage;
        this.ui = ui;
        this.taskList = taskList;
    }

    /**
     * Parses and executes one line of user input.
     *
     * @param input raw user command
     * @return {@code false} when the command requests exit, or {@code true} otherwise
     */
    public boolean parse(String input) {
        if (input == null || input.isBlank()) {
            ui.printCommandList();
            return true;
        }

        Command command = Command.fromKeyword(input.trim().split(" ", 2)[0]);
        if (!isValidSpacing(input)) {
            showFormatError(command);
            return true;
        }

        String[] parts = input.split(" ", 2);
        String arguments = (parts.length > 1) ? parts[1] : "";

        switch (command) {
            case BYE:
                ui.printBye();
                return false;
            case LIST:
                ui.printList(taskList.toString());
                break;
            case MARK:
                setTaskDoneStatus(arguments, true);
                break;
            case UNMARK:
                setTaskDoneStatus(arguments, false);
                break;
            case TODO:
                addTodo(input, arguments);
                break;
            case DEADLINE:
                addDeadline(arguments);
                break;
            case EVENT:
                addEvent(arguments);
                break;
            case DELETE:
                deleteTask(arguments);
                break;
            case FIND:
                findTasks(arguments);
                break;
            default:
                ui.printCommandList();
        }
        return true;
    }

    private void setTaskDoneStatus(String arguments, boolean isDone) {
        try {
            if (!isPositiveInteger(arguments)) {
                throw new NubishException(TASK_NUMBER_MESSAGE);
            }
            int taskIndex = Integer.parseInt(arguments) - 1;
            Task task = taskList.get(taskIndex);

            if (isDone) {
                task.markAsDone();
                ui.printMark(task.toString());
            } else {
                task.unmarkAsDone();
                ui.printUnmark(task.toString());
            }
        } catch (NubishException e) {
            ui.showError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            ui.showError(TASK_NUMBER_MESSAGE);
        }
    }

    private void addTodo(String input, String arguments) {
        try {
            if (arguments.isEmpty()) {
                throw new NubishException(TODO_DESCRIPTION_MESSAGE);
            }
            if (hasAnyArgumentToken(arguments)) {
                throw new NubishException(TODO_DESCRIPTION_MESSAGE);
            }
            validateStorageDelimiter(arguments);
            taskList.add(new Todo(arguments));
            ui.printTodoAdded(input, taskList.size());
        } catch (NubishException e) {
            ui.showError(e.getMessage());
        }
    }

    private void addDeadline(String arguments) {
        try {
            if (countOccurrences(arguments, ArgumentToken.BY.getToken()) != 1
                    || hasAnyArgumentTokenExcept(arguments, ArgumentToken.BY)) {
                throw new NubishException(DEADLINE_FORMAT_MESSAGE);
            }
            String[] deadlineParts = arguments.split(" " + ArgumentToken.BY.getToken() + " ", -1);
            if (deadlineParts.length != 2) {
                throw new NubishException(DEADLINE_FORMAT_MESSAGE);
            }

            String taskName = deadlineParts[0];
            String deadline = deadlineParts[1];
            if (taskName.isEmpty()) {
                throw new NubishException("Hrmmm... The description of a deadline cannot be empty.");
            }
            if (deadline.isEmpty()) {
                throw new NubishException("Hrmmm... The deadline of the task cannot be empty.");
            }
            validateStorageDelimiter(taskName);
            taskList.add(new Deadline(taskName, deadline));
            ui.printDeadlineAdded(taskName, deadline, taskList.size());
        } catch (NubishException e) {
            ui.showError(e.getMessage());
        } catch (DateTimeParseException e) {
            ui.showError(DEADLINE_FORMAT_MESSAGE);
        }
    }

    private void addEvent(String arguments) {
        try {
            if (countOccurrences(arguments, ArgumentToken.FROM.getToken()) != 1
                    || countOccurrences(arguments, ArgumentToken.TO.getToken()) != 1
                    || hasAnyArgumentTokenExcept(arguments, ArgumentToken.FROM, ArgumentToken.TO)) {
                throw new NubishException(EVENT_FORMAT_MESSAGE);
            }

            String[] eventParts = arguments.split(" " + ArgumentToken.FROM.getToken() + " ", -1);
            if (eventParts.length != 2) {
                throw new NubishException(EVENT_FORMAT_MESSAGE);
            }

            String[] timeParts = eventParts[1].split(" " + ArgumentToken.TO.getToken() + " ", -1);
            if (timeParts.length != 2) {
                throw new NubishException(EVENT_FORMAT_MESSAGE);
            }

            String eventName = eventParts[0];
            String fromTime = timeParts[0];
            String toTime = timeParts[1];

            if (eventName.isEmpty()) {
                throw new NubishException("Hrmmm... The name of an event cannot be empty.");
            }
            if (fromTime.isEmpty()) {
                throw new NubishException("Hrmmm... The start of an event cannot be empty.");
            }
            if (toTime.isEmpty()) {
                throw new NubishException("Hrmmm... The end of an event cannot be empty.");
            }
            validateStorageDelimiter(eventName);
            taskList.add(new Event(eventName, fromTime, toTime));
            ui.printEventAdded(eventName, fromTime, toTime, taskList.size());
        } catch (NubishException e) {
            ui.showError(e.getMessage());
        } catch (DateTimeParseException e) {
            ui.showError(EVENT_DATE_TIME_MESSAGE);
        }
    }

    private void deleteTask(String arguments) {
        try {
            if (!isPositiveInteger(arguments)) {
                throw new NubishException(TASK_NUMBER_MESSAGE);
            }

            int deleteIndex = Integer.parseInt(arguments) - 1;

            Task task = taskList.remove(deleteIndex);
            ui.printTaskDelete(task.toString(), taskList.size());
        } catch (NubishException e) {
            ui.showError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            ui.showError(TASK_NUMBER_MESSAGE);
        }
    }

    private void findTasks(String arguments) {
        try {
            if (arguments.isEmpty()) {
                throw new NubishException("Hrmmm... Please enter keywords to search for");
            }

            TaskList foundList = taskList.find(arguments);
            ui.find(foundList.toString());
        } catch (NubishException e) {
            ui.showError(e.getMessage());
        }
    }

    private boolean isValidSpacing(String input) {
        return input.equals(input.trim()) && !input.contains("  ");
    }

    private boolean isPositiveInteger(String arguments) {
        return arguments.matches("[1-9]\\d*");
    }

    private boolean hasAnyArgumentToken(String arguments) {
        for (ArgumentToken token : ArgumentToken.values()) {
            if (arguments.contains(token.getToken())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyArgumentTokenExcept(String arguments, ArgumentToken... allowedTokens) {
        for (ArgumentToken token : ArgumentToken.values()) {
            if (!isAllowedToken(token, allowedTokens) && arguments.contains(token.getToken())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedToken(ArgumentToken token, ArgumentToken... allowedTokens) {
        for (ArgumentToken allowedToken : allowedTokens) {
            if (token == allowedToken) {
                return true;
            }
        }
        return false;
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int searchIndex = 0;
        while ((searchIndex = text.indexOf(token, searchIndex)) != -1) {
            count++;
            searchIndex += token.length();
        }
        return count;
    }

    private void validateStorageDelimiter(String text) {
        if (text.contains("|")) {
            throw new NubishException(STORAGE_DELIMITER_MESSAGE);
        }
    }

    private void showFormatError(Command command) {
        switch (command) {
            case MARK:
            case UNMARK:
            case DELETE:
                ui.showError(TASK_NUMBER_MESSAGE);
                break;
            case TODO:
                ui.showError(TODO_DESCRIPTION_MESSAGE);
                break;
            case DEADLINE:
                ui.showError(DEADLINE_FORMAT_MESSAGE);
                break;
            case EVENT:
                ui.showError(EVENT_FORMAT_MESSAGE);
                break;
            case FIND:
                ui.showError("Hrmmm... Please enter keywords to search for");
                break;
            default:
                ui.printCommandList();
        }
    }

}
