package megia.ui;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.scene.image.Image;

/**
 * Validates and persists the user's selected profile image.
 *
 * <p>The image itself remains at the user's chosen location. Only its normalized path is stored,
 * so the task data file and the chatbot transcript remain independent of profile preferences.</p>
 */
public final class ProfileImageService {
    private static final String USER_IMAGE_PATH_KEY = "userImagePath";

    private final Preferences preferences;

    /**
     * Creates a profile image service backed by the application's user preferences.
     */
    public ProfileImageService() {
        this(Preferences.userNodeForPackage(ProfileImageService.class));
    }

    ProfileImageService(Preferences preferences) {
        this.preferences = preferences;
    }

    /**
     * Returns the persisted user image when its path is still readable and supported.
     *
     * @return Persisted image, or an empty optional when no usable image is configured.
     */
    public Optional<Image> loadUserImage() {
        String storedPath = preferences.get(USER_IMAGE_PATH_KEY, "");
        if (storedPath.isBlank()) {
            return Optional.empty();
        }

        try {
            Optional<Image> image = loadImage(Path.of(storedPath));
            if (image.isEmpty()) {
                preferences.remove(USER_IMAGE_PATH_KEY);
            }
            return image;
        } catch (InvalidPathException exception) {
            preferences.remove(USER_IMAGE_PATH_KEY);
            return Optional.empty();
        }
    }

    /**
     * Saves a readable image path after validating that JavaFX can decode it.
     *
     * @param imagePath Path to the image selected by the user.
     * @return True when the image was valid and its path was persisted.
     */
    public boolean saveUserImage(Path imagePath) {
        if (loadImage(imagePath).isEmpty()) {
            return false;
        }

        try {
            preferences.put(USER_IMAGE_PATH_KEY, imagePath.toAbsolutePath().normalize().toString());
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    Optional<Image> loadImage(Path imagePath) {
        if (imagePath == null) {
            return Optional.empty();
        }

        try {
            if (!Files.isRegularFile(imagePath) || !Files.isReadable(imagePath)) {
                return Optional.empty();
            }
            Image image = new Image(imagePath.toUri().toString(), false);
            if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
                return Optional.empty();
            }
            return Optional.of(image);
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }
}
