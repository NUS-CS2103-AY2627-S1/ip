package victoria.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import victoria.Victoria;

/** Displays Victoria's task commands in a JavaFX chat window. */
public class Main extends Application {
    /** Loads the main window and supplies it with the shared Victoria command service. */
    @Override
    public void start(Stage stage) throws IOException {
        Victoria victoria = new Victoria();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        MainWindow controller = loader.getController();
        controller.setVictoria(victoria);

        Scene scene = new Scene(root);
        stage.setTitle("Victoria");
        stage.setScene(scene);
        stage.setMinHeight(300);
        stage.setMinWidth(417);
        stage.show();
    }
}
