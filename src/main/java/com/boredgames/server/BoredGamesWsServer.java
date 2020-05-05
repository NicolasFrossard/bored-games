package com.boredgames.server;

import com.boredgames.server.events.*;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.ObjectUtils;
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


    @OnOpen
    public void myOnOpen(final Session session) throws IOException {
        LOGGER.info("connexion: {} - {}", session.getUserProperties().get("javax.websocket.endpoint.remoteAddress"), session.getId());
        session.getAsyncRemote().sendText("welcome");
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
                    session.getBasicRemote().sendText(MAPPER.writeValueAsString(new BoredEventDto(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame))));
                    break;

                case EVENT_CONNECT_WITH_PLAYER_NAME:
                    ConnectWithPlayerNameEvent event = MAPPER.treeToValue(eventDto.getEvent(), ConnectWithPlayerNameEvent.class);
                    Player player = theMindGame.getPlayerByName(event.getPlayerName());
                    if (player != null) {
                        LOGGER.debug("Player {} reconnected", player.getName());
                        player.setConnected();
                    }
                    else {
                        theMindGame.addPlayer(new Player(event.getPlayerName(), false, true, session.getId()));
                        player = theMindGame.getPlayerByName(event.getPlayerName());
                        LOGGER.debug("New player {} connected", player.getName());
                    }
                    //broadcast(EVENT_PLAYER_CONNECTED, );
                    broadcast(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
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
        LOGGER.info("close session {} - {}", session.getId(), cr);
        Player player = theMindGame.getPlayerBySessionId(session.getId());
        if (player != null)
            player.setDisconnected();
    }

    private void broadcast(BoredEventType type, JsonNode event) throws IOException {
        BoredEventDto eventDto = new BoredEventDto(type, event);
        //theMindGame.broadcastEvent(MAPPER.writeValueAsString(eventDto));

    }

    public void broadcastEvent(String message) throws IOException {
        Iterator playersIterator = theMindGame.getPlayers().entrySet().iterator();
        while (playersIterator.hasNext()) {
            Map.Entry mapElt = (Map.Entry)playersIterator.next();
            Session session = (Session)mapElt.getKey();
            session.getBasicRemote().sendText(message);
        }
    }
}