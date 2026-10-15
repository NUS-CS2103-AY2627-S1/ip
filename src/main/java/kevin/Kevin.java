package kevin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

import kevin.task.Deadline;
import kevin.task.Event;
import kevin.task.Task;
import kevin.task.TaskList;
import kevin.task.ToDo;

/**
 * Chatbot named Kevin.
 * Handles all logic and runs the chatbot.
 */
public class Kevin {
    private Ui ui;
    private TaskList tasks;
    private Storage storage;

    /**
     * Creates Kevin.
     * Loads tasks from tasks.txt, or creates empty TaskList if does not exist.
     *
     * @param filePath tasks.txt.
     */
    public Kevin(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            this.tasks = storage.load();
            ui.storeLoadingTasksMessage(storage.createLoadingTasksMessage());
        } catch (KevinException e) {
            ui.storeLoadingTasksMessage(e.getMessage());
            this.tasks = new TaskList();
        }
    }

    /**
     * Runs the chatbot.
     */
    public void run() {
        Parser parser = new Parser(ui.start());
        boolean isNotBye = true;

        while (isNotBye) {
            String response = respond(parser);
            ui.print(response);
            if (parser.isBye()) {
                break;
            }
            parser = new Parser(ui.readNextLine());
        }
    }

    /**
     * Executes logic of each command and responds with a String.
     *
     * @param parser Parser containing input.
     */
    public String respond(Parser parser) {
        try {
            if (parser.isList()) {
                return tasks.list();

            } else if (parser.startsWith("mark")) {
                return handleMark(parser);

            } else if (parser.startsWith("unmark")) {
                return handleUnmark(parser);

            } else if (parser.startsWith("todo")) {
                return handleToDo(parser);

            } else if (parser.startsWith("deadline")) {
                return handleDeadline(parser);

            } else if (parser.startsWith("event")) {
                return handleEvent(parser);

            } else if (parser.startsWith("delete")) {
                return handleDelete(parser);

            } else if (parser.startsWith("find")) {
                String keyword = parser.parseKeyword();
                return tasks.find(keyword);

            } else if (parser.startsWith("snooze")) {
              return handleSnooze(parser);

            } else if (parser.isBye()) {
                return "Bye. Hope I was of assistance to you!";
            } else {
                return "??? Sorry but I don't speak gibberish.\n";
            }
        } catch (KevinException e) {
            return e.getMessage() + '\n';
        }
    }

    /**
     * Handles mark commands.
     * Parses taskIndex, marks Task and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If input does not have a task number.
     */
    public String handleMark(Parser parser) throws KevinException {
        int taskIndex = parser.parseIndex("mark");
        Task markedTask = tasks.mark(taskIndex);
        storage.save(tasks);

        return "Yay...finally done:\n  "
                + markedTask + "\n";
    }

    /**
     * Handles unmark commands.
     * Parses taskIndex, unmarks Task and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If input does not have a task number.
     */
    public String handleUnmark(Parser parser) throws KevinException {
        int taskIndex = parser.parseIndex("unmark");
        Task unmarkedTask = tasks.unmark(taskIndex);
        storage.save(tasks);

        return "Huh...havent finish ah:\n  "
                + unmarkedTask + "\n";
    }

    /**
     * Handles snooze commands.
     * Parses taskIndex, extracts later DateTimes,
     * snoozes task to later date and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If try to snooze a Task with no date eg ToDo.
     */
    public String handleSnooze(Parser parser) throws KevinException {
        int taskIndex = parser.parseIndex("snooze");
        Task taskToSnooze = tasks.get(taskIndex);

        if (taskToSnooze instanceof Deadline deadline) {
            LocalDateTime byDate = parseDateTimeString(parser.parseByDate());
            deadline.snooze(byDate);
        } else if (taskToSnooze instanceof Event event) {
            Matcher matcher = parser.parseFromAndToDate();
            LocalDateTime fromDate = parseDateTimeString(matcher.group("from"));
            LocalDateTime toDate = parseDateTimeString(matcher.group("to"));

            event.snooze(fromDate, toDate);
        } else {
            throw new KevinException("Cannot snooze a task with no date.");
        }
        storage.save(tasks);

        return "Snoozed:\n  " + taskToSnooze + "\n";
    }

    /**
     * Handles delete commands.
     * Parses taskIndex, deletes Task and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If input does not have a task number.
     */
    public String handleDelete(Parser parser) throws KevinException {
        int taskIndex = parser.parseIndex("delete");
        Task deletedTask = tasks.delete(taskIndex);
        storage.save(tasks);

        return "Say goodbye to:\n  "
                + deletedTask + "\n'Only' " + tasks.size()
                + " tasks left...\n";
    }

    /**
     * Handles ToDo commands.
     * Parses description, adds ToDo to TaskList and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If input does not have a description or a by date.
     */
    public String handleToDo(Parser parser) throws KevinException {
        String description = parser.parseToDo();
        ToDo todo = new ToDo(description);
        tasks.add(todo);
        storage.save(tasks);

        return "Sigh...another one:\n  " + todo
                + "\n'Only' " + tasks.size() + " tasks left...\n";
    }

    /**
     * Handles Deadline commands.
     * Parses description, by date, adds Deadline to TaskList and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If input does not have a description or a /by date.
     */
    public String handleDeadline(Parser parser) throws KevinException {
        Matcher matcher = parser.parseDeadline();

        String description = matcher.group("description");
        LocalDateTime byDateTime = parseDateTimeString(matcher.group("by"));
        Deadline deadline = new Deadline(description, byDateTime);

        tasks.add(deadline);
        storage.save(tasks);

        return "Sigh...another one:\n  " + deadline
                + "\n'Only' " + tasks.size() + " tasks left...\n";
    }

    /**
     * Handles Event commands.
     * Parses description, from and to date, adds Event to TaskList and saves updated TaskList.
     *
     * @param parser Parser containing input.
     * @return Response to input.
     * @throws KevinException If input does not have a description, /from date or /to date.
     */
    public String handleEvent(Parser parser) throws KevinException {
        Matcher matcher = parser.parseEvent();

        String description = matcher.group("description");
        LocalDateTime from = parseDateTimeString(matcher.group("from"));
        LocalDateTime to = parseDateTimeString(matcher.group("to"));
        Event event = new Event(description, from, to);

        tasks.add(event);
        storage.save(tasks);

        return "Sigh...another one:\n  " + event
                + "\n'Only' " + tasks.size() + " tasks left...\n";
    }

    /**
     * Starts the chatbot.
     */
    public static void main(String[] args)  {
        new Kevin("data/tasks.txt").run();
    }

    /**
     * Parses DateTimeString from the user input into LocalDateTime.
     *
     * @throws KevinException If user input wrong format for date time.
     */
    public static LocalDateTime parseDateTimeString(String dateTimeString) throws KevinException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[d/M/yy hmma][d/M/yy ha]");
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (Exception e) {
            throw new KevinException("Invalid DateTime format. Please input date time using D/M/YY Ham/pm or HMMam/pm");
        }
    }

    /**
     * Gets Banner from UI as a String.
     */
    public String getBanner() {
        return ui.createBanner();
    }
}
