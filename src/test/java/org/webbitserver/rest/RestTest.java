package org.webbitserver.rest;

import org.junit.After;
import org.junit.Test;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.webbitserver.WebServers.createWebServer;
import static org.webbitserver.rest.Rest.params;

public class RestTest {
    private WebServer webServer = createWebServer(59504);
    private Rest rest = new Rest(webServer);

    @After
    public void die() throws IOException, InterruptedException, ExecutionException {
        webServer.stop().get();
    }

    @Test
    public void exposesTemplateUriParams() throws IOException, InterruptedException, ExecutionException {
        rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
                res.content(String.format("Name: %s\nPet: %s\n", params(req).get("name"), params(req).get("petName"))).end();
            }
        });
        webServer.start().get();
        String result = HttpClient.contents(HttpClient.httpGet(webServer, "/people/Mickey/pets/Pluto"));
        assertEquals("Name: Mickey\nPet: Pluto\n", result);
    }

    @Test
    public void exposesEmptyTemplateUriParamsWhenThereAreNone() throws IOException, InterruptedException, ExecutionException {
        rest.GET("/foo/bar", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
                res.content(String.valueOf(params(req).size())).end();
            }
        });
        webServer.start().get();
        String result = HttpClient.contents(HttpClient.httpGet(webServer, "/foo/bar"));
        assertEquals("0", result);
    }

    @Test
    public void providesEasyRedirectApi() throws IOException, InterruptedException, ExecutionException {

        rest.GET("/people/{name}/animals/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
                rest.redirect(res, "/people/{name}/pets/{petName}",
                        "name", rest.stringParam(req, "name"),
                        "petName", rest.stringParam(req, "petName")
                );
            }
        });

        rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
                res.content(String.format("Name: %s\nPet: %s\n", rest.stringParam(req, "name"), rest.stringParam(req, "petName"))).end();
            }
        });
        webServer.start().get();
        String result = HttpClient.contents(HttpClient.httpGet(webServer, "/people/Mickey/animals/Pluto"));
        assertEquals("Name: Mickey\nPet: Pluto\n", result);
    }
}
