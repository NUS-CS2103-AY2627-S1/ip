package duke;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Configures and displays the Hermes JavaFX application. */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        VBox root = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setHermes(new HermesMini());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/Hermes.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Hermes — Your Task Messenger");
        stage.setMinWidth(440);
        stage.setMinHeight(480);
        stage.show();
    }
}
