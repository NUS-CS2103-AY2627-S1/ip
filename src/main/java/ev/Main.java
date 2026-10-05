package ev;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the graphical user interface for E.V. using FXML.
 */
public class Main extends Application {
    private final EV ev = new EV();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("E.V.");
            stage.setMinHeight(420);
            stage.setMinWidth(380);

            fxmlLoader.<MainWindow>getController().setEv(ev);
            stage.show();
        } catch (IOException e) {
            showStartupFailure(e);
        }
    }

    /**
     * Tells the user why the interface could not be built, then closes the application.
     *
     * @param cause the failure that stopped the window from loading.
     */
    private void showStartupFailure(IOException cause) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "E.V. could not build its interface: " + cause.getMessage());

        alert.showAndWait();
        Platform.exit();
    }
}
