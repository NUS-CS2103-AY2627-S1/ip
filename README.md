# serangooner

[![Java CI](https://github.com/limenxi07/ip/actions/workflows/gradle.yml/badge.svg)](https://github.com/limenxi07/ip/actions/workflows/gradle.yml)

```text
  ____   U _____ u   ____        _      _   _     ____    U  ___ u   U  ___ u  _   _   U _____ u   ____     
 / __"| u\| ___"|/U |  _"\ u U  /"\  u | \ |"| U /"___|u   \/"_ \/    \/"_ \/ | \ |"|  \| ___"|/U |  _"\ u  
<\___ \/  |  _|"   \| |_) |/  \/ _ \/ <|  \| |>\| |  _ /   | | | |    | | | |<|  \| |>  |  _|"   \| |_) |/  
 u___) |  | |___    |  _ <    / ___ \ U| |\  |u | |_| |.-,_| |_| |.-,_| |_| |U| |\  |u  | |___    |  _ <    
 |____/>> |_____|   |_| \_\  /_/   \_\ |_| \_|   \____| \_)-\___/  \_)-\___/  |_| \_|   |_____|   |_| \_\   
  )(  (__)<<   >>   //   \\_  \\    >> ||   \\,-._)(|_       \\         \\    ||   \\,-.<<   >>   //   \\_  
 (__)    (__) (__) (__)  (__)(__)  (__)(_")  (_/(__)__)     (__)       (__)   (_")  (_/(__) (__) (__)  (__) 
```

a AI* chatbot that helps you track stuff

*AI = average intelligence

## how to use serangooner

you need java 25 or later. check yours with `java -version`.

### run the released jar

1. download `serangooner.jar` from the [latest release](https://github.com/limenxi07/ip/releases/latest)
2. put it in a folder of its own
3. open a command window in that folder and run:

```bash
java -jar "serangooner.jar"
```

that opens serangooner's chat window. type a command in the box at the
bottom and press enter, or click send.

serangooner keeps your tasks in `data/serangooner.txt` beside the jar, and
makes that folder itself on the first save. run it from the same folder
each time and your list will still be waiting.

### build the jar yourself

from the project root:

```bash
./gradlew shadowJar
```

that writes `build/libs/serangooner.jar`, dependencies and all. to start
serangooner without packaging it, run:

```bash
./gradlew run
```

### talk to serangooner in a terminal

serangooner answers on the command line as well as in a window, which is
handy when you want to pipe a batch of commands in. name the command line
version yourself and you get it instead of the window:

```bash
java -cp "serangooner.jar" serangooner.Serangooner
```

it understands exactly the same commands and shares the same
`data/serangooner.txt`, so you can switch between the two whenever you like.

enjoy talking to serangooner!

## why serangooner?
i'm not even a serangoon resident. i just think serangoon is cool, and
that people living in serangoon should be called serangooners.

## AI declaration

Claude Code (Opus 5 - High) was used to write code, and refactored the 
codebase to follow the Java coding standard.

## credits

the gui (`Launcher`, `Main`, `MainWindow`, `DialogBox` and their fxml) is
based on the [javafx tutorial](https://se-education.org/guides/tutorials/javaFx.html)
from the cs2103t website.
