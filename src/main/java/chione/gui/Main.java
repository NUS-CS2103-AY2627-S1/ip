package chione.gui;

import java.io.IOException;

import chione.Chione;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The window Chione runs in.
 *
 * <p>Its whole job is to put the main window on screen and hand it a
 * {@link Chione} to talk to. What the window looks like is described in
 * {@code MainWindow.fxml}, and how it behaves belongs to {@link MainWindow}, so
 * neither concern is written out here.
 */
public class Main extends Application {
    /**
     * The chatbot behind the window.
     *
     * <p>Built here rather than inside {@link MainWindow} so that the window
     * stays a piece of user interface: it is handed a chatbot to talk to, and
     * could be handed a different one.
     */
    private final Chione chione = new Chione();

    /**
     * Creates the application.
     *
     * <p>JavaFX constructs this class itself when {@link chione.gui.Launcher}
     * starts it, so the constructor has nothing to do: it exists so that the
     * documentation build has something to attach this comment to.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root);
            // How things look is kept in a stylesheet, apart from what they are
            // (the FXML) and what they do (the controllers), so colors and
            // spacing can be tuned without touching either.
            scene.getStylesheets().add(Main.class.getResource("/css/chione.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Chione");
            // The window may be made as large as the user likes; the layout
            // follows. Only shrinking is limited, to where the input bar and a
            // few lines of conversation would no longer fit.
            stage.setMinHeight(360.0);
            stage.setMinWidth(380.0);

            fxmlLoader.<MainWindow>getController().setChione(chione);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the program rather than supplied by the
            // user, so failing to read it means the build is broken, not that
            // anyone did anything wrong. There is no window to apologize in.
            e.printStackTrace();
        }
    }
}
