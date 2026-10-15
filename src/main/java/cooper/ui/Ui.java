package cooper.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cooper.note.Note;
import cooper.note.NoteList;
import cooper.parser.NoteCommand.Operation;
import cooper.task.Task;

/**
 * Handles all text-based interaction with the user.
 */
public class Ui {
    private static final String INTRO = "Cooper here, your task co-pilot. What's our next mission?";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    private final Scanner scanner = new Scanner(System.in);

    /** Formats all notes or matching notes with their original collection numbers. */
    public String getNotesMessage(NoteList notes, boolean searching, String keyword) {
        List<Integer> numbers = searching ? notes.find(keyword)
                : IntStream.rangeClosed(1, notes.size()).boxed().toList();

        if (numbers.isEmpty()) {
            return searching ? "No matching notes found!"
                    : "Your mission notebook is empty. Start with: note add <text>";
        }

        String heading = searching ? "Here are the matching notes:" : "Here are your notes:";
        return heading + "\n" + numbers.stream()
                .map(number -> formatNote(number, notes.get(number)))
                .collect(Collectors.joining("\n"));
    }

    /** Returns the exact acknowledgement after a note mutation has been saved. */
    public String getNoteChangedMessage(Operation operation, int number, Note note, int count) {
        String heading = switch (operation) {
            case ADD -> "Logged in the mission notebook. I've added this note:";
            case EDIT -> "Got it. I've updated this note:";
            case DELETE -> "Noted. I've removed this note:";
            default -> throw new AssertionError("Expected a note mutation");
        };

        String message = heading + "\n" + formatNote(number, note);

        if (operation != Operation.EDIT) {
            message += "\nNow you have " + count + (count == 1 ? " note" : " notes") + " in the list.";
        }
        return message;
    }

    private String formatNote(int number, Note note) {
        return number + ".[N] " + note.text();
    }

    /** Explains that task commands remain usable after a notes loading failure. */
    public String getNotesLoadingErrorMessage() {
        return "I couldn't load your notes. Notes are unavailable until you fix the notes file "
                + "and restart me. Your tasks are still available.";
    }

    /**
     * Returns whether another command is available from standard input.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command from standard input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Returns tasks numbered from one, separated by newlines. */
    private String getNumFormattedString(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> String.format("%d.%s", i + 1, tasks.get(i)))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns Cooper's welcome message string
     * */
    public String getWelcomeMessage() {
        return INTRO;
    }

    /**
     * Returns Cooper's bye message string.
     * */
    public String getByeMessage() {
        return "I'm signing off. Until our next mission!";
    }

    /**
     * Displays an error message to the user.
     */
    public String getErrorMessage(String message) {
        return message;
    }

    /**
     * Displays a warning that saved tasks could not be loaded.
     */
    public String getLoadingErrorMessage() {
        return "I couldn't load your tasks. Fix the task file and restart me. Your notes are still available.";
    }

    /**
     * Returns message string of adding task
     *
     * @param task Task to be added
     * @param taskCount Number of tasks (after addition)
     * @return String of adding new task
     */
    public String getAddedTaskMessage(Task task, int taskCount) {
        return String.format("On the flight plan. I've added this task:\n%s\n%s",
                task.toString(), taskCountMessage(taskCount));
    }

    /**
     * Returns message string of deleting task
     *
     * @param task Task to be deleted
     * @param taskCount Number of tasks (after deletion)
     * @return String of deleting task
     */
    public String getDeletedTaskMessage(Task task, int taskCount) {
        return String.format("Noted. I've removed this task:\n%s\n%s", task.toString(), taskCountMessage(taskCount));
    }

    /**
     * Returns message string of marking task
     *
     * @param task Task to be marked
     * @return String of marking task
     */
    public String getMarkedTaskMessage(Task task) {
        return "Mission accomplished! I've marked this task as done:\n" + task.toString();
    }

    /**
     * Returns message string of unmarking task
     *
     * @param task Task to be unmarked
     * @return String of unmarking task
     */
    public String getUnmarkedTaskMessage(Task task) {
        return "Back on the flight plan. I've marked this task as not done yet:\n" + task + "\nKeep going! :)";
    }

    /**
     * Returns message string of listing tasks with one-based numbering, or an empty-list message if necessary.
     *
     * @param tasks List of tasks to display
     * @return String of all tasks in their order
     */
    public String getTaskListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your flight plan is clear. Ready when you are.";
        }

        StringBuilder message = new StringBuilder("Here's your flight plan:\n");
        String messageBody = getNumFormattedString(tasks);
        message.append(messageBody);

        return message.toString();
    }

    /**
     * Returns message string of matching tasks with one-based numbering, or an empty-list message if necessary.
     *
     * @param tasks List of matching tasks to display
     * @return String of all tasks in their order
     */
    public String getMatchingTasksMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks found!";
        }

        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:\n");
        String messageBody = getNumFormattedString(tasks);
        message.append(messageBody);

        return message.toString();
    }
    /**
     * Formats a date-time for display to the user.
     *
     * @param time Date-time to format.
     * @return Date-time in {@code MMM dd uuuu HH:mm} format.
     */
    public static String formatDate(LocalDateTime time) {
        return time.format(DISPLAY_FORMAT);
    }

    /** Builds a task-count message with the correct singular or plural noun. */
    private String taskCountMessage(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return String.format("\n\tNow you have %d %s in the list.", taskCount, noun);
    }
}
