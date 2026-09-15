package nova;

/**
 * The two halves of a command argument split on a marker, each trimmed.
 * A "marker" is a separator in Nova's command syntax, such as "/by" in
 * "deadline return book /by 2019-10-15".
 *
 * @param before text before the marker.
 * @param after  text after the marker.
 */
record MarkerParts(String before, String after) {
}
