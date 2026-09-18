package lune;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lune.exception.LuneException;
import lune.task.Deadline;
import lune.task.Event;
import lune.task.Task;
import lune.task.TaskList;
import lune.task.Todo;

/**
 * Entry point and console loop for Lune, a CLI task-tracking chatbot.
 * Reads commands from stdin, dispatches them via processCommand(), and
 * persists the task list to disk after every successful change. Also
 * exposes getResponse(), an instance method the JavaFX GUI (lune.gui) uses
 * to reuse this exact same command logic outside the console loop.
 */
public class Lune {
    private static final String LINE =
            "    ____________________________________________________________\n";
    // Every line of a successful command's message (built in the handleX
    // methods below) is hand-indented by this many spaces, to line up under
    // LINE in the console; dedent() strips exactly this much back off.
    private static final int CONSOLE_INDENT_WIDTH = 5;
    private static final Path SAVE_FILE = Path.of("data", "lune.txt");
    private static final Path ARCHIVE_FILE = Path.of("data", "archive.txt");
    // Path's own toString() uses the OS's native separator (e.g. backslash
    // on Windows), which is accurate but makes messages look different
    // across OSes — this fixed, forward-slash form is for display only;
    // ARCHIVE_FILE itself is still what's actually used for file I/O.
    private static final String ARCHIVE_FILE_DISPLAY = "data/archive.txt";
    // Accepted alongside plain "yyyy-mm-dd" (tried first, via LocalDate.parse):
    // a date with a time attached, e.g. "2/12/2019 1800" for 6pm on 2 Dec 2019.
    // "uuuu" (proleptic year), not "yyyy" (year-of-era) — STRICT resolution
    // can't resolve a year-of-era without an explicit era in the input.
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    // Display format for an archive session's timestamp header, e.g.
    // "Sep 10 2026, 2:30 PM".
    private static final DateTimeFormatter ARCHIVE_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    private final TaskList tasks;
    private CommandType lastCommandType = CommandType.UNKNOWN;

    /**
     * The commands processCommand() can dispatch on. Enum constant names
     * double as the literal command word (lowercased via word()), so adding
     * a command word to check for is a one-line change instead of a new
     * string literal scattered across an if-else chain.
     *
     * Package-private (not private) so LuneTest can construct commands and
     * exercise fromInput() directly.
     */
    enum CommandType {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, FIND, ARCHIVE, UNKNOWN;

        /**
         * Returns this command's literal word as typed by the user, e.g.
         * "todo" for TODO.
         */
        String word() {
            return name().toLowerCase();
        }

        /**
         * Maps an input line's leading word to the matching CommandType,
         * or UNKNOWN if it doesn't match any recognized command (including
         * "bye", which is handled separately in main() rather than here).
         */
        static CommandType fromInput(String input) {
            String word = input.contains(" ") ? input.substring(0, input.indexOf(' ')) : input;
            return Arrays.stream(values())
                    .filter(type -> type != UNKNOWN && type.word().equals(word))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }

    /**
     * Creates a new Lune, loading any previously-saved tasks from disk.
     */
    public Lune() {
        this.tasks = new TaskList(loadTasks());
    }

    /**
     * Prints the banner/greeting, loads any saved tasks, then reads and
     * executes commands from stdin until "bye" or input runs out.
     */
    public static void main(String[] args) {
        // Windows consoles often default to a legacy, non-UTF-8 code page,
        // which mangles characters like the em dash used throughout Lune's
        // messages into "�" — forcing UTF-8 here fixes that regardless of
        // the terminal's own code page. The GUI is unaffected either way,
        // since JavaFX renders text directly with Unicode-aware fonts.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        String banner = " _                     \n"
                + "| |   _   _ _ __   ___ \n"
                + "| |  | | | | '_ \\ / _ \\\n"
                + "| |__| |_| | | | |  __/\n"
                + "|_____\\__,_|_| |_|\\___|\n";
        System.out.println(banner);

        System.out.println(LINE + "     Oh, hey — I'm Lune.\n     What's rattling around in that head of yours?\n"
                + LINE);

        // TaskList grows as needed, so there's no fixed task limit to enforce.
        TaskList tasks = new TaskList(loadTasks());

        // Scanner is enough here since input is just read line-by-line;
        // no need for buffered/streamed reading at this stage.
        // hasNextLine() guards against input ending without "bye" (e.g. a
        // piped file, or Ctrl+D) — without it, nextLine() throws
        // NoSuchElementException once stdin is exhausted.
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().strip();
            if (input.equals("bye")) {
                break;
            }
            try {
                String message = processCommand(input, tasks);
                System.out.println(LINE + message + LINE);
                saveTasks(tasks);
            } catch (LuneException e) {
                System.out.println(LINE + "     " + e.getMessage() + "\n" + LINE);
            }
        }
        System.out.println(LINE + "     Bye! Go forth and be marginally more organized.\n" + LINE);
    }

    /**
     * Generates Lune's response to one line of user input, for the GUI to
     * display in a chat bubble. Reuses the exact same command logic as the
     * console loop in main() (processCommand()), but returns plain text
     * instead of the console's line-separator/indent-prefixed formatting.
     */
    public String getResponse(String rawInput) {
        String input = rawInput.strip();
        if (input.equals("bye")) {
            lastCommandType = CommandType.UNKNOWN;
            return "Bye! Go forth and be marginally more organized.";
        }
        lastCommandType = CommandType.fromInput(input);
        try {
            String message = processCommand(input, tasks);
            saveTasks(tasks);
            return dedent(message);
        } catch (LuneException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns the CommandType of the most recent command handled by
     * getResponse(), as its lowercase word (e.g. "todo", "mark"). Lets the
     * GUI style each reply bubble according to what kind of command
     * produced it.
     */
    public String getCommandType() {
        return lastCommandType.word();
    }

    /**
     * Executes one non-"bye" command and returns the resulting message
     * (formatted for the console: each line prefixed with indentation,
     * the way main() prints it). Throws LuneException, with a message
     * meant to be shown to the user as-is, for any command Lune can't
     * carry out.
     *
     * <p>Package-private (not private) so LuneTest can exercise it
     * directly, for every case except ARCHIVE — that one performs real
     * file I/O and is covered by the console-level test-ui suite instead.</p>
     */
    static String processCommand(String input, TaskList tasks) throws LuneException {
        switch (CommandType.fromInput(input)) {
            case LIST:
                return handleList(tasks);
            case MARK:
                return handleMark(input, tasks);
            case UNMARK:
                return handleUnmark(input, tasks);
            case DELETE:
                return handleDelete(input, tasks);
            case TODO:
                return handleTodo(input, tasks);
            case DEADLINE:
                return handleDeadline(input, tasks);
            case EVENT:
                return handleEvent(input, tasks);
            case ON:
                return handleOn(input, tasks);
            case FIND:
                return handleFind(input, tasks);
            case ARCHIVE:
                if (!input.equals("archive")) {
                    throw new LuneException("Ugh, archive doesn't take any extra words... just try: archive");
                }
                return archiveTasks(tasks);
            case UNKNOWN:
                // Fallthrough
            default:
                throw new LuneException("*sigh* I don't recognize that command... try todo, deadline, event, "
                        + "list, mark, unmark, delete, on, find, archive, or bye.");
        }
    }

    /**
     * Handles "list": renders every task in the list, numbered.
     */
    private static String handleList(TaskList tasks) {
        return "     Here's what you've got going on:\n" + formatNumbered(tasks, i -> true);
    }

    /**
     * Handles "mark &lt;n&gt;": marks the given task as done.
     */
    private static String handleMark(String input, TaskList tasks) throws LuneException {
        int index = parseTaskIndex(input, CommandType.MARK, tasks.size());
        tasks.get(index).markAsDone();
        return "     Oh, satisfying. Marked as done:\n"
                + "       " + tasks.get(index) + "\n";
    }

    /**
     * Handles "unmark &lt;n&gt;": marks the given task as not done.
     */
    private static String handleUnmark(String input, TaskList tasks) throws LuneException {
        int index = parseTaskIndex(input, CommandType.UNMARK, tasks.size());
        tasks.get(index).markAsUndone();
        return "     Fair enough, back to not-done:\n"
                + "       " + tasks.get(index) + "\n";
    }

    /**
     * Handles "delete &lt;n&gt;": removes the given task from the list.
     */
    private static String handleDelete(String input, TaskList tasks) throws LuneException {
        int index = parseTaskIndex(input, CommandType.DELETE, tasks.size());
        Task removed = tasks.remove(index);
        return "     Poof. Gone:\n"
                + "       " + removed + "\n"
                + "     That's " + tasks.size() + " task(s) on the board now.\n";
    }

    /**
     * Handles "todo &lt;description&gt;": adds a new Todo, after validating
     * the description is non-empty, contains no save-format delimiter, and
     * isn't a duplicate of an existing todo.
     */
    private static String handleTodo(String input, TaskList tasks) throws LuneException {
        String description = input.startsWith("todo ")
                ? collapseWhitespace(input.substring("todo ".length())) : "";
        if (description.isEmpty()) {
            throw new LuneException("Ugh, a todo needs a description... try: todo <what to do>");
        }
        if (description.contains(" | ")) {
            throw new LuneException("Ugh, a description can't contain \" | \"... try phrasing it "
                    + "differently.");
        }
        if (isDuplicateDescription(tasks, Todo.class, description)) {
            throw new LuneException("Ugh, you already have a todo like that: " + description);
        }
        tasks.add(new Todo(description));
        return formatAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Handles "deadline &lt;description&gt; /by &lt;date&gt;": adds a new
     * Deadline, after the same description validation as handleTodo(), plus
     * checks specific to the /by clause (present, not duplicated, non-empty,
     * a valid date/time).
     */
    private static String handleDeadline(String input, TaskList tasks) throws LuneException {
        String rest = input.startsWith("deadline ") ? input.substring("deadline ".length()) : "";
        int byIndex = rest.indexOf(" /by ");
        String description = collapseWhitespace(byIndex == -1 ? rest : rest.substring(0, byIndex));
        if (description.isEmpty()) {
            throw new LuneException("Ugh, a deadline needs a description... "
                    + "try: deadline <what to do> /by <date>");
        }
        if (byIndex == -1) {
            throw new LuneException("*sigh* — a deadline needs a /by date... "
                    + "try: deadline " + description + " /by <date>");
        }
        if (description.contains(" | ")) {
            throw new LuneException("Ugh, a description can't contain \" | \"... try phrasing it "
                    + "differently.");
        }
        if (isDuplicateDescription(tasks, Deadline.class, description)) {
            throw new LuneException("Ugh, you already have a deadline like that: " + description);
        }
        if (rest.indexOf(" /by ", byIndex + 1) != -1) {
            throw new LuneException("Ugh, you specified /by more than once... "
                    + "try: deadline <what to do> /by <date>");
        }
        String byText = collapseWhitespace(rest.substring(byIndex + " /by ".length()));
        if (byText.isEmpty()) {
            throw new LuneException("Ugh, a deadline's /by date can't be empty.");
        }
        LocalDateTime by = parseDateTime("/by", byText);
        tasks.add(new Deadline(description, by));
        return formatAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Handles "event &lt;description&gt; /from &lt;date&gt; /to &lt;date&gt;":
     * adds a new Event, after the same description validation as
     * handleTodo(), plus checks specific to /from and /to (present, not
     * duplicated, non-empty, valid dates/times, and /to not before /from).
     */
    private static String handleEvent(String input, TaskList tasks) throws LuneException {
        String rest = input.startsWith("event ") ? input.substring("event ".length()) : "";
        int fromIndex = rest.indexOf(" /from ");
        int toIndex = rest.indexOf(" /to ");
        String description = collapseWhitespace(fromIndex == -1 ? rest : rest.substring(0, fromIndex));
        if (description.isEmpty()) {
            throw new LuneException("Ugh, an event needs a description... "
                    + "try: event <what to do> /from <date> /to <date>");
        }
        if (fromIndex == -1) {
            throw new LuneException("*sigh* — an event needs a /from date... "
                    + "try: event " + description + " /from <date> /to <date>");
        }
        if (toIndex == -1 || toIndex < fromIndex) {
            throw new LuneException("Mm, an event needs a /to date after /from... "
                    + "try: event " + description + " /from <date> /to <date>");
        }
        if (description.contains(" | ")) {
            throw new LuneException("Ugh, a description can't contain \" | \"... try phrasing it "
                    + "differently.");
        }
        if (isDuplicateDescription(tasks, Event.class, description)) {
            throw new LuneException("Ugh, you already have an event like that: " + description);
        }
        if (rest.indexOf(" /from ", fromIndex + 1) != -1) {
            throw new LuneException("Ugh, you specified /from more than once... "
                    + "try: event <what to do> /from <date> /to <date>");
        }
        if (rest.indexOf(" /to ", toIndex + 1) != -1) {
            throw new LuneException("Ugh, you specified /to more than once... "
                    + "try: event <what to do> /from <date> /to <date>");
        }
        String fromText = collapseWhitespace(rest.substring(fromIndex + " /from ".length(), toIndex));
        String toText = collapseWhitespace(rest.substring(toIndex + " /to ".length()));
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new LuneException("Ugh, an event's /from and /to dates can't be empty.");
        }
        LocalDateTime from = parseDateTime("/from", fromText);
        LocalDateTime to = parseDateTime("/to", toText);
        if (to.isBefore(from)) {
            throw new LuneException("Ugh, an event's /to can't be before its /from...");
        }
        tasks.add(new Event(description, from, to));
        return formatAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Handles "on &lt;date&gt;": lists every deadline/event occurring on
     * that date.
     */
    private static String handleOn(String input, TaskList tasks) throws LuneException {
        String text = input.equals("on") ? "" : collapseWhitespace(input.substring("on ".length()));
        if (text.isEmpty()) {
            throw new LuneException("Ugh, tell me which date... try: on <date>");
        }
        LocalDate queryDate = parseDateTime("on", text).toLocalDate();
        return "     Here's what's happening on " + Task.formatDate(queryDate) + ":\n"
                + formatNumbered(tasks, i -> tasks.get(i).occursOn(queryDate));
    }

    /**
     * Handles "find &lt;keyword&gt;": lists every task whose description
     * contains keyword (case-insensitive).
     */
    private static String handleFind(String input, TaskList tasks) throws LuneException {
        String keyword = input.equals("find") ? "" : collapseWhitespace(input.substring("find ".length()));
        if (keyword.isEmpty()) {
            throw new LuneException("Ugh, tell me what to search for... try: find <keyword>");
        }
        String lowerKeyword = keyword.toLowerCase();
        IntPredicate matchesKeyword = i -> tasks.get(i).getDescription().toLowerCase()
                .contains(lowerKeyword);
        return "     Here's what matched your search:\n" + formatNumbered(tasks, matchesKeyword);
    }

    /**
     * Parses and validates the task number argument of a mark/unmark/delete
     * command (e.g. "mark 2"), returning it as a 0-based index. Throws if
     * the argument is missing, isn't a number, or is out of range for the
     * current task count.
     */
    static int parseTaskIndex(String input, CommandType command, int taskCount) throws LuneException {
        String commandWord = command.word();
        String arg = input.equals(commandWord) ? "" : input.substring(commandWord.length() + 1).trim();
        if (arg.isEmpty()) {
            throw new LuneException("Ugh, which task number should I " + commandWord
                    + "? Try: " + commandWord + " 2");
        }
        int number;
        try {
            number = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new LuneException("Mm, \"" + arg + "\" doesn't look like a task number.");
        }
        if (number < 1 || number > taskCount) {
            throw new LuneException("Ugh, task " + number + " doesn't exist... "
                    + "you currently have " + taskCount + " task(s).");
        }
        int index = number - 1;
        // Bad user input is already rejected above via LuneException; this
        // documents that the checks above are themselves correct, i.e. any
        // number that reaches here truly does convert to a valid 0-based
        // index — a bug here would be in this method's own logic, not the
        // user's command.
        assert index >= 0 && index < taskCount : "validated index must be within bounds";
        return index;
    }

    /**
     * Parses a user-supplied date/time, trying "yyyy-mm-dd" (as a start of
     * day) first, then "d/m/yyyy HHmm". label identifies which field this
     * is (e.g. "/by") for the error message if neither format matches.
     */
    static LocalDateTime parseDateTime(String label, String text) throws LuneException {
        try {
            return LocalDate.parse(text).atStartOfDay();
        } catch (DateTimeParseException isoFailure) {
            try {
                return LocalDateTime.parse(text, SLASH_DATE_TIME_FORMAT);
            } catch (DateTimeParseException slashFailure) {
                throw new LuneException("Ugh, \"" + text + "\" isn't a valid " + label
                        + " date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).");
            }
        }
    }

    /**
     * Renders every task whose (0-based) position in tasks satisfies
     * matches, each as "     &lt;n&gt;.&lt;task&gt;\n" numbered by that
     * original position — not by its position among the matches. Shared by
     * "list" (matches everything), "on" (matches by date), and "find"
     * (matches by keyword).
     */
    private static String formatNumbered(TaskList tasks, IntPredicate matches) {
        return IntStream.range(0, tasks.size())
                .filter(matches)
                .mapToObj(i -> "     " + (i + 1) + "." + tasks.get(i) + "\n")
                .collect(Collectors.joining());
    }

    /**
     * Handles "archive": appends every current task, as a timestamped,
     * human-readable record, to ARCHIVE_FILE, then clears the list so the
     * user can start fresh. Writes nothing to ARCHIVE_FILE when the list is
     * already empty — there's nothing meaningful to record.
     */
    private static String archiveTasks(TaskList tasks) {
        int archivedCount = tasks.size();
        if (archivedCount > 0) {
            String session = "Archived on " + LocalDateTime.now().format(ARCHIVE_TIMESTAMP_FORMAT) + ":\n"
                    + formatArchiveEntry(tasks) + "\n";
            try {
                Files.createDirectories(ARCHIVE_FILE.getParent());
                Files.writeString(ARCHIVE_FILE, session, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                return "     Ugh, I couldn't write to the archive file: " + e.getMessage() + "\n";
            }
            tasks.clear();
        }
        return "     Archived " + archivedCount + " task(s) to " + ARCHIVE_FILE_DISPLAY + ".\n"
                + "     Ah, a blank slate. Delightful.\n";
    }

    /**
     * Renders every task as "&lt;n&gt;.&lt;task&gt;\n", numbered from 1, for
     * ARCHIVE_FILE — a plain record on disk, so unlike formatNumbered() this
     * has no console-specific indent to line up under LINE.
     */
    private static String formatArchiveEntry(TaskList tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i) + "\n")
                .collect(Collectors.joining());
    }

    /**
     * Collapses any run of whitespace in text down to a single space, and
     * strips leading/trailing whitespace. Applied to every free-text field
     * (descriptions, date text) so "read   book" and "read book" are the
     * same input, and so extra spacing around a date's time component
     * (e.g. "2/12/2019   1800") doesn't stop it from parsing.
     */
    static String collapseWhitespace(String text) {
        return text.strip().replaceAll("\\s+", " ");
    }

    /**
     * Returns whether tasks already contains a task of exactly the same
     * type as type with the same description (case-insensitive). Used to
     * reject adding an accidental duplicate.
     */
    static boolean isDuplicateDescription(TaskList tasks, Class<? extends Task> type, String description) {
        return tasks.stream().anyMatch(t -> t.getClass() == type && t.getDescription().equalsIgnoreCase(description));
    }

    private static String formatAdded(Task task, int taskCount) {
        return "     Added. One more thing to think about:\n"
                + "       " + task + "\n"
                + "     That's " + taskCount + " task(s) on the board now.\n";
    }

    /**
     * Strips processCommand()'s console-only formatting (the fixed 5-space
     * indent every line is prefixed with, for lining up under LINE) so the
     * message reads cleanly as plain text in a GUI chat bubble. A line
     * that had extra indentation beyond the fixed 5 spaces (e.g. a task
     * shown under a confirmation message) keeps that extra indent.
     */
    private static String dedent(String message) {
        StringBuilder result = new StringBuilder();
        for (String line : message.split("\n", -1)) {
            // dedent() is only ever called on processCommand()'s successful
            // return value (getResponse() returns LuneException messages,
            // which have no such indent, unchanged) — every non-empty line
            // of that value is built with the fixed 5-space console indent,
            // so this should always hold. The substring branch below stays
            // as a safety net in case that construction invariant is ever
            // broken by a future edit.
            assert line.isEmpty() || line.length() >= 5 : "processCommand() success messages are always indented";
            result.append(line.length() >= 5 ? line.substring(5) : line).append("\n");
        }
        return result.toString().strip();
    }

    /**
     * Writes every task to SAVE_FILE, one per line, overwriting whatever was
     * there before. Called after every successful command so the file on
     * disk always reflects the current in-memory list.
     */
    private static void saveTasks(TaskList tasks) {
        // Each task contributes its own trailing "\n" (rather than joining
        // with "\n" as a separator) so an empty task list still produces an
        // empty string, matching the original loop's behavior exactly.
        String content = tasks.stream()
                .map(task -> task.toSaveFormat() + "\n")
                .collect(Collectors.joining());
        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.writeString(SAVE_FILE, content);
        } catch (IOException e) {
            System.out.println(LINE + "     Ugh, I couldn't save your tasks to disk: "
                    + e.getMessage() + "\n" + LINE);
        }
    }

    /**
     * Reads SAVE_FILE (in the format written by saveTasks()) and rebuilds
     * the task list from it. Returns an empty list if the file doesn't
     * exist yet (e.g. first run). A line that can't be parsed (hand-edited
     * or corrupted) is skipped individually, with a warning, rather than
     * discarding every other task in the file.
     *
     * Known limitation: a task whose description/by/from/to itself contains
     * the literal " | " delimiter will not round-trip correctly (it'll be
     * skipped as malformed on the next load), since the save format doesn't
     * escape the delimiter. Not fixed here — full escaping is out of
     * proportion for what this file format needs to do.
     */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(SAVE_FILE)) {
            return tasks;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(SAVE_FILE);
        } catch (IOException e) {
            System.out.println(LINE + "     Ugh, I couldn't read " + SAVE_FILE + " ("
                    + e.getMessage() + ")... starting with an empty list.\n" + LINE);
            return tasks;
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseSavedTask(line));
            } catch (IllegalArgumentException e) {
                System.out.println(LINE + "     Ugh, skipping unreadable line " + (i + 1)
                        + " in " + SAVE_FILE + ": " + e.getMessage() + "\n" + LINE);
            }
        }
        return tasks;
    }

    /**
     * Parses one line of the on-disk save format (as written by
     * saveTasks()) into the matching Task. Throws IllegalArgumentException,
     * describing exactly what's wrong, for any line that doesn't have the
     * right number of fields, an unknown type letter, an invalid done
     * flag, an empty description, or an invalid date/time.
     */
    static Task parseSavedTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("expected at least 3 fields (type | done | description), found "
                    + parts.length);
        }
        String type = parts[0];
        String doneFlag = parts[1];
        String description = parts[2];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new IllegalArgumentException("done flag must be \"0\" or \"1\", found \"" + doneFlag + "\"");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("description can't be empty");
        }
        Task task;
        switch (type) {
            case "T":
                task = parseSavedTodo(parts, description);
                break;
            case "D":
                task = parseSavedDeadline(parts, description);
                break;
            case "E":
                task = parseSavedEvent(parts, description);
                break;
            default:
                throw new IllegalArgumentException("unknown task type \"" + type + "\"");
        }
        // Every case above either assigns task or throws, so the compiler
        // already treats task as definitely assigned here; this documents
        // that invariant explicitly, so it fails loudly if a future case is
        // ever added that forgets to do either.
        assert task != null : "every switch case above must assign task or throw";
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Builds a Todo from a save-file line's fields (type "T"): exactly 3
     * fields, with no extra date fields.
     */
    private static Task parseSavedTodo(String[] parts, String description) {
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "a todo (T) line needs exactly 3 fields, found " + parts.length);
        }
        return new Todo(description);
    }

    /**
     * Builds a Deadline from a save-file line's fields (type "D"): exactly
     * 4 fields, with a non-empty /by date/time.
     */
    private static Task parseSavedDeadline(String[] parts, String description) {
        if (parts.length != 4 || parts[3].isBlank()) {
            throw new IllegalArgumentException(
                    "a deadline (D) line needs exactly 4 fields with a non-empty /by, found "
                            + parts.length);
        }
        return new Deadline(description, parseSavedDateTime(parts[3]));
    }

    /**
     * Builds an Event from a save-file line's fields (type "E"): exactly 5
     * fields, with non-empty /from and /to date/times.
     */
    private static Task parseSavedEvent(String[] parts, String description) {
        if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
            throw new IllegalArgumentException(
                    "an event (E) line needs exactly 5 fields with non-empty /from and /to, found "
                            + parts.length);
        }
        return new Event(description, parseSavedDateTime(parts[3]), parseSavedDateTime(parts[4]));
    }

    /**
     * Parses a date/time field from the on-disk save format (LocalDateTime's
     * own ISO string form), throwing IllegalArgumentException with a clear
     * message if it isn't valid.
     */
    static LocalDateTime parseSavedDateTime(String text) {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("\"" + text + "\" isn't a valid saved date/time");
        }
    }
}
