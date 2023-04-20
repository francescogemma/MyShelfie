package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.Connection;
import org.junit.jupiter.api.*;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean receivedLastString = new AtomicBoolean(false);
        Object lock = new Object();

        Thread client = new Thread(() -> {
            Connection connection;
            try {
                connection = new TCPConnection(HOST, PORT);
                connection.send("hello");
                Assertions.assertEquals("world", connection.receive());
                connection.send("hello1");
                connection.send("hello2");

                synchronized(lock) {
                    while(!receivedLastString.get()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Assertions.fail();
                        }
                    }
                    connection.disconnect();
                }
            } catch (Exception e) {
                Assertions.fail();
            }
        });
        client.start();

        try {
            Connection connectionServerSide = new TCPConnection(serverSocket.accept());
            Assertions.assertEquals("hello", connectionServerSide.receive());
            connectionServerSide.send("world");
            Assertions.assertEquals("hello1", connectionServerSide.receive());
            Assertions.assertEquals("hello2", connectionServerSide.receive());

            synchronized(lock) {
                receivedLastString.set(true);
                lock.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
