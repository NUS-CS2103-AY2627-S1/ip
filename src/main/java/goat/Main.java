package goat;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the JavaFX GUI for Goat.
 */
public class Main extends Application {
    private final Goat goat = new Goat();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setGoat(goat);

        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.setTitle("Goat");
        stage.setMinHeight(520);
        stage.setMinWidth(380);
        stage.show();
    }
}
