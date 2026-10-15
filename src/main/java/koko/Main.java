package koko;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Koko JavaFX application.
 */
public class Main extends Application {
    private final Koko koko = new Koko();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/main.fxml"));

            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
            stage.setTitle("Koko");
            stage.setMinWidth(400);
            stage.setMinHeight(500);
            stage.setWidth(500);
            stage.setHeight(650);

            MainWindow controller = fxmlLoader.getController();
            controller.setKoko(koko);

            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
