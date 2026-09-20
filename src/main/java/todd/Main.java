package todd;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** Provides the JavaFX entry point for Todd's graphical interface. */
public class Main extends Application {
    private final Todd todd = new Todd();

    /** Loads and displays the main chat window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

        loader.<MainWindow>getController().setTodd(todd);
        stage.setScene(scene);
        stage.setTitle("Todd");
        stage.setMinWidth(420);
        stage.setMinHeight(480);
        stage.show();
    }
}
