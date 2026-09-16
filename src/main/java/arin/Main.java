package arin;

import java.io.IOException;
import java.util.Objects;

import arin.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Opens Arin's interactive JavaFX task companion.
 */
public class Main extends Application {
    /**
     * Loads the responsive chat layout and connects it to a persistent command session.
     *
     * @param stage Primary stage provided by JavaFX.
     * @throws IOException If the packaged layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("/view/MainWindow.fxml")));
        loader.setController(new MainWindow(new Arin()));
        Scene scene = new Scene(loader.load(), 640, 700);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/view/arin.css")).toExternalForm());
        stage.setTitle("Arin");
        stage.setMinWidth(440);
        stage.setMinHeight(480);
        stage.setScene(scene);
        stage.show();
    }
}
