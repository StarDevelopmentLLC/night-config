![Night Config](logo.png)

[![Maven Central](https://img.shields.io/maven-central/v/com.stardevllc.night-config/core.svg)](https://central.sonatype.com/search?q=g%3Acom.stardevllc.night-config)
[![javadoc](https://javadoc.io/badge2/com.stardevllc.night-config/core/javadoc.svg)](https://javadoc.io/doc/com.stardevllc.night-config/core)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/TheElectronWill/night-config/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/TheElectronWill/night-config/tree/master)

# Introduction

NightConfig is a powerful yet easy-to-use java configuration library, written in Java 8.

It supports the following formats:
- [JSON](https://www.json.org/)
- [YAML v1.1](https://yaml.org/)
- [TOML v1.0](https://github.com/toml-lang/toml)
- [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md)

# How to use

- Please read the extensive [wiki](https://github.com/TheElectronWill/Night-Config/wiki).
- You can also try the runnable [examples](examples/src/main/java) (see [below](#running-the-examples)).

## Glimpse

```java
// Simple builder:
FileConfig conf = FileConfig.of("the/file/config.toml");

// Advanced builder, default resource, autosave and much more (-> cf the wiki)
CommentedFileConfig config = CommentedFileConfig.builder("myConfig.toml").defaultResource("defaultConfig.toml").autosave().build();
config.load(); // This actually reads the config

String name = config.get("username"); // Generic return type!
List<String> names = config.get("users_list"); // Generic return type!
long id = config.getLong("account.id"); // Compound path: key "id" in subconfig "account"
int points = config.getIntOrElse("account.score", defaultScore); // Default value

config.set("account.score", points*2);

String comment = config.getComment("user");
// NightConfig saves the config's comments (for TOML and HOCON)

// config.save(); not needed here thanks to autosave()
config.close(); // Close the FileConfig once you're done with it :)
```

## Running the examples

Each file in `examples/src/main/java` has a main function and shows how to use NightConfig for many different use cases.

To run an example:
1. Clone this repository.
2. `cd` to it
3. Run `./gradlew examples:run -PmainClass=${CLASS}` by replacing `${CLASS}` with the example of your choice.

For example, to run [FileConfigExample.java](examples/src/main/java/FileConfigExample.java):
```sh
./gradlew examples:run -PmainClass=FileConfigExample
```

The file be compiled automatically, and the given main class will be executed.

# Project building

NightConfig is built with Gradle. The project is divided in several modules, the "core" module plus one module per supported configuration format. Please [read the wiki for more information](https://github.com/TheElectronWill/Night-Config/wiki/Modules-and-dependencies).

The releases are available on [Maven Central](https://search.maven.org/search?q=com.stardevllc.night-config) and [JitPack](https://jitpack.io/#TheElectronWill/Night-Config).

## Old Android modules

Older versions of Android (before Android Oreo) didn't provide the packages `java.util.function` and `java.nio.file`, which NightConfig heavily uses.
To attempt to mitigate these issues, I made a special version of each modules, suffixed with `_android`, that you could use instead of the regular modules.

These old `_android` modules are deprecated and will no longer receive updates.
The maintainance burden of these modules is not worth it, and these versions of Android have reached end of life since several years already.
