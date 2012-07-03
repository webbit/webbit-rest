package jmustachedemo;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.handler.AbstractResourceHandler;
import org.webbitserver.handler.TemplateEngine;
import org.webbitserver.netty.NettyWebServer;
import org.webbitserver.rest.Rest;
import org.webbitserver.rest.jmustache.JMustacheEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        WebServer webServer = new NettyWebServer(9992);
        Rest rest = new Rest(webServer);
        final AbstractResourceHandler mustache = JMustacheEngine.fromStaticFiles("src/test/resources/crud/views");
        rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
            @Override
            public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
                // Set the template context. For this example, simply use the extracted uri template variables.
                Map<String, Object> context = new HashMap<String, Object>();
                context.put("uri", req.data(Rest.URI_TEMPLATE_VARIABLES));
                req.data(TemplateEngine.TEMPLATE_CONTEXT, context);

                // render our template
                req.uri("pets/show.html");
                mustache.handleHttpRequest(req, res, ctl);
            }
        });
        webServer.start().get();
        System.out.println("Try this: curl -i localhost:9992/people/Mickey/pets/Pluto");
    }
}
