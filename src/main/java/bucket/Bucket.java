package bucket;

import java.nio.file.Path;
import java.time.format.DateTimeParseException;

/**
 * The chatbot itself: holds the task list and turns one line of user input
 * into one reply.
 *
 * The command loop used to live here. It has been pulled out into getResponse so
 * that a caller decides when to hand over the next line. The console loop below
 * still reads lines in a while loop, while the GUI calls getResponse once per
 * button press, which is the only shape an event-driven front end can use.
 *
 * Assertions here record assumptions between this class and its callers. They
 * deliberately say nothing about what the user typed: bad commands are ordinary
 * events answered with a message, and assertions are off by default anyway, so
 * using them to police input would leave it unchecked in a real run.
 */
public class Bucket {
    private final TaskList items;
    private final Ui ui;
    private final Path saveFile;

    /**
     * Creates a chatbot that reads and writes the usual save file.
     * JavaFX needs a no-argument constructor, which is what this one is for.
     */
    public Bucket() {
        this(Storage.getDefaultPath());
    }

    /**
     * Creates a chatbot that reads and writes the given save file.
     * Naming the file makes the chatbot testable: a test can point it at a scratch
     * file rather than the one belonging to whoever is using the app.
     *
     * @param saveFile File to load from and save to.
     */
    public Bucket(Path saveFile) {
        assert saveFile != null : "Bucket needs a save file, not null";

        this.ui = new Ui();
        this.saveFile = saveFile;
        this.items = Storage.load(saveFile);

        // Storage.load returns an empty list for a missing, empty or unreadable file,
        // so it never hands back null. Every method below leans on that by using items
        // without a null check, which makes this the right place to pin the contract.
        assert items != null : "Storage.load() must never return null";

        // The save file keeps whatever order it was written in, so sort once here to
        // make sure the list shown in the greeting is already in date order.
        items.sort();
    }

    /**
     * Returns whether a line asks the chatbot to shut down.
     * Both front ends need to know this, so it lives here rather than in either one.
     *
     * @param input Raw line the user typed.
     * @return True if the line is the bye command.
     */
    public static boolean isExitCommand(String input) {
        assert input != null : "isExitCommand() needs a line, not null";
        return input.trim().equalsIgnoreCase("bye");
    }

    /**
     * Returns the greeting plus whatever was loaded from the save file.
     * Shown once when a front end starts up.
     *
     * @return Startup message.
     */
    public String getWelcomeMessage() {
        String body = items.isEmpty() ? ui.getNoTasks() : ui.getList(items);
        return ui.getWelcome() + "\n\n" + body;
    }

    /**
     * Runs one line of user input and returns what the chatbot says back.
     * The list is written out afterwards, so a crash cannot lose a change.
     *
     * Bad input is answered with a message rather than an exception: a GUI has no
     * console to print a stack trace to, and one mistyped command should not take
     * the window down.
     *
     * @param input Raw line the user typed.
     * @return Reply to show the user.
     */
    public Response getResponse(String input) {
        // Both front ends supply a real line: Scanner.nextLine and TextField.getText
        // never return null. A null here means a third caller broke that contract,
        // which is a programming error rather than something the user did.
        assert input != null : "getResponse() needs a line, not null";

        Parser parser = new Parser(input);
        String command = parser.getCommand();
        String argument = parser.getArgument();

        // Parser splits with a limit of two and substitutes "" for a missing second
        // half, so both of these exist for every possible line, including a blank one.
        assert command != null : "Parser must always yield a command word";
        assert argument != null : "Parser must always yield an argument, empty if absent";

        try {
            Response response = execute(command, argument);

            // Every branch of execute returns a reply. A null would reach the GUI and
            // be rendered as the word "null" in a dialog box, which is hard to trace.
            assert response != null : "every command must produce a reply to show";

            Storage.save(items, saveFile);
            return response;

        } catch (DateTimeParseException e) {
            return Response.error(ui.getError("OOPS!!! Dates need to look like 2019-10-15."));
        } catch (ArrayIndexOutOfBoundsException e) {
            return Response.error(ui.getError("OOPS!!! That command is missing a part. Try\n"
                    + "  deadline return book /by 2019-10-15\n"
                    + "  event project meeting /from 2019-10-15 /to 2019-10-16"));
        } catch (NumberFormatException e) {
            return Response.error(ui.getError("OOPS!!! I need a task number, e.g. mark 2."));
        } catch (IndexOutOfBoundsException e) {
            return Response.error(ui.getError("OOPS!!! There is no task with that number."));
        }
    }

    /**
     * Picks the right action for a command word and returns its reply.
     * Every branch is a single call, so this reads as the list of commands Bucket
     * understands, and how each one works is left to the method it names.
     * Anything malformed is left to throw, so getResponse can turn every failure
     * into a message in one place.
     *
     * @param command Command word, e.g. "deadline".
     * @param argument Rest of the line, if any.
     * @return Reply to show the user.
     */
    private Response execute(String command, String argument) {
        if (command.equals("todo")) {
            return addTodo(argument);

        } else if (command.equals("deadline")) {
            return addTask(Parser.toDeadline(argument));

        } else if (command.equals("event")) {
            return addTask(Parser.toEvent(argument));

        } else if (command.equals("mark") || command.equals("unmark")) {
            return markTask(argument, command.equals("mark"));

        } else if (command.equals("list")) {
            return listTasks();

        } else if (command.equals("find")) {
            return findTasks(argument);

        } else if (command.equals("delete")) {
            return deleteTask(argument);

        } else if (isExitCommand(command)) {
            return Response.of(ui.getGoodbye());

        } else {
            return Response.error(
                    ui.getError("OOPS!!! I'm sorry, but I don't know what that means :-("));
        }
    }

    /**
     * Adds a todo and returns the confirmation for it.
     *
     * @param description Text the user typed after the command word.
     * @return Confirmation text, or an error if the description was missing.
     */
    private Response addTodo(String description) {
        if (description.isEmpty()) {
            return Response.error(ui.getError("OOPS!!! The description of a todo cannot be empty."));
        }
        return addTask(new Todo(description));
    }

    /**
     * Marks the task the user picked as done or not done.
     *
     * @param argument Task number as the user typed it.
     * @param isDone True to mark it done, false to mark it not done.
     * @return Confirmation text.
     */
    private Response markTask(String argument, boolean isDone) {
        // No assertion on the index: "mark 99" is the user's mistake, and the
        // IndexOutOfBoundsException it raises is already answered with a message.
        Task task = items.get(Parser.toIndex(argument));
        task.setDone(isDone);

        // setDone is the only way done state changes, so it must have taken effect.
        assert task.getDoneIcon().equals(isDone ? "X" : " ") : "setDone() did not take effect";

        return Response.of(ui.getMarked(task, isDone));
    }

    /**
     * Orders the tasks by date and returns the whole list for display.
     *
     * The real list is sorted, not a copy of it. The numbers shown come from each
     * task's position, and mark and delete look tasks up by that same position, so
     * sorting only what is displayed would make "mark 2" act on a different task
     * from the one printed as 2.
     *
     * @return List text.
     */
    private Response listTasks() {
        items.sort();
        return Response.of(ui.getList(items));
    }

    /**
     * Returns the tasks whose description contains the keyword.
     *
     * @param keyword Text to search descriptions for.
     * @return Matching tasks, or an error if no keyword was given.
     */
    private Response findTasks(String keyword) {
        if (keyword.isEmpty()) {
            return Response.error(ui.getError("OOPS!!! Tell me what to search for, e.g. find book."));
        }
        return Response.of(ui.getFound(items.find(keyword)));
    }

    /**
     * Removes the task the user picked and returns the confirmation for it.
     *
     * @param argument Task number as the user typed it.
     * @return Confirmation text.
     */
    private Response deleteTask(String argument) {
        int index = Parser.toIndex(argument);
        Task task = items.get(index);

        int sizeBefore = items.size();
        items.removeItem(index);

        // The count in the confirmation message comes straight from size(), so a list
        // that did not shrink would quietly report the wrong number back to the user.
        assert items.size() == sizeBefore - 1 : "deleting must shrink the list by exactly one";

        return Response.of(ui.getRemoved(task, items.size()));
    }

    /**
     * Adds a task to the list and returns the confirmation for it.
     *
     * @param task Task being added.
     * @return Confirmation text.
     */
    private Response addTask(Task task) {
        // Every caller builds this from a constructor or from Parser, all of which
        // either return a task or throw, so a null means one of them went wrong.
        assert task != null : "addTask() needs a task, not null";

        int sizeBefore = items.size();
        items.addItem(task);

        // The count in the confirmation message comes straight from size(), so a list
        // that did not grow would quietly report the wrong number back to the user.
        assert items.size() == sizeBefore + 1 : "adding must grow the list by exactly one";

        return Response.of(ui.getAdded(task, items.size()));
    }

    /**
     * Runs the chatbot on the console.
     * The GUI does not come through here; it starts at Launcher instead.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Bucket bucket = new Bucket();
        Ui ui = new Ui();
        ui.start();

        System.out.println(ui.getBanner());
        printBlock(bucket.getWelcomeMessage());

        String input = ui.readCommand();
        while (!isExitCommand(input)) {
            // The console shows every reply the same way, so it only needs the text.
            // Highlighting errors differently is something only the GUI does.
            printBlock(bucket.getResponse(input).text());
            input = ui.readCommand();
        }

        printBlock(bucket.getResponse(input).text());
        ui.close();
    }

    /**
     * Prints one message fenced by dividers, so console output stays evenly spaced.
     *
     * @param message Text to print.
     */
    private static void printBlock(String message) {
        assert message != null : "printBlock() needs text, not null";

        System.out.println(Ui.LINE_BREAK);
        System.out.println(message);
        System.out.println(Ui.LINE_BREAK);
    }
}
