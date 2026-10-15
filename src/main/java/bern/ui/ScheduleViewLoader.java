package bern.ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Loads schedule components whose Java object is both the FXML root and controller.
 */
final class ScheduleViewLoader {
    /**
     * Prevents instantiation of this utility class.
     */
    private ScheduleViewLoader() {
    }

    /**
     * Populates a component from FXML and reports the resource path when loading fails.
     *
     * @param component The existing root and controller to populate.
     * @param resourcePath The absolute classpath location of its FXML layout.
     * @throws IllegalStateException If the layout cannot be found or loaded.
     */
    static void load(Parent component, String resourcePath) {
        URL resource = ScheduleViewLoader.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Unable to find schedule layout: " + resourcePath);
        }
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setRoot(component);
            loader.setController(component);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load schedule layout: " + resourcePath, e);
        }
    }
}
