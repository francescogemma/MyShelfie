package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManagerTest;
import it.polimi.ingsw.controller.servercontroller.MenuController;
import it.polimi.ingsw.event.*;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.networking.TCP.TCPConnection;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;
import jdk.jfr.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.w3c.dom.events.Event;

import java.util.Objects;

@Execution(ExecutionMode.SAME_THREAD)
class VirtualViewTest {
    private final String username1 = "Giacomo";
    private final String username2 = "Michele";
    private final String username3 = "Cristiano";
    private final String username4 = "Francesco";

    private final int numberOfRepetition = 30;

    private EventTransceiver transceiver1;
    private EventTransceiver transceiver2;
    private EventTransceiver transceiver3;
    private EventTransceiver transceiver4;

    private VirtualView virtualView1;
    private VirtualView virtualView2;
    private VirtualView virtualView3;
    private VirtualView virtualView4;

    @BeforeEach
    void setUp () {
        Logger.setShouldPrint(false);
        transceiver1 = new MockNetworkEventTransceiver();
        transceiver2 = new MockNetworkEventTransceiver();
        transceiver3 = new MockNetworkEventTransceiver();
        transceiver4 = new MockNetworkEventTransceiver();

        virtualView1 = new VirtualView(transceiver1);
        virtualView2 = new VirtualView(transceiver2);
        virtualView3 = new VirtualView(transceiver3);
        virtualView4 = new VirtualView(transceiver4);

        try {
            DBManagerTest.MenuControllerTest.setUp();
        } catch (Throwable e) {
            System.out.println("Set up failed");
            System.exit(-1);
        }

        MenuController.getInstance().joinMenu(virtualView1);
        MenuController.getInstance().joinMenu(virtualView2);
        MenuController.getInstance().joinMenu(virtualView3);
        MenuController.getInstance().joinMenu(virtualView4);
    }

    private void authenticate(VirtualView virtualView, EventTransceiver transceiver, String username) throws DisconnectedException {
        Requester<Response, LoginEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Assertions.assertTrue(requester.request(new LoginEventData(username, "ciao")).isOk());

        transceiver.broadcast(new PlayerHasJoinMenu());
    }

    private void createNewGame(VirtualView view, EventTransceiver transceiver, String gameName) throws DisconnectedException {
        Requester<Response, CreateNewGameEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Assertions.assertTrue(requester.request(new CreateNewGameEventData(gameName)).isOk());
    }

    private void joinGame(VirtualView view, EventTransceiver transceiver, String gameName) throws DisconnectedException {
        Requester<Response, JoinLobbyEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Assertions.assertTrue(requester.request(new JoinLobbyEventData(gameName)).isOk());
    }

    private void pauseGame(VirtualView view, EventTransceiver transceiver, String username) throws DisconnectedException {
        Requester<Response, PauseGameEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Response response = requester.request(new PauseGameEventData());

        if (response.isOk()) return;

        Assertions.fail(response.message());
    }

    private void exitGame(VirtualView view, EventTransceiver transceiver) throws DisconnectedException {
        Requester<Response, PlayerExitGame> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Assertions.assertTrue(requester.request(new PlayerExitGame()).isOk());
    }

    private void startGame(VirtualView view, EventTransceiver transceiver) throws DisconnectedException {
        Requester<Response, StartGameEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Assertions.assertTrue(requester.request(new StartGameEventData()).isOk());
    }

    private void joinLobby(VirtualView view, EventTransceiver transceiver, String gameName) throws DisconnectedException {
        Requester<Response, JoinLobbyEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Response response = requester.request(new JoinLobbyEventData(gameName));

        if (response.isOk())
            return;

        Assertions.fail(response.message());
    }

    private void exitLobby(VirtualView view, EventTransceiver transceiver) throws DisconnectedException {
        Requester<Response, ExitLobbyEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Assertions.assertTrue(requester.request(new ExitLobbyEventData()).isOk());
    }

    private void connectPlayer(VirtualView view, EventTransceiver transceiver) throws DisconnectedException {
        Requester<Response, JoinGameEventData> requester = Response.requester(transceiver, transceiver, new Object());
        requester.registerAllListeners();
        Response response = requester.request(new JoinGameEventData(""));

        if (response.isOk()) return;
        Assertions.fail(response.message());
    }

    @RepeatedTest(numberOfRepetition)
    void test() throws DisconnectedException, InterruptedException {
        Thread thread1, thread2, thread3, thread4;

        authenticate(virtualView1, transceiver1, username1);
        authenticate(virtualView2, transceiver2, username2);
        authenticate(virtualView3, transceiver3, username3);
        authenticate(virtualView4, transceiver4, username4);

        createNewGame(virtualView1, transceiver1, "Prova");

        Runnable runnable1 = () -> {
            for (int i = 0; i < 100; i++) {
                try {
                    joinLobby(virtualView1, transceiver1, "Prova");
                    exitLobby(virtualView1, transceiver1);
                } catch (DisconnectedException e) {
                    Assertions.fail("cicle: %d for virtualview1".formatted(i));
                }
            }
        };

        Runnable runnable2 = () -> {
            for (int i = 0; i < 100; i++) {
                try {
                    joinLobby(virtualView2, transceiver2, "Prova");
                    exitLobby(virtualView2, transceiver2);
                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        Runnable runnable3 = () -> {
            for (int i = 0; i < 100; i++) {
                try {
                    joinLobby(virtualView3, transceiver3, "Prova");
                    exitLobby(virtualView3, transceiver3);
                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        Runnable runnable4 = () -> {
            for (int i = 0; i < 100; i++) {
                try {
                    joinLobby(virtualView4, transceiver4, "Prova");
                    exitLobby(virtualView4, transceiver4);

                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        thread1 = new Thread(runnable1);
        thread2 = new Thread(runnable2);
        thread3 = new Thread(runnable3);
        thread4 = new Thread(runnable4);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
    }

    @RepeatedTest(numberOfRepetition)
    void test2() throws DisconnectedException, InterruptedException {
        Thread thread1, thread2, thread3;

        authenticate(virtualView1, transceiver1, username1);
        authenticate(virtualView2, transceiver2, username2);
        authenticate(virtualView3, transceiver3, username3);
        authenticate(virtualView4, transceiver4, username4);

        createNewGame(virtualView1, transceiver1, "Prova");

        Runnable runnable1 = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    joinLobby(virtualView1, transceiver1, "Prova");
                    exitLobby(virtualView1, transceiver1);
                } catch (DisconnectedException e) {
                    Assertions.fail("cicle: %d for virtualview1".formatted(i));
                }
            }
        };

        Runnable runnable2 = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    joinLobby(virtualView2, transceiver2, "Prova");
                    exitLobby(virtualView2, transceiver2);
                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        Runnable runnable3 = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    joinLobby(virtualView3, transceiver3, "Prova");
                    exitLobby(virtualView3, transceiver3);
                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        joinLobby(virtualView1, transceiver1, "Prova");
        joinLobby(virtualView2, transceiver2, "Prova");
        joinLobby(virtualView3, transceiver3, "Prova");
        joinLobby(virtualView4, transceiver4, "Prova");

        startGame(virtualView1, transceiver1);

        Thread.sleep(100);

        connectPlayer(virtualView1, transceiver1);
        connectPlayer(virtualView2, transceiver2);
        connectPlayer(virtualView3, transceiver3);
        connectPlayer(virtualView4, transceiver4);

        Thread.sleep(1000);

        pauseGame(virtualView1, transceiver1, "Giacomo");

        thread1 = new Thread(runnable1);
        thread2 = new Thread(runnable2);
        thread3 = new Thread(runnable3);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }

    @RepeatedTest(numberOfRepetition)
    @Description("With 2 game in list")
    void test3() throws DisconnectedException, InterruptedException {
        Thread thread1, thread2, thread3;

        authenticate(virtualView1, transceiver1, username1);
        authenticate(virtualView2, transceiver2, username2);
        authenticate(virtualView3, transceiver3, username3);
        authenticate(virtualView4, transceiver4, username4);

        createNewGame(virtualView1, transceiver1, "Prova");
        createNewGame(virtualView2, transceiver2, "Prova2");

        Runnable runnable1 = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    joinLobby(virtualView1, transceiver1, "Prova");
                    exitLobby(virtualView1, transceiver1);
                } catch (DisconnectedException e) {
                    Assertions.fail("cicle: %d for virtualview1".formatted(i));
                }
            }
        };

        Runnable runnable2 = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    joinLobby(virtualView2, transceiver2, "Prova");
                    exitLobby(virtualView2, transceiver2);
                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        Runnable runnable3 = () -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    joinLobby(virtualView3, transceiver3, "Prova");
                    exitLobby(virtualView3, transceiver3);
                } catch (DisconnectedException e) {
                    Assertions.fail();
                }
            }
        };

        joinLobby(virtualView1, transceiver1, "Prova");
        joinLobby(virtualView2, transceiver2, "Prova");
        joinLobby(virtualView3, transceiver3, "Prova");
        joinLobby(virtualView4, transceiver4, "Prova");

        startGame(virtualView1, transceiver1);

        Thread.sleep(100);

        connectPlayer(virtualView1, transceiver1);
        connectPlayer(virtualView2, transceiver2);
        connectPlayer(virtualView3, transceiver3);
        connectPlayer(virtualView4, transceiver4);

        Thread.sleep(1000);

        pauseGame(virtualView1, transceiver1, "Giacomo");

        thread1 = new Thread(runnable1);
        thread2 = new Thread(runnable2);
        thread3 = new Thread(runnable3);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }
}

