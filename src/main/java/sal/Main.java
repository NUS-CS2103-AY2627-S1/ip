package sal;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A GUI for Sal using FXML.
 */
public class Main extends Application {
    private final Sal sal = new Sal();

    @Override
    public void start(Stage stage) {
        try {
            Font.loadFont(Main.class.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 13);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/css/chat.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Sal");
            fxmlLoader.<MainWindow>getController().setSal(sal);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
