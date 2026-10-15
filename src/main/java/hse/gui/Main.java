package hse.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Provides the JavaFX graphical interface for HSE. */
public class Main extends Application {

    private final HseApplication hseApplication = new HseApplication();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/view/styles.css").toExternalForm());
            stage.setTitle("HSE");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setHseApplication(hseApplication);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
