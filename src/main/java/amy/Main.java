package amy;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Amy using FXML.
 */
public class Main extends Application {

    private static final String APPLICATION_TITLE = "Amy";

    private final Amy amy = new Amy(Storage.getDefaultSavePath());

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle(APPLICATION_TITLE);
            stage.setMinHeight(360);
            stage.setMinWidth(480);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/ css/main.css").toExternalForm());
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setAmy(amy);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            showStartupError(exception);
        }
    }

    /** Displays a helpful message when Amy's GUI cannot be loaded. */
    private void showStartupError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(APPLICATION_TITLE);
        alert.setHeaderText("Startup failed");
        alert.setContentText("Please try again to load Amy.");
        alert.showAndWait();
        exception.printStackTrace();
    }
}
