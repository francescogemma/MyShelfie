package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManagerTest;
import it.polimi.ingsw.event.*;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.networking.TCP.TCPConnection;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.w3c.dom.events.Event;

import java.util.Objects;

/*
class VirtualViewTest {
    private final String username1 = "Giacomo";
    private final String username2 = "Michele";
    private final String username3 = "Cristiano";

    private EventTransceiver transceiver1;
    private EventTransceiver transceiver2;
    private EventTransceiver transceiver3;

    private VirtualView virtualView1;
    private VirtualView virtualView2;
    private VirtualView virtualView3;

    @BeforeEach
    void setUp () {
        Logger.setShouldPrint(false);
        transceiver1 = new MockNetworkEventTransceiver();
        transceiver2 = new MockNetworkEventTransceiver();
        transceiver3 = new MockNetworkEventTransceiver();

        virtualView1 = new VirtualView(transceiver1);
        virtualView2 = new VirtualView(transceiver2);
        virtualView3 = new VirtualView(transceiver3);

        try {
            DBManagerTest.MenuControllerTest.setUp();
        } catch (Throwable e) {
            System.out.println("Set up failed");
            System.exit(-1);
        }

        MenuController.getInstance().join(virtualView1);
        MenuController.getInstance().join(virtualView2);
        MenuController.getInstance().join(virtualView3);
    }

    private void authenticate(VirtualView virtualView, EventTransceiver transceiver, String username) throws DisconnectedException {
        Requester<Response, LoginEventData> responseRequester = Response.requester(transceiver, transceiver, new Object());
        Assertions.assertTrue(responseRequester.request(new LoginEventData(username, "ciao")).isOk());
    }

    private void createNewGame(VirtualView view, EventTransceiver transceiver, String gameName) throws DisconnectedException {
        Requester<Response, CreateNewGameEventData> responseNewGame = Response.requester(transceiver, transceiver, new Object());
        Assertions.assertTrue(responseNewGame.request(new CreateNewGameEventData(gameName)).isOk());
        synchronized (view) {
            Assertions.assertTrue(view.isAuthenticated());
            Assertions.assertFalse(view.isInGame());
        }
    }

    private void joinGame(VirtualView view, EventTransceiver transceiver, String gameName) throws DisconnectedException {
        Requester<Response, JoinGameEventData> responseJoinGameResponse = Response.requester(transceiver, transceiver, new Object());
        Assertions.assertTrue(responseJoinGameResponse.request(new JoinGameEventData(gameName)).isOk());
        synchronized (view) {
            Assertions.assertTrue(view.isAuthenticated());
            Assertions.assertTrue(view.isInGame());
        }
    }

    private void pauseGame(VirtualView view, EventTransceiver transceiver, String username) throws DisconnectedException {
        Requester<Response, PauseGameEventData> requester = Response.requester(transceiver, transceiver, new Object());
        Assertions.assertTrue(requester.request(new PauseGameEventData()).isOk());
        synchronized (view) {
            Assertions.assertTrue(view.isAuthenticated());
            Assertions.assertFalse(view.isInGame());
        }
    }

    private void exitGame(VirtualView view, EventTransceiver transceiver) throws DisconnectedException {
        Requester<Response, PlayerExitGame> requester = Response.requester(transceiver, transceiver, new Object());
        Assertions.assertTrue(requester.request(new PlayerExitGame()).isOk());

        synchronized (view) {
            Assertions.assertFalse(view.isInGame());
        }
    }

    private void startGame(VirtualView view, EventTransceiver transceiver) throws DisconnectedException {
        Requester<Response, StartGameEventData> requester = Response.requester(transceiver, transceiver, new Object());
        Assertions.assertTrue(requester.request(new StartGameEventData()).isOk());

        synchronized (view) {
            Assertions.assertTrue(view.isInGame());
        }
    }

    @RepeatedTest(1)
    void t1() throws DisconnectedException {
        // authenticate virtualview1
        Requester<Response, LoginEventData> responseRequester = Response.requester(transceiver1, transceiver1, new Object());
        Assertions.assertTrue(responseRequester.request(new LoginEventData(username1, "ciao")).isOk());
        synchronized (virtualView1) {
            Assertions.assertTrue(virtualView1.isAuthenticated());
            Assertions.assertFalse(virtualView1.isInGame());
        }

        // authenticate virtualView2
        responseRequester = Response.requester(transceiver2, transceiver2, new Object());
        Assertions.assertTrue(responseRequester.request(new LoginEventData(username2, "ciao")).isOk());
        synchronized (virtualView2) {
            Assertions.assertTrue(virtualView2.isAuthenticated());
            Assertions.assertFalse(virtualView2.isInGame());
        }

        // authenticate virtualview3
        responseRequester = Response.requester(transceiver3, transceiver3, new Object());
        Assertions.assertTrue(responseRequester.request(new LoginEventData(username3, "ciao")).isOk());
        synchronized (virtualView3) {
            Assertions.assertTrue(virtualView3.isAuthenticated());
            Assertions.assertFalse(virtualView3.isInGame());
        }

        // creo una nuova partita
        Requester<Response, CreateNewGameEventData> responseNewGame = Response.requester(transceiver1, transceiver1, new Object());
        Assertions.assertTrue(responseNewGame.request(new CreateNewGameEventData("Prova")).isOk());
        synchronized (virtualView1) {
            Assertions.assertTrue(virtualView1.isAuthenticated());
            Assertions.assertFalse(virtualView1.isInGame());
        }

        // richiedo di entrare in partita per virtualView1
        Requester<Response, JoinGameEventData> responseJoinGameResponse = Response.requester(transceiver1, transceiver1, new Object());
        Assertions.assertTrue(responseJoinGameResponse.request(new JoinGameEventData("Prova")).isOk());
        synchronized (virtualView1) {
            Assertions.assertTrue(virtualView1.isAuthenticated());
            Assertions.assertTrue(virtualView1.isInGame());
        }

        // richiedo di entrare in partita per virtualView2
        responseJoinGameResponse = Response.requester(transceiver2, transceiver2, new Object());
        Assertions.assertTrue(responseJoinGameResponse.request(new JoinGameEventData("Prova")).isOk());
        synchronized (virtualView1) {
            Assertions.assertTrue(virtualView1.isAuthenticated());
            Assertions.assertTrue(virtualView1.isInGame());
        }
        synchronized (virtualView2) {
            Assertions.assertTrue(virtualView2.isAuthenticated());
            Assertions.assertTrue(virtualView2.isInGame());
        }

        // richiedo di uscire dal game
        Requester<Response, PlayerExitGame> responsePlayerExitGameResponse = Response.requester(transceiver1, transceiver1, new Object());
        Assertions.assertTrue(responsePlayerExitGameResponse.request(new PlayerExitGame()).isOk());

        synchronized (virtualView1) {
            Assertions.assertTrue(virtualView1.isAuthenticated());
            Assertions.assertFalse(virtualView1.isInGame());
        }
        synchronized (virtualView2) {
            Assertions.assertTrue(virtualView2.isAuthenticated());
            Assertions.assertTrue(virtualView2.isInGame());
        }
    }

    @RepeatedTest(500)
    void testPauseGame () throws DisconnectedException, InterruptedException {
        authenticate(virtualView1, transceiver1, username1);
        authenticate(virtualView2, transceiver2, username2);
        authenticate(virtualView3, transceiver3, username3);

        createNewGame(virtualView1, transceiver1, "Prova");

        joinGame(virtualView1, transceiver1, "Prova");
        joinGame(virtualView2, transceiver2, "Prova");
        joinGame(virtualView3, transceiver3, "Prova");

        startGame(virtualView1, transceiver1);

        pauseGame(virtualView1, transceiver1, username1);

        synchronized (virtualView2) {
            Assertions.assertFalse(virtualView2.isInGame());
        }

        synchronized (virtualView3) {
            Assertions.assertFalse(virtualView3.isInGame());
        }

        joinGame(virtualView1, transceiver1, "Prova");
        joinGame(virtualView2, transceiver2, "Prova");
        joinGame(virtualView3, transceiver3, "Prova");

        exitGame(virtualView2, transceiver2);

        synchronized (virtualView1) {
            Assertions.assertTrue(virtualView1.isInGame());
        }

        synchronized (virtualView3) {
            Assertions.assertTrue(virtualView3.isInGame());
        }

        exitGame(virtualView3, transceiver3);

        Thread thread2 = new Thread(() -> {
            for(int k = 0; k < 100; k++) {
                try {
                    joinGame(virtualView2, transceiver2, "Prova");
                    synchronized (virtualView1) {
                        Assertions.assertTrue(virtualView1.isInGame());
                    }

                    exitGame(virtualView2, transceiver2);

                    synchronized (virtualView1) {
                        Assertions.assertTrue(virtualView1.isInGame());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                System.out.println(k);
            }
        });

        Thread thread3 = new Thread(() -> {
            for(int k = 0; k < 100; k++) {
                try {
                    joinGame(virtualView3, transceiver3, "Prova");
                    synchronized (virtualView1) {
                        Assertions.assertTrue(virtualView1.isInGame());
                    }

                    exitGame(virtualView3, transceiver3);

                    synchronized (virtualView1) {
                        Assertions.assertTrue(virtualView1.isInGame());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                System.out.println(k);
            }
        });

        thread2.start();
        thread3.start();

        thread2.join();
        thread3.join();
    }

    @RepeatedTest(1)
    void testStopOwner() throws DisconnectedException {
        authenticate(virtualView1, transceiver1, username1);
        authenticate(virtualView2, transceiver2, username2);

        createNewGame(virtualView1, transceiver1, "Prova");

        joinGame(virtualView1, transceiver1, "Prova");
        joinGame(virtualView2, transceiver2, "Prova");

        startGame(virtualView1, transceiver1);

        pauseGame(virtualView1, transceiver1, username1);

        joinGame(virtualView1, transceiver1, "Prova");
        joinGame(virtualView2, transceiver2, "Prova");

        startGame(virtualView1, transceiver1);

        Requester<Response, PauseGameEventData> requester = Response.requester(transceiver2, transceiver2, new Object());
        Response request = requester.request(new PauseGameEventData());
        Assertions.assertFalse(request.isOk());
        synchronized (virtualView2) {
            Assertions.assertTrue(virtualView2.isAuthenticated());
            Assertions.assertTrue(virtualView2.isInGame());
        }
    }
}

*/
