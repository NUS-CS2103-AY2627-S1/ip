package plana.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import plana.client.Client;
import plana.client.ClientList;

/**
 * Saves and loads client records using a file separate from Plana's task file.
 */
public class ClientStorage {
    private static final String CLIENT_TYPE = "C";
    private static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    private final Path dataFile;

    /**
     * Creates client storage using Plana's default client data file.
     */
    public ClientStorage() {
        this(Path.of("data", "clients.txt"));
    }

    /**
     * Creates client storage using the supplied data-file path.
     *
     * @param filePath the file used to load and save clients.
     */
    public ClientStorage(String filePath) {
        this(Path.of(filePath));
    }

    private ClientStorage(Path filePath) {
        this.dataFile = filePath;
    }

    /**
     * Writes the current client list to disk.
     *
     * @param clients the clients that should be saved.
     * @return true when every client was written successfully.
     */
    public boolean saveClients(ClientList clients) {
        Path temporaryFile = null;
        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            String fileContents = serializeClients(clients);
            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, "plana-clients-", ".tmp");
            Files.writeString(temporaryFile, fileContents, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            reportStorageError("save", exception);
            return false;
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    // Cleanup is best-effort after the save has completed or failed.
                }
            }
        }
        return true;
    }

    /**
     * Loads saved clients, returning an empty list when no file exists.
     *
     * @return the clients loaded from disk.
     */
    public ClientList loadClients() {
        ArrayList<Client> clients = new ArrayList<>();
        Set<String> emails = new HashSet<>();
        try {
            if (Files.notExists(dataFile)) {
                return new ClientList();
            }
            if (!Files.isRegularFile(dataFile)) {
                reportStorageError("load", new IOException("save path is not a regular file"));
                return new ClientList();
            }
            try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Client client = parseClient(line);
                    if (client != null && emails.add(client.getEmail())) {
                        clients.add(client);
                    }
                }
            }
        } catch (IOException | SecurityException exception) {
            reportStorageError("load", exception);
        }
        return new ClientList(clients);
    }

    private String serializeClients(ClientList clients) {
        if (clients == null || clients.size() == 0) {
            return "";
        }
        StringBuilder fileContents = new StringBuilder();
        for (Client client : clients) {
            if (client != null) {
                fileContents.append(client.toStorageString()).append(System.lineSeparator());
            }
        }
        return fileContents.toString();
    }

    private Client parseClient(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        List<String> parts = splitRecord(line);
        if (parts == null || parts.size() != 7 || !CLIENT_TYPE.equals(parts.get(0).trim())) {
            return null;
        }
        String email = parts.get(1).trim().toLowerCase(Locale.ROOT);
        String name = parts.get(2).trim();
        String phone = parts.get(3).trim();
        String address = parts.get(4).trim();
        String preferences = parts.get(5).trim();
        String notes = parts.get(6).trim();
        if (!isValidClient(email, name, phone, address, preferences, notes)) {
            return null;
        }
        return new Client(name, email, phone, address, preferences, notes);
    }

    private boolean isValidClient(String email, String name, String phone, String address,
                                  String preferences, String notes) {
        return !name.isBlank() && isValidText(name, 100)
                && email.length() <= 254 && email.matches(EMAIL_PATTERN)
                && isValidText(phone, 30) && isValidText(address, 200)
                && isValidText(preferences, 300) && isValidText(notes, 500)
                && isValidPhone(phone);
    }

    private boolean isValidText(String value, int maximumLength) {
        return value.length() <= maximumLength
                && value.chars().noneMatch(character -> Character.isISOControl((char) character));
    }

    private boolean isValidPhone(String phone) {
        if (phone.isBlank()) {
            return true;
        }
        if (!phone.matches("[0-9+(). -]+")) {
            return false;
        }
        long digitCount = phone.chars().filter(Character::isDigit).count();
        return digitCount >= 7 && digitCount <= 15;
    }

    private List<String> splitRecord(String line) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean isEscaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                currentPart.append('\\').append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                String field = unescapeField(currentPart.toString().trim());
                if (field == null) {
                    return null;
                }
                parts.add(field);
                currentPart.setLength(0);
            } else {
                currentPart.append(character);
            }
        }
        if (isEscaped) {
            return null;
        }
        String field = unescapeField(currentPart.toString().trim());
        if (field == null) {
            return null;
        }
        parts.add(field);
        return parts;
    }

    private String unescapeField(String value) {
        StringBuilder unescaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character != '\\') {
                unescaped.append(character);
                continue;
            }
            if (i + 1 >= value.length()) {
                return null;
            }
            char escapedCharacter = value.charAt(++i);
            switch (escapedCharacter) {
                case '\\', '|' -> unescaped.append(escapedCharacter);
                case 'n' -> unescaped.append('\n');
                case 'r' -> unescaped.append('\r');
                default -> {
                    return null;
                }
            }
        }
        return unescaped.toString();
    }

    private void reportStorageError(String operation, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        System.err.println("Unable to " + operation + " clients: " + message);
    }
}
