package nova;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for Nova, built from the FXML views under
 * {@code src/main/resources/view}.
 */
public class Main extends Application {
    /**
     * Smallest window the layout still reads well in. Lower than it needs to
     * be for the controls alone, because the transcript no longer wastes width
     * on chrome: at 320 a reply still gets 92% of the window for its text.
     */
    private static final double MIN_WIDTH = 320.0;
    private static final double MIN_HEIGHT = 280.0;

    private final Nova nova = new Nova();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Nova");

            // The badge used to sit beside every reply, where it cost width
            // that long replies need for text. As the window icon it is shown
            // once, by the window manager, and costs the layout nothing.
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Nova.png")));

            // The layout is fluid now, so resizing is worth allowing. The
            // minimum stops the input bar being squeezed to nothing.
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);

            fxmlLoader.<MainWindow>getController().setNova(nova);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load MainWindow.fxml", e);
        }
    }
}
