package walnut;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays a separate window containing Walnut's command reference.
 *
 * <p>The help window can be resized within a minimum width and height so that
 * the command descriptions remain readable while allowing users to view
 * longer content.</p>
 */
public class HelpWindow {

    /**
     * Loads and displays the Walnut help window.
     *
     * <p>The help window is created with a default size of 600 by 500 pixels
     * and can be resized by the user. Its minimum size prevents the content
     * from becoming too cramped.</p>
     *
     * <p>If the help-window FXML file cannot be loaded, the exception is
     * printed for debugging purposes.</p>
     */
    public static void show() {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(
                            HelpWindow.class.getResource(
                                    "/view/HelpWindow.fxml"
                            )
                    );

            AnchorPane helpPane = fxmlLoader.load();

            Stage helpStage = new Stage();
            helpStage.setTitle("Walnut Help");
            helpStage.setMinWidth(400);
            helpStage.setMinHeight(300);
            helpStage.setWidth(600);
            helpStage.setHeight(500);
            helpStage.setScene(new Scene(helpPane));
            helpStage.show();

        } catch (IOException e) {
            Logger.getLogger(HelpWindow.class.getName())
                    .log(Level.SEVERE, "Failed to load HelpWindow FXML", e);
        }
    }
}
