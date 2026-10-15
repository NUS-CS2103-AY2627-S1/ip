package thomas.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

class PlaceTest {
    @Test
    void update_optionalFieldsWithNone_clearsFields() throws Exception {
        Place place = new Place("Sushi", "Restaurant", "Town", 4, "12", Place.parseDate("2026-09-08"), "Nice");

        place.update("visited", "none");
        place.update("note", "none");

        assertNull(place.getVisitedDate());
        assertNull(place.getNote());
    }

    @Test
    void constructor_priceAndRatingValidation_rejectsInvalidValues() {
        assertThrows(ThomasException.class, () ->
                new Place("Sushi", "Restaurant", "Town", 6, "12", null, null));
        assertThrows(ThomasException.class, () ->
                new Place("Sushi", "Restaurant", "Town", 4, "12.345", null, null));
    }

    @Test
    void constructor_validPrice_normalizesToTwoDecimalPlaces() throws Exception {
        Place place = new Place("Sushi", "Restaurant", "Town", 4, "12", null, null);

        assertEquals("12.00", place.getPrice());
    }

    @Test
    void constructor_validDetails_exposesAndFormatsAllFields() throws Exception {
        Place place = new Place(" Sushi ", " Restaurant ", " Town ", 4, "12.5",
                Place.parseDate("2026-09-08"), " Nice ");

        assertEquals("Sushi", place.getName());
        assertEquals("Restaurant", place.getType());
        assertEquals("Town", place.getAddress());
        assertEquals(4, place.getRating());
        assertEquals("12.50", place.getPrice());
        assertEquals("2026-09-08", place.getVisitedDate().toString());
        assertEquals("Nice", place.getNote());
        assertEquals("P | Sushi | Restaurant | Town | 4 | 12.50 | 2026-09-08 | Nice",
                place.toFileFormat());
    }

    @Test
    void update_eachSupportedField_updatesValue() throws Exception {
        Place place = new Place("Sushi", "Restaurant", "Town", 4, "12", null, null);

        place.update("name", "Cafe");
        place.update("type", "Bakery");
        place.update("at", "City");
        place.update("rating", "5");
        place.update("price", "7.2");
        place.update("visited", "2026-09-09");
        place.update("note", "Quiet");

        assertEquals("Cafe", place.getName());
        assertEquals("Bakery", place.getType());
        assertEquals("City", place.getAddress());
        assertEquals(5, place.getRating());
        assertEquals("7.20", place.getPrice());
        assertEquals("2026-09-09", place.getVisitedDate().toString());
        assertEquals("Quiet", place.getNote());
    }

    @Test
    void constructor_invalidTextAndPrice_rejectsValues() {
        assertThrows(ThomasException.class, () ->
                new Place(" ", "Restaurant", "Town", 4, "12", null, null));
        assertThrows(ThomasException.class, () ->
                new Place("Su|shi", "Restaurant", "Town", 4, "12", null, null));
        assertThrows(ThomasException.class, () ->
                new Place("Sushi", "Restaurant", "Town", 4, "-1", null, null));
        assertThrows(ThomasException.class, () ->
                new Place("Sushi", "Restaurant", "Town", 4, "money", null, null));
    }

    @Test
    void parsers_invalidOrBoundaryValues_validateInput() throws Exception {
        assertEquals(1, Place.parseRating(" 1 "));
        assertEquals("2026-02-28", Place.parseDate(" 2026-02-28 ").toString());
        assertThrows(ThomasException.class, () -> Place.parseRating("four"));
        assertThrows(ThomasException.class, () -> Place.parseDate("2026-02-30"));
    }

    @Test
    void update_unknownField_throwsException() throws Exception {
        Place place = new Place("Sushi", "Restaurant", "Town", 4, "12", null, null);

        assertThrows(ThomasException.class, () -> place.update("unknown", "value"));
    }
}
