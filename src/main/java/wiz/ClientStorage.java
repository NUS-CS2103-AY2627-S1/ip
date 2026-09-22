package wiz;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles loading clients from and saving clients to a persistent storage file.
 */
public class ClientStorage {

    private final String filePath;

    /**
     * Constructs a ClientStorage instance with the specified file path.
     *
     * @param filePath The path of the file to store clients in.
     */
    public ClientStorage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "File path cannot be null or blank";
        this.filePath = filePath;
    }

    /**
     * Saves the provided list of clients to the storage file.
     *
     * @param clients The list of clients to save.
     * @throws IOException If writing to the file fails.
     */
    public void save(ArrayList<Client> clients) throws IOException {
        assert filePath != null && !filePath.isBlank() : "File path cannot be null or blank";
        assert clients != null : "Client list to save cannot be null";
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            boolean isCreated = parent.mkdirs();
            assert isCreated || parent.exists() : "Parent directories must exist";
        }

        List<String> lines = clients.stream()
                .map(Client::toFileString)
                .collect(Collectors.toList());

        Files.write(file.toPath(), lines);
    }

    /**
     * Loads clients from the storage file.
     *
     * @return An ArrayList of clients loaded from storage.
     * @throws IOException If reading or parsing the file fails.
     */
    public ArrayList<Client> load() throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<String> lines = Files.readAllLines(path);
        ArrayList<Client> clients = new ArrayList<>();
        for (String line : lines) {
            if (!line.isBlank()) {
                clients.add(parseClient(line));
            }
        }
        return clients;
    }

    /**
     * Parses a single line from the clients data file into a Client object.
     *
     * @param line The serialized client string.
     * @return The parsed Client instance.
     * @throws IOException If the client string format is invalid.
     */
    public Client parseClient(String line) throws IOException {
        assert line != null : "Line to parse cannot be null";
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new IOException("Invalid client data file format.");
        }

        String name = parts[0].trim();
        String phone = parts[1].trim();
        String email = parts[2].trim();
        String note = parts.length > 3 ? parts[3].trim() : "";

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            throw new IOException("Client name, phone, and email cannot be empty.");
        }

        return new Client(name, phone, email, note);
    }
}
