# My Shelfie

**Software Engineering** project - A.Y. 2022/2023

**Prefessor**: San Pietro Pierluigi

## Team members

* [Cristiano Migali](https://github.com/m1gwings)
* [Giacomo Maria Groppi](https://github.com/giacomogroppi)
* [Francesco Gemma](https://github.com/francescogemma)
* [Michele Miotti](https://github.com/michele-miotti-uni)

## Project specification

The project consists of developing a Java software version of the board game [My Shelfie](https://www.craniocreations.it/en/product/my-shelfie).

## Implemented features

This project comes up with an intuitive CLI interface and a GUI interface, you can choose which one to use once you launch the app.
The implemented features are the following:

* **Complete rules:**
    all the rules implemented.
* **Multiple games:**
    allows the server to manage multiple games simultaneously.
* **Flexibility to disconnections:**
    disconnected players can reconnect and continue the game.
* **Persistency:**
    Games can be saved so the user can stop and restart a game.

## How to run

### Client

You can run the client version of the application by following these instructions:

1. Open the terminal, any terminal of any OS should work (Windows, Linux, MacOS)
2. Go to the directory containing the JAR file of the client (`my-shelfie-client.jar`)
3. execute the client JAR using the command:
    ```
    java -jar my-shelfie-client.jar
    ```
    this command does not take any argument

### Server

You can run the server version of the application by following these instructions:

1. Open the terminal, any terminal of any OS should work (Windows, Linux, MacOS)
2. Go to the directory containing the JAR file of the server (`my-shelfie-server.jar`)
3. execute the server JAR using the command:
    ```
    java -jar my-shelfie-server.jar [hostname]
    ```
    where `[hostname]` indicates the server's hostname, it's an optional argument for setting the `java.rmi.server.hostname` property
