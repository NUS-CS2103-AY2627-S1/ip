/**
 * Defines Record as a named module and grants JavaFX only the reflective access needed
 * to construct its FXML controllers.
 */
module my.record.app {
    requires javafx.controls;
    requires javafx.fxml;

    exports recordbase;
    exports recordbase.exceptions;
    exports recordbase.types;
    exports recordbase.utils;
    // JavaFX's launcher constructs the Application subclass reflectively.
    exports recordbase.ui to javafx.graphics;

    // Controllers are internal implementation details, but FXMLLoader needs reflective access.
    opens recordbase.ui to javafx.fxml;
}
