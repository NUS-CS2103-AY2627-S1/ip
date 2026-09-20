# Augustus

Greetings, citizen.

Augustus is a chatbot that helps you organise todos, deadlines, and events in one place.

Use simple commands to manage your tasks, track important deadlines, and keep your schedule organised.

The User Guide is available at [https://heshtin.github.io/ip/](https://heshtin.github.io/ip/).

## Setting up in IntelliJ IDEA

1. Ensure that **Java 25** is installed on your computer.
2. Clone this repository to your computer.
3. Open **IntelliJ IDEA**.
4. Select **Open** and choose the root folder of this project.
5. Allow IntelliJ to import the Gradle project and download the required
   dependencies.
6. Ensure that the project SDK is set to **Java 25**.
7. Locate:

   `src/main/java/augustus/Launcher.java`

8. Right-click `Launcher.java` and select **Run 'Launcher.main()'**.
9. The Augustus GUI should open.

Alternatively, you can run the application from the terminal:

```
./gradlew run
```

## Running the JAR file

If you are using the released JAR file:

1. Place `Augustus.jar` in a folder of your choice.
2. Open a terminal window in that folder.
3. Run:

```
java -jar Augustus.jar
```

4. The Augustus GUI should open.

## Use of AI

This project was developed with assistance from the AI-powered coding assistant ChatGPT. It was used to support debugging, code improvement, testing, and documentation. All suggestions were reviewed and adapted before being incorporated into the project.