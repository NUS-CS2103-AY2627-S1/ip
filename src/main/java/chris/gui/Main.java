package chris.gui;

import java.io.IOException;

import chris.Chris;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays Chris's JavaFX GUI using the FXML main window. */
public class Main extends Application {
    private final Chris chris = new Chris();

    /** Loads and displays the main chat window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = loader.load();
        Scene scene = new Scene(mainWindow);
        scene.getStylesheets().add(Main.class.getResource("/view/Chris.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Chris - Task Companion");
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
        loader.<MainWindow>getController().setChris(chris);
        stage.show();
    }
}
