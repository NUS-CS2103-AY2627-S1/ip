package benbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Processes BenBot commands and updates the in-memory task list. */
public class TaskLoader {
    /** The maximum number of tasks the task array can contain. */
    private final int maxTasks;

    /** The response lines produced while processing the most recent command. */
    private final List<String> responseLines = new ArrayList<>();

    /** The contacts managed independently from the task list. */
    private final EntityList<Contact> contacts = new EntityList<>();

    /** The notes managed independently from the task list. */
    private final EntityList<Note> notes = new EntityList<>();

    /** The expenses managed independently from the task list. */
    private final ExpenseTracker expenseTracker = new ExpenseTracker();

    /**
     * Creates a command processor with the specified maximum task capacity.
     *
     * @param maxTasks the maximum number of tasks that may be stored.
     */
    public TaskLoader(int maxTasks) {
        assert maxTasks > 0 : "Task-list capacity must be positive";
        this.maxTasks = maxTasks;
    }

    /** Returns the maximum number of tasks this processor can store. */
    int getMaxTasks() {
        return maxTasks;
    }

    /**
     * Processes one command and optionally prints BenBot's response.
     *
     * @param line the command entered by the user.
     * @param tasks the task array to update.
     * @param taskCountPointer the current task count, stored in a one-element array.
     * @param shouldPrint whether to display command responses and error messages.
     * @return whether the command requests that BenBot exits.
     */
    public boolean addTask(String line, Task[] tasks, int[] taskCountPointer, boolean shouldPrint) {
        assert line != null : "A command line must be provided";
        assert tasks != null && tasks.length >= maxTasks
                : "The task array must provide the configured capacity";
        assert taskCountPointer != null && taskCountPointer.length == 1
                : "The task count holder must contain exactly one value";

        int taskCount = taskCountPointer[0];
        assert taskCount >= 0 && taskCount <= maxTasks
                : "The task count must remain within the configured capacity";
        assert hasPopulatedTaskPrefix(tasks, taskCount)
                : "Every task before the task count must be initialized";

        String strippedLine = line.strip();
        responseLines.clear();
        boolean shouldExit = false;
        try {
            if (strippedLine.isEmpty()) {
                throw new InvalidCommandException("Please enter a command.");
            }
            shouldExit = executeCommand(strippedLine, tasks, taskCountPointer, shouldPrint);
        } catch (BenBotException e) {
            printMessage(shouldPrint, e.getMessage());
        }

        int updatedTaskCount = taskCountPointer[0];
        assert updatedTaskCount >= 0 && updatedTaskCount <= maxTasks
                : "Processing a command must leave the task count within capacity";
        assert hasPopulatedTaskPrefix(tasks, updatedTaskCount)
                : "Processing a command must leave all counted tasks initialized";
        return shouldExit;
    }

    /**
     * Returns the response produced by the most recently processed command.
     * This allows graphical interfaces to display replies without redirecting standard output.
     *
     * @return the complete response, with each response line separated by the platform line separator.
     */
    public String getLastResponse() {
        return String.join(System.lineSeparator(), responseLines);
    }

    /** Returns commands that recreate all non-task entities managed by this processor. */
    List<String> getExtensionStorageCommands() {
        List<String> commands = new ArrayList<>(contacts.toStorageCommands());
        commands.addAll(notes.toStorageCommands());
        commands.addAll(expenseTracker.toStorageCommands());
        return List.copyOf(commands);
    }

    /** Returns the total number of tasks and non-task entities currently managed. */
    int getStoredItemCount(int taskCount) {
        return taskCount + contacts.size() + notes.size() + expenseTracker.size();
    }

    /** Dispatches a valid, non-empty command to the operation that handles it. */
    private boolean executeCommand(String line, Task[] tasks, int[] taskCountPointer,
                                   boolean shouldPrint) throws BenBotException {
        String[] words = line.split("\\s+");
        String command = words[0];

        switch (command) {
            case "bye" -> {
                sayGoodbye(words, shouldPrint);
                return true;
            }
            case "list" -> listTasks(words, tasks, taskCountPointer[0], shouldPrint);
            case "todo" -> addTodoTask(words, tasks, taskCountPointer, shouldPrint);
            case "deadline" -> addDeadlineTask(words, tasks, taskCountPointer, shouldPrint);
            case "event" -> addEventTask(words, tasks, taskCountPointer, shouldPrint);
            case "delete" -> deleteTask(words, tasks, taskCountPointer, shouldPrint);
            case "mark" -> markTaskDone(words, tasks, taskCountPointer[0], shouldPrint);
            case "unmark" -> markTaskUndone(words, tasks, taskCountPointer[0], shouldPrint);
            case "find" -> findTasks(words, tasks, taskCountPointer[0], shouldPrint);
            case "contact" -> addContact(words, shouldPrint);
            case "contacts" -> listContacts(words, shouldPrint);
            case "delete-contact" -> deleteContact(words, shouldPrint);
            case "note" -> addNote(words, shouldPrint);
            case "notes" -> listNotes(words, shouldPrint);
            case "delete-note" -> deleteNote(words, shouldPrint);
            case "expense" -> addExpense(words, shouldPrint);
            case "expenses" -> listExpenses(words, shouldPrint);
            case "delete-expense" -> deleteExpense(words, shouldPrint);
            default -> throw new InvalidCommandException("I don't know what that means.");
        }
        return false;
    }

    private void addContact(String[] words, boolean shouldPrint) throws InvalidCommandException {
        Contact contact = Contact.createFromCommand(words);
        contacts.add(contact);
        String contactWord = contacts.size() == 1 ? "contact" : "contacts";
        printMessage(shouldPrint,
                "Got it. I've added this contact:",
                "  " + contact,
                "Now you have " + contacts.size() + " " + contactWord + ".");
    }

    private void listContacts(String[] words, boolean shouldPrint) throws InvalidCommandException {
        requireNoArguments(words, "contacts");
        printMessage(shouldPrint, "Here are your contacts:");
        printMessage(shouldPrint, contacts.toNumberedDisplayLines());
    }

    private void deleteContact(String[] words, boolean shouldPrint) throws InvalidCommandException {
        int contactIndex = getEntityIndex(words, contacts.size(), "delete-contact", "contact");
        Contact removedContact = contacts.remove(contactIndex);
        String contactWord = contacts.size() == 1 ? "contact" : "contacts";
        printMessage(shouldPrint,
                "Noted. I've removed this contact:",
                "  " + removedContact,
                "Now you have " + contacts.size() + " " + contactWord + ".");
    }

    private void addNote(String[] words, boolean shouldPrint) throws InvalidCommandException {
        Note note = Note.createFromCommand(words);
        notes.add(note);
        String noteWord = notes.size() == 1 ? "note" : "notes";
        printMessage(shouldPrint,
                "Got it. I've added this note:",
                "  " + note,
                "Now you have " + notes.size() + " " + noteWord + ".");
    }

    private void listNotes(String[] words, boolean shouldPrint) throws InvalidCommandException {
        requireNoArguments(words, "notes");
        printMessage(shouldPrint, "Here are your notes:");
        printMessage(shouldPrint, notes.toNumberedDisplayLines());
    }

    private void deleteNote(String[] words, boolean shouldPrint) throws InvalidCommandException {
        int noteIndex = getEntityIndex(words, notes.size(), "delete-note", "note");
        Note removedNote = notes.remove(noteIndex);
        String noteWord = notes.size() == 1 ? "note" : "notes";
        printMessage(shouldPrint,
                "Noted. I've removed this note:",
                "  " + removedNote,
                "Now you have " + notes.size() + " " + noteWord + ".");
    }

    private void addExpense(String[] words, boolean shouldPrint) throws InvalidCommandException {
        Expense expense = Expense.createFromCommand(words);
        expenseTracker.add(expense);
        String expenseWord = expenseTracker.size() == 1 ? "expense" : "expenses";
        printMessage(shouldPrint,
                "Got it. I've added this expense:",
                "  " + expense,
                "Now you have " + expenseTracker.size() + " " + expenseWord + ".");
    }

    private void listExpenses(String[] words, boolean shouldPrint) throws InvalidCommandException {
        requireNoArguments(words, "expenses");
        printMessage(shouldPrint, "Here are your expenses:");
        printMessage(shouldPrint, expenseTracker.toNumberedDisplayLines());
        printMessage(shouldPrint, "Total expenses: $" + expenseTracker.getTotalAmount().toPlainString());
    }

    private void deleteExpense(String[] words, boolean shouldPrint) throws InvalidCommandException {
        int expenseIndex = getEntityIndex(
                words, expenseTracker.size(), "delete-expense", "expense");
        Expense removedExpense = expenseTracker.remove(expenseIndex);
        String expenseWord = expenseTracker.size() == 1 ? "expense" : "expenses";
        printMessage(shouldPrint,
                "Noted. I've removed this expense:",
                "  " + removedExpense,
                "Now you have " + expenseTracker.size() + " " + expenseWord + ".");
    }

    private void sayGoodbye(String[] words, boolean shouldPrint) throws InvalidCommandException {
        requireNoArguments(words, "bye");
        printMessage(shouldPrint, "Bye. Hope to see you again soon!");
    }

    private void listTasks(String[] words, Task[] tasks, int taskCount, boolean shouldPrint)
            throws InvalidCommandException {
        requireNoArguments(words, "list");
        printMessage(shouldPrint, "Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            printMessage(shouldPrint, (i + 1) + "." + tasks[i]);
        }
    }

    private void addTodoTask(String[] words, Task[] tasks, int[] taskCountPointer,
                             boolean shouldPrint) throws BenBotException {
        requireDescription(words, "todo DESCRIPTION");
        ensureTaskListHasSpace(taskCountPointer[0]);
        appendTask(new ToDo(words), tasks, taskCountPointer, shouldPrint);
    }

    private void addDeadlineTask(String[] words, Task[] tasks, int[] taskCountPointer,
                                 boolean shouldPrint) throws BenBotException {
        validateDeadline(words);
        ensureTaskListHasSpace(taskCountPointer[0]);
        appendTask(new Deadline(words), tasks, taskCountPointer, shouldPrint);
    }

    private void addEventTask(String[] words, Task[] tasks, int[] taskCountPointer,
                              boolean shouldPrint) throws BenBotException {
        validateEvent(words);
        ensureTaskListHasSpace(taskCountPointer[0]);
        appendTask(new Event(words), tasks, taskCountPointer, shouldPrint);
    }

    private void appendTask(Task task, Task[] tasks, int[] taskCountPointer, boolean shouldPrint)
            throws InvalidCommandException {
        int taskCount = taskCountPointer[0];
        ensureTaskIsUnique(task, tasks, taskCount);
        tasks[taskCount] = task;
        taskCount++;
        taskCountPointer[0] = taskCount;
        printTaskAdded(task, taskCount, shouldPrint);
    }

    /** Ensures that an equivalent task is not already present in the task list. */
    private void ensureTaskIsUnique(Task newTask, Task[] tasks, int taskCount)
            throws InvalidCommandException {
        String newTaskStorageCommand = newTask.toStorageString();
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].toStorageString().equals(newTaskStorageCommand)) {
                throw new InvalidCommandException("That task is already in your list.");
            }
        }
    }

    private void deleteTask(String[] words, Task[] tasks, int[] taskCountPointer,
                            boolean shouldPrint) throws InvalidTaskNumberException {
        int taskCount = taskCountPointer[0];
        int taskIndex = getTaskIndex(words, taskCount, "delete");
        Task removedTask = tasks[taskIndex];
        taskCount = removeTask(tasks, taskIndex, taskCount);
        taskCountPointer[0] = taskCount;
        printTaskRemoved(removedTask, taskCount, shouldPrint);
    }

    private void markTaskDone(String[] words, Task[] tasks, int taskCount,
                              boolean shouldPrint) throws InvalidTaskNumberException {
        Task task = tasks[getTaskIndex(words, taskCount, "mark")];
        task.markDone();
        printMessage(shouldPrint, "Nice! I've marked this task as done:", "  " + task);
    }

    private void markTaskUndone(String[] words, Task[] tasks, int taskCount,
                                boolean shouldPrint) throws InvalidTaskNumberException {
        Task task = tasks[getTaskIndex(words, taskCount, "unmark")];
        task.markUndone();
        printMessage(shouldPrint, "OK, I've marked this task as not done yet:", "  " + task);
    }

    private void findTasks(String[] words, Task[] tasks, int taskCount,
                           boolean shouldPrint) throws InvalidCommandException {
        requireDescription(words, "find KEYWORD");
        String keyword = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
        printMatchingTasks(keyword, tasks, taskCount, shouldPrint);
    }

    /** Prints the confirmation shown after adding a task. */
    private void printTaskAdded(Task task, int taskCount, boolean shouldPrint) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        printMessage(shouldPrint,
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Prints the confirmation shown after deleting a task. */
    private void printTaskRemoved(Task task, int taskCount, boolean shouldPrint) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        printMessage(shouldPrint,
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Prints every task whose description contains the given keyword, ignoring case. */
    private void printMatchingTasks(String keyword, Task[] tasks, int taskCount, boolean shouldPrint) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        printMessage(shouldPrint, "Here are the matching tasks in your list:");

        String[] matchingTasks = IntStream.range(0, taskCount)
                .filter(index -> tasks[index].getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .mapToObj(index -> (index + 1) + "." + tasks[index])
                .toArray(String[]::new);
        printMessage(shouldPrint, matchingTasks);
    }

    /** Prints responses only when output is enabled. */
    private void printMessage(boolean shouldPrint, String... messages) {
        responseLines.addAll(List.of(messages));
        if (!shouldPrint) {
            return;
        }
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /**
     * Removes a task by shifting all later tasks one position to the left.
     *
     * @return the new number of stored tasks
     */
    private int removeTask(Task[] tasks, int taskIndex, int taskCount) {
        assert taskIndex >= 0 && taskIndex < taskCount
                : "Only an existing task can be removed";
        assert tasks[taskIndex] != null : "The task selected for removal must be initialized";
        for (int i = taskIndex; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        return taskCount - 1;
    }

    /** Ensures that a command without extra words has the expected format. */
    private void requireNoArguments(String[] words, String command) throws InvalidCommandException {
        if (words.length != 1) {
            throw new InvalidCommandException("Use: " + command);
        }
    }

    /** Ensures that a task command contains a description. */
    private void requireDescription(String[] words, String usage) throws InvalidCommandException {
        if (words.length < 2) {
            throw new InvalidCommandException("Use: " + usage);
        }
    }

    /** Ensures the fixed-size task array has a free position. */
    private void ensureTaskListHasSpace(int taskCount) throws TaskListFullException {
        if (taskCount >= maxTasks) {
            throw new TaskListFullException();
        }
    }

    /** Validates the required description and {@code /by} parts of a deadline. */
    private void validateDeadline(String[] words) throws InvalidCommandException {
        int byIndex = CommandWords.findMarker(words, "/by");
        boolean isMarkerCountInvalid = CommandWords.countMarker(words, "/by") != 1;
        if (isMarkerCountInvalid || byIndex <= 1 || byIndex >= words.length - 1) {
            throw new InvalidCommandException("Use: deadline DESCRIPTION /by DATE");
        }
    }

    /** Validates the required description, {@code /from}, and {@code /to} parts of an event. */
    private void validateEvent(String[] words) throws InvalidCommandException {
        int fromIndex = CommandWords.findMarker(words, "/from");
        int toIndex = CommandWords.findMarker(words, "/to");
        boolean isMarkerCountInvalid = CommandWords.countMarker(words, "/from") != 1
                || CommandWords.countMarker(words, "/to") != 1;
        boolean isFromMarkerMissing = fromIndex == words.length;
        boolean isToMarkerMissing = toIndex == words.length;
        boolean isDescriptionMissing = fromIndex <= 1;
        boolean areMarkersOutOfOrder = toIndex < fromIndex;
        boolean isStartMissing = toIndex == fromIndex + 1;
        boolean isEndMissing = toIndex == words.length - 1;
        if (isMarkerCountInvalid || isFromMarkerMissing || isToMarkerMissing || isDescriptionMissing
                || areMarkersOutOfOrder || isStartMissing || isEndMissing) {
            throw new InvalidCommandException("Use: event DESCRIPTION /from START /to END");
        }
    }

    /** Converts and validates the one-based task number in a mark command. */
    private int getTaskIndex(String[] words, int taskCount, String command)
            throws InvalidTaskNumberException {
        if (words.length != 2) {
            throw new InvalidTaskNumberException("Use: " + command + " TASK_NUMBER");
        }

        try {
            if (!words[1].chars().allMatch(Character::isDigit)) {
                throw new NumberFormatException();
            }
            int taskNumber = Integer.parseInt(words[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new InvalidTaskNumberException("That task number does not exist.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException("The task number must be a whole number.");
        }
    }

    /** Converts and validates a one-based number for a non-task entity. */
    private int getEntityIndex(String[] words, int entityCount, String command, String entityName)
            throws InvalidCommandException {
        if (words.length != 2) {
            throw new InvalidCommandException(
                    "Use: " + command + " " + entityName.toUpperCase(Locale.ROOT) + "_NUMBER");
        }

        try {
            if (!words[1].chars().allMatch(Character::isDigit)) {
                throw new NumberFormatException();
            }
            int entityNumber = Integer.parseInt(words[1]);
            if (entityNumber < 1 || entityNumber > entityCount) {
                throw new InvalidCommandException("That " + entityName + " number does not exist.");
            }
            return entityNumber - 1;
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(
                    "The " + entityName + " number must be a whole number.");
        }
    }

    /** Returns whether the occupied prefix of a task array contains no gaps. */
    private boolean hasPopulatedTaskPrefix(Task[] tasks, int taskCount) {
        if (taskCount < 0 || taskCount > tasks.length) {
            return false;
        }
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i] == null) {
                return false;
            }
        }
        return true;
    }
}
