package verity.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import verity.client.ClientList;

/**
 * Loads clients from and saves clients to a separate data file.
 */
public class ClientStorage {
    private final Path dataFilePath;

    /**
     * Creates client storage for a specified file.
     *
     * @param dataFilePath Client data-file path.
     */
    public ClientStorage(Path dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    /**
     * Returns stored client lines.
     *
     * @return Client lines, or an empty list when the file is missing.
     * @throws IOException If the existing file cannot be read.
     */
    public List<String> loadClientLines() throws IOException {
        if (Files.notExists(dataFilePath)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
    }

    /**
     * Saves all clients and the next generated ID.
     *
     * @param clientList Clients to save.
     * @throws IOException If the data cannot be written.
     */
    public void saveClients(ClientList clientList) throws IOException {
        String records = clientList.getClients().stream()
                .map(client -> client.serialize() + System.lineSeparator())
                .collect(Collectors.joining());
        String contents = "NEXT_ID\t" + clientList.getNextId()
                + System.lineSeparator() + records;
        writeToFile(contents);
    }

    public Path getDataFilePath() {
        return dataFilePath;
    }

    private void writeToFile(String contents) throws IOException {
        AtomicFileWriter.write(dataFilePath, contents);
    }
}
