package verity.command;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import verity.client.Client;
import verity.client.PreferredContactMethod;
import verity.exception.VerityException;

/**
 * Represents a command that edits selected client fields.
 */
public class ClientEditCommand extends Command {
    private final String clientId;
    private final Map<String, String> changes;
    private final Set<String> clearedFields;

    /**
     * Creates a client-edit command.
     *
     * @param clientId Client ID to edit.
     * @param changes Replacement field values.
     * @param clearedFields Optional fields to clear.
     */
    public ClientEditCommand(String clientId, Map<String, String> changes,
            Set<String> clearedFields) {
        super(false);
        this.clientId = clientId;
        this.changes = Map.copyOf(changes);
        this.clearedFields = Set.copyOf(clearedFields);
    }

    @Override
    public String execute(CommandContext context)
            throws IOException, VerityException {
        Client existing = context.getClients().getById(clientId);
        Client replacement = new Client(
                existing.getNumericId(),
                updatedValue("name", existing.getFullName()),
                updatedValue("phone", existing.getPhone()),
                updatedValue("email", existing.getEmail()),
                updatedValue("address", existing.getAddress()),
                updatedValue("company", existing.getCompany()),
                updatedValue("notes", existing.getNotes()),
                updatedPreferredContact(existing));

        context.getClients().replace(replacement);
        context.getClientStorage().saveClients(context.getClients());
        return context.getUi().getClientUpdatedMessage(replacement);
    }

    private String updatedValue(String fieldName, String existingValue) {
        if (clearedFields.contains(fieldName)) {
            return "";
        }
        return changes.getOrDefault(fieldName, existingValue);
    }

    private PreferredContactMethod updatedPreferredContact(Client existing)
            throws VerityException {
        if (clearedFields.contains("preferred")) {
            return null;
        }
        if (changes.containsKey("preferred")) {
            return PreferredContactMethod.parse(changes.get("preferred"));
        }
        return existing.getPreferredContactMethod();
    }
}
