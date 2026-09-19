package carl.ui;

import java.io.IOException;

import carl.Carl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Serves as the main GUI entry point for the Carl application.
 */
public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle(Carl.BOT_NAME);
            stage.setMinWidth(340);
            stage.setMinHeight(440);
            stage.setResizable(true);
            Carl carl = new Carl();
            carl.start();
            fxmlLoader.<MainWindow>getController().setCarl(carl);
            stage.show();
        } catch (IOException | RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Carl could not start");
            alert.setHeaderText("The interface could not be loaded.");
            alert.setContentText("Check that Carl's resource files are present, then restart the application.");
            alert.showAndWait();
        }
    }

}
