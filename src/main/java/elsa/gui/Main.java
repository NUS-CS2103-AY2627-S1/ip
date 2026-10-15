package elsa.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import elsa.Elsa;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The JavaFX application that shows the chatbot's window.
 *
 * <p>A JavaFX application is not written as a method that draws a window and
 * returns. JavaFX itself creates this object, starts a thread to draw on, and
 * then calls {@link #start(Stage)} once the toolkit is ready.
 *
 * <p>What that method does is all this class is for: read the layout from
 * {@code /view/MainWindow.fxml}, put it in a window, and hand the chatbot to the
 * controller the file names. The controls themselves are described in that file
 * and driven by {@link MainWindow}, so nothing here knows there is a text field
 * or a send button at all.
 */
public class Main extends Application {
    /**
     * The smallest the window may be dragged to, in pixels.
     * Below this the place to type is squeezed out and the conversation is too
     * narrow to read, so the window is stopped rather than allowed to become
     * useless. The size it opens at is in the FXML, but a minimum belongs to the
     * window rather than to the layout inside it.
     */
    private static final double MIN_WIDTH = 360.0;
    private static final double MIN_HEIGHT = 480.0;

    /** The chatbot the window is a face for. */
    private final Elsa elsa = new Elsa();

    /**
     * Creates the application object.
     * JavaFX makes this object itself, by reflection, so it needs a constructor
     * that takes no arguments. Java supplies one to any class that declares
     * none, but declaring it says out loud that JavaFX depends on it and that
     * removing it would break the application at run time rather than at
     * compile time.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(resource("/view/MainWindow.fxml"));

        BorderPane root;
        try {
            root = loader.load();
        } catch (IOException e) {
            // Not recoverable and not the user's doing: the layout is part of the
            // program, so a copy that cannot be read means the build is wrong.
            throw new IllegalStateException("/view/MainWindow.fxml could not be read", e);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(resource("/css/elsa.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Elsa");

        // The title bar and the taskbar show a default placeholder until the
        // window is given an icon of its own, which leaves the program looking
        // like any other unfinished JavaFX application wherever it is not in
        // front. Elsa's own portrait is already in the build for the
        // conversation, so it is what the window is known by too.
        stage.getIcons().add(new Image(resource("/images/ElsaPortrait.png").toExternalForm()));
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        // The loader made the controller while reading the file, so this is the
        // one the window's controls are wired to, not a new one.
        loader.<MainWindow>getController().startWith(elsa);

        stage.show();
    }

    /**
     * Returns where one of the program's own files is, ready for JavaFX to read.
     *
     * @param path the file's place in the resources, beginning with a slash.
     * @return the file's location.
     * @throws NullPointerException if the file is missing from the build, which
     *     is worth failing loudly for: JavaFX would otherwise draw a window with
     *     no styling, or none at all, and say nothing about why.
     */
    private URL resource(String path) {
        return Objects.requireNonNull(getClass().getResource(path), path + " is missing from the build");
    }
}
