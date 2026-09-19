package yuno.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts and displays Yuno's JavaFX user interface.
 */
public class Main extends Application {
    /**
     * Creates the JavaFX application entry point.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();

        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.setTitle("Yuno");
        stage.setResizable(true);
        stage.setMinHeight(480);
        stage.setMinWidth(380);
        stage.show();

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.initializeYuno(stage);
    }
}
