package jonk;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the Jonk chatbot GUI using FXML.
 */
public class Main extends Application {
    private final Jonk jonk = new Jonk();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.setTitle("Jonk • Mission Control");
        stage.setResizable(false);
        fxmlLoader.<MainWindow>getController().setJonk(jonk);
        stage.show();
    }
}
