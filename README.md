# NyonBot User Guide

![nyon](https://static.tvtropes.org/pmwiki/pub/images/kawkaw_battle_idle_1_400x280.png)

![screenshot of ui](https://nje14.github.io/ip/Ui.png)

NyonBot is a simple chatbot based off the _Duke_ greenfield Java project template

NyonBot is based on the [KawKaw NPC from deltarune](https://deltarune.wiki/w/Kawkaw)

## Quick start guide

Download the latest release from [releases](https://github.com/nje14/ip/releases)

If there are no releases present:

1. Go to the latest [github action](https://github.com/nje14/ip/actions)

2. Select the latest working workflow (should have a :white_check_mark: next to it)

3. Click on artifacts

4. Download `nyonbot-fat.jar`

Once downloaded, go to terminal / powershell and run
```bash
java -jar nyonbot-fat.jar
```

## Prerequisites

Requires:

* Java 25 or later

That's it

## Important notes 

**Strictly for non-commercial, internal use only**

This software comes with a `help`/`man`

All dates **MUST** be entered in the format dd/MM/yyyy HHmm

## Features

NyonBot supports the following features

NyonBot also supports some common aliases. Refer to docs for specifics.

|Command|Description|Usage|Docs|
|---|---|---|---|
|deadline|creates a new deadline to do by specified date|`deadline <name> --by <date>`|[Deadline docs](https://nje14.github.io/ip/DEADLINE)|
|delete|deletes a task|`delete <index \| name>`|[Delete docs](https://nje14.github.io/ip/DELETE)|
|echo|echoes input|`echo [text]`|[Echo docs](https://nje14.github.io/ip/ECHO)|
|event|creates an event with specified start and end dates|`event <name> --from <start date> --to <end date>`|[Event docs](https://nje14.github.io/ip/EVENT)|
|exit|exits software|`exit`|[Exit docs](https://nje14.github.io/ip/EXIT)|
|find|filters events by search|`find <search text>`|[Find docs](https://nje14.github.io/ip/FIND)|
|help|pulls up help documents|`help [command]`|[Help docs](https://nje14.github.io/ip/HELP)|
|list|lists all tasks|`list`|[List docs](https://nje14.github.io/ip/LIST)|
|mark|marks task as complete|`mark <index\|name>`|[Mark docs](https://nje14.github.io/ip/MARK)|
|nyon|nyon|`nyon`|[Nyon docs](https://nje14.github.io/ip/NYON)|
|on|returns all tasks occuring on specified date|`on <date>`|[On docs](https://nje14.github.io/ip/ON)|
|todo|creates a todo|`todo <name>`|[Todo docs](https://nje14.github.io/ip/TODO)|
|unmark|marks task as incomplete|`unmark <index\|name>`|[Unmark docs](https://nje14.github.io/ip/UNMARK)|

## Attribution

MainWindow and DialogBox was adapted from the [se-education JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html)

Parts of the code were created with assistance from NUS ChatGPT

Images and sounds were taken from the [deltarune wiki](https://deltarune.wiki)

Big Shot font taken from https://www.dafont.com/big-shot.font
