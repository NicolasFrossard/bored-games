package com.boredgames.server;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

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

    private Logger log;

    public BoredGamesWsServer() {
        this.log = Logger.getLogger("global");
    }

    @OnOpen
    public void myOnOpen(final Session session) throws IOException {
        log.info("connexion de <" + session.getUserProperties().get("javax.websocket.endpoint.remoteAddress") + ">");
        session.getAsyncRemote().sendText("welcome");
    }

    @OnMessage
    public void myOnMsg(final Session session, String message) {
        log.info("message! <" + message + ">");
        session.getAsyncRemote().sendText(message.toUpperCase());
    }

    @OnClose
    public void myOnClose(final Session session, CloseReason cr) {
        log.info("close session <" + cr.toString() + ">");
    }
}