package monkey;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import monkey.ui.MainWindow;

/** Provides the JavaFX entry point for Monkey's graphical user interface. */
public class Main extends Application {
    private final Monkey monkey = new Monkey();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        Scene scene = new Scene(mainWindow);

        stage.setScene(scene);
        stage.setTitle("Monkey");
        stage.setMinHeight(400);
        stage.setMinWidth(360);

        MainWindow controller = fxmlLoader.getController();
        controller.setMonkey(monkey);
        stage.show();
    }
}
