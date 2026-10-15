package thomas.place;

import java.math.BigDecimal;
import java.time.LocalDate;

import thomas.ThomasException;

/**
 * Represents a saved place and its details.
 */
public class Place {
    private String name;
    private String type;
    private String address;
    private int rating;
    private BigDecimal price;
    private LocalDate visitedDate;
    private String note;

    /**
     * Creates a place with its required details.
     *
     * @param name Place name.
     * @param type Place type.
     * @param address Place address.
     * @param rating Whole-star rating.
     * @param price Estimated price.
     * @param visitedDate Date visited, or {@code null}.
     * @param note Note, or {@code null}.
     * @throws ThomasException If a value is invalid.
     */
    public Place(String name, String type, String address, int rating, String price,
                 LocalDate visitedDate, String note) throws ThomasException {
        this.name = validateText(name);
        this.type = validateText(type);
        this.address = validateText(address);
        setRating(rating);
        setPrice(price);
        this.visitedDate = visitedDate;
        this.note = validateOptionalText(note);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public int getRating() {
        return rating;
    }

    public String getPrice() {
        return price.toPlainString();
    }

    public LocalDate getVisitedDate() {
        return visitedDate;
    }

    public String getNote() {
        return note;
    }

    /** Updates one named place detail. */
    public void update(String field, String value) throws ThomasException {
        switch (field) {
            case "name":
                name = validateText(value);
                break;
            case "type":
                type = validateText(value);
                break;
            case "at":
                address = validateText(value);
                break;
            case "rating":
                setRating(parseRating(value));
                break;
            case "price":
                setPrice(value);
                break;
            case "visited":
                visitedDate = value.equalsIgnoreCase("none") ? null : parseDate(value);
                break;
            case "note":
                note = value.equalsIgnoreCase("none") ? null : validateText(value);
                break;
            default:
                throw new ThomasException("Unknown place field: /" + field + ".");
        }
    }

    private static String validateText(String value) throws ThomasException {
        if (value == null || value.trim().isEmpty()) {
            throw new ThomasException("Place details cannot be empty.");
        }
        String trimmed = value.trim();
        if (trimmed.contains("|") || trimmed.contains("\n") || trimmed.contains("\r")) {
            throw new ThomasException("Place details cannot contain \"|\" or a line break.");
        }
        return trimmed;
    }

    private static String validateOptionalText(String value) throws ThomasException {
        return value == null ? null : validateText(value);
    }

    private void setRating(int value) throws ThomasException {
        if (value < 1 || value > 5) {
            throw new ThomasException("Rating must be a whole number from 1 to 5.");
        }
        rating = value;
    }

    private void setPrice(String value) throws ThomasException {
        try {
            BigDecimal parsed = new BigDecimal(value.trim());
            if (parsed.signum() < 0 || parsed.scale() > 2) {
                throw new NumberFormatException();
            }
            price = parsed.setScale(2);
        } catch (NumberFormatException e) {
            throw new ThomasException("Price must be a non-negative monetary amount with at most two decimal places.");
        }
    }

    /** Returns a whole-number rating parsed from text. */
    public static int parseRating(String value) throws ThomasException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ThomasException("Rating must be a whole number from 1 to 5.");
        }
    }

    /** Returns an ISO local date parsed from text. */
    public static LocalDate parseDate(String value) throws ThomasException {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new ThomasException("Date visited must use YYYY-MM-DD and be a valid calendar date.");
        }
    }

    /** Returns this place in the separate place storage format. */
    public String toFileFormat() {
        return "P | " + name + " | " + type + " | " + address + " | " + rating + " | " + getPrice()
                + " | " + (visitedDate == null ? "" : visitedDate) + " | " + (note == null ? "" : note);
    }
}
