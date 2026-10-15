package anders;

import anders.ui.Ui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/** Provides the JavaFX window for Anders. */
public class Main extends Application {
    /** Creates and displays a compact, resizable window. */
    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow();
        stage.setTitle(Ui.NAME + " - " + Ui.TAGLINE);
        stage.setMinWidth(360);
        stage.setMinHeight(400);
        stage.setScene(new Scene(mainWindow, 460, 640));
        mainWindow.applyCss();
        mainWindow.layout();
        SnapshotParameters iconSettings = new SnapshotParameters();
        iconSettings.setFill(Color.TRANSPARENT);
        stage.getIcons().add(mainWindow.lookup("#brandIcon").snapshot(iconSettings, null));
        stage.show();
    }
}
