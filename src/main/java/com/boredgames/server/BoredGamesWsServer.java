package com.boredgames.server;

import com.boredgames.server.events.*;
import com.boredgames.server.types.GameStatus;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@Metered
@Timed
@ExceptionMetered
@ServerEndpoint("/annotated-ws")
public class BoredGamesWsServer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoredGamesWsServer.class);

    private static final TheMindGame theMindGame = new TheMindGame();

    private static final Map<String, Session> sessions = new HashMap<>();

    @OnOpen
    public void myOnOpen(final Session session) throws IOException {
        LOGGER.info("connexion: {} - {}", session.getUserProperties().get("javax.websocket.endpoint.remoteAddress"), session.getId());
        session.getAsyncRemote().sendText("welcome");
        sessions.put(session.getId(), session);
    }

    @OnMessage
    public void myOnMsg(final Session session, String message) {
        LOGGER.info("message! {}", message);
        try {
            BoredEventDto eventDto = MAPPER.readValue(message, BoredEventDto.class);
            switch (eventDto.getType()) {
                case EVENT_TEST:
                    TestEvent testEvent = MAPPER.treeToValue(eventDto.getEvent(), TestEvent.class);
                    String received = MAPPER.writeValueAsString(testEvent);
                    LOGGER.info("success! we got: {}", received);
                    session.getBasicRemote().sendText(MAPPER.writeValueAsString(eventDto));
                    break;
                // if we want to get the event core (if we need one, depending on the event)
                // GetGameStateEvent event = eventDto.getEvent().orElseThrow(MissingMandatoryEventException::new);

                case EVENT_GET_GAME_STATUS:
                    GameStatusEvent gameStatusEvent = new GameStatusEvent(theMindGame.getGameStatus());
                    BoredEventDto sentEventDto = new BoredEventDto(BoredEventType.EVENT_GAME_STATUS, MAPPER.valueToTree(gameStatusEvent));
                    session.getBasicRemote().sendText(MAPPER.writeValueAsString(sentEventDto));
                    break;

                case EVENT_GET_GAME_STATE:
                    session.getBasicRemote().sendText(MAPPER.writeValueAsString(new BoredEventDto(BoredEventType.EVENT_GAME_STATE,
                            MAPPER.valueToTree(theMindGame))));
                    break;

                case EVENT_CONNECT_WITH_PLAYER_NAME:
                    ConnectWithPlayerNameEvent event = MAPPER.treeToValue(eventDto.getEvent(), ConnectWithPlayerNameEvent.class);
                    Player player = theMindGame.getPlayerByName(event.getPlayerName());
                    if (player != null) {
                        if (!player.isConnected()) {
                            LOGGER.debug("Player {} reconnected", player.getName());
                            player.setConnected(session.getId());
                            session.getBasicRemote().sendText(MAPPER.writeValueAsString(new BoredEventDto(BoredEventType.EVENT_CONNECTION_SUCCESS,
                                    MAPPER.valueToTree(session.getId()))));
                            broadcastEvent(BoredEventType.EVENT_INFO, MAPPER.valueToTree("Player came back: " + player.getName()));
                            broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
                        }
                    }
                    else if (theMindGame.getGameStatus() == GameStatus.TO_BE_STARTED) {
                        if (theMindGame.getPlayers().isEmpty()) // first player is admin
                            theMindGame.addPlayer(new Player(event.getPlayerName(), true, true, session.getId()));
                        else
                            theMindGame.addPlayer(new Player(event.getPlayerName(), false, true, session.getId()));
                        player = theMindGame.getPlayerByName(event.getPlayerName());
                        LOGGER.debug("New player {} connected", player.getName());
                        broadcastEvent(BoredEventType.EVENT_INFO, MAPPER.valueToTree("New player connected: " + player.getName()));
                        broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
                    }
                    else {
                        session.getBasicRemote().sendText(MAPPER.writeValueAsString(new BoredEventDto(BoredEventType.EVENT_WARNING,
                                MAPPER.valueToTree("Rejected player " + event.getPlayerName() + " as the game started already. Sorry, dude."))));
                    }
                    break;

                default:
                    LOGGER.error("Unmanaged event: {}", eventDto.getType());
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("failed to deserialize message: {}", message, e);
        }
    }

    @OnClose
    public void myOnClose(final Session session, CloseReason cr) {
        LOGGER.debug("close session {} - {}", session.getId(), cr);
        Player player = theMindGame.getPlayerBySessionId(session.getId());
        if (player != null) {
            player.setDisconnected();
            LOGGER.info("Player {} disconnected", player.getName());
            try {
                broadcastEvent(BoredEventType.EVENT_ERROR, MAPPER.valueToTree("Player disconnected: " + player.getName()));
                broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
            } catch (Exception e) {
                LOGGER.error("failed to broadcast player disconnection");
            }
        }
        else {
            LOGGER.debug("Session with no player");
        }
        sessions.remove(session.getId());
    }


    public void broadcastEvent(BoredEventType type, JsonNode event) throws IOException {
        BoredEventDto eventDto = new BoredEventDto(type, event);
        Iterator playersIterator = theMindGame.getPlayers().entrySet().iterator();
        while (playersIterator.hasNext()) {
            Map.Entry mapElt = (Map.Entry)playersIterator.next();
            Player player = (Player)mapElt.getValue();
            if (player.isConnected()) {
                sessions.get(player.getSessionId()).getBasicRemote().sendText(MAPPER.writeValueAsString(eventDto));
            }
        }
    }
}