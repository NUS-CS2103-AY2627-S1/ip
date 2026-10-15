/**
 * Chione's window: the JavaFX classes that show the conversation on screen.
 *
 * <p>Nothing in here knows what a task is or what a command does. The window
 * hands a line of text to {@link chione.Chione} and shows whatever comes back,
 * which is why the same chatbot can also be used from the console without any of
 * these classes being involved.
 *
 * <p>Each window is described in two places: an FXML file under
 * {@code resources/view} says what it looks like, and a class here says how it
 * behaves. Keeping the two apart means the layout can be adjusted — by hand or in
 * Scene Builder — without touching any Java.
 */
package chione.gui;
