package dulio;

import java.io.IOException;

import dulio.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the initial Dulio JavaFX window.
 */
public class DulioApp extends Application {
    private Dulio dulio = new Dulio();

    /**
     * Creates the Dulio JavaFX application.
     */
    public DulioApp() {
    }

    /**
     * Creates and displays the initial Dulio window.
     *
     * @param stage the primary stage for the application.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DulioApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);
            stage.setScene(scene);
            stage.setTitle("Dulio");
            fxmlLoader.<MainWindow>getController().setDulio(dulio);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
