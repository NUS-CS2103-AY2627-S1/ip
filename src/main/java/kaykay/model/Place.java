package kaykay.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kaykay.storage.Storage;

/**
 * Represents a place saved for future reference.
 */
public final class Place {
    /** Format used to display and store visit dates. */
    public static final DateTimeFormatter VISIT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MM uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Name used to identify the place. */
    private final String name;

    /** Optional category such as restaurant or attraction. */
    private final String type;

    /** Optional address or general location. */
    private final String location;

    /** Optional date on which the user visited the place. */
    private final LocalDate visitedOn;

    /** Optional rating from one to five. */
    private final Integer rating;

    /** Optional free-form information about the place. */
    private final String notes;

    /**
     * Creates a place with its recorded details.
     *
     * @param name name used to identify the place.
     * @param type optional place category, or an empty string.
     * @param location optional address or general location, or an empty string.
     * @param visitedOn optional visit date, or {@code null}.
     * @param rating optional rating from one to five, or {@code null}.
     * @param notes optional free-form information, or an empty string.
     */
    public Place(String name, String type, String location, LocalDate visitedOn,
            Integer rating, String notes) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.visitedOn = visitedOn;
        this.rating = rating;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getVisitedOn() {
        return visitedOn;
    }

    public Integer getRating() {
        return rating;
    }

    public String getNotes() {
        return notes;
    }

    /**
     * Checks whether another place contains exactly the same recorded details.
     *
     * @param otherPlace place to compare.
     * @return true if every recorded detail is equal.
     */
    public boolean hasSameDetails(Place otherPlace) {
        return name.equals(otherPlace.name)
                && type.equals(otherPlace.type)
                && location.equals(otherPlace.location)
                && Objects.equals(visitedOn, otherPlace.visitedOn)
                && Objects.equals(rating, otherPlace.rating)
                && notes.equals(otherPlace.notes);
    }

    /**
     * Checks whether any recorded detail contains a keyword, ignoring letter case.
     *
     * @param keyword text to search for.
     * @return true if any displayed detail contains the keyword.
     */
    public boolean hasMatchingDetail(String keyword) {
        String visitedOnText = visitedOn == null ? "" : visitedOn.format(VISIT_DATE_FORMATTER);
        String ratingText = rating == null ? "" : rating.toString();
        String searchableDetails = String.join("\n", name, type, location, visitedOnText, ratingText, notes);
        return searchableDetails.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the place in the format used by the data file.
     *
     * @return serialized place details.
     */
    public String toFileFormat() {
        String visitedOnText = visitedOn == null ? "" : visitedOn.format(VISIT_DATE_FORMATTER);
        String ratingText = rating == null ? "" : rating.toString();
        return String.format("P | %s | %s | %s | %s | %s | %s",
                Storage.escape(name), Storage.escape(type), Storage.escape(location),
                visitedOnText, ratingText, Storage.escape(notes));
    }

    /**
     * Returns all recorded details in a compact user-facing form.
     *
     * @return formatted place details.
     */
    @Override
    public String toString() {
        List<String> details = new ArrayList<>();
        addDetail(details, "type", type);
        addDetail(details, "location", location);
        if (visitedOn != null) {
            addDetail(details, "visited", visitedOn.format(VISIT_DATE_FORMATTER));
        }
        if (rating != null) {
            addDetail(details, "rating", rating + "/5");
        }
        addDetail(details, "notes", notes);
        String detailsText = details.isEmpty() ? "" : " (" + String.join("; ", details) + ")";
        return "[P] " + name + detailsText;
    }

    /** Adds a labeled detail when its value is present. */
    private static void addDetail(List<String> details, String label, String value) {
        if (!value.isBlank()) {
            details.add(label + ": " + value);
        }
    }
}
