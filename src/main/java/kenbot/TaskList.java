package kenbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;

import kenbot.task.Tag;
import kenbot.task.Task;

/**
 * Holds the user's tasks.
 *
 * <p>The list is private, so the only way to reach a task is through the
 * methods below. Every one of them checks the task number before using it,
 * which stops an invalid number from ever reaching the list.</p>
 */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Creates an empty task list. */
    public TaskList() {
    }

    /**
     * Creates a task list holding the given tasks, used when loading a saved
     * list from the hard disk.
     *
     * @param initialTasks the tasks to start with, in the order they are stored
     */
    public TaskList(List<Task> initialTasks) {
        // Storage always hands over a list, empty at worst, even when the save
        // file is missing or unreadable. A null here would mean Storage broke
        // that promise, and would otherwise surface much later as a confusing
        // NullPointerException.
        assert initialTasks != null : "Storage must supply a list, not null";
        tasks.addAll(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * <p>An exact copy of a task already stored is refused. Adding the same
     * thing twice is almost always a double keypress rather than an intention,
     * and two identical lines are impossible to tell apart afterwards when
     * marking or deleting one of them.</p>
     *
     * @param task the task to store
     * @throws KenbotException if the same task is already in the list
     */
    public void add(Task task) throws KenbotException {
        for (Task existing : tasks) {
            if (existing.isSameTask(task)) {
                throw new KenbotException("That's already on the list:\n  " + existing);
            }
        }
        tasks.add(task);
    }

    /**
     * Removes a task from the list.
     *
     * <p>Tasks after the removed one move up, so the numbers shown by
     * {@code describe} always run from 1 with no gaps.</p>
     *
     * @param argument the task number, as the user typed it
     * @return the task that was removed, so the caller can display it
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    public Task delete(String argument) throws KenbotException {
        return tasks.remove(indexOf(argument, "delete"));
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the stored tasks for reading, without exposing the real list.
     *
     * <p>A copy is returned so a caller cannot add or remove tasks behind this
     * class's back; every change still has to go through {@code add} or
     * {@code delete}.</p>
     *
     * @return the tasks, in the order they are stored
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Marks a task as done.
     *
     * @param argument the task number, as the user typed it
     * @return the task that was marked, so the caller can display it
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    public Task mark(String argument) throws KenbotException {
        Task task = tasks.get(indexOf(argument, "mark"));
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as not done.
     *
     * @param argument the task number, as the user typed it
     * @return the task that was unmarked, so the caller can display it
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    public Task unmark(String argument) throws KenbotException {
        Task task = tasks.get(indexOf(argument, "unmark"));
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the whole list as text, ready to be displayed.
     *
     * <p>Text is returned rather than printed so the same method can serve the
     * console today and a graphical window later.</p>
     *
     * @return the numbered list, or a note that there is nothing in it
     */
    public String describe() {
        if (tasks.isEmpty()) {
            return "Nothing on the list yet.";
        }
        return formatNumbered("Here's what you've got:", tasks);
    }

    /**
     * Returns the tasks whose description contains the given text.
     *
     * <p>Upper and lower case are ignored, and part of a word counts, so
     * {@code find book} also finds "bookshop". Matches are numbered from 1
     * rather than keeping their positions in the full list, so the numbers
     * shown here are not the ones to use with {@code mark} or {@code delete}.</p>
     *
     * @param keyword the text to look for
     * @return the matching tasks as numbered text, or a note that none matched
     * @throws KenbotException if no keyword was given
     */
    public String find(String keyword) throws KenbotException {
        if (keyword.isBlank()) {
            throw new KenbotException("What am I looking for? Like: find book");
        }

        String wanted = keyword.trim().toLowerCase(Locale.ROOT);

        // A keyword starting with # asks about tags rather than descriptions,
        // matching how a tag is written when a task is created.
        boolean searchesTags = wanted.startsWith("#");
        String needle = searchesTags ? wanted.substring(1) : wanted;
        if (needle.isBlank()) {
            throw new KenbotException("Which tag? Like: find #fun");
        }

        // Choosing which tasks match is what a stream does best: one filter,
        // and no counter kept by hand.
        List<Task> matches = tasks.stream()
                .filter(task -> searchesTags ? hasMatchingTag(task, needle)
                        : task.getDescription().toLowerCase(Locale.ROOT).contains(needle))
                .toList();

        if (matches.isEmpty()) {
            return "Nothing matches '" + keyword.trim() + "'.";
        }
        return formatNumbered("Found these:", matches);
    }

    /**
     * Returns whether any of a task's tags contains the given text.
     *
     * <p>Part of a tag counts, and upper and lower case are ignored, so searching
     * behaves the same way whether the keyword is a tag or a description. Note
     * that two tags differing only in case are still two different tags; this
     * only affects how they are searched for.</p>
     *
     * @param task the task to look at
     * @param wanted the text to look for, already lower case and without its #
     * @return true if one of the task's tags contains the text
     */
    private static boolean hasMatchingTag(Task task, String wanted) {
        return task.getTags().stream()
                .anyMatch(tag -> tag.getName().toLowerCase(Locale.ROOT).contains(wanted));
    }

    /**
     * Attaches one or more tags to a task.
     *
     * @param argument the task number followed by the tags, as the user typed them
     * @return the task with its new tags, so the caller can display it
     * @throws KenbotException if the number or any of the tags cannot be used
     */
    public Task tag(String argument) throws KenbotException {
        return applyTags(argument, "tag", Task::addTag,
                "It's already got those.");
    }

    /**
     * Removes one or more tags from a task.
     *
     * @param argument the task number followed by the tags, as the user typed them
     * @return the task without those tags, so the caller can display it
     * @throws KenbotException if the number or any of the tags cannot be used
     */
    public Task untag(String argument) throws KenbotException {
        return applyTags(argument, "untag", Task::removeTag,
                "It doesn't have those.");
    }

    /**
     * Adds or removes the tags named after a task number.
     *
     * <p>Both commands read their argument the same way and differ only in what
     * they do with each tag, so the reading is written once here. Nothing
     * changing is reported rather than passed over quietly, because a command
     * that appears to work but does nothing is worse than one that explains
     * itself.</p>
     *
     * @param argument the task number followed by the tags
     * @param commandName the command being carried out, used in the message
     * @param change what to do with one tag, which returns whether it changed anything
     * @param nothingChanged what to say if no tag changed
     * @return the task, so the caller can display it
     * @throws KenbotException if the number or any of the tags cannot be used,
     *         or if no tag changed
     */
    private Task applyTags(String argument, String commandName,
            BiPredicate<Task, Tag> change, String nothingChanged) throws KenbotException {
        String[] parts = argument.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new KenbotException("Which task to " + commandName
                    + ", and with what? Like: " + commandName + " 2 fun");
        }

        Task task = tasks.get(indexOf(parts[0], commandName));

        // Every tag is checked before any is applied, so a typo in the second
        // tag cannot leave the first one half-applied.
        List<Tag> wanted = new ArrayList<>();
        for (String name : parts[1].trim().split("\\s+")) {
            wanted.add(Tag.of(name));
        }

        boolean changedSomething = false;
        for (Tag tag : wanted) {
            changedSomething |= change.test(task, tag);
        }
        if (!changedSomething) {
            throw new KenbotException(nothingChanged);
        }
        return task;
    }

    /**
     * Returns tasks as a numbered list under a heading.
     *
     * <p>Numbering starts at 1 and follows the order of the list given, so a
     * caller that passes only some of the tasks gets those numbered 1, 2, 3
     * rather than keeping their positions in the full list.</p>
     *
     * <p>Static because it works only on what it is handed: it is the one place
     * that decides how a list of tasks is laid out, so {@code describe} and
     * {@code find} cannot drift apart.</p>
     *
     * @param heading the line shown above the tasks
     * @param shown the tasks to list, in the order they should appear
     * @return the heading followed by one numbered line per task
     */
    private static String formatNumbered(String heading, List<Task> shown) {
        StringBuilder text = new StringBuilder(heading);
        for (int i = 0; i < shown.size(); i++) {
            text.append("\n").append(i + 1).append(".").append(shown.get(i));
        }
        return text.toString();
    }

    /**
     * Works out which position in the list the user meant, or explains why the
     * number cannot be used.
     *
     * <p>A position is returned rather than the task itself so that
     * {@code delete} can remove by position. Removing by object would depend on
     * how tasks compare to each other, which is not something this class
     * should rely on.</p>
     *
     * @param argument the task number, as the user typed it
     * @param commandName the command being carried out, used in the message
     * @return the position in the list, counting from 0
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    private int indexOf(String argument, String commandName) throws KenbotException {
        if (argument.isBlank()) {
            throw new KenbotException("Which task to " + commandName
                    + "? Like: " + commandName + " 2");
        }

        String wanted = argument.trim();
        int number;
        try {
            number = Integer.parseInt(wanted);
        } catch (NumberFormatException e) {
            // Java's own error is turned into ours, so the caller only ever
            // has to handle one kind of problem.
            throw new KenbotException("'" + wanted + "' isn't a task number.");
        }

        if (tasks.isEmpty()) {
            throw new KenbotException("There's no task " + number
                    + ", the list is empty.");
        }
        if (number < 1 || number > tasks.size()) {
            throw new KenbotException("There's no task " + number + ". You've got "
                    + tasks.size() + ".");
        }

        int index = number - 1;
        // Every caller feeds this straight to tasks.get() or tasks.remove()
        // without checking it again. The checks above are what make that safe,
        // so this records the promise: reorder or loosen them and the failure
        // shows up here rather than as an out-of-bounds error further away.
        assert index >= 0 && index < tasks.size()
                : "indexOf produced " + index + " for a list of " + tasks.size();
        return index;
    }
}
