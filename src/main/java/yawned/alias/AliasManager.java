package yawned.alias;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import yawned.parser.CommandType;
import yawned.storage.AliasStorage;

/** Resolves and persists user-defined aliases for canonical task commands. */
public class AliasManager {
    private final AliasStorage storage;
    private final Map<String, CommandType> aliases;

    /**
     * Creates an alias manager using aliases loaded from storage.
     *
     * @param storage Storage used for alias mappings.
     */
    public AliasManager(AliasStorage storage) {
        this.storage = storage;
        this.aliases = new LinkedHashMap<>(storage.loadAliases());
    }

    /**
     * Resolves the command type represented by a complete user input.
     *
     * @param input Complete user input.
     * @return Resolved canonical command type, or {@link CommandType#UNKNOWN}.
     */
    public CommandType resolveCommandType(String input) {
        CommandType builtInCommandType = CommandType.fromInput(input);
        if (builtInCommandType != CommandType.UNKNOWN) {
            return builtInCommandType;
        }
        int firstSpaceIndex = input.indexOf(' ');
        boolean hasArguments = firstSpaceIndex >= 0;
        String typedWord = hasArguments ? input.substring(0, firstSpaceIndex) : input;
        CommandType customCommandType = aliases.get(normalize(typedWord));
        if (customCommandType == null || (hasArguments && !customCommandType.acceptsArguments())) {
            return CommandType.UNKNOWN;
        }
        return customCommandType;
    }

    /**
     * Creates or replaces an alias mapping.
     *
     * @param aliasName Alias name to save.
     * @param targetWord Canonical command word to target.
     * @return Result of the creation request.
     */
    public AliasResult defineAlias(String aliasName, String targetWord) {
        if (!isValidAliasName(aliasName)) {
            return AliasResult.INVALID_NAME;
        }
        String normalizedAliasName = normalize(aliasName);
        if (CommandType.isReservedAliasName(normalizedAliasName)) {
            return AliasResult.RESERVED_NAME;
        }
        CommandType targetCommandType = CommandType.fromAliasTarget(targetWord);
        if (targetCommandType == CommandType.UNKNOWN) {
            return AliasResult.INVALID_TARGET;
        }
        Map<String, CommandType> updatedAliases = new LinkedHashMap<>(aliases);
        updatedAliases.put(normalizedAliasName, targetCommandType);
        if (!storage.saveAliases(updatedAliases)) {
            return AliasResult.SAVE_FAILED;
        }
        aliases.clear();
        aliases.putAll(updatedAliases);
        return AliasResult.SUCCESS;
    }

    /**
     * Removes an existing alias mapping.
     *
     * @param aliasName Alias name to remove.
     * @return Result of the removal request.
     */
    public AliasResult removeAlias(String aliasName) {
        if (!isValidAliasName(aliasName)) {
            return AliasResult.INVALID_NAME;
        }
        String normalizedAliasName = normalize(aliasName);
        if (!aliases.containsKey(normalizedAliasName)) {
            return AliasResult.NOT_FOUND;
        }
        Map<String, CommandType> updatedAliases = new LinkedHashMap<>(aliases);
        updatedAliases.remove(normalizedAliasName);
        if (!storage.saveAliases(updatedAliases)) {
            return AliasResult.SAVE_FAILED;
        }
        aliases.clear();
        aliases.putAll(updatedAliases);
        return AliasResult.SUCCESS;
    }

    /**
     * Returns a read-only snapshot of the custom aliases in creation order.
     *
     * @return Custom alias mappings in creation order.
     */
    public Map<String, CommandType> getAliases() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(aliases));
    }

    /**
     * Returns whether an alias name contains one or more letters only.
     *
     * @param aliasName Alias name to check.
     * @return Whether the name is valid.
     */
    public static boolean isValidAliasName(String aliasName) {
        return !aliasName.isEmpty() && aliasName.codePoints().allMatch(Character::isLetter);
    }

    /**
     * Returns an alias name in its case-insensitive storage form.
     *
     * @param aliasName Alias name to normalize.
     * @return Lowercase alias name.
     */
    public static String normalize(String aliasName) {
        return aliasName.toLowerCase(Locale.ROOT);
    }
}
