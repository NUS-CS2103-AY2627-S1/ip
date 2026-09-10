package surveyprogram.gui;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import surveyprogram.DeviceG;

/**
 * Launches the application's FXML-based GUI.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            loadRobotoFont();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle("EVIL ROARING GROUP CHAT");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setCommandProcessor(
                    DeviceG.createCommandProcessor());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the bundled Roboto font for use by the application's stylesheets.
     *
     * @throws IOException if the bundled font cannot be found or read
     */
    private void loadRobotoFont() throws IOException {
        try (InputStream fontStream = Main.class.getResourceAsStream("/fonts/Roboto-Regular.ttf")) {
            if (fontStream == null) {
                throw new IOException("Bundled Roboto font could not be found.");
            }
            Font.loadFont(fontStream, 12);
        }
    }
}
