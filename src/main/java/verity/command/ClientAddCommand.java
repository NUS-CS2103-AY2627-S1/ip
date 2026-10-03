package verity.command;

import java.io.IOException;

import verity.client.Client;
import verity.client.PreferredContactMethod;
import verity.exception.VerityException;

/**
 * Represents a command that adds a client.
 */
public class ClientAddCommand extends Command {
    private final String fullName;
    private final String phone;
    private final String email;
    private final String address;
    private final String company;
    private final String notes;
    private final PreferredContactMethod preferredContactMethod;

    /**
     * Creates a client-add command.
     */
    public ClientAddCommand(String fullName, String phone, String email,
            String address, String company, String notes,
            PreferredContactMethod preferredContactMethod) {
        super(false);
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.company = company;
        this.notes = notes;
        this.preferredContactMethod = preferredContactMethod;
    }

    @Override
    public String execute(CommandContext context)
            throws IOException, VerityException {
        Client client = context.getClients().addNewClient(
                fullName, phone, email, address, company, notes,
                preferredContactMethod);
        context.getClientStorage().saveClients(context.getClients());
        return context.getUi().getClientAddedMessage(client);
    }
}
