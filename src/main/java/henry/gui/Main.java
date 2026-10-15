package henry.gui;

import java.io.IOException;

import henry.Henry;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays Henry's JavaFX interface using the tutorial's FXML layout.
 */
public class Main extends Application {
    private final Henry henry = new Henry();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Henry, your chatbot companion");
        stage.setMinHeight(320);
        stage.setMinWidth(360);
        fxmlLoader.<MainWindow>getController().setHenry(henry);
        stage.show();
    }
}
