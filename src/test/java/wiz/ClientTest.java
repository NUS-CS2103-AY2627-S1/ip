package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class ClientTest {

    @Test
    public void clientToString_withoutNote_correctFormat() {
        Client client = new Client("Alice Tan", "91234567", "alice@example.com");
        assertEquals("[C] Alice Tan (Phone: 91234567, Email: alice@example.com)", client.toString());
    }

    @Test
    public void clientToString_withNote_correctFormat() {
        Client client = new Client("Bob Lee", "98765432", "bob@example.com", "Life policy #123");
        assertEquals("[C] Bob Lee (Phone: 98765432, Email: bob@example.com, Note: Life policy #123)",
                client.toString());
    }

    @Test
    public void toFileString_withAndWithoutNote_correctFormat() {
        Client clientWithoutNote = new Client("Alice Tan", "91234567", "alice@example.com");
        assertEquals("Alice Tan | 91234567 | alice@example.com | ", clientWithoutNote.toFileString());

        Client clientWithNote = new Client("Bob Lee", "98765432", "bob@example.com", "Life policy #123");
        assertEquals("Bob Lee | 98765432 | bob@example.com | Life policy #123", clientWithNote.toFileString());
    }

    @Test
    public void equalsAndHashCode_sameValues_equal() {
        Client client1 = new Client("Alice Tan", "91234567", "alice@example.com", "VIP");
        Client client2 = new Client("Alice Tan", "91234567", "alice@example.com", "VIP");
        Client client3 = new Client("Bob Lee", "98765432", "bob@example.com");

        assertEquals(client1, client2);
        assertEquals(client1.hashCode(), client2.hashCode());
        assertNotEquals(client1, client3);
    }
}
