package com.boredgames.server;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @OnOpen
    public void myOnOpen(final Session session) throws IOException {
        LOGGER.info("connexion: {}", session.getUserProperties().get("javax.websocket.endpoint.remoteAddress"));
        session.getAsyncRemote().sendText("welcome");
    }

    @OnMessage
    public void myOnMsg(final Session session, String message) {
        LOGGER.info("message! {}", message);
        try {
            EventDto testEvent = MAPPER.readValue(message, EventDto.class);
            String received = MAPPER.writeValueAsString(testEvent);
            LOGGER.info("success! we got: {}", received);
            session.getBasicRemote().sendText(received);
        } catch (Exception e) {
            LOGGER.error("failed to deserialize message: {}", message, e);
        }
    }

    @OnClose
    public void myOnClose(final Session session, CloseReason cr) {
        LOGGER.info("close session {}", cr);
    }

    private static class EventDto {
        @JsonProperty
        private final String type;

        @JsonProperty
        private final MessageEvent event;

        @JsonCreator
        public EventDto(@JsonProperty("type") String type, @JsonProperty("event") MessageEvent event) {
            this.type = type;
            this.event = event;
        }
    }

    private static class MessageEvent {
        @JsonProperty
        private final String message;

        @JsonCreator
        public MessageEvent(@JsonProperty("message") String message) {
            this.message = message;
        }
    }
}