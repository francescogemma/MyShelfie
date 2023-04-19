package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.Connection;
import org.junit.jupiter.api.*;

import java.net.ServerSocket;

class TCPConnectionTest {
    private ServerSocket serverSocket;
    private final int PORT = 12345;
    private final String HOST = "localhost";

    @BeforeEach
    public void setUp() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            Assertions.fail("error while creating server socket");
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            Assertions.fail("error while closing server socket");
        }
    }

    @RepeatedTest(100)
    @DisplayName("test basic usage")
    void connection_basicUsage_correctOutput() {
        Thread client = new Thread(() -> {
            Connection connection = null;
            try {
                connection = new TCPConnection(HOST, PORT);
                connection.send("hello");
                Assertions.assertEquals("world", connection.receive());
            } catch (Exception e) {
                Assertions.fail();
            }
        });
        client.start();

        try {
            Connection connectionServerSide = new TCPConnection(serverSocket.accept());
            Assertions.assertEquals("hello", connectionServerSide.receive());
            connectionServerSide.send("world");
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
