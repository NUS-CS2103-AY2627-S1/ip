package megia.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Writes one complete serialized task collection to a storage path.
 *
 * <p>The package-private seam allows tests to inject deterministic write failures while the
 * default writer retains atomic replacement behavior.</p>
 */
@FunctionalInterface
interface StorageWriter {
    /**
     * Writes serialized task data.
     *
     * @param storagePath Absolute path of the storage file.
     * @param output Serialized task data.
     * @throws IOException If the write cannot complete.
     */
    void write(Path storagePath, String output) throws IOException;
}
