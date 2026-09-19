package nyonbot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles all resource loading for the NyonBot program.
 */
public class ResourceLoader {
    /**
     * Reads a .txt file and returns its entire contents as one string.
     *
     * @param filename String path to file; omit the preceding /.
     * @return contents of filename.
     * @throws IllegalStateException if read is interrupted or file cannot be found.
     **/
    public static String readTextFile(String filename) {
        if (filename.length() > 0 && filename.charAt(0) == '/') {
            filename = filename.substring(1);
        }
        try (InputStream input = NyonBot.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new IllegalStateException("Could not load text from " + filename);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Exception occurred while reading from " + filename + ": " + e);
        }
    }
}
