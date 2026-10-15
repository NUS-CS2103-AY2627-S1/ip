package edith.gui;

import java.io.IOException;

import edith.Edith;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts Edith's JavaFX user interface and loads its FXML view. */
public class Main extends Application {
    private static final double WINDOW_HEIGHT = 640.0;
    private static final double WINDOW_WIDTH = 440.0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

        MainWindow controller = fxmlLoader.getController();
        controller.setEdith(Edith.createForGui());

        stage.setTitle("EDITH");
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setScene(scene);
        stage.show();
    }
}
