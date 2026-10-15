package bob;

import java.nio.file.Path;
import java.util.List;

import bob.command.Command;
import bob.command.CommandWord;
import bob.parser.Parser;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a chatbot that greets the user, remembers the tasks the user types,
 * lists them back on request, marks them as done, removes the ones the user
 * no longer wants, and exits when the user types {@code bye}.
 *
 * <p>Tasks come in three kinds — {@link bob.task.Todo Todo}, {@link bob.task.Deadline Deadline} and
 * {@link bob.task.Event Event} — each added with its own command word. The words the chatbot
 * understands are listed in {@link bob.command.CommandWord CommandWord}; making sense of a whole line
 * written with one of them is {@link Parser}'s work, and this class is left to
 * say what each command does once it has been understood.
 *
 * <p>A chatbot is an object rather than a class of static members. It has three
 * things it works with — a {@link Ui}, a {@link Storage} and a {@link TaskList} —
 * and something that has state of its own is what an object is for. Making them
 * fields of an instance also means there can be more than one: a second chatbot
 * can be pointed at a different save file, which is what makes this class
 * testable at all, and what a graphical front end would need in order to hold a
 * chatbot of its own.
 *
 * <p>Nothing here writes to the console or reads from the keyboard directly.
 * Every line the user sees is handed to {@link Ui}, and every line they type
 * comes back from it, so this class is left deciding <em>what</em> to say while
 * {@code Ui} decides how it looks on screen.
 *
 * <p>The tasks themselves are held by {@link TaskList}, which is also asked for
 * the searches the listing commands need. This class no longer knows that they
 * sit in a list at all — it asks for a task by its number and is given one.
 *
 * <p>The task list outlives a single run: it is read from a file on startup and
 * written back whenever it changes, so the user finds their tasks where they
 * left them. All of that is done by {@link Storage}; this class only says when
 * to load and when to save.
 *
 * <p>A deadline and an event carry dates the chatbot understands rather than
 * text it merely repeats: each is read into a {@link bob.task.TaskDateTime TaskDateTime}, which is what
 * lets a date be shown back in a friendlier form than it was typed in.
 *
 * <p>Because those dates are understood, the chatbot can be asked about them
 * rather than only told them: {@link bob.command.CommandWord#ON CommandWord.ON},
 * {@link bob.command.CommandWord#BEFORE CommandWord.BEFORE} and
 * {@link bob.command.CommandWord#AFTER CommandWord.AFTER} pick out the tasks falling on, before,
 * or after a given day, and {@link bob.command.CommandWord#NEXT CommandWord.NEXT} shows the few
 * with the soonest dates on them.
 * A fifth view asks about the words rather than the dates:
 * {@link bob.command.CommandWord#FIND CommandWord.FIND} picks out the tasks whose description
 * mentions a keyword, which is how a long list is searched rather than read through.
 * All five are views of the one task list — they change nothing, so nothing is
 * saved after them — and each shows a task with the number it has in
 * {@link bob.command.CommandWord#LIST CommandWord.LIST}, so a task found this way can be marked or deleted
 * without looking it up again.
 *
 * <p>Anything the user types that cannot be carried out — an unknown command,
 * a missing description, a task number that does not exist, a due date that is
 * not a date — is reported by throwing a {@link BobException} carrying the
 * explanation. Most of those are thrown by {@link Parser}, which reads what the
 * user wrote; the one exception is a task number that is a number but names no
 * task, since only this class knows how long the list is.
 * {@link #handleCommand} catches all of them alike and shows the message, so a
 * mistyped command never ends the conversation.
 * A failure to save is reported the same way, so it is seen rather than passing
 * silently, and the conversation carries on.
 * Even a bug in the chatbot itself is caught there, as a last resort, so that it
 * costs the user one command rather than the whole conversation.
 *
 * <p>The chatbot has two front ends, and this class serves both. {@link #run()}
 * holds the conversation at a console, reading lines and printing answers until
 * the user says goodbye. A window instead drives the conversation itself, handing
 * over one line at a time and being given the answer back as text, through
 * {@link #getGreeting()}, {@link #getResponse}, {@link #isExit()} and
 * {@link #isLastResponseError()}, and asking {@link #hasUnsavedChanges()} and
 * {@link #getResponseToClosing()} when the user closes it. Both go
 * through the same {@link #handleCommand}, so neither front end can drift into
 * answering differently from the other; which of the two a chatbot is for is
 * settled when it is made, by {@code new Bob(...)} or by {@link #forGui}.
 */
public class Bob {

    /**
     * Explanation shown when a command stopped partway because of a mistake in the
     * chatbot itself, rather than in what the user typed.
     *
     * <p>It cannot say what went wrong, since nothing expected it to. What it can
     * do is warn that the command may have been carried out only in part, and say
     * how the user can see for themselves how far it got.
     */
    private static final String UNEXPECTED_ERROR_MESSAGE =
            "Something went wrong inside Bob while doing that. Type list to check what changed.";

    /** Everything printed to the user, and every line read back from them. */
    private final Ui ui;

    /** The file the task list is read from and written back to. */
    private final Storage storage;

    /** The tasks the user has told the chatbot about, and everything done to them. */
    private final TaskList tasks;

    /**
     * What {@link Storage} had to say about reading the save file, kept until
     * there is a good moment to say it.
     *
     * <p>The tasks are read in the constructor but the report on reading them is
     * not printed there, because the greeting has to come first and a constructor
     * that printed would be deciding when its own output appeared. Holding the
     * messages leaves that decision to {@link #run()}, where the order of the
     * conversation is set out.
     */
    private final List<String> loadMessages;

    /**
     * Whether the last command carried out was the one that ends the conversation.
     *
     * <p>The only field here that changes, because it is the only thing the
     * chatbot remembers about the conversation itself rather than about the tasks.
     * {@link #run()} reads it to know when to stop looping, and a graphical front
     * end reads it through {@link #isExit()} to know when to close its window.
     */
    private boolean isExit = false;

    /**
     * Whether the last command carried out could not be, and was answered with an
     * explanation instead of with what was asked for.
     *
     * <p>Remembered for the same reason {@link #isExit} is: it is something about
     * the conversation rather than about the tasks, and a window reads it through
     * {@link #isLastResponseError()} to decide how to show the answer. A complaint
     * looks like every other answer unless the window is told which it is, and an
     * error that reads like a confirmation is one the user can miss.
     *
     * <p>The console front end never asks. It has no second way of showing a line,
     * so an explanation there is simply the text of the answer.
     */
    private boolean isLastResponseError = false;

    /**
     * Creates a chatbot that keeps its tasks in one named file, picking up
     * whatever was saved there last time.
     *
     * <p>Everything this chatbot works with is settled when it is made and never
     * swapped afterwards, so all four of those fields are {@code final} and the
     * compiler enforces it. That was not possible while the class was made of
     * static fields: the task list had to start as an empty placeholder and be
     * replaced once the save file had been read.
     *
     * <p>The file is a parameter rather than a constant reached at startup, so a
     * second chatbot can be pointed somewhere else — at a scratch file in a test,
     * for instance — without disturbing the one the user is talking to.
     *
     * <p>{@link Storage#load()} does not throw: a chatbot that cannot read its
     * save file can still be used for the rest of the session, so a problem with
     * the file comes back as a message to report rather than as a failure to
     * start. That is why nothing here is wrapped in a {@code try}.
     *
     * @param filePath where to keep the task list, for example
     *                 {@link Storage#DEFAULT_FILE_PATH}.
     */
    public Bob(Path filePath) {
        this(filePath, Ui.forConsole());
    }

    /**
     * Returns a chatbot whose answers come back as text, for a window to show in
     * speech bubbles rather than print.
     *
     * <p>The only difference from the constructor above is which shape of
     * {@link Ui} the chatbot is given, and everything that follows from it: this
     * one neither prints nor reads, so its caller must ask for the greeting with
     * {@link #getGreeting()} and for each answer with {@link #getResponse}.
     *
     * <p>A named method rather than a second public constructor, because the two
     * would differ only in an argument the caller would have to know how to build.
     * A name says which chatbot is being asked for.
     *
     * @param filePath where to keep the task list, for example
     *                 {@link Storage#DEFAULT_FILE_PATH}.
     */
    public static Bob forGui(Path filePath) {
        return new Bob(filePath, Ui.forGui());
    }

    /**
     * Creates a chatbot talking through the given {@link Ui}, which is what
     * decides whether it holds its conversation at a console or in a window.
     *
     * <p>Private: the two ways in above each name a front end, so no caller has to
     * know that the difference between them is a {@code Ui}.
     */
    private Bob(Path filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(filePath);
        Storage.LoadResult result = storage.load();
        tasks = new TaskList(result.tasks());
        loadMessages = result.messages();
    }

    /**
     * Holds the conversation: greets the user, reports on the tasks picked up
     * from last time, answers commands until they say goodbye, then signs off.
     *
     * <p>Every line is read, made sense of, and carried out in the same three
     * steps, whatever the user typed. This method no longer asks which command it
     * is holding: {@link Parser} returns a {@link Command}, and running it is a
     * call to {@link Command#execute}. That is what lets a command be added to
     * the chatbot without this method changing at all.
     *
     * <p>Each answer is framed in its own block, opened before the line is
     * understood so that a complaint about it is framed like any other answer.
     * Nothing between the two ever throws — {@link #handleCommand} answers a
     * command it cannot carry out rather than failing — so the block is closed on
     * the next line rather than in a {@code finally}.
     *
     * <p>The loop ends either because a command said it was the last one, or
     * because the input ran out — which happens when it is piped in from a file
     * with no {@code bye} on the end. Only the first prints a farewell of its
     * own, through {@link bob.command.ExitCommand ExitCommand}, so the second is given one here. A user
     * whose input simply stopped is still owed a sign-off, and the same last try at
     * saving changes that an earlier save failed to write.
     */
    public void run() {
        ui.showGreeting();
        ui.showLoadReport(tasks.size(), loadMessages);
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.openBlock();
            handleCommand(fullCommand);
            ui.closeBlock();
        }
        ui.close();
        if (!isExit) {
            ui.openBlock();
            saveBeforeInputEnds();
            ui.showFarewell();
            ui.closeBlock();
        }
    }

    /**
     * Tries once more to save changes an earlier save failed to write, when the input
     * runs out without a {@code bye}, and says whether they are now saved or are being
     * left behind.
     *
     * <p>The same last try {@link bob.command.ExitCommand ExitCommand} makes, except that
     * a failure cannot hold back the goodbye: with the input gone, nobody is left to fix
     * the problem and try again.
     */
    private void saveBeforeInputEnds() {
        if (!storage.hasUnsavedChanges()) {
            return;
        }
        try {
            storage.save(tasks.asList());
        } catch (BobException e) {
            ui.showQuitWithoutSaving();
            return;
        }
        ui.showSavedBeforeQuitting();
    }

    /**
     * Carries out one line the user typed, saying through the {@link Ui} whatever
     * the command had to say — or, if it could not be carried out, why not.
     *
     * <p>Both front ends run their commands through here, which is what keeps them
     * answering alike: the same words, in the same order, for the same input. What
     * differs is only where those words end up, and that is the {@code Ui}'s
     * business rather than this method's.
     *
     * <p>This is the one place a {@link BobException} is caught. Any command that
     * cannot be carried out reports itself by throwing, the message is shown here
     * as the answer, and the caller carries on — so a mistake costs the user a
     * line, not the whole conversation.
     *
     * <p>It is also the last line of defense against a bug. A
     * {@link RuntimeException} reaching here means the chatbot itself went wrong
     * partway through a command, not that the user typed something wrong, so there
     * is nothing specific to explain — only a warning that the command may be half
     * done. Letting it escape instead would end a console conversation over one
     * faulty command, and leave a window with no answer at all.
     *
     * <p>Because it is the one place, it is also where {@link #isLastResponseError}
     * is settled: the two {@code catch} blocks below are reached by every answer
     * that is a complaint and by no answer that is not, so nothing else has to be
     * consulted to know which of the two the user is about to be given.
     */
    private void handleCommand(String fullCommand) {
        isLastResponseError = false;
        try {
            Command command = Parser.parse(fullCommand);
            // Parser answers a line it cannot make sense of by throwing, so the
            // only other way out of it is a command that is ready to be run.
            assert command != null : "Parser returned no command for: " + fullCommand;
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (BobException e) {
            ui.showError(e.getMessage());
            isLastResponseError = true;
        } catch (RuntimeException e) {
            // A deliberately broad catch: this is the top-level, last-resort handler,
            // and a bug could surface as any kind of RuntimeException. The stack trace
            // still goes to System.err so the bug is not hidden from whoever fixes it.
            // A failed assertion is an Error, not a RuntimeException, so it is not
            // caught here and still stops the program as it is meant to.
            ui.showError(UNEXPECTED_ERROR_MESSAGE);
            e.printStackTrace();
            isLastResponseError = true;
        }
    }

    /**
     * Returns the greeting the chatbot opens with, together with anything it has
     * to say about the tasks it picked up from the save file.
     *
     * <p>For a window, which has to be given the opening line to put in the first
     * speech bubble. At a console {@link #run()} prints the same two things itself,
     * as the first thing it does.
     *
     * @return the greeting as one piece of text, its lines separated by newlines.
     */
    public String getGreeting() {
        ui.showGreeting();
        ui.showLoadReport(tasks.size(), loadMessages);
        return ui.consumeShownText();
    }

    /**
     * Returns what the chatbot has to say in reply to one line typed by the user.
     *
     * <p>This is the whole of what a window needs in order to hold a conversation:
     * hand over a line, put the answer in a bubble. It is the same work
     * {@link #run()} does for one turn of its loop, minus the reading and the
     * printing, which a window does for itself.
     *
     * @param input one whole line as the user typed it.
     * @return everything the chatbot says in reply, its lines separated by
     *         newlines, or the explanation of why the line could not be carried
     *         out. Empty on a chatbot made for the console, which prints its reply
     *         rather than returning it.
     */
    public String getResponse(String input) {
        handleCommand(input);
        return ui.consumeShownText();
    }

    /**
     * Returns whether the last line given to {@link #getResponse} was the one that
     * ends the conversation, so that a window knows when to close itself.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the last line given to {@link #getResponse} could not be
     * carried out, so that a window can show the explanation as a complaint rather
     * than as an ordinary answer.
     *
     * <p>Asked after {@link #getResponse}, in the way {@link #isExit()} is, and
     * describing the same answer that call returned.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether a change is still in the list only, because an earlier save
     * failed to write it, so that a window being closed knows that closing at once
     * would lose it.
     */
    public boolean hasUnsavedChanges() {
        return storage.hasUnsavedChanges();
    }

    /**
     * Returns what the chatbot has to say when its window is closed with a change
     * still unsaved.
     *
     * <p>Closing the window is taken as the user saying goodbye, so the answer is
     * exactly the one {@code bye} would get: the save is tried once more, and a
     * failure holds back quitting once. Afterwards {@link #isExit()} and
     * {@link #isLastResponseError()} describe this answer, as they would after
     * {@link #getResponse}. The window does not have to know which word ends the
     * conversation, only that it is closing.
     *
     * @return everything the chatbot says on being closed, its lines separated by newlines.
     */
    public String getResponseToClosing() {
        return getResponse(CommandWord.BYE.getKeyword());
    }

    /**
     * Starts a chatbot on the usual save file and talks to whoever runs it.
     *
     * <p>This is the one thing that has to be {@code static}, since it is called
     * before there is an object to call it on. It makes one and steps aside.
     *
     * @param args command line arguments, which this chatbot does not use.
     */
    public static void main(String[] args) {
        new Bob(Storage.DEFAULT_FILE_PATH).run();
    }
}
