package com.boredgames.server;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.Console;
import java.io.IOException;
import java.util.logging.Logger;

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
        session.getAsyncRemote().sendText(message.toUpperCase());

        try {
            TestEvent testEvent = MAPPER.readValue(message, TestEvent.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to deserialize message: {}", message);
        }
    }

    @OnClose
    public void myOnClose(final Session session, CloseReason cr) {
        LOGGER.info("close session {}", cr);
    }

    private static class TestEvent {
        @JsonProperty
        private final String type;

        @JsonProperty
        private final String message;

        public TestEvent(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }
}