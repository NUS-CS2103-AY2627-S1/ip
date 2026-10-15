package dennis.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point: builds the main window from FXML, wires in the
 * {@link GuiResponder} backend, and shows the stage.
 */
public class MainApp extends Application {
    /** Backend session that produces Dennis's replies. */
    private final GuiResponder responder = new GuiResponder();

    /** Required by JavaFX, which instantiates this class reflectively. */
    public MainApp() {
    }

    /**
     * Loads {@code MainWindow.fxml}, injects the backend into its controller,
     * and displays the window.
     *
     * @param stage the primary stage supplied by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            MainWindow controller = loader.getController();
            controller.setResponder(responder);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    MainApp.class.getResource("/view/dennis.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Dennis");
            stage.setMinHeight(220);
            stage.setMinWidth(400);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load the GUI layout from /view/MainWindow.fxml", e);
        }
    }
}
