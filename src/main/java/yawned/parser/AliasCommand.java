package yawned.parser;

/**
 * Represents the arguments of an alias creation or removal command.
 *
 * @param aliasName Name affected by a definition or removal request, or empty when listing.
 * @param targetCommand Canonical target command for a definition request, or empty otherwise.
 * @param action Requested alias action.
 */
public record AliasCommand(String aliasName, String targetCommand, AliasAction action) {
}
