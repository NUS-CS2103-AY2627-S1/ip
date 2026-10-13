package ruby.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void toString_allOptionalDetailsBlank_returnsNameOnly() {
        assertEquals("Jane", new Contact("Jane", "", "", "").toString());
    }

    @Test
    void toString_nullOptionalDetails_omitsThem() {
        assertEquals("Jane", new Contact("Jane", null, null, null).toString());
    }

    @Test
    void toDataString_preservesEmptyOptionalDetails() {
        assertEquals("C | Jane |  |  | ", new Contact("Jane", "", "", "").toDataString());
    }
}
