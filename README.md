# Arthur Dent: A Bewildered Chat Bot 

This is a greenfield Java project. It's based on [Arthur Dent](https://en.wikipedia.org/wiki/Arthur_Dent), the protagonist of the novel series [The Hitchhiker's Guide to the Galaxy](https://en.wikipedia.org/wiki/The_Hitchhiker%27s_Guide_to_the_Galaxy). Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, JavaFX 17, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Follow the guide [[se-edu/guides] IDEA: Importing a Gradle project](https://se-education.org/guides/tutorials/intellijImportGradleProject.html) to import the project into IDEA.
1. After that, run the `[run]` task in the Gradle tool window. A GUI window should appear.

## AI Acknowledgements
- Localised changes are attributed in the class code itself with the `// AI-assisted` prefix.
- Inline code completion was also used to assist with development.
- Test cases were generated as per the `test-ui` skill.