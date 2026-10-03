package verity.ui;

import java.util.List;

import verity.client.Client;
import verity.task.Task;
import verity.task.TaskList;

/**
 * Formats user-facing messages about clients.
 */
final class ClientMessageFormatter {

    String getClientAddedMessage(Client client) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("    Client added:\n");
        appendClientDetails(response, client, "    ");
        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    String getClientUpdatedMessage(Client client) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("    Client updated:\n");
        appendClientDetails(response, client, "    ");
        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    String getClientListMessage(List<Client> clients) {
        if (clients.isEmpty()) {
            return Ui.HORIZONTAL_LINE
                    + "    There are no clients.\n"
                    + Ui.HORIZONTAL_LINE;
        }
        return getClientCollectionMessage("    Clients:\n\n", clients);
    }

    String getClientDetailsMessage(Client client, TaskList tasks) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("    Client details:\n");
        appendClientDetails(response, client, "    ");
        response.append("\n    Assigned tasks:\n");
        boolean hasAssignedTask = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!task.hasClientId(client.getId())) {
                continue;
            }
            hasAssignedTask = true;
            response.append("      ")
                    .append(i + 1)
                    .append(".")
                    .append(task.getStatus())
                    .append("\n");
        }
        if (!hasAssignedTask) {
            response.append("      None\n");
        }
        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    String getMatchingClientsMessage(List<Client> clients) {
        if (clients.isEmpty()) {
            return Ui.HORIZONTAL_LINE
                    + "    There are no matching clients.\n"
                    + Ui.HORIZONTAL_LINE;
        }
        return getClientCollectionMessage("    Matching clients:\n\n", clients);
    }

    String getClientAssociatedMessage(Client client, TaskList tasks,
            List<Integer> taskIndexes) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("    Client ")
                .append(client.getId())
                .append(" was associated with:\n");
        for (int taskIndex : taskIndexes) {
            response.append("      ")
                    .append(taskIndex + 1)
                    .append(".")
                    .append(tasks.get(taskIndex).getStatus())
                    .append("\n");
        }
        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    String getClientDissociatedMessage(Client client, List<Integer> taskIndexes) {
        String taskNumbers = taskIndexes.stream()
                .map(taskIndex -> Integer.toString(taskIndex + 1))
                .collect(java.util.stream.Collectors.joining(", "));
        String noun = taskIndexes.size() == 1 ? "task " : "tasks ";
        return Ui.HORIZONTAL_LINE
                + "    Client " + client.getId() + " was dissociated from "
                + noun + taskNumbers + ".\n"
                + Ui.HORIZONTAL_LINE;
    }

    String getClientDeleteWarningMessage(Client client, int assignedTaskCount) {
        String taskNoun = assignedTaskCount == 1 ? "task" : "tasks";
        return Ui.HORIZONTAL_LINE
                + "    Warning: Deleting client " + client.getId()
                + " (" + client.getFullName() + ") is permanent.\n"
                + "    The client is currently assigned to " + assignedTaskCount
                + " " + taskNoun + ".\n"
                + "    Those tasks will be kept and annotated.\n\n"
                + "    Type `client delete " + client.getId()
                + " confirm` to continue.\n"
                + Ui.HORIZONTAL_LINE;
    }

    String getClientDeletedMessage(Client client, int affectedTaskCount) {
        String taskNoun = affectedTaskCount == 1 ? "task" : "tasks";
        return Ui.HORIZONTAL_LINE
                + "    Client deleted:\n"
                + "      " + client.getId() + " " + client.getFullName() + "\n\n"
                + "    Removed the client from " + affectedTaskCount
                + " " + taskNoun + ".\n"
                + "    The affected tasks were kept and annotated.\n"
                + Ui.HORIZONTAL_LINE;
    }

    private String getClientCollectionMessage(String heading, List<Client> clients) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE).append(heading);
        for (Client client : clients) {
            response.append("    ").append(client.getId()).append("\n");
            appendClientDetails(response, client, "      ", false);
            response.append("\n");
        }
        response.append("    Total: ")
                .append(clients.size())
                .append(clients.size() == 1 ? " client.\n" : " clients.\n");
        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    private void appendClientDetails(StringBuilder response, Client client, String indent) {
        appendClientDetails(response, client, indent, true);
    }

    private void appendClientDetails(StringBuilder response, Client client,
            String indent, boolean hasId) {
        if (hasId) {
            response.append(indent).append("ID: ").append(client.getId()).append("\n");
        }
        response.append(indent).append("Name: ").append(client.getFullName()).append("\n")
                .append(indent).append("Phone: ").append(displayValue(client.getPhone())).append("\n")
                .append(indent).append("Email: ").append(displayValue(client.getEmail())).append("\n")
                .append(indent).append("Address: ").append(displayValue(client.getAddress())).append("\n")
                .append(indent).append("Company: ").append(displayValue(client.getCompany())).append("\n")
                .append(indent).append("Notes: ")
                .append(displayMultilineValue(client.getNotes(), indent + "       "))
                .append("\n")
                .append(indent).append("Preferred contact: ")
                .append(client.getPreferredContactMethod() == null
                        ? "-"
                        : client.getPreferredContactMethod().getValue())
                .append("\n");
    }

    private String displayValue(String value) {
        return value.isEmpty() ? "-" : value;
    }

    private String displayMultilineValue(String value, String continuationIndent) {
        return displayValue(value).replace("\n", "\n" + continuationIndent);
    }
}
