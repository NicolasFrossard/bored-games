package com.boredgames.server;

import com.boredgames.server.resources.HelloResource;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.websockets.WebsocketBundle;

import java.io.IOException;

public class BoredGamesApplication extends Application<BoredGamesConfiguration> {

    public static void main(final String[] args) throws Exception {
        new BoredGamesApplication().run(args);
    }

    @Override
    public String getName() {
        return "BoredGames";
    }

    @Override
    public void initialize(final Bootstrap<BoredGamesConfiguration> bootstrap) {
        //bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html", "static"));
        bootstrap.addBundle(new AssetsBundle("/dist", "/", "index.html", "static"));
        bootstrap.addBundle(new WebsocketBundle(BoredGamesWsServer.class));
    }

    @Override
    public void run(final BoredGamesConfiguration configuration,
                    final Environment environment) {
        final HelloResource helloResource = new HelloResource(
                configuration.getTemplate(),
                configuration.getDefaultName());

        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(helloResource);
    }
}


