package crud;

import org.webbitserver.WebServer;
import org.webbitserver.netty.NettyWebServer;
import org.webbitserver.rest.Rest;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        WebServer webServer = new NettyWebServer(9992);
        Rest rest = new Rest(webServer);
        rest.GET("/people/{name}/pets/{petName}", new JMustacheView("crud/views", "pets/show.html"));
        webServer.start().get();
        System.out.println("Try this: curl -i localhost:9992/people/Mickey/pets/Pluto");
    }
}
