package baymax;

import java.io.IOException;

import baymax.ui.CloseGuard;
import baymax.ui.MainWindow;
import baymax.ui.UiAssets;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Creates Baymax's JavaFX window and connects it to the command-processing core.
 */
public class Main extends Application {
    private static final String APPLICATION_FONT_PATH = "/fonts/Orbitron.ttf";

    private Baymax baymax;

    /**
     * Loads and displays the main JavaFX scene.
     *
     * @param stage the primary JavaFX stage
     * @throws IOException if the FXML scene cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        baymax = new Baymax();
        Font applicationFont = UiAssets.loadFont(APPLICATION_FONT_PATH);

        FXMLLoader fxmlLoader =
                new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        root.setStyle("-fx-font-family: '" + applicationFont.getFamily() + "';");
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setBot(baymax);
        mainWindow.showBotMessage(
                "Hello. I am Baymax, your personal task companion.\n"
                        + "I am here to keep your tasks healthy and organized.\n"
                        + "How may I assist you?");

        Scene scene = new Scene(root);
        stage.setTitle("Baymax Care Companion");
        stage.setScene(scene);
        stage.setMinWidth(420.0);
        stage.setMinHeight(600.0);
        stage.setOnCloseRequest(event -> {
            if (!CloseGuard.canClose(baymax::saveTasks, exception -> showSaveFailure(stage, exception))) {
                event.consume();
            }
        });
        stage.show();
    }

    /** Presents recovery choices before allowing a window with unsaved tasks to close. */
    private CloseGuard.Decision showSaveFailure(Stage stage, IOException exception) {
        ButtonType retry = new ButtonType("Retry", ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        ButtonType discard = new ButtonType("Exit without saving", ButtonData.OTHER);
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "Your latest changes have not been saved.\n"
                        + "Retry after fixing the storage problem, or cancel to keep Baymax open.\n\n"
                        + exception.getMessage(), retry, cancel, discard);
        alert.initOwner(stage);
        alert.setTitle("Unable to save care plan");
        alert.setHeaderText("Baymax could not save your tasks.");
        ButtonType choice = alert.showAndWait().orElse(cancel);
        if (choice == retry) {
            return CloseGuard.Decision.RETRY;
        }
        return choice == discard ? CloseGuard.Decision.EXIT_WITHOUT_SAVING : CloseGuard.Decision.CANCEL;
    }
}
