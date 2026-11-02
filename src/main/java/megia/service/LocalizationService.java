package megia.service;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;

import megia.exception.ErrorCode;

/**
 * Loads and formats localized text, falling back to English when possible.
 */
public final class LocalizationService {
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String LANGUAGE_PREFERENCE_KEY = "language";
    private static final ResourceBundle.Control BUNDLE_CONTROL =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
    private static final Properties PROPERTIES = PropertiesService.getProperties();
    private static final Preferences LANGUAGE_PREFERENCES = getPreferences();
    private static final CopyOnWriteArrayList<Runnable> LANGUAGE_LISTENERS =
            new CopyOnWriteArrayList<>();
    private static final ResourceBundle ENGLISH_MESSAGES;
    private static final ResourceBundle ENGLISH_EXCEPTIONS;
    private static volatile Locale locale;
    private static volatile ResourceBundle messages;
    private static volatile ResourceBundle exceptions;

    static {
        ENGLISH_MESSAGES = loadBundle("i18n.messages.messages", Locale.of(DEFAULT_LANGUAGE));
        ENGLISH_EXCEPTIONS = loadBundle("i18n.exceptions.exceptions", Locale.of(DEFAULT_LANGUAGE));
        locale = resolveLocale(resolveInitialLanguage());
        reloadBundles();
    }

    private LocalizationService() {
    }

    /**
     * Formats the localized message associated with a structured error.
     * Arguments are supplied in the order expected by the localized template.
     * An explicitly null argument array is treated as empty. If formatting fails,
     * this method returns the unformatted localized template.
     *
     * @param errorCode Error whose message should be retrieved.
     * @param arguments Values interpolated into the message.
     * @return Formatted localized error message.
     */
    public static String getException(ErrorCode errorCode, Object... arguments) {
        Locale currentLocale = locale;
        String template = getLocalizedValue(exceptions, ENGLISH_EXCEPTIONS, errorCode.name());
        Object[] formattingArguments = arguments == null ? new Object[0] : arguments;
        try {
            return String.format(currentLocale, template, formattingArguments);
        } catch (IllegalFormatException exception) {
            System.err.printf("Invalid format for exception message '%s': %s%n",
                    errorCode, exception.getMessage());
            return template;
        }
    }

    /**
     * Returns the localized message associated with a message key.
     *
     * @param key Message key to retrieve.
     * @return Localized message text.
     */
    public static String getMessage(String key) {
        return getLocalizedValue(messages, ENGLISH_MESSAGES, key);
    }

    /**
     * Returns the active two-letter language code.
     *
     * @return Active language code.
     */
    public static String getLanguage() {
        return locale.getLanguage();
    }

    /**
     * Changes the active language, persists the selection, and notifies GUI listeners.
     * Preference write failures are reported diagnostically but do not prevent the in-memory
     * language change.
     *
     * @param language Requested two-letter language code.
     */
    public static void setLanguage(String language) {
        Locale newLocale = resolveLocale(language);
        boolean hasChanged;
        synchronized (LocalizationService.class) {
            hasChanged = !newLocale.equals(locale);
            locale = newLocale;
            reloadBundles();
            persistLanguage(newLocale.getLanguage());
        }
        if (hasChanged) {
            notifyLanguageListeners();
        }
    }

    /**
     * Registers a listener that runs after the active language changes.
     *
     * @param listener Callback used to refresh language-sensitive UI.
     */
    public static void addLanguageChangeListener(Runnable listener) {
        LANGUAGE_LISTENERS.add(Objects.requireNonNull(listener));
    }

    /**
     * Removes a previously registered language-change listener.
     *
     * @param listener Callback to remove.
     */
    public static void removeLanguageChangeListener(Runnable listener) {
        LANGUAGE_LISTENERS.remove(listener);
    }

    private static String resolveInitialLanguage() {
        String preferredLanguage = readPreferredLanguage();
        return preferredLanguage == null
                ? PROPERTIES.getProperty("language")
                : preferredLanguage;
    }

    private static String readPreferredLanguage() {
        if (LANGUAGE_PREFERENCES == null) {
            return null;
        }
        try {
            return LANGUAGE_PREFERENCES.get(LANGUAGE_PREFERENCE_KEY, null);
        } catch (RuntimeException exception) {
            System.err.println("Unable to read the language preference. Using configuration.");
            return null;
        }
    }

    private static Locale resolveLocale(String language) {
        String configuredLanguage = language == null ? DEFAULT_LANGUAGE : language.strip();
        if (configuredLanguage.isEmpty()) {
            System.err.println("The configured language is empty. Using English.");
            return Locale.of(DEFAULT_LANGUAGE);
        }

        try {
            return Locale.of(configuredLanguage);
        } catch (RuntimeException exception) {
            System.err.printf("The configured language '%s' is invalid. Using English.%n",
                    configuredLanguage);
            return Locale.of(DEFAULT_LANGUAGE);
        }
    }

    private static Preferences getPreferences() {
        try {
            return Preferences.userNodeForPackage(LocalizationService.class);
        } catch (RuntimeException exception) {
            System.err.println("Unable to access language preferences. Using in-memory settings.");
            return null;
        }
    }

    private static void persistLanguage(String language) {
        if (LANGUAGE_PREFERENCES == null) {
            return;
        }
        try {
            LANGUAGE_PREFERENCES.put(LANGUAGE_PREFERENCE_KEY, language);
        } catch (RuntimeException exception) {
            System.err.println("Unable to save the language preference.");
        }
    }

    private static void reloadBundles() {
        messages = loadBundle("i18n.messages.messages", locale);
        exceptions = loadBundle("i18n.exceptions.exceptions", locale);
    }

    private static void notifyLanguageListeners() {
        for (Runnable listener : LANGUAGE_LISTENERS) {
            try {
                listener.run();
            } catch (RuntimeException exception) {
                System.err.println("A language-change listener could not refresh.");
            }
        }
    }

    private static ResourceBundle loadBundle(String baseName, Locale targetLocale) {
        try {
            return ResourceBundle.getBundle(baseName, targetLocale, BUNDLE_CONTROL);
        } catch (MissingResourceException exception) {
            if (!targetLocale.getLanguage().equals(DEFAULT_LANGUAGE)) {
                System.err.printf("Localization for '%s' is unavailable. Using English.%n",
                        targetLocale.getLanguage());
            } else {
                System.err.printf("Required English localization bundle '%s' is unavailable.%n", baseName);
            }
            return null;
        }
    }

    private static String getLocalizedValue(
            ResourceBundle selectedBundle,
            ResourceBundle fallbackBundle,
            String key) {
        if (selectedBundle != null && selectedBundle.containsKey(key)) {
            return selectedBundle.getString(key);
        }
        if (fallbackBundle != null && fallbackBundle.containsKey(key)) {
            System.err.printf("Localization key '%s' is missing; using English.%n", key);
            return fallbackBundle.getString(key);
        }

        System.err.printf("Localization key '%s' is missing from all bundles.%n", key);
        return "Missing message: " + key;
    }
}
