package billy.gui;

import java.io.IOException;

import billy.Billy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Billy as a window rather than a console conversation.
 *
 * <p>Its whole job is to put the window on the screen: it loads the layout,
 * hands the controller a Billy to talk to, and shows the stage. What the window
 * then does is {@link MainWindow}'s business.
 */
public class Main extends Application {

    /** The Billy this window talks to, kept for the life of the window. */
    private final Billy billy = new Billy();

    /**
     * Builds the window and shows it.
     *
     * @param stage the window JavaFX has already made for us to fill
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            scene.getStylesheets().add(Main.class.getResource("/css/dialog-box.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Billy");
            // The same face the conversation uses, so Billy is recognizable in
            // the dock and the task switcher rather than being a generic Java
            // coffee cup there and a chatbot only once opened.
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaBilly.png")));
            // A floor rather than a fixed size: the layout is built out of
            // anchors and a growing input row, so the window is free to be
            // resized to anything at or above this and the content follows.
            stage.setMinHeight(320.0);
            stage.setMinWidth(360.0);

            // Handing Billy over has to wait until the layout is loaded, since
            // that is what creates the controller in the first place.
            fxmlLoader.<MainWindow>getController().setBilly(billy);
            stage.show();
        } catch (IOException e) {
            // Nothing can be done about a layout file that will not load: there
            // is no window to report it in, so it goes to the console instead.
            e.printStackTrace();
        }
    }
}
