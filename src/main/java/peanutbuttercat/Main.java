package peanutbuttercat;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Creates and displays the JavaFX window for PeanutButterCat.
 */
public class Main extends Application {
    private final PeanutButterCat peanutButterCat = new PeanutButterCat();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);
            stage.setScene(scene);
            stage.setTitle("PeanutButterCat — Cozy Task Keeper");
            stage.setMinHeight(420);
            stage.setMinWidth(457);
            fxmlLoader.<MainWindow>getController().setPeanutButterCat(peanutButterCat);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            System.err.println("Unable to start PeanutButterCat: " + exception.getMessage());
            Label errorMessage = new Label("PeanutButterCat could not start because its interface files "
                    + "are missing or unreadable. Please reinstall the application.");
            errorMessage.setWrapText(true);
            StackPane errorPane = new StackPane(errorMessage);
            errorPane.setStyle("-fx-padding: 24;");
            stage.setScene(new Scene(errorPane, 457, 160));
            stage.setTitle("PeanutButterCat — Startup Error");
            stage.show();
        }
    }
}
