package friday;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** JavaFX entry point that loads Friday's main chat window from FXML. */
public class Main extends Application {
    private final Friday friday = new Friday();

    /** Creates and displays the primary Friday chat window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        BorderPane root = loader.load();
        loader.<friday.ui.MainWindow>getController().setFriday(friday);
        Scene scene = new Scene(root);
        stage.setTitle("FRIDAY | Mission Control");
        stage.setMinWidth(360);
        stage.setMinHeight(440);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.setWidth(480);
        stage.setHeight(640);
        stage.show();
    }
}
