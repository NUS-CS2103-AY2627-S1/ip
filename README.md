# Wei the Panda

Wei the Panda is a chatbot that keeps track of your todos, deadlines and events.
Type a command, and Wei plants it in your task grove and saves it for next time.

```text
 (@)___(@)
 ( o   o )    Wei the Panda
  \  v  /     keeper of your bamboo grove
   `---'
```

See the [User Guide](docs/README.md) for every command.

## Setting up in IntelliJ

Prerequisites: JDK 25, and the most recent version of IntelliJ.

1. Open IntelliJ (if you are not on the welcome screen, click `File` > `Close Project` first).
1. Click `Open`, select the project directory, and click `OK`. Accept the defaults for any further prompts.
1. Configure the project to use **JDK 25** as explained in [IntelliJ's JDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk),
   and set the **Project language level** to `SDK default`.
1. To start the graphical version, run `./gradlew run`.
   To start the console version, right-click `src/main/java/zhangwei/ZhangWei.java` and choose `Run ZhangWei.main()`.
   You should see the panda banner shown above.

**Warning:** Keep `src/main/java` as the root folder for Java files, as this is where Gradle expects to find them.

## Building and running the JAR

Prerequisites: JDK 25 (`sdk use java 25.0.3.fx-zulu` if you use SDKMAN).
Run `java -version` first to confirm the terminal is using Java 25.

Build the fat JAR, which contains the compiled classes and every runtime dependency:

```text
./gradlew shadowJar
```

The result is written to `build/libs/zhangwei.jar`. Run it with:

```text
java -jar build/libs/zhangwei.jar
```

Tasks are saved to `./data/zhangwei.txt`, relative to the folder you run the command from.

## Running the tests

```text
./gradlew test
```
