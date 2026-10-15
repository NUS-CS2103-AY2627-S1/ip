package piplupbot;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents everything the bot says to the user, wherever the user is reading
 * it.
 *
 * <p>Every command reports what it did through this interface rather than
 * through {@code System.out}, so the classes that decide <em>what</em> to say
 * never have to know <em>where</em> it ends up. That is what lets one bot have
 * two faces: {@link TextUi} prints each reply in the console, between dividers,
 * and {@link GuiUi} adds it to the conversation in its window. A command is
 * handed one or the other and cannot tell which.</p>
 *
 * <p>Only {@link #show} differs between the two faces, so it is the one method
 * each face must write for itself. The greeting, the goodbye, an error and a
 * list are the same words in both, so they are written once here, in terms of
 * {@link #show}, as {@code default} methods. Marking a method {@code default}
 * gives it a body inside the interface, and every class that implements the
 * interface then has the method too, unless it overrides it -- as
 * {@link TextUi#showWelcome} does, to draw its banner first.</p>
 *
 * <p>This is an interface rather than an abstract class, unlike
 * {@link piplupbot.command.Command Command}, because {@link GuiUi} already has a
 * parent class: to be a JavaFX window it must extend JavaFX's
 * {@code Application}, and a Java class can extend only one class. It can
 * implement any number of interfaces, though. What an interface cannot have is
 * instance fields, and this one needs none: whatever a face has to remember,
 * such as the console's {@code Scanner} or the window's text area, is kept by
 * that face.</p>
 */
public interface Ui {
    /**
     * Shows one reply, made of one or more lines.
     * How the lines are laid out is up to each face.
     *
     * @param lines The lines of text to display.
     */
    void show(String... lines);

    /**
     * Shows a heading with items listed beneath it, all as one reply rather than
     * one reply per item.
     *
     * <p>Pasting the heading on top of the items is a question of layout, so it
     * belongs here beside {@link #show} rather than in the class that chose the
     * words. The heading is a parameter because the same items can be introduced
     * differently depending on why they are being shown.</p>
     *
     * <p>The items are taken as varargs, like {@link #show}'s lines, so the two
     * ways of showing a reply are called the same way. An array still passes
     * unchanged -- that is what varargs is underneath -- so a caller holding a
     * whole list, as {@code list} and {@code find} do, hands it straight over,
     * while one with a couple of lines in mind writes them out instead of
     * wrapping them in an array first.</p>
     *
     * @param heading The line that introduces the items.
     * @param items   The lines to show beneath it, possibly none.
     */
    default void showList(String heading, String... items) {
        String[] lines = new String[items.length + 1];
        lines[0] = heading;
        System.arraycopy(items, 0, lines, 1, items.length);
        show(lines);
    }

    /**
     * Greets the user.
     */
    default void showWelcome() {
        show("Hello! I'm PiplupBot. Pip-pip!", "What can I do for you?");
    }

    /**
     * Says goodbye, in answer to the {@code bye} command.
     */
    default void showGoodbye() {
        show("Pip-pip! Off for a swim. Hope to see you again soon!");
    }

    /**
     * Explains an error the bot recognized.
     *
     * <p>Taking the exception itself, rather than the lines inside it, keeps the
     * caller from having to know that the message is stored as separate lines.</p>
     *
     * @param e The error to explain.
     */
    default void showError(PiplupBotException e) {
        show(e.getMessageLines());
    }
}
