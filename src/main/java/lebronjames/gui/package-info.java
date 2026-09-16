/**
 * The graphical front end: a chat window built with JavaFX.
 *
 * <p>{@link lebronjames.gui.Main} builds the window,
 * {@link lebronjames.gui.MainWindow} passes what the user types to
 * {@link lebronjames.LebronJames} and shows the reply, and
 * {@link lebronjames.gui.DialogBox} is one speech bubble in that conversation.
 * Layouts live in {@code src/main/resources/view}.
 *
 * <p>Nothing here decides what a command means or what it does; that stays in
 * the parser and command classes, which is why the console interface and this
 * one can share them unchanged.
 */
package lebronjames.gui;
