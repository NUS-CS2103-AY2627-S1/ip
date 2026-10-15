package bob.ui;

import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * Holds the pictures the window is built from.
 *
 * <p>These were loaded where they were used, which was fine while the only place
 * that used them was the conversation. The window's icon needs the same picture of
 * the chatbot, and a second copy of the file name in a second class is a copy that
 * can be missed when the file is renamed. Naming each file once, here, means there
 * is one place to change when a picture is swapped for a different one.
 *
 * <p>Loaded once and shared, rather than re-read for every speech bubble. An
 * {@link Image} cannot be altered once it has been read, so there is nothing to be
 * gained by each bubble holding one of its own, and a conversation of any length
 * would otherwise read the same two files over and over.
 *
 * <p>The paths are kept alongside the pictures because the two toolkits at work
 * here want the file in different forms: JavaFX draws the {@link Image}, while the
 * operating system's dock is asked for its icon through the older AWT classes,
 * which cannot be handed a JavaFX one.
 */
public final class Images {

    /** Where the chatbot's picture is kept, inside the packaged program. */
    public static final String BOB_PATH = "/images/DaBob.png";

    /** Where the user's picture is kept, inside the packaged program. */
    public static final String USER_PATH = "/images/DaUser.png";

    /** The chatbot's picture, shown beside everything it says. */
    public static final Image BOB = load(BOB_PATH);

    /** The user's picture, shown beside everything they type. */
    public static final Image USER = load(USER_PATH);

    /**
     * Prevents this class from being instantiated. It is a place to keep the
     * pictures, not a thing in its own right, so there is no reason to make one.
     */
    private Images() {
    }

    /**
     * Returns the picture kept at the given path inside the packaged program.
     *
     * @param path where the picture file sits, counting from the root of the
     *             resources folder.
     * @throws IllegalStateException If there is no file there, which means a
     *                               broken build rather than anything the user did.
     */
    private static Image load(String path) {
        InputStream stream = Images.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Could not find the picture at " + path);
        }
        return new Image(stream);
    }
}
