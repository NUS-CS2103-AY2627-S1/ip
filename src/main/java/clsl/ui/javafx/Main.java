package clsl.ui.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Starts the JavaFX graphical user interface. */
public class Main extends Application {
    private static final double INITIAL_SCENE_WIDTH = 900;
    private static final double INITIAL_SCENE_HEIGHT = 650;
    private static final double MIN_SCENE_WIDTH = 700;
    private static final double MIN_SCENE_HEIGHT = 520;

    @Override
    public void start(Stage stage) throws IOException {
        Parent mainLayout = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        assert mainLayout != null : "Main.fxml must load successfully";
        Scene scene = new Scene(mainLayout, INITIAL_SCENE_WIDTH, INITIAL_SCENE_HEIGHT);
        stage.setTitle("Clsl");
        stage.setMinWidth(MIN_SCENE_WIDTH);
        stage.setMinHeight(MIN_SCENE_HEIGHT);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
