package sozius.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sozius.Sozius;
import sozius.exception.SoziusException;

/**
 * A GUI for Sozius using FXML.
 */
public class Gui extends Application {

    private Sozius sozius = new Sozius();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap, 440, 600);
            stage.setTitle("Sozius — Task assistant");
            stage.setResizable(true);
            stage.setMinWidth(340);
            stage.setMinHeight(400);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setSozius(sozius); // inject the Sozius instance
            fxmlLoader.<MainWindow>getController().setStage(stage);
            stage.show();
            stage.setOnCloseRequest(event -> {
                try {
                    sozius.saveTasks();
                } catch (SoziusException e) {
                    System.err.println("Could not save tasks on exit: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
