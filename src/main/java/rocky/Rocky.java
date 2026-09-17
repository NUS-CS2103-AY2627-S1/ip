package rocky;

import java.io.File;
import java.util.List;

import rocky.expense.Expense;
import rocky.expense.ExpenseList;
import rocky.parser.Parser;
import rocky.storage.Storage;
import rocky.task.Task;
import rocky.task.TaskList;
import rocky.task.TaskType;
import rocky.ui.Ui;

/**
 * Core of Rocky, a simple chatbot for tracking todos, deadlines, and events,
 * and for recording expenses. Commands are plain text (e.g. "todo read book"),
 * and {@link #getResponse} turns one such command into the reply to show the
 * user. Keeping the reply as a returned string rather than printing it lets
 * the text UI in {@link #main} and the JavaFX GUI share exactly the same
 * logic. Tasks and expenses are each saved to their own file under
 * {@code data/} after every change.
 */
public class Rocky {
    private static final TaskList tasks = new TaskList();
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "duke.txt";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_EXPENSE = "expense";
    private static final String COMMAND_EXPENSES = "expenses";
    private static final String COMMAND_DELETE_EXPENSE = "delete-expense";
    private static final String EXPENSE_FILE = "expenses.txt";
    // What Rocky says when a command succeeds, in his Project Hail Mary voice.
    // Error messages keep their plain wording so the fix is easy to understand.
    private static final String REPLY_TODO_ADDED = "New task. Do not worry. I remember it for you.";
    private static final String REPLY_DEADLINE_ADDED = "Task with deadline. I track time carefully.";
    private static final String REPLY_EVENT_ADDED = "Task spans two times. Start and end. I mark both.";
    private static final String REPLY_LIST = "Here is everything. Rocky show full list now";
    private static final String REPLY_MARKED = "Task complete! Good good good. I record success.";
    private static final String REPLY_UNMARKED = "Task not complete. I undo the mark.";
    private static final String REPLY_DELETED = "Task removed. Gone.";
    private static final String REPLY_FIND =
            "Searching... I compare each task to your word. Matches only.";
    private static final String REPLY_EXPENSE_ADDED = "Resource spent. I log amount and category.";
    private static final String REPLY_EXPENSES = "All spending, here. I show you where resources went.";
    private static final String REPLY_EXPENSE_DELETED = "Expense record removed. I forget this transaction.";
    private static final String REPLY_BYE = "Goodbye, friend. I power down now. Talk later.";
    private static final Storage storage = new Storage(new File(DATA_DIR, DATA_FILE));
    private static final ExpenseList expenses = new ExpenseList(new File(DATA_DIR, EXPENSE_FILE));
    private static boolean isLoaded = false;

    /** Not meant to be instantiated; every member here is static. */
    private Rocky() {
    }

    /**
     * Rocky's reply to one command.
     *
     * @param text the reply to show the user, which may span several lines.
     * @param isError whether the reply explains what was wrong with the command.
     */
    public record Reply(String text, boolean isError) {
    }

    /**
     * Loads previously saved tasks and expenses, unless they have been
     * loaded already. Safe to call more than once, so every UI can call it
     * on startup.
     */
    public static void initialize() {
        if (!isLoaded) {
            tasks.addAll(storage.load());
            expenses.load();
            isLoaded = true;
        }
    }

    /** Returns Rocky's opening message. */
    public static String getGreeting() {
        return "Hello! I'm Rocky.\nWhat can I do for you?";
    }

    /**
     * Returns true if the given input is the command to exit.
     *
     * @param input one full command line as typed by the user.
     */
    public static boolean isExitCommand(String input) {
        return input.trim().equals(COMMAND_BYE);
    }

    /**
     * Runs one user command and returns Rocky's reply to it. Errors in the
     * command are reported as the reply rather than thrown, so a UI can show
     * them the same way it shows any other response.
     *
     * @param input one full command line, e.g. "deadline return book /by 2019-12-01".
     * @return the reply to show the user, which may span several lines.
     */
    public static String getResponse(String input) {
        return getReply(input).text();
    }

    /**
     * Runs one user command and returns Rocky's reply to it, along with
     * whether the reply reports a problem with the command. The GUI uses
     * this to show error replies differently from successful ones.
     *
     * @param input one full command line, e.g. "mark 2".
     * @return the reply text, and whether it is an error message.
     */
    public static Reply getReply(String input) {
        // Both UIs pass what the user typed; Scanner.nextLine() and TextField.getText() never give null.
        assert input != null : "getReply() should never receive a null command";

        try {
            return new Reply(runCommand(input.trim()), false);
        } catch (RockyException e) {
            return new Reply(e.getMessage(), true);
        }
    }

    /**
     * Runs one command line and returns Rocky's reply to it.
     *
     * @param input the command line, with surrounding whitespace removed.
     * @return the reply to show the user.
     * @throws RockyException if the command is unknown or its details are invalid.
     */
    private static String runCommand(String input) throws RockyException {
        String commandWord = Parser.getCommandWord(input);

        if (isExitCommand(input)) {
            return REPLY_BYE;
        } else if (input.equals(COMMAND_LIST)) {
            return listTasks();
        } else if (commandWord.equals(COMMAND_MARK)) {
            return setDone(input, true);
        } else if (commandWord.equals(COMMAND_UNMARK)) {
            return setDone(input, false);
        } else if (commandWord.equals(COMMAND_DELETE)) {
            return deleteTask(input);
        } else if (commandWord.equals(COMMAND_FIND)) {
            return findTasks(input);
        } else if (input.equals(COMMAND_EXPENSES)) {
            return listExpenses();
        } else if (commandWord.equals(COMMAND_EXPENSE)) {
            return addExpense(input);
        } else if (commandWord.equals(COMMAND_DELETE_EXPENSE)) {
            return deleteExpense(input);
        }

        TaskType type = TaskType.fromKeyword(commandWord);
        if (type == null) {
            throw new RockyException("Hmm, \"" + commandWord + "\" isn't a command I know.");
        }
        return addTask(type, input);
    }

    /**
     * Greets the user, then reads and answers one command per line from
     * standard input until the user types "bye".
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        // Load any previously saved tasks before greeting the user.
        initialize();

        Ui ui = new Ui();
        ui.showWelcome(getGreeting());

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showReply(getResponse(input));

            if (isExitCommand(input)) {
                break;
            }
        }

        ui.close();
    }

    /** Returns every task in the list, or a friendly message if it's empty. */
    private static String listTasks() {
        if (tasks.isEmpty()) {
            return "Your list is empty. Add something!";
        }
        return formatNumberedList(REPLY_LIST, tasks.getTasks());
    }

    /**
     * Returns every task whose description contains the given keyword
     * (case-insensitive), e.g. "find book".
     *
     * @param input the full "find ..." command line.
     * @throws RockyException if no keyword is given.
     */
    private static String findTasks(String input) throws RockyException {
        String keyword = Parser.getTextAfterCommand(input, COMMAND_FIND);

        if (keyword.isEmpty()) {
            throw new RockyException(
                    "What should I search for? Try again, e.g.: find book");
        }

        List<Task> matches = tasks.find(keyword);

        if (matches.isEmpty()) {
            return "No matching tasks found.";
        }
        return formatNumberedList(REPLY_FIND, matches);
    }

    /**
     * Returns the heading followed by the given items (e.g. tasks or
     * expenses) as a numbered list, one item per line, numbered from 1.
     *
     * @param heading the line shown above the list.
     * @param items the items to number, in display order.
     */
    private static String formatNumberedList(String heading, List<?> items) {
        StringBuilder builder = new StringBuilder(heading);
        for (int i = 0; i < items.size(); i++) {
            builder.append("\n").append(i + 1).append(".").append(items.get(i));
        }
        return builder.toString();
    }

    /**
     * Parses and adds a new todo/deadline/event task from a full command
     * line (e.g. "deadline return book /by 2019-12-01"), then saves the list.
     *
     * @param type which kind of task the command word identified.
     * @param input the full user input line, including the command word.
     * @return confirmation of the addition.
     * @throws RockyException if the description or required dates are
     *     missing, malformed, or in the wrong format.
     */
    private static String addTask(TaskType type, String input) throws RockyException {
        Task task = Parser.parseTask(type, input);
        tasks.add(task);
        storage.save(tasks.getTasks());
        return getAddedReply(type) + "\n  " + task
                + "\nNow you have " + describeCount(tasks.size(), "task") + " in the list.";
    }

    /**
     * Returns what Rocky says after adding a task of the given type. Every
     * TaskType must have a case here; the compiler reports any that don't.
     */
    private static String getAddedReply(TaskType type) {
        return switch (type) {
            case TODO -> REPLY_TODO_ADDED;
            case DEADLINE -> REPLY_DEADLINE_ADDED;
            case EVENT -> REPLY_EVENT_ADDED;
        };
    }

    /**
     * Removes the task named by a "delete N" command and saves the list.
     *
     * @param input the full "delete ..." command line.
     * @return confirmation of the removal.
     * @throws RockyException if no task number is given, it isn't a number,
     *     or it's out of range.
     */
    private static String deleteTask(String input) throws RockyException {
        int index = Parser.parseIndex(input, tasks.size(), "list", "task");
        Task removed = tasks.remove(index);
        storage.save(tasks.getTasks());
        return REPLY_DELETED + "\n  " + removed
                + "\nNow you have " + describeCount(tasks.size(), "task") + " in the list.";
    }

    /**
     * Marks or unmarks the task named by a "mark N"/"unmark N" command and
     * saves the list.
     *
     * @param input the full "mark ..."/"unmark ..." command line.
     * @param isDone true to mark the task done, false to mark it not done.
     * @return confirmation of the change.
     * @throws RockyException if no task number is given, it isn't a number,
     *     or it's out of range.
     */
    private static String setDone(String input, boolean isDone) throws RockyException {
        int index = Parser.parseIndex(input, tasks.size(), "list", "task");
        Task task = tasks.get(index);

        String message;
        if (isDone) {
            task.mark();
            message = REPLY_MARKED;
        } else {
            task.unmark();
            message = REPLY_UNMARKED;
        }

        // The reply and the save file both report this state, so it must match what was asked for.
        assert task.isDone() == isDone : "task's done state should now match the command";

        storage.save(tasks.getTasks());
        return message + "\n  " + task;
    }

    /** Returns every recorded expense, or a friendly message if there are none. */
    private static String listExpenses() {
        if (expenses.isEmpty()) {
            return "You haven't recorded any expenses yet.";
        }
        return formatNumberedList(REPLY_EXPENSES, expenses.getExpenses());
    }

    /**
     * Records the expense described by an "expense ..." command, e.g.
     * "expense lunch /amount 12.50 /category food", and saves it.
     *
     * @param input the full "expense ..." command line.
     * @return confirmation of the new expense.
     * @throws RockyException if any detail of the expense is missing or invalid.
     */
    private static String addExpense(String input) throws RockyException {
        Expense expense = Parser.parseExpense(Parser.getTextAfterCommand(input, COMMAND_EXPENSE));
        expenses.add(expense);
        return REPLY_EXPENSE_ADDED + "\n  " + expense
                + "\nNow you have " + describeCount(expenses.size(), "expense") + ".";
    }

    /**
     * Removes the expense named by a "delete-expense N" command and saves the list.
     *
     * @param input the full "delete-expense ..." command line.
     * @return confirmation of the removal.
     * @throws RockyException if no expense number is given, it isn't a number,
     *     or it's out of range.
     */
    private static String deleteExpense(String input) throws RockyException {
        int index = Parser.parseIndex(input, expenses.size(), "expense list", "expense");
        Expense removed = expenses.remove(index);
        return REPLY_EXPENSE_DELETED + "\n  " + removed
                + "\nNow you have " + describeCount(expenses.size(), "expense") + ".";
    }

    /**
     * Returns the count followed by the item name, made plural when needed,
     * e.g. "1 task" or "3 expenses".
     *
     * @param count how many items there are.
     * @param itemName what to call one item, e.g. "task".
     */
    private static String describeCount(int count, String itemName) {
        return count + " " + itemName + (count == 1 ? "" : "s");
    }
}
