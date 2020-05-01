package com.boredgames.server;

import com.boredgames.server.events.BoredEventDto;
import com.boredgames.server.events.BoredEventType;
import com.boredgames.server.events.GameStatusEvent;
import com.boredgames.server.events.TestEvent;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import java.io.IOException;

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
        LOGGER.info("connexion: {}", session.getUserProperties().get("javax.websocket.endpoint.remoteAddress"));
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

                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("failed to deserialize message: {}", message, e);
        }

        LOGGER.error("All deserialization failed");
    }

    @OnClose
    public void myOnClose(final Session session, CloseReason cr) {
        LOGGER.info("close session {}", cr);
    }

}