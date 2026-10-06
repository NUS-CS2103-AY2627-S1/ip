package caesar;

import java.io.IOException;

import caesar.gui.GuiUi;
import caesar.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the JavaFX user interface for Caesar. */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            GuiUi guiUi = new GuiUi();
            fxmlLoader.<MainWindow>getController().setCaesar(
                    new Caesar("data/tasks.txt", guiUi), guiUi);

            stage.setTitle("Caesar");
            stage.setResizable(true);
            stage.setScene(new Scene(mainWindow));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load MainWindow.fxml", exception);
        }
    }
}
