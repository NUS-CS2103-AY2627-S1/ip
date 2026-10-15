package nutrisoy.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import nutrisoy.Duke;

/**
 * JavaFX application that displays the NutriSoy chat interface.
 */
public class Main extends Application {
    /**
     * Loads and displays the main application window.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the main window layout cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow mainWindow = loader.getController();
        Duke duke = new Duke(Duke.DEFAULT_STORAGE_PATH);
        mainWindow.setDuke(duke);

        stage.setTitle("NutriSoy - " + Ui.BOT_NAME);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/styles/main.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(440);
        stage.setMinHeight(480);
        stage.setOnCloseRequest(event -> {
            if (duke.hasUnsavedChanges()) {
                Alert warning = new Alert(Alert.AlertType.CONFIRMATION,
                        "Some changes have not been saved. Close and discard those changes?",
                        ButtonType.CANCEL, ButtonType.YES);
                warning.initOwner(stage);
                warning.setTitle("NutriSoy - Unsaved changes");
                warning.setHeaderText("Your tasks need a save first");
                if (warning.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.YES) {
                    event.consume();
                }
            }
        });
        stage.show();
    }
}
