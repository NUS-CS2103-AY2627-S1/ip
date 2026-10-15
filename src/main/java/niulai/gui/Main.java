package niulai.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import niulai.NiuLai;

/**
 * JavaFX application for NiuLai.
 *
 * <p>The FXML files define the view while {@link MainWindow} handles user interaction.</p>
 */
public class Main extends Application {
    /**
     * Creates and displays the main NiuLai window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        try {
            URL mainWindowResource = requireResource("/view/MainWindow.fxml");
            requireResource("/view/DialogBox.fxml");
            URL styleResource = requireResource("/view/style.css");
            GuiUi ui = new GuiUi();
            NiuLai chatbot = new NiuLai(NiuLai.DEFAULT_FILE_PATH, ui);
            FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
            BorderPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setChatbot(chatbot, ui);

            chatbot.startSession();
            controller.showBotMessage(ui.consumeOutput());

            Scene scene = new Scene(mainWindow);
            scene.getStylesheets().add(styleResource.toExternalForm());
            stage.setTitle("NiuLai");
            stage.setMinWidth(520.0);
            stage.setMinHeight(520.0);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | IllegalStateException e) {
            System.err.println("Unable to start the NiuLai GUI: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Resolves a required application resource or reports its exact missing path.
     *
     * @param path the absolute classpath resource path
     * @return the resolved resource URL
     * @throws IllegalStateException if the resource is missing
     */
    static URL requireResource(String path) {
        URL resource = Main.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Required GUI resource is missing: " + path);
        }
        return resource;
    }
}
