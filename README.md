# My Shelfie

**Software Engineering** project - A.Y. 2022/2023

**Prefessor**: San Pietro Pierluigi

**Grade**: 30/30 cum laude

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

*NOTA: My Shelfie è un gioco da tavolo sviluppato ed edito da Cranio Creations Srl. I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa approvazione di Cranio Creations Srl a solo scopo didattico. È vietata la distribuzione, la copia o la riproduzione dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti contenuti.*

> Make sure to use Java 17 Runtime if you are on a Windows machine.
> This program makes use of Unicode characters which won't render properly on the terminal if you use a newer version due to a change in the supported Unicode standard which doesn't match the one used by Windows

### Client

You can run the client version of the application by following these instructions:

1. <a href="https://github.com/francescogemma/ing-sw-2023-gemma-groppi-migali-miotti/raw/main/deliverables/final/jar/my-shelfie-client.jar"> Download the client JAR </a>
2. Open the terminal, any terminal of any OS should work (Windows, Linux, MacOS)
3. Go to the directory containing the JAR file of the client (`my-shelfie-client.jar`)
4. execute the client JAR using the command:
    ```
    java -jar my-shelfie-client.jar
    ```
    this command does not take any argument

### Server

You can run the server version of the application by following these instructions:

1. <a href="https://github.com/francescogemma/ing-sw-2023-gemma-groppi-migali-miotti/raw/main/deliverables/final/jar/my-shelfie-server.jar"> Download the server JAR </a>
2. Open the terminal, any terminal of any OS should work (Windows, Linux, MacOS)
3. Go to the directory containing the JAR file of the server (`my-shelfie-server.jar`)
4. execute the server JAR using the command:
    ```
    java -jar my-shelfie-server.jar [hostname]
    ```
    where `[hostname]` is a mandatory argument for setting the `java.rmi.server.hostname` property, which corresponds to the IP or domain of the server on the network interface through which it communicates with the clients
