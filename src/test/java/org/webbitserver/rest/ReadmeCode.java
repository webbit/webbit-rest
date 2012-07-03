package org.webbitserver.rest;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.netty.NettyWebServer;

import java.util.concurrent.ExecutionException;

public class ReadmeCode {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        WebServer webServer = new NettyWebServer(9991);
        final Rest rest = new Rest(webServer);
        rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
                String name = rest.stringParam(req, "name");
                String petName = rest.stringParam(req, "petName");
                res.content(String.format("Name: %s\nPet: %s\n", name, petName)).end();
            }
        });
        webServer.start().get();
        System.out.println("Try this: curl -i localhost:9991/people/Mickey/pets/Pluto");
    }
}
