package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Entry point for the Remy JavaFX application. Loads the main window FXML,
 * wires it to a {@link Remy} instance, and displays the stage.
 */
public class Main extends Application {
    private Remy remy = new Remy();

    /**
     * Starts the JavaFX application by loading the main window and showing the stage.
     *
     * @param stage the primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        assert stage != null : "stage null";

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            assert ap != null : "AnchorPane load failed";

            Scene scene = new Scene(ap);
            stage.setScene(scene);

            MainWindow controller = fxmlLoader.getController();
            assert controller != null : "controller null -> FXML load failed";

            controller.setRemy(remy);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
