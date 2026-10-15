package piplupbot;

import javafx.application.Application;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Starts the program in the face the user asked for: the window, or the
 * console when given {@code --cli}. This is the program's front door: the main
 * class named in {@code build.gradle}, and so the one that
 * {@code ./gradlew run} and the JAR file start.
 *
 * <p>Why not start the window, {@link GuiUi}, directly? Because it extends
 * JavaFX's {@code Application}. When the class Java is asked to start is an
 * {@code Application}, Java insists on finding JavaFX as a module, and stops
 * with "JavaFX runtime components are missing" when JavaFX is on the classpath
 * instead -- which is where Gradle and the JAR file put it. Starting from an
 * ordinary class avoids that check. So this class may call on
 * {@code Application}, as {@link #main} does, but must never extend it.</p>
 *
 * <p>And why not start {@link PiplupBot} directly? It is an ordinary class too,
 * but its own {@code main} opens the console, as it always has: the text-UI
 * tests start it with no arguments and expect the console. So this class is
 * what makes the window the default.</p>
 */
public class Launcher {
    /** The argument that asks for the console instead of the window. */
    private static final String CLI_FLAG = "--cli";

    /**
     * Starts PiplupBot in the window, or in the console if the first argument
     * is {@code --cli}.
     *
     * <p>For the window, JavaFX is given the {@link GuiUi} class rather than an
     * object: JavaFX creates the window itself, and the window makes its own bot
     * with the same save file. JavaFX is loaded only when the window is chosen,
     * so the console starts as quickly as it always did, and without JavaFX's
     * warnings.</p>
     *
     * @param args {@code --cli} for the console; nothing, for the window.
     */
    public static void main(String[] args) {
        boolean isCli = args.length > 0 && args[0].equals(CLI_FLAG);
        if (isCli) {
            new PiplupBot(PiplupBot.DEFAULT_FILE_PATH).run();
        } else {
            // Returns once the window has been closed.
            Application.launch(GuiUi.class, args);
        }
    }
}
