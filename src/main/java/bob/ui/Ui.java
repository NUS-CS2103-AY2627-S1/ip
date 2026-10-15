package bob.ui;

import java.util.List;
import java.util.Scanner;

import bob.task.Task;
import bob.task.TaskList;

/**
 * Handles everything the chatbot says to the user and everything it hears back.
 *
 * <p>This was part of {@link bob.Bob Bob}, mixed in with the code that decides what each
 * command does. Two quite different questions were being answered in the one
 * class: <em>what</em> to tell the user, and <em>how</em> a line of chatbot
 * output looks on screen. This class now answers the second one alone.
 *
 * <p>What that buys is that the shape of the conversation is stated in one
 * place. The indent, the horizontal rules framing each block, the banner and the
 * greeting all live here, so changing how the chatbot looks is a change to this
 * file and to no other. {@link bob.Bob Bob} is left saying what it wants said, in lines
 * of plain text, and never touches {@code System.out} itself.
 *
 * <p>Reading the user's input belongs here for the same reason: it is the other
 * half of the same conversation, and keeping it here means no other class has to
 * know that the input happens to arrive on {@code System.in} through a
 * {@link Scanner}.
 *
 * <p>What this class knows has since grown from the frame around the output to
 * the output itself: how a task is written in a listing, how a listing is laid
 * out, and the fixed wording of the confirmations. Those were composed line by
 * line by whoever ran the command, which meant the numbering of a listing was
 * written out afresh in each of the four commands that produce one. The wording
 * that varies with the command — the heading naming a day, say — is still passed
 * in, since only the command knows what it looked for.
 *
 * <p>That makes this class depend on {@link Task} and {@link TaskList}, which is
 * the right way round: what shows the tasks may know what a task is, while a
 * task knows nothing about being shown.
 *
 * <p>There are now two places that output can go, because the chatbot has two
 * front ends: the console it started with, and a window that shows the same
 * conversation in speech bubbles.
 * Rather than a second class saying all the same things a different way, one
 * {@code Ui} comes in two shapes — {@link #forConsole()} and {@link #forGui()} —
 * and every caller says what it wants said in exactly the same way whichever
 * shape it holds. Only the handful of methods that actually touch the screen ask
 * which shape they are, and what they do about it is set out on each of them.
 *
 * <p>Output is built up as a <em>block</em>: {@link #openBlock()}, then one
 * {@link #showLine} per line, then {@link #closeBlock()}. The block is what
 * separates one answer from the next on screen, and letting the caller print its
 * lines one at a time is what lets an answer be as long as it needs to be —
 * a listing of every task, say — without this class having to know in advance
 * how many lines that will take.
 */
public class Ui {

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Bob";

    /** Leading whitespace that sets the chatbot's output apart from the user's input. */
    private static final String INDENT = "    ";

    /**
     * Horizontal rule separating blocks of output.
     * Built with {@code repeat} so the width is stated as a number
     * rather than as a row of underscores that has to be counted by eye.
     */
    private static final String DIVIDER = "_".repeat(60);

    /**
     * Line shown above every complaint, in the chatbot's Minion voice.
     *
     * <p>Added once, here where every error is shown, rather than written into each
     * message. The explanations underneath stay in plain English, so they still say
     * clearly how to put the mistake right, and the opener of all of them is
     * changed by changing this one line.
     */
    private static final String ERROR_OPENER = "MINION EMERGENCY!";

    /**
     * Banner shown when the chatbot starts: the name King Bob, in ASCII lettering
     * drawn after the figlet "slant" font, above a picture of a Minion. Stored as
     * one line per element so each line can be indented the same way as every
     * other line the chatbot prints.
     *
     * <p>The banner says King Bob while {@link #NAME} is plain Bob. King is the
     * title the character is shown off with; in conversation he calls himself by
     * his name alone.
     *
     * <p>The picture is drawn in braille characters rather than ASCII. Each braille
     * character is a small grid of dots, so it shows far finer detail than a slash
     * or an underscore can, which is what lets the picture look like a Minion at
     * this size. The price is that a console unable to show Unicode, such as an
     * older Windows one, prints a question mark in place of each character.
     *
     * <p>No line is wider than the {@link #DIVIDER} that frames each block of
     * output, so the art never spills past the right-hand end of the rule.
     *
     * <p>Each backslash is written twice because a backslash starts an escape
     * sequence in a Java string literal; {@code \\} is the escape that means one
     * literal backslash, so the doubled ones here print singly.
     *
     * <p>Held in an immutable {@link List#of} list rather than an array. The coding
     * standard reserves the {@code SCREAMING_SNAKE_CASE} name for constants, and a
     * {@code static final String[]} is not one — the reference cannot be reassigned,
     * but any code could still overwrite an element. An immutable list cannot be
     * changed at all, so the name is honest about what it holds.
     */
    private static final List<String> BANNER_LINES = List.of(
            "        __ __  _                     ____        __",
            "       / //_/ (_) ____   ____       / __ )____  / /_",
            "      / ,<   / / / __ \\ / __ \\     / __  / __ \\/ __ \\",
            "     / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /",
            "    /_/ |_|/_/ /_/ /_/ \\__, /    /_____/\\____/_.___/",
            "                      /____/",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀",
            "    ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀",
            "    ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀",
            "    ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀",
            "    ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀",
            "    ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇",
            "    ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀",
            "    ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
            "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀");

    /**
     * Where the user's typing is read from.
     *
     * <p>Made once and kept, rather than a new one per line: a {@link Scanner}
     * reads ahead into a buffer of its own, so a second one made partway through
     * the conversation could find the text it wants already taken by the first.
     *
     * <p>A {@code Scanner} reads nothing until it is asked to, so the one belonging
     * to a window — which takes its input from a text field and never asks — costs
     * nothing beyond the object itself.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Whether output is printed to the console, rather than collected for
     * whoever asked to show it themselves.
     *
     * <p>The two front ends want the same words in different shapes. A console
     * wants them printed as they are decided, indented and framed by dividers; a
     * window wants them handed back as one piece of text to put in a speech
     * bubble, with no framing at all, because the bubble is the frame.
     *
     * <p>Which of the two this is, is settled when the {@code Ui} is made and never
     * changes afterwards, so every method below has only to ask.
     */
    private final boolean isPrintingToConsole;

    /**
     * What has been shown since {@link #consumeShownText()} was last called.
     *
     * <p>Filled only when this {@code Ui} is not printing: a console has already
     * shown its lines and has no reason to hold on to them.
     */
    private final StringBuilder shownText = new StringBuilder();

    /**
     * Creates a {@code Ui} in one of its two shapes.
     *
     * <p>Private, because a caller that passed the wrong {@code true} or
     * {@code false} would get a chatbot talking to nobody. The two factory methods
     * below name the shapes instead, so the choice is made by picking a name
     * rather than by remembering which way round the flag goes.
     */
    private Ui(boolean isPrintingToConsole) {
        this.isPrintingToConsole = isPrintingToConsole;
    }

    /**
     * Returns a {@code Ui} that prints what it is told to the console, and reads
     * the user's replies back from the keyboard.
     */
    public static Ui forConsole() {
        return new Ui(true);
    }

    /**
     * Returns a {@code Ui} that collects what it is told, for a graphical front end
     * to fetch with {@link #consumeShownText()} and show in its own way.
     *
     * <p>Nothing is printed and nothing is read: a window supplies both halves of
     * the conversation itself.
     */
    public static Ui forGui() {
        return new Ui(false);
    }

    /**
     * Shows the banner and welcome message as one block.
     *
     * <p>The banner is console-only. It is drawn by lining characters up in
     * columns, which holds together only in a fixed-width font; a window shows its
     * text in a proportional one, where the same lines come out ragged.
     */
    public void showGreeting() {
        openBlock();
        if (isPrintingToConsole) {
            for (String bannerLine : BANNER_LINES) {
                showLine(bannerLine);
            }
        }
        showLine("Bello! Me " + NAME + "!");
        showLine("Wat yu want " + NAME + " do? Banana?");
        closeBlock();
    }

    /**
     * Prints the sign-off message.
     *
     * <p>Unlike {@link #showGreeting()} this does not frame itself in a block,
     * because it is usually printed inside the block already opened for the
     * {@code bye} that asked for it. The one caller that prints it outside a
     * block opens one around it.
     */
    public void showFarewell() {
        showLine("Poopaye! " + NAME + " go find banana, see yu soon!");
    }

    /**
     * Prints that changes an earlier save failed to write have now been saved, as the
     * chatbot quits.
     */
    public void showSavedBeforeQuitting() {
        showLine("Your changes are saved now.");
    }

    /**
     * Prints that the chatbot is quitting with changes it could not save.
     *
     * <p>Not shown as an error. Why the save failed was shown when the change was made,
     * and quitting anyway is what the user asked for.
     */
    public void showQuitWithoutSaving() {
        showLine(NAME + " quit without saving yur changes. Dey gone!");
    }

    /**
     * Returns whether the user has typed another line for the chatbot to read.
     *
     * <p>This is asked rather than assumed, so the chatbot also ends cleanly when
     * the input runs out — for example when it is piped in from a file that has
     * no {@code bye} line on the end.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line the user typed, exactly as they typed it.
     *
     * <p>Surrounding spaces are left in place for {@link bob.parser.Parser Parser}
     * to remove. A window hands its lines to the chatbot without coming through
     * here, so trimming in the parser is what makes the two front ends read a line
     * alike.
     *
     * <p>Call only when {@link #hasNextCommand()} has just said there is one.
     *
     * @return one whole line as the user typed it.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints an explanation of something that went wrong, under the
     * {@link #ERROR_OPENER} line, one line per line of the message, so that a
     * message written as several lines is indented like any other output.
     *
     * <p>Printed inside whatever block is already open, so an error reads as the
     * answer to the command that caused it rather than as an interruption.
     *
     * @param message the explanation to show, which may span several lines.
     */
    public void showError(String message) {
        showLine(ERROR_OPENER);
        for (String line : message.split("\n")) {
            showLine(line);
        }
    }

    /**
     * Prints what was picked up from the save file, and anything the user should
     * know about reading it, as one block.
     *
     * <p>Nothing is printed on the two ordinary cases — no save file yet, or a
     * save file that read cleanly and was empty — because a user who has nothing
     * saved does not need to be told about a file they have never seen.
     *
     * @param taskCount how many tasks were read back.
     * @param messages  what {@link bob.storage.Storage Storage} had to say about reading them, which
     *                  is empty when it had nothing to report.
     */
    public void showLoadReport(int taskCount, List<String> messages) {
        if (taskCount == 0 && messages.isEmpty()) {
            return;
        }
        openBlock();
        if (taskCount > 0) {
            showLine(NAME + " found " + describeTaskCount(taskCount)
                    + " from before.");
        }
        for (String message : messages) {
            showLine(message);
        }
        closeBlock();
    }

    /**
     * Confirms a task just added, showing it back and saying how long the list
     * now is.
     *
     * @param task      the task that was added.
     * @param taskCount how many tasks there are now.
     */
    public void showAddedTask(Task task, int taskCount) {
        showLine("Okay! " + NAME + " add dis:");
        showLine("  " + task);
        showLine("Now yu have " + describeTaskCount(taskCount) + " in da list.");
    }

    /**
     * Confirms a task just removed, showing it one last time so the user can see
     * which one is gone rather than working it out from the new numbering.
     *
     * @param task      the task that was removed.
     * @param taskCount how many tasks are left.
     */
    public void showRemovedTask(Task task, int taskCount) {
        showLine("Bee-do! " + NAME + " throw away dis:");
        showLine("  " + task);
        showLine("Now yu have " + describeTaskCount(taskCount) + " in da list.");
    }

    /**
     * Returns how many tasks there are, worded to agree with the number, for
     * example {@code 1 task} or {@code 3 tasks}.
     *
     * <p>Every message in this class that states a number of tasks is worded
     * through here, so the singular is handled once rather than in each message.
     *
     * @param taskCount how many tasks to describe.
     */
    private static String describeTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

    /**
     * Confirms a task just marked done, or just marked not done again.
     *
     * @param task   the task whose status changed.
     * @param isDone the status it now has.
     */
    public void showMarkedTask(Task task, boolean isDone) {
        showLine(isDone
                ? "Kanpai! Dis one finish:"
                : "Bi-do! Dis one not finish yet:");
        showLine("  " + task);
    }

    /**
     * Says that a task already had the status it was just asked for, showing it so
     * the user can check it is the task they meant.
     *
     * <p>Worded as an ordinary answer rather than as a complaint, since the task ends
     * up just as the user asked.
     *
     * @param task   the task, which has not changed.
     * @param isDone the status it was asked for, and already has.
     */
    public void showAlreadyMarkedTask(Task task, boolean isDone) {
        showLine(isDone
                ? "Dis one already finish! Nothing to change:"
                : "Dis one not finish yet! Nothing to change:");
        showLine("  " + task);
    }

    /**
     * Confirms a task just edited, showing it as it was and as it now is.
     *
     * <p>Both are shown because an edit changes only the details the user typed, and
     * seeing the two one above the other is what makes plain which details those were.
     *
     * @param before the task as it was before the edit.
     * @param after  the task as it is now.
     */
    public void showEditedTask(Task before, Task after) {
        showLine("Tadaa! Dis was:");
        showLine("  " + before);
        showLine("Now is:");
        showLine("  " + after);
    }

    /**
     * Prints a numbered listing of some of the tasks, or says there are none.
     *
     * <p>Which tasks to show arrives as their positions rather than as the tasks
     * themselves, because the number shown against each one is its place in the
     * whole list. That is what makes a shortened listing useful: a number read off
     * it can be typed straight into {@code mark}, {@code unmark} or
     * {@code delete}, which would not be true of numbers counting the matches.
     *
     * <p>The two lines of wording are passed in because they are the part that
     * belongs to the command that asked — a heading naming a day, or naming how
     * many urgent tasks were found, is not something this class could write.
     *
     * @param tasks        the list the positions refer to.
     * @param indexes      the positions of the tasks to show, counting from 0.
     * @param heading      the line introducing them, printed only if there are any.
     * @param emptyMessage the line to print instead when there are none.
     */
    public void showTasks(TaskList tasks, List<Integer> indexes, String heading,
            String emptyMessage) {
        if (indexes.isEmpty()) {
            showLine(emptyMessage);
            return;
        }
        showLine(heading);
        for (int index : indexes) {
            showLine(formatNumberedTask(tasks, index));
        }
    }

    /**
     * Returns one task written as a line of a listing, for example
     * {@code 2.[D][ ] return book (by: Dec 02 2026)}.
     */
    private static String formatNumberedTask(TaskList tasks, int index) {
        // The user counts from 1, the list counts from 0.
        return (index + 1) + "." + tasks.get(index);
    }

    /**
     * Starts a block of output with a divider.
     *
     * <p>Does nothing when the output is being collected rather than printed. A
     * window separates one answer from the next by putting each in a speech bubble
     * of its own, so a rule drawn in text would only be a second frame inside the
     * first.
     */
    public void openBlock() {
        if (isPrintingToConsole) {
            System.out.println(INDENT + DIVIDER);
        }
    }

    /**
     * Shows one line of chatbot output: printed with the usual indent at the
     * console, or added to the text being collected for a window.
     *
     * @param text what the line should say, without any indent of its own.
     */
    public void showLine(String text) {
        if (isPrintingToConsole) {
            System.out.println(INDENT + " " + text);
        } else {
            shownText.append(text).append('\n');
        }
    }

    /**
     * Ends a block with a divider, plus a blank line before whatever comes next.
     *
     * <p>Does nothing when the output is being collected, for the reason given on
     * {@link #openBlock()}.
     */
    public void closeBlock() {
        if (isPrintingToConsole) {
            System.out.println(INDENT + DIVIDER);
            System.out.println();
        }
    }

    /**
     * Returns everything shown since this was last called, and forgets it, so that
     * the next call returns only what is said next.
     *
     * <p>This is how a graphical front end collects an answer. It runs a command,
     * which tells this {@code Ui} its lines one at a time exactly as it would at a
     * console, and then asks here for the lot as the one piece of text that goes
     * into a speech bubble.
     *
     * <p>Always empty on a {@code Ui} made by {@link #forConsole()}, which prints
     * its lines rather than keeping them.
     *
     * @return the lines shown since the last call, separated by newlines, with no
     *         blank line at either end.
     */
    public String consumeShownText() {
        String text = shownText.toString().strip();
        shownText.setLength(0);
        return text;
    }

    /**
     * Stops reading the user's input, releasing the {@link Scanner} behind it.
     *
     * <p>Called once, when the conversation is over. Only reading stops: the
     * farewell is printed after this, and printing is unaffected.
     *
     * <p>A more advanced alternative is to make this class {@code AutoCloseable}
     * and have the caller wrap it in a try-with-resources, which would close it
     * even if the chatbot stopped part-way with an error. That is not worth its
     * keep here, where the only resource is the standard input of a program that
     * is about to end anyway.
     */
    public void close() {
        scanner.close();
    }
}
