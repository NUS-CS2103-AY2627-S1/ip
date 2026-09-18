package lloyd;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import lloyd.command.CommandType;
import lloyd.command.ParsedCommand;
import lloyd.command.Parser;
import lloyd.storage.Storage;
import lloyd.task.Deadline;
import lloyd.task.Event;
import lloyd.task.Task;
import lloyd.task.TaskList;
import lloyd.task.Todo;
import lloyd.ui.Ui;

/** Processes commands and manages the persistent task list for Lloyd's user interfaces. */
public class Lloyd {
    private static final int REMINDER_DAYS = 3;

    private static final DateTimeFormatter DEADLINE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter CHECK_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private static final String DEADLINE_ARGUMENT_SEPARATOR = " /by ";
    private static final String EVENT_START_ARGUMENT_SEPARATOR = " /from ";
    private static final String EVENT_END_ARGUMENT_SEPARATOR = " /to ";

    private static final String BANNER =
            """
                     _      _                 _
                    | |    | |               | |
                    | |    | | ___  _   _  __| |
                    | |    | |/ _ \\| | | |/ _` |
                    | |____| | (_) | |_| | (_| |
                    |______|_|\\___/ \\__, |\\__,_|
                                     __/ |      \s
                                    |___/       \s""";
    private static final String GREETING =
            "Lloyd Frontera, the greatest estate developer, at your service!"
                    + "\nGot a problem? Excellent. Problems are profits waiting for an engineer."
                    + "\nNow, what needs doing?";
    private static final String FAREWELL =
            "Goodbye! Your tasks will be waiting.";
    private static final String LOAD_ERROR =
            "Unable to load tasks from data/lloyd.txt.\n"
                    + "Check that the file is readable and contains valid task data.";
    private static final String SAVE_ERROR =
            "Unable to save the change; your task list was not changed.\n"
                    + "Check that data/lloyd.txt is writable and task details do not contain |.";
    private static final String DEADLINE_FORMAT_GUIDANCE =
            "Enter a deadline in this format:\n"
                    + "deadline DESCRIPTION /by DD/MM/YYYY\n"
                    + "Date format: DD/MM/YYYY (day/month/year).\n"
                    + "Example: deadline submit report /by 06/08/2026";
    private static final String EVENT_FORMAT_GUIDANCE =
            "Enter an event in this format:\n"
                    + "event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM\n"
                    + "Date and time format: DD/MM/YYYY HHMM"
                    + " (day/month/year, 24-hour time).\n"
                    + "Example: event project meeting /from 06/08/2026 1400"
                    + " /to 06/08/2026 1600";
    private static final String CHECK_DATE_FORMAT_GUIDANCE =
            "Enter a date using DD/MM/YYYY (day/month/year).\n"
                    + "Example: check 06/08/2026";
    private static final String HELP_MESSAGE =
            """
                    Here's what I can do:

                    Add tasks:
                      todo DESCRIPTION
                      deadline DESCRIPTION /by DD/MM/YYYY
                      event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM

                    View tasks:
                      list
                      find KEYWORD
                      check DD/MM/YYYY
                      reminder

                    Manage tasks:
                      mark TASK_NUMBER
                      unmark TASK_NUMBER
                      delete TASK_NUMBER

                    Other commands:
                      help [COMMAND]
                      bye

                    Dates use DD/MM/YYYY.
                    Times use the 24-hour HHMM format; for example, 1430 means 2:30 PM.""";
    private static final String HELP_TOPICS =
            "todo, deadline, event, list, find, check, reminder, mark, unmark, delete, help, bye";

    private final Storage storage;
    private final TaskList taskList;
    private final Parser parser;
    private final Clock clock;
    private boolean isExitRequested;

    /**
     * Creates a Lloyd application backed by the default task file.
     *
     * @throws IOException If the task file cannot be loaded.
     */
    public Lloyd() throws IOException {
        this(Path.of("data", "lloyd.txt"));
    }

    /**
     * Creates a Lloyd application backed by the supplied task file.
     *
     * @param storagePath Location from which tasks are loaded and saved.
     * @throws IOException If the task file cannot be loaded.
     */
    public Lloyd(Path storagePath) throws IOException {
        this(storagePath, Clock.systemDefaultZone());
    }

    /**
     * Creates a Lloyd application with a supplied clock for date-dependent behavior.
     *
     * @param storagePath Location from which tasks are loaded and saved.
     * @param clock Clock used to determine the current local date.
     * @throws IOException If the task file cannot be loaded.
     */
    Lloyd(Path storagePath, Clock clock) throws IOException {
        storage = new Storage(storagePath);
        taskList = new TaskList(storage.load());
        parser = new Parser();
        this.clock = clock;
    }

    /**
     * Runs Lloyd through the original console interface.
     *
     * <p>The graphical application uses {@link #getResponse(String)} directly;
     * this entry point remains available for console testing.</p>
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        printResponse(BANNER + "\n" + GREETING);

        try {
            Lloyd lloyd = new Lloyd();
            while (!lloyd.isExitRequested() && ui.hasNextCommand()) {
                printResponse(lloyd.getResponse(ui.readCommand()));
            }
            if (!lloyd.isExitRequested()) {
                printResponse(FAREWELL);
            }
        } catch (IOException e) {
            printResponse(LOAD_ERROR);
        } finally {
            ui.close();
        }
    }

    /**
     * Returns the greeting shown when a user interface starts.
     *
     * @return Lloyd's greeting without the console-only text banner.
     */
    public String getGreeting() {
        return GREETING.stripLeading();
    }

    /**
     * Processes one command and returns its response text.
     *
     * @param input Command entered by the user.
     * @return Lloyd's textual response.
     */
    public String getResponse(String input) {
        return getReply(input).text();
    }

    /**
     * Processes one command and returns the response and mood.
     *
     * @param input Command entered by the user.
     * @return Lloyd's complete reply.
     */
    public LloydResponse getReply(String input) {
        ParsedCommand command = parser.parse(input);
        assert command != null
                : "Parser must return a command for every non-null input";

        return switch (command.getCommandType()) {
            case BYE -> exit();
            case LIST -> listTasks();
            case FIND -> findTasks(command);
            case CHECK -> checkDate(command);
            case REMINDER -> showReminders(command);
            case HELP -> showHelp(command);
            case MARK -> markTask(command);
            case UNMARK -> unmarkTask(command);
            case DELETE -> deleteTask(command);
            case TODO -> addTodo(command);
            case DEADLINE -> addDeadline(command);
            case EVENT -> addEvent(command);
            default -> annoyed("Enter a valid command.\n"
                    + "Commands: todo, deadline, event, list, find, check, reminder,"
                    + " mark, unmark, delete, help, bye");
        };
    }

    /** Creates a confident Lloyd response. */
    private static LloydResponse confident(String text) {
        return new LloydResponse(text, LloydMood.CONFIDENT);
    }

    /** Creates a delighted Lloyd response. */
    private static LloydResponse delighted(String text) {
        return new LloydResponse(text, LloydMood.DELIGHTED);
    }

    /** Creates an annoyed Lloyd response. */
    private static LloydResponse annoyed(String text) {
        return new LloydResponse(text, LloydMood.ANNOYED);
    }

    /** Creates an alarmed Lloyd response. */
    private static LloydResponse alarmed(String text) {
        return new LloydResponse(text, LloydMood.ALARMED);
    }

    /**
     * Reports whether the latest command asked Lloyd to exit.
     *
     * @return {@code true} after Lloyd processes the {@code bye} command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Marks the application as finished and returns Lloyd's farewell. */
    private LloydResponse exit() {
        isExitRequested = true;
        return confident(FAREWELL);
    }

    /** Returns all tasks in their current order. */
    private LloydResponse listTasks() {
        if (taskList.size() == 0) {
            return confident("Your task list is empty.");
        }
        return confident(formatNumberedTasks(
                "Tasks:", taskList.asList()));
    }

    /** Returns tasks containing the requested keyword. */
    private LloydResponse findTasks(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed("Enter a keyword.\nExample: find book");
        }

        String keyword = command.getArguments();
        List<Task> matchingTasks = taskList.find(keyword);
        if (matchingTasks.isEmpty()) {
            return annoyed("No tasks contain the keyword: " + keyword);
        }

        return confident(formatNumberedTasks(
                "Matching tasks:", matchingTasks));
    }

    /** Returns deadlines and event endpoints on the requested date. */
    private LloydResponse checkDate(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed(CHECK_DATE_FORMAT_GUIDANCE);
        }

        try {
            LocalDate checkedDate = LocalDate.parse(
                    command.getArguments(), DEADLINE_FORMAT);
            StringBuilder scheduledTasks = new StringBuilder(
                    "Schedule for "
                            + checkedDate.format(CHECK_DISPLAY_FORMAT) + ":\n");
            int matchCount = 0;
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                if (isScheduledOn(task, checkedDate)) {
                    scheduledTasks.append(String.format(
                            "%d. %s%n", i + 1, task));
                    matchCount++;
                }
            }

            if (matchCount == 0) {
                return confident("No deadlines or event endpoints fall on "
                        + checkedDate.format(CHECK_DISPLAY_FORMAT) + ".");
            }
            return confident(scheduledTasks.toString().stripTrailing());
        } catch (DateTimeParseException e) {
            return annoyed(CHECK_DATE_FORMAT_GUIDANCE);
        }
    }

    /** Returns incomplete dated tasks that are overdue or due within the reminder window. */
    private LloydResponse showReminders(ParsedCommand command) {
        if (command.hasArguments()) {
            return annoyed("Enter reminder without additional information.");
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate reminderEndDate = today.plusDays(REMINDER_DAYS);
        List<ReminderEntry> overdueTasks = new ArrayList<>();
        List<ReminderEntry> dueSoonTasks = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            LocalDate scheduledDate = getReminderDate(task);
            if (task.isDone() || scheduledDate == null) {
                continue;
            }

            ReminderEntry entry = new ReminderEntry(i + 1, task, scheduledDate);
            if (scheduledDate.isBefore(today)) {
                overdueTasks.add(entry);
            } else if (!scheduledDate.isAfter(reminderEndDate)) {
                dueSoonTasks.add(entry);
            }
        }

        if (overdueTasks.isEmpty() && dueSoonTasks.isEmpty()) {
            return delighted("No urgent tasks! You have no overdue tasks"
                    + " or tasks due within the next 3 days.");
        }

        Comparator<ReminderEntry> byDateThenTaskNumber = Comparator
                .comparing(ReminderEntry::scheduledDate)
                .thenComparingInt(ReminderEntry::taskNumber);
        overdueTasks.sort(byDateThenTaskNumber);
        dueSoonTasks.sort(byDateThenTaskNumber);

        StringBuilder reminders = new StringBuilder(
                "Reminders:");
        appendReminderSection(reminders, "Overdue", overdueTasks);
        appendReminderSection(
                reminders, "Due within 3 days, including today", dueSoonTasks);
        return alarmed(reminders.toString());
    }

    /** Returns general guidance or detailed guidance for one supported command. */
    private LloydResponse showHelp(ParsedCommand command) {
        if (!command.hasArguments()) {
            return confident(HELP_MESSAGE);
        }

        String helpTopic = command.getArguments();
        CommandType helpCommandType = CommandType.from(helpTopic);
        if (helpCommandType == CommandType.UNKNOWN) {
            return annoyed("No help is available for: " + helpTopic
                    + "\nAvailable help topics: " + HELP_TOPICS);
        }
        return confident(getCommandHelp(helpCommandType));
    }

    /** Returns detailed usage guidance for one recognized command. */
    private String getCommandHelp(CommandType commandType) {
        assert commandType != CommandType.UNKNOWN
                : "Detailed help requires a recognized command type";
        return switch (commandType) {
            case TODO -> """
                    todo - Add a task without a date or time.

                    Format:
                      todo DESCRIPTION

                    Example:
                      todo read book""";
            case DEADLINE -> """
                    deadline - Add a task with a due date.

                    Format:
                      deadline DESCRIPTION /by DD/MM/YYYY

                    Example:
                      deadline submit report /by 06/08/2026""";
            case EVENT -> """
                    event - Add an event with a start and end date and time.

                    Format:
                      event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM

                    Example:
                      event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600""";
            case LIST -> """
                    list - Display all tasks in their current order.

                    Format:
                      list""";
            case FIND -> """
                    find - Display tasks containing a keyword.

                    Format:
                      find KEYWORD

                    Example:
                      find book""";
            case CHECK -> """
                    check - Display deadlines and event endpoints on a date.

                    Format:
                      check DD/MM/YYYY

                    Example:
                      check 06/08/2026""";
            case REMINDER -> """
                    reminder - Display incomplete dated tasks that need attention.

                    Shows overdue tasks and tasks due within 3 days, including today.

                    Format:
                      reminder""";
            case MARK -> """
                    mark - Mark a task as complete.

                    Format:
                      mark TASK_NUMBER

                    Example:
                      mark 1""";
            case UNMARK -> """
                    unmark - Mark a task as incomplete.

                    Format:
                      unmark TASK_NUMBER

                    Example:
                      unmark 1""";
            case DELETE -> """
                    delete - Remove a task from the list.

                    Format:
                      delete TASK_NUMBER

                    Example:
                      delete 1""";
            case HELP -> """
                    help - Display general or command-specific guidance.

                    Format:
                      help
                      help COMMAND

                    Example:
                      help todo""";
            case BYE -> """
                    bye - End the current session.

                    Format:
                      bye""";
            case UNKNOWN -> throw new IllegalArgumentException(
                    "Detailed help requires a recognized command type");
        };
    }

    /** Returns the date used to classify a task for reminders, or {@code null} for todos. */
    private LocalDate getReminderDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom().toLocalDate();
        }
        return null;
    }

    /** Appends a nonempty reminder section while preserving original task numbers. */
    private void appendReminderSection(
            StringBuilder reminders, String heading, List<ReminderEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }

        reminders.append("\n").append(heading).append(":");
        for (ReminderEntry entry : entries) {
            reminders.append(String.format(
                    "\n%d. %s", entry.taskNumber(), entry.task()));
        }
    }

    /** Marks one task as complete when its number is valid. */
    private LloydResponse markTask(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed("Enter a task number.\nExample: mark 1");
        }

        try {
            int taskNumber = Integer.parseInt(command.getArguments());
            if (!isValidTaskNumber(taskNumber)) {
                return annoyed(invalidTaskNumberMessage());
            }

            int taskIndex = taskNumber - 1;
            assert taskIndex >= 0 && taskIndex < taskList.size()
                    : "A validated task number must map to an existing list index";
            Task task = taskList.get(taskIndex);
            boolean wasDone = task.isDone();
            task.mark();
            assert task.isDone() : "A marked task must report that it is complete";
            if (!saveTasks()) {
                if (!wasDone) {
                    task.unmark();
                }
                assert task.isDone() == wasDone
                        : "A failed save must restore the task's previous completion state";
                return alarmed(SAVE_ERROR);
            }
            return delighted("Task completed:\n" + task);
        } catch (NumberFormatException e) {
            return annoyed(invalidNumberMessage());
        }
    }

    /** Marks one task as incomplete when its number is valid. */
    private LloydResponse unmarkTask(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed("Enter a task number.\nExample: unmark 1");
        }

        try {
            int taskNumber = Integer.parseInt(command.getArguments());
            if (!isValidTaskNumber(taskNumber)) {
                return annoyed(invalidTaskNumberMessage());
            }

            int taskIndex = taskNumber - 1;
            assert taskIndex >= 0 && taskIndex < taskList.size()
                    : "A validated task number must map to an existing list index";
            Task task = taskList.get(taskIndex);
            boolean wasDone = task.isDone();
            task.unmark();
            assert !task.isDone() : "An unmarked task must report that it is incomplete";
            if (!saveTasks()) {
                if (wasDone) {
                    task.mark();
                }
                assert task.isDone() == wasDone
                        : "A failed save must restore the task's previous completion state";
                return alarmed(SAVE_ERROR);
            }
            return annoyed("Task marked as incomplete:\n" + task);
        } catch (NumberFormatException e) {
            return annoyed(invalidNumberMessage());
        }
    }

    /** Deletes one task when its number is valid. */
    private LloydResponse deleteTask(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed("Enter a task number.\nExample: delete 1");
        }

        try {
            int taskNumber = Integer.parseInt(command.getArguments());
            if (!isValidTaskNumber(taskNumber)) {
                return annoyed(invalidTaskNumberMessage());
            }

            int taskIndex = taskNumber - 1;
            assert taskIndex >= 0 && taskIndex < taskList.size()
                    : "A validated task number must map to an existing list index";
            int previousTaskCount = taskList.size();
            Task deletedTask = taskList.remove(taskIndex);
            assert taskList.size() == previousTaskCount - 1
                    : "Deleting one task must reduce the task count by one";
            if (!saveTasks()) {
                taskList.add(taskIndex, deletedTask);
                assert taskList.size() == previousTaskCount
                        : "A failed save must restore the previous task count";
                assert taskList.get(taskIndex) == deletedTask
                        : "A failed save must restore the deleted task at its original index";
                return alarmed(SAVE_ERROR);
            }
            return delighted("Task deleted:\n" + deletedTask
                    + "\nTotal tasks: " + taskList.size() + ".");
        } catch (NumberFormatException e) {
            return annoyed(invalidNumberMessage());
        }
    }

    /** Adds a todo when a description is present. */
    private LloydResponse addTodo(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed("Enter a task description.\nExample: todo read book");
        }

        int previousTaskCount = taskList.size();
        Todo todo = new Todo(command.getArguments());
        taskList.add(todo);
        assertTaskWasAppended(todo, previousTaskCount);
        if (!saveTasks()) {
            taskList.removeLast();
            assert taskList.size() == previousTaskCount
                    : "A failed save must restore the previous task count";
            return alarmed(SAVE_ERROR);
        }
        return delighted(createTaskAddedMessage(taskList.getLast(), taskList.size()));
    }

    /** Adds a deadline when its description and date are valid. */
    private LloydResponse addDeadline(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed(DEADLINE_FORMAT_GUIDANCE);
        }

        String deadlineDetails = command.getArguments();
        int byIndex = deadlineDetails.indexOf(DEADLINE_ARGUMENT_SEPARATOR);
        if (byIndex < 0) {
            return annoyed(DEADLINE_FORMAT_GUIDANCE);
        }

        String deadlineDescription = deadlineDetails.substring(0, byIndex).trim();
        String deadlineText = deadlineDetails.substring(
                byIndex + DEADLINE_ARGUMENT_SEPARATOR.length()).trim();
        if (deadlineDescription.isEmpty() || deadlineText.isEmpty()) {
            return annoyed(DEADLINE_FORMAT_GUIDANCE);
        }

        int previousTaskCount = taskList.size();
        Deadline deadline;
        try {
            LocalDate deadlineDate = LocalDate.parse(deadlineText, DEADLINE_FORMAT);
            deadline = new Deadline(deadlineDescription, deadlineDate);
            taskList.add(deadline);
        } catch (DateTimeParseException e) {
            return annoyed(DEADLINE_FORMAT_GUIDANCE);
        }
        assertTaskWasAppended(deadline, previousTaskCount);

        if (!saveTasks()) {
            taskList.removeLast();
            assert taskList.size() == previousTaskCount
                    : "A failed save must restore the previous task count";
            return alarmed(SAVE_ERROR);
        }
        return delighted(createTaskAddedMessage(taskList.getLast(), taskList.size()));
    }

    /** Adds an event when its description and endpoints are valid. */
    private LloydResponse addEvent(ParsedCommand command) {
        if (!command.hasArguments()) {
            return annoyed(EVENT_FORMAT_GUIDANCE);
        }

        String eventDetails = command.getArguments();
        int fromIndex = eventDetails.indexOf(EVENT_START_ARGUMENT_SEPARATOR);
        int toIndex = eventDetails.indexOf(
                EVENT_END_ARGUMENT_SEPARATOR,
                fromIndex + EVENT_START_ARGUMENT_SEPARATOR.length());
        if (fromIndex < 0 || toIndex < 0) {
            return annoyed(EVENT_FORMAT_GUIDANCE);
        }
        assert fromIndex < toIndex
                : "The /from delimiter must precede the /to delimiter";

        String eventDescription = eventDetails.substring(0, fromIndex).trim();
        String startText = eventDetails.substring(
                fromIndex + EVENT_START_ARGUMENT_SEPARATOR.length(), toIndex).trim();
        String endText = eventDetails.substring(
                toIndex + EVENT_END_ARGUMENT_SEPARATOR.length()).trim();
        if (eventDescription.isEmpty() || startText.isEmpty() || endText.isEmpty()) {
            return annoyed(EVENT_FORMAT_GUIDANCE);
        }

        int previousTaskCount = taskList.size();
        Event event;
        try {
            LocalDateTime start = LocalDateTime.parse(startText, EVENT_FORMAT);
            LocalDateTime end = LocalDateTime.parse(endText, EVENT_FORMAT);
            event = new Event(eventDescription, start, end);
            taskList.add(event);
        } catch (DateTimeParseException e) {
            return annoyed(EVENT_FORMAT_GUIDANCE);
        } catch (IllegalArgumentException e) {
            return annoyed("Enter an event end time that is not before its start.\n"
                    + "Example: event project meeting /from 06/08/2026 1400"
                    + " /to 06/08/2026 1600");
        }
        assertTaskWasAppended(event, previousTaskCount);

        if (!saveTasks()) {
            taskList.removeLast();
            assert taskList.size() == previousTaskCount
                    : "A failed save must restore the previous task count";
            return alarmed(SAVE_ERROR);
        }
        return delighted(createTaskAddedMessage(taskList.getLast(), taskList.size()));
    }

    /** Reports whether a one-based task number refers to an existing task. */
    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= taskList.size();
    }

    /** Returns the standard response for a numeric task index outside the list. */
    private String invalidTaskNumberMessage() {
        return "Enter the number of an existing task.";
    }

    /** Returns the standard response for a task number that is not numeric. */
    private String invalidNumberMessage() {
        return "Enter a numeric task number.";
    }

    /** Creates the standard response shown after adding any type of task. */
    private String createTaskAddedMessage(Task task, int taskCount) {
        assert task != null : "An added-task response must describe an existing task";
        assert taskCount == taskList.size()
                : "An added-task response must report the current task count";
        return "Task added:\n"
                + task
                + "\nTotal tasks: " + taskCount + ".";
    }

    /** Formats a heading and tasks as a one-based numbered list. */
    private static String formatNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder numberedTasks = new StringBuilder(heading).append("\n");
        for (int i = 0; i < tasks.size(); i++) {
            numberedTasks.append(String.format("%d. %s%n", i + 1, tasks.get(i)));
        }
        return numberedTasks.toString().stripTrailing();
    }

    /** Reports whether a deadline or event endpoint matches the checked date. */
    private boolean isScheduledOn(Task task, LocalDate checkedDate) {
        assert task != null : "Only tasks from the task list may be checked";
        assert checkedDate != null : "A schedule check requires a parsed date";
        if (task instanceof Deadline deadline) {
            return deadline.getBy().equals(checkedDate);
        }
        if (task instanceof Event event) {
            return event.getFrom().toLocalDate().equals(checkedDate)
                    || event.getTo().toLocalDate().equals(checkedDate);
        }
        return false;
    }

    /** Verifies the internal postconditions shared by all task-addition commands. */
    private void assertTaskWasAppended(Task addedTask, int previousTaskCount) {
        assert taskList.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";
        assert taskList.getLast() == addedTask
                : "A newly added task must be the final task in the list";
    }

    /** Saves the current tasks and reports whether the operation succeeded. */
    private boolean saveTasks() {
        try {
            storage.save(taskList.asList());
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Displays a response through the compatibility console interface. */
    private static void printResponse(String message) {
        Ui.showResponse(message);
    }

    /** Associates a dated task with its original task number for reminder output. */
    private record ReminderEntry(int taskNumber, Task task, LocalDate scheduledDate) {
    }
}
