package duck;

import java.io.IOException;
import java.util.Objects;

import duck.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Duck's JavaFX user interface.
 */
public class Main extends Application {
    /** Chatbot that handles commands entered in the graphical interface. */
    private final Duck duck = new Duck("data/duck.txt");

    /** Creates the JavaFX application. */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "Main window FXML resource is missing."));
        AnchorPane mainLayout = loader.load();
        MainWindow controller = loader.getController();
        controller.setDuck(this.duck);

        Scene scene = new Scene(mainLayout);

        stage.setTitle("Duck");
        stage.setResizable(true);
        stage.setMinHeight(400.0);
        stage.setMinWidth(417.0);
        stage.setScene(scene);
        stage.show();
    }
}
