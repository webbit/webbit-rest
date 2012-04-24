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
import static org.webbitserver.rest.Rest.param;
import static org.webbitserver.rest.Rest.params;
import static org.webbitserver.rest.Rest.redirect;

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
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
                res.content(String.format("Name: %s\nPet: %s\n", params(req).get("name"), params(req).get("petName"))).end();
            }
        });
        webServer.start().get();
        String result = HttpClient.contents(HttpClient.httpGet(webServer, "/people/Mickey/pets/Pluto"));
        assertEquals("Name: Mickey\nPet: Pluto\n", result);
    }

    @Test
    public void providesEasyRedirectApi() throws IOException, InterruptedException, ExecutionException {

        rest.GET("/people/{name}/animals/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
                Rest.redirect(res, "/people/{name}/pets/{petName}",
                        "name", param(req, "name"),
                        "petName", param(req, "petName")
                );
            }
        });

        rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
                res.content(String.format("Name: %s\nPet: %s\n", param(req, "name"), param(req, "petName"))).end();
            }
        });
        webServer.start().get();
        String result = HttpClient.contents(HttpClient.httpGet(webServer, "/people/Mickey/animals/Pluto"));
        assertEquals("Name: Mickey\nPet: Pluto\n", result);
    }
}
