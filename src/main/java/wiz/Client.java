package wiz;

import java.util.Objects;

/**
 * Represents a client with contact information and optional notes.
 */
public class Client {

    /** The full name of the client. */
    private final String name;
    /** The phone contact number of the client. */
    private final String phone;
    /** The email address of the client. */
    private final String email;
    /** Additional notes or remarks associated with the client. */
    private final String note;

    /**
     * Constructs a Client with name, phone, email, and an empty note.
     *
     * @param name The name of the client.
     * @param phone The phone number of the client.
     * @param email The email address of the client.
     */
    public Client(String name, String phone, String email) {
        this(name, phone, email, "");
    }

    /**
     * Constructs a Client with name, phone, email, and note.
     *
     * @param name The name of the client.
     * @param phone The phone number of the client.
     * @param email The email address of the client.
     * @param note Additional notes or policy details for the client.
     */
    public Client(String name, String phone, String email, String note) {
        assert name != null && !name.isBlank() : "Client name cannot be null or blank";
        assert phone != null && !phone.isBlank() : "Client phone cannot be null or blank";
        assert email != null && !email.isBlank() : "Client email cannot be null or blank";
        assert note != null : "Client note cannot be null";

        this.name = name.trim();
        this.phone = phone.trim();
        this.email = email.trim();
        this.note = note.trim();
    }

    /**
     * Returns the name of the client.
     *
     * @return The client's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the phone number of the client.
     *
     * @return The client's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the email address of the client.
     *
     * @return The client's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the note associated with the client.
     *
     * @return The client's note.
     */
    public String getNote() {
        return note;
    }

    /**
     * Formats the client for persistent storage.
     *
     * @return The serialized string representation for saving.
     */
    public String toFileString() {
        return name + " | " + phone + " | " + email + " | " + note;
    }

    @Override
    public String toString() {
        if (note.isEmpty()) {
            return "[C] " + name + " (Phone: " + phone + ", Email: " + email + ")";
        }
        return "[C] " + name + " (Phone: " + phone + ", Email: " + email + ", Note: " + note + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Client)) {
            return false;
        }
        Client other = (Client) obj;
        return Objects.equals(name, other.name)
                && Objects.equals(phone, other.phone)
                && Objects.equals(email, other.email)
                && Objects.equals(note, other.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, note);
    }
}
