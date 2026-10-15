package sine;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import sine.gui.MainWindow;

/**
 * Displays the graphical user interface for Sine.
 */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "data/sine.txt";

    private final Sine sine = new Sine(DATA_FILE_PATH);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.setTitle("Sine");
        stage.setMinHeight(420.0);
        stage.setMinWidth(360.0);
        fxmlLoader.<MainWindow>getController().setSine(sine);
        stage.show();
    }
}
