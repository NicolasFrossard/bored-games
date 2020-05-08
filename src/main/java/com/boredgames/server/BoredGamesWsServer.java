package com.boredgames.server;

import com.boredgames.server.events.*;
import com.boredgames.server.types.GameStatus;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private static final Map<String, Session> sessions = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void myOnOpen(final Session session) throws IOException {
        LOGGER.info("connexion: {} - {}", session.getUserProperties().get("javax.websocket.endpoint.remoteAddress"), session.getId());
        session.getAsyncRemote().sendText("welcome");
        sessions.put(session.getId(), session);
    }

    @OnMessage
    public void myOnMsg(final Session session, String message) {
        Optional<Player> optPlayer;
        Player player;
        LOGGER.info("message! {}", message);
        try {
            BoredEventDto eventDto = MAPPER.readValue(message, BoredEventDto.class);
            switch (eventDto.getType()) {

                case EVENT_GET_GAME_STATE:
                    session.getBasicRemote().sendText(MAPPER.writeValueAsString(new BoredEventDto(BoredEventType.EVENT_GAME_STATE,
                            MAPPER.valueToTree(theMindGame))));
                    break;

                case EVENT_CONNECT_WITH_PLAYER_NAME:
                    ConnectWithPlayerNameEvent playerNameEvent = MAPPER.treeToValue(eventDto.getEvent(), ConnectWithPlayerNameEvent.class);
                    optPlayer = theMindGame.getPlayerByName(playerNameEvent.getPlayerName());
                    if (optPlayer.isPresent()) {
                        player = optPlayer.get();
                        if (!player.isConnected()) {
                            LOGGER.debug("Player {} reconnected", player.getName());
                            player.setConnected(session.getId());
                            sendEventToPlayer(player, new BoredEventDto(BoredEventType.EVENT_CONNECTION_SUCCESS, MAPPER.valueToTree(session.getId())));
                            broadcastEvent(BoredEventType.EVENT_INFO, MAPPER.valueToTree("Player came back: " + player.getName()));
                            broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
                        }
                    }
                    else if (theMindGame.getStatus() == GameStatus.TO_BE_STARTED) {
                        if (theMindGame.getPlayers().isEmpty()) // first player is admin
                            theMindGame.addPlayer(new Player(playerNameEvent.getPlayerName(), true, true, session.getId()));
                        else
                            theMindGame.addPlayer(new Player(playerNameEvent.getPlayerName(), false, true, session.getId()));
                        player = theMindGame.getPlayerBySessionId(session.getId()).orElseThrow(Exception::new);
                        LOGGER.debug("New player {} connected", player.getName());
                        sendEventToPlayer(player, new BoredEventDto(BoredEventType.EVENT_CONNECTION_SUCCESS, MAPPER.valueToTree(session.getId())));
                        broadcastEvent(BoredEventType.EVENT_INFO, MAPPER.valueToTree("New player connected: " + player.getName()));
                        broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
                    }
                    else {
                        session.getBasicRemote().sendText(MAPPER.writeValueAsString(new BoredEventDto(BoredEventType.EVENT_WARNING,
                                MAPPER.valueToTree("Rejected player " + playerNameEvent.getPlayerName() + " as the game started already. Sorry, dude."))));
                    }
                    break;

                case EVENT_START_THE_GAME:
                    player = theMindGame.getPlayerBySessionId(session.getId()).orElseThrow(() -> new RuntimeException("unknown sessionId"));
                    if (!player.isAdmin()) {
                        sendEventToPlayer(player, new BoredEventDto(BoredEventType.EVENT_WARNING,
                                MAPPER.valueToTree("Only the admin can do that. Please don't be such a goose")));
                        break;
                    }
                    LOGGER.info("Starting the game");
                    theMindGame.start();
                    theMindGame.moveToNextRound();
                    broadcastEvent(BoredEventType.EVENT_GAME_STARTED, null);
                    broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
                    break;

                case EVENT_STOP_THE_GAME:
                    player = theMindGame.getPlayerBySessionId(session.getId()).orElseThrow(() -> new RuntimeException("unknown sessionId"));
                    if (!player.isAdmin()) {
                        sendEventToPlayer(player, new BoredEventDto(BoredEventType.EVENT_WARNING,
                                MAPPER.valueToTree("Only the admin can do that. Please don't be such a goose")));
                        break;
                    }
                    LOGGER.info("Stopping the game");
                    theMindGame.stop();
                    broadcastEvent(BoredEventType.EVENT_GAME_STOPPED, null);
                    broadcastEvent(BoredEventType.EVENT_GAME_STATE, MAPPER.valueToTree(theMindGame));
                    break;

                case EVENT_PLAY_CARD:
                    player = theMindGame.getPlayerBySessionId(session.getId()).orElseThrow(() -> new RuntimeException("unknown sessionId"));
                    PlayCardEvent playCardEvent = MAPPER.treeToValue(eventDto.getEvent(), PlayCardEvent.class);
                    LOGGER.debug("player {} is playing card {} for round {}", player.getName(), playCardEvent.getCard(), playCardEvent.getRound());

                    if (theMindGame.getRound() != playCardEvent.getRound()) {
                        LOGGER.warn("We got card {} for round {}, but we are on round {}, ignoring card",
                                playCardEvent.getCard(), playCardEvent.getRound(), theMindGame.getRound());
                        break;
                    }

                    if (theMindGame.getPlayedCards().contains(playCardEvent.getCard())) {
                        LOGGER.error("Card {} was already played, ignoring card", playCardEvent.getCard());
                        break;
                    }

                    break;

                default:
                    LOGGER.error("Unmanaged event: {}", eventDto.getType());
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("failed to deserialize/process message: {}", message, e);
        }
    }

    @OnClose
    public void myOnClose(final Session session, CloseReason cr) {
        LOGGER.debug("close session {} - {}", session.getId(), cr);
        Optional<Player> player = theMindGame.getPlayerBySessionId(session.getId());
        if (player.isPresent()) {
            player.get().setDisconnected();
            LOGGER.info("Player {} disconnected", player.get().getName());
            try {
                broadcastEvent(BoredEventType.EVENT_ERROR, MAPPER.valueToTree("Player disconnected: " + player.get().getName()));
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


    private void broadcastEvent(BoredEventType type, JsonNode event) {
        BoredEventDto eventDto = new BoredEventDto(type, event);
        theMindGame.getPlayers().stream().filter(Player::isConnected).forEach(player -> this.sendEventToPlayer(player, eventDto));
    }

    private void sendEventToPlayer(Player player, BoredEventDto eventDto) {
        try {
            sessions.get(player.getSocketId()).getBasicRemote().sendText(MAPPER.writeValueAsString(eventDto));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write event as String", e);
        }
    }
}