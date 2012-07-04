package jmustachedemo;

import org.webbitserver.EventSourceConnection;
import org.webbitserver.EventSourceHandler;
import org.webbitserver.EventSourceMessage;
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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.webbitserver.rest.Rest.params;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Executor webThread = Executors.newSingleThreadExecutor();
        final WebServer webServer = new NettyWebServer(webThread, 9992);
        Rest rest = new Rest(webServer);
        final AbstractResourceHandler mustache = JMustacheEngine.fromStaticFiles("src/test/resources/jmustachedemo/views");
        PetEventSourceHandler petEventSourceHandler = new PetEventSourceHandler(webThread);
        rest.GET("/people/{name}/pets/{petName}", new PetMustacheHandler(mustache), petEventSourceHandler);
        webServer.start().get();
        System.out.println("Try this: curl -i localhost:9992/people/Mickey/pets/Pluto");
        System.out.println("Or this:  curl -i -H \"Accept: text/event-stream\" localhost:9992/people/Mickey/pets/Pluto");
    }

    private static class PetMustacheHandler implements HttpHandler {
        private final AbstractResourceHandler mustache;

        public PetMustacheHandler(AbstractResourceHandler mustache) {
            this.mustache = mustache;
        }

        @Override
        public void handleHttpRequest(final HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
            // Set the template context. For this example, simply use the extracted uri template variables.
            req.data(TemplateEngine.TEMPLATE_CONTEXT, new HashMap<String, Object>() {{
                put("uri", params(req));
            }});

            // render our template
            req.uri("pets/show.html");
            mustache.handleHttpRequest(req, res, ctl);
        }
    }

    private static class PetEventSourceHandler implements EventSourceHandler {
        private final Set<EventSourceConnection> connections = new HashSet<EventSourceConnection>();

        public PetEventSourceHandler(final Executor webThread) {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    webThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(Thread.currentThread());
                            String now = new Date().toString();
                            for (EventSourceConnection connection : connections) {
                                String petName = (String) params(connection.httpRequest()).get("petName");
                                connection.send(new EventSourceMessage(petName + " " + now));
                            }
                        }
                    });
                }
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }

        @Override
        public void onOpen(EventSourceConnection connection) throws Exception {
            connections.add(connection);
        }

        @Override
        public void onClose(EventSourceConnection connection) throws Exception {
            connections.remove(connection);
        }
    }
}
