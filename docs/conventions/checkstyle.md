# Checkstyle

Source: <https://se-education.org/guides/tutorials/checkstyle.html>

Checkstyle is a static analysis tool that checks Java code against a set of
style rules. It is the machine-readable half of [java.md](java.md): it catches
layout, naming and import problems, and cannot catch a missing header comment
that says WHAT a method is for.

## Configuration

Checkstyle expects its configuration in `./config/checkstyle/` by convention.
Two files live there:

| File | Contains |
| --- | --- |
| `config/checkstyle/checkstyle.xml` | The set of code style rules to follow. |
| `config/checkstyle/suppressions.xml` | Which rules to suppress in which files. |

Configuration matching the SE-EDU Java coding standard is published in the
[AddressBook Level 3 project](https://github.com/se-edu/addressbook-level4/tree/master/config/checkstyle).

To suppress a rule for a segment of code, wrap it:

```java
//CHECKSTYLE.OFF: RuleName
...
//CHECKSTYLE.ON: RuleName
```

Suppress a rule only when the code is right and the rule is wrong for that one
spot, and say why in the commit body. A suppression that hides a real violation
is a violation.

## Gradle

`build.gradle` carries the plugin and pins the tool version:

```gradle
plugins {
    id 'checkstyle'
    // other plugins
}

checkstyle {
    toolVersion = '11.0.0'
}
```

The version above is the one this project is on. The upstream guide shows
`14.1.0`; `checkstyle.xml` is written against a particular version, so change
the two together or the config will fail to parse.

The plugin adds two tasks:

| Task | Checks |
| --- | --- |
| `checkstyleMain` | The main source set complies with the style rules. |
| `checkstyleTest` | The test source set complies with the style rules. |

Run both before committing:

```bash
./gradlew checkstyleMain checkstyleTest
```

Reports land in `build/reports/checkstyle/`. A violation fails the build, so
`./gradlew build` covers this too.

## IDE integration

### Visual Studio Code

Install the **Checkstyle for Java** extension, then point it at the project
configuration — `Checkstyle: Set Configuration File` and choose
`config/checkstyle/checkstyle.xml`. Violations then appear in the Problems panel
as you type. The full walkthrough is at
<https://se-education.org/guides/tutorials/vscSettingUpCheckstyle.html>.

### IntelliJ IDEA

1. Install the **Checkstyle-IDEA** plugin: `File > Settings > Plugins >
   Marketplace`, find the plugin, then restart the IDE.
2. `File > Settings > Tools > Checkstyle`.
3. Set **Scan Scope** to *Only Java sources (including tests)*, so the plugin
   checks test code as well.
4. Set the **Checkstyle version** to the one in `build.gradle`.
5. Click **+** under *Configuration File*, give it a description, choose *Use a
   local Checkstyle file*, select `config/checkstyle/checkstyle.xml`, then
   `Next > Finish`.
6. Mark the new configuration **Active** and click **OK**.

Verify the setup by temporarily breaking a rule — an extra line break before a
`{` will do — and running the check.

## Troubleshooting

| Problem | Reason | Solution |
| --- | --- | --- |
| The plugin reports *The Checkstyle rules file could not be parsed … blacklisted for 60s*. | `checkstyle.xml` targets a particular version, and the plugin is set to another. | Select the version that matches `build.gradle` and click **Apply**. |
| The plugin does not highlight errors; real-time scanning seems broken. | The plugin does not always start straight after setup. | Restart the IDE. |

## Resources

- [Checkstyle home page](https://checkstyle.org/)
- [Gradle Checkstyle plugin documentation](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
