# Releasing Nori

Use this checklist to publish a distributable version of Nori without adding generated binaries to Git.

## Build and verify the artifact

1. Confirm that Java 25 is active with `java -version`.
2. Run `./gradlew clean check shadowJar` on macOS or Linux, or
   `.\gradlew.bat clean check shadowJar` on Windows.
3. Find the generated artifact at `build/libs/Nori.jar`.
4. Run the automated release checks:

   ```bash
   python test/jar_release_test.py
   ```

   Every case must print `PASS`. The script stops at the first failure and exits
   non-zero, so it can gate the release.
5. Launch the graphical interface by hand, because no script can confirm a
   window is drawn. Copy only `Nori.jar` into an empty folder outside the
   repository, open a command window there, and run `java -jar "Nori.jar"`.
6. Add a task, exit Nori, and launch it again to confirm that the adjacent
   `data` folder preserves the task.

Java 25 prints several warnings on startup — an unsupported-configuration
notice, restricted native access, and a `sun.misc.Unsafe` deprecation from the
JavaFX renderer. These are expected for a shaded JAR loaded from the class path
and do not indicate a problem. A stack trace does.

The `build` directory is ignored by Git. Do not force-add `Nori.jar`; regenerate
it for each release instead.

## Why the size check matters

`test/jar_release_test.py` fails any JAR under 5 MB. A build that does not
bundle JavaFX still produces a working-looking artifact of roughly 40 KB: it
compiles, and it even runs from the console, because the console interface never
touches JavaFX. It fails only when a user opens the window, on a machine that
has no JavaFX installed — which is every machine the release targets.

The `v0.1` release shipped a 35 KB `Nori.jar` for exactly this reason. Check the
size, and check it before publishing rather than after.

## Create the GitHub release

1. Open the repository's **Releases** page on GitHub and choose **Draft a new release**.
2. Create a version tag such as `v0.1` and target the commit intended for distribution.
3. Use a short release title and summarize the user-visible functionality.
4. Attach `build/libs/Nori.jar` under **Attach binaries by dropping them here or selecting them**.
5. Publish the release, then download its attached JAR and repeat the empty-folder launch check.

For later versions, increment the release tag rather than replacing an existing published tag.
