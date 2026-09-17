package harvey.ui;

import java.io.IOException;

import harvey.Harvey;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/**
 * The JavaFX application: builds the window and hands it a chatbot to talk to.
 */
public class Main extends Application {
    /** Smallest the window may be shrunk to, so the input row is never squeezed away. */
    private static final double MIN_HEIGHT = 320;

    /**
     * Smallest usable width, below which replies wrap into unreadable slivers. Kept small,
     * since the app is likely to share the screen with other windows.
     */
    private static final double MIN_WIDTH = 360;

    /** The chatbot behind the window. Built here so the window itself holds no logic. */
    private final Harvey harvey = new Harvey();

    /**
     * Builds and shows the main window.
     *
     * @param stage the window JavaFX provides.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Harvey");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Harvey.png")));
            stage.setMinHeight(MIN_HEIGHT);
            stage.setMinWidth(MIN_WIDTH);

            // The controller is created by the loader, so it can only be given the
            // chatbot after loading, not through a constructor.
            fxmlLoader.<MainWindow>getController().setHarvey(harvey);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the program, so failing to read it means the
            // build is broken rather than anything the user did.
            e.printStackTrace();
        }
    }
}
