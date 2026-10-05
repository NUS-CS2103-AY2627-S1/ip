package friday;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the JavaFX GUI for Friday.
 */
public class Main extends Application {
    private final Friday friday = new Friday("data/duke.txt");

    /**
     * Starts the JavaFX application window.
     *
     * @param stage primary application stage
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        Scene scene = new Scene(mainWindow);

        fxmlLoader.<MainWindow>getController().setFriday(friday);
        stage.setTitle("FRIDAY Assistant");
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(350);
        stage.show();
    }
}
