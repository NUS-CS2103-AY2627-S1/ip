package verity.parser;

import java.util.ArrayList;
import java.util.List;

import verity.client.Client;
import verity.client.ClientList;
import verity.client.PreferredContactMethod;
import verity.exception.VerityException;
import verity.storage.StorageTextCodec;

/**
 * Reconstructs clients from their saved data representation.
 */
class SavedClientParser {
    private static final int CLIENT_FIELD_COUNT = 9;

    /**
     * Reconstructs a client list from stored lines.
     *
     * @param savedLines Stored client lines.
     * @return Reconstructed client list.
     * @throws VerityException If the data is corrupted.
     */
    ClientList parseSavedClients(List<String> savedLines) throws VerityException {
        if (savedLines.isEmpty()) {
            return new ClientList();
        }

        int nextId = parseNextId(savedLines.get(0));
        List<Client> clients = new ArrayList<>();
        for (int i = 1; i < savedLines.size(); i++) {
            try {
                clients.add(parseClient(savedLines.get(i)));
            } catch (VerityException exception) {
                throw new VerityException(
                        "Line " + (i + 1) + ": " + exception.getMessage());
            }
        }
        return new ClientList(nextId, clients);
    }

    private int parseNextId(String header) throws VerityException {
        String[] fields = header.split("\t", -1);
        if (fields.length != 2 || !fields[0].equals("NEXT_ID")) {
            throw new VerityException("Line 1: expected a NEXT_ID header.");
        }
        try {
            int nextId = Integer.parseInt(fields[1]);
            if (nextId <= 0) {
                throw new NumberFormatException();
            }
            return nextId;
        } catch (NumberFormatException exception) {
            throw new VerityException("Line 1: NEXT_ID must be positive.");
        }
    }

    private Client parseClient(String line) throws VerityException {
        String[] fields = line.split("\t", -1);
        if (fields.length != CLIENT_FIELD_COUNT || !fields[0].equals("C")) {
            throw new VerityException("a client must have exactly nine fields.");
        }

        int numericId = Client.parseId(fields[1]);
        return new Client(
                numericId,
                StorageTextCodec.decode(fields[2]),
                StorageTextCodec.decode(fields[3]),
                StorageTextCodec.decode(fields[4]),
                StorageTextCodec.decode(fields[5]),
                StorageTextCodec.decode(fields[6]),
                StorageTextCodec.decode(fields[7]),
                PreferredContactMethod.parse(fields[8]));
    }
}
