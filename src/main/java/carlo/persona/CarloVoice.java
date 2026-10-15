package carlo.persona;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import carlo.task.Task;
import carlo.task.TaskGroup;
import carlo.task.TaskSchedule;

/**
 * Decides what Carlo says in response to what the user does, and what mood
 * he says it in. Keeping all of Carlo's lines here (rather than scattered
 * through the UI code) makes his personality easy to tweak in one place.
 *
 * <p>Carlo Acutis was a teenage programmer who loved video games, so his
 * lines are friendly and encouraging, with the odd gaming reference.
 */
public class CarloVoice {
    /** How far ahead Carlo looks when deciding whether to remind the user about something. */
    public static final Duration REMINDER_WINDOW = Duration.ofMinutes(30);

    private static final List<String> COMPLETION_LINES = List.of(
            "Nice one! \"%s\" is done.",
            "GG! \"%s\" cleared.",
            "\"%s\", done and dusted. Small steps add up!",
            "That's \"%s\" off your plate. Keep it up!");

    private final Random random;

    /**
     * Creates a voice that picks lines at random.
     */
    public CarloVoice() {
        this(new Random());
    }

    /**
     * Creates a voice that picks lines using the given random number
     * generator. Tests can pass a seeded {@link Random} to get the same
     * lines every time.
     *
     * @param random the source of randomness for picking lines
     */
    public CarloVoice(Random random) {
        this.random = random;
    }

    /**
     * Returns Carlo's opening briefing: a greeting for the time of day and
     * a summary of what needs attention.
     *
     * @param tasks all of the user's tasks
     * @param now the current date and time
     * @return the briefing message
     */
    public CarloMessage greet(List<Task> tasks, LocalDateTime now) {
        String greeting = greetingFor(now);
        List<Task> overdue = tasksIn(tasks, TaskGroup.OVERDUE, now);
        List<Task> soon = tasksIn(tasks, TaskGroup.SOON, now);

        if (!overdue.isEmpty()) {
            String text = overdue.size() == 1
                    ? "\"" + overdue.getFirst().getDescription() + "\" is overdue. "
                            + "No stress, let's tackle that one first?"
                    : "You have " + overdue.size() + " overdue tasks. "
                            + "Let's pick one and start there, one step at a time.";
            return new CarloMessage(greeting + " " + text, CarloMood.CONCERNED);
        }
        if (!soon.isEmpty()) {
            Task first = soon.getFirst();
            String text = "\"" + first.getDescription() + "\" is coming up ("
                    + TaskSchedule.describeTiming(first, now) + ")"
                    + (soon.size() > 1 ? ", plus " + (soon.size() - 1) + " more soon." : ".")
                    + " You've got this!";
            return new CarloMessage(greeting + " " + text, CarloMood.HEADS_UP);
        }
        if (tasks.stream().anyMatch(task -> !task.isDone())) {
            return new CarloMessage(greeting + " Nothing urgent today. "
                    + "A good day to get ahead on something from your list!", CarloMood.CHEERFUL);
        }
        return new CarloMessage(greeting + " Your list is clear! "
                + "Tell me what's coming up and I'll keep track of it.", CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's response to a task being added.
     *
     * @param task the task that was added
     * @param now the current date and time
     * @return the response message
     */
    public CarloMessage taskAdded(Task task, LocalDateTime now) {
        String text = "Got it! I've added \"" + task.getDescription() + "\"";
        String timing = TaskSchedule.describeTiming(task, now);
        if (!timing.isEmpty()) {
            text += " (" + timing + ")";
        }

        return switch (TaskSchedule.groupOf(task, now)) {
            case OVERDUE -> new CarloMessage(text + ". That one's already past, so let's get to it soon!",
                    CarloMood.CONCERNED);
            case SOON -> new CarloMessage(text + ". That's coming up quickly!", CarloMood.HEADS_UP);
            default -> new CarloMessage(text + ".", CarloMood.CHEERFUL);
        };
    }

    /**
     * Returns Carlo's response to a task being marked as done.
     *
     * @param task the task that was completed
     * @param openTaskCount how many tasks are still not done
     * @return the response message
     */
    public CarloMessage taskCompleted(Task task, long openTaskCount) {
        String line = COMPLETION_LINES.get(random.nextInt(COMPLETION_LINES.size()));
        String text = String.format(line, task.getDescription());
        if (openTaskCount == 0) {
            text += " That's everything on your list. Rest well, you've earned it!";
        }
        return new CarloMessage(text, CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's response to a completed task being marked as not done.
     *
     * @param task the task that was reopened
     * @return the response message
     */
    public CarloMessage taskReopened(Task task) {
        return new CarloMessage("No worries, I've put \"" + task.getDescription()
                + "\" back on your list.", CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's response to a task being deleted.
     *
     * @param task the task that was deleted
     * @return the response message
     */
    public CarloMessage taskDeleted(Task task) {
        return new CarloMessage("Removed \"" + task.getDescription() + "\". One less thing to juggle!",
                CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's explanation of how to add tasks, shown when the user types {@code help}.
     *
     * @return the help message
     */
    public CarloMessage help() {
        return new CarloMessage("""
                Type one of these below and press Enter:
                • todo read chapter 4
                • deadline essay /by 2026-10-15 1800
                • event retreat /from 2026-10-20 /to 2026-10-22
                Dates can be yyyy-mm-dd, d/m/yyyy, today or tomorrow, with an optional HHmm time. \
                Or press + for a form!""", CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's response to a task being edited.
     *
     * @param task the task after editing
     * @param now the current date and time
     * @return the response message
     */
    public CarloMessage taskEdited(Task task, LocalDateTime now) {
        String text = "Updated \"" + task.getDescription() + "\"";
        String timing = TaskSchedule.describeTiming(task, now);
        if (!timing.isEmpty() && !task.isDone()) {
            text += " (" + timing + ")";
        }
        return new CarloMessage(text + ".", CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's response to a change being undone.
     *
     * @param undoneAction the change that was undone, e.g. {@code deleting "essay"}
     * @return the response message
     */
    public CarloMessage undone(String undoneAction) {
        return new CarloMessage("Undid " + undoneAction + ". Everyone deserves a second chance!",
                CarloMood.CHEERFUL);
    }

    /**
     * Returns Carlo's reminder about a task that is starting or due soon.
     *
     * @param task the task to remind the user about
     * @param now the current date and time
     * @return the reminder message
     */
    public CarloMessage reminder(Task task, LocalDateTime now) {
        return new CarloMessage("Heads up! \"" + task.getDescription() + "\": "
                + TaskSchedule.describeTiming(task, now) + ".", CarloMood.HEADS_UP);
    }

    /**
     * Returns Carlo's response when something the user asked for went wrong.
     *
     * @param problem a description of what went wrong
     * @return the response message
     */
    public CarloMessage problem(String problem) {
        return new CarloMessage(problem, CarloMood.HEADS_UP);
    }

    private static String greetingFor(LocalDateTime now) {
        int hour = now.getHour();
        if (hour < 12) {
            return "Good morning!";
        }
        if (hour < 18) {
            return "Good afternoon!";
        }
        return "Good evening!";
    }

    private static List<Task> tasksIn(List<Task> tasks, TaskGroup group, LocalDateTime now) {
        return tasks.stream()
                .filter(task -> TaskSchedule.groupOf(task, now) == group)
                .sorted(TaskSchedule.bySoonestFirst())
                .toList();
    }
}
