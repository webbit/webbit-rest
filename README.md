# Webbit-REST

Webbit-REST is a small [Sinatra](http://www.sinatrarb.com/)-inspired API for [Webbit](https://github.com/webbit/webbit).
It is based on [RFC6750](http://tools.ietf.org/html/rfc6570) and the excellent [wo-furi](http://code.google.com/p/wo-furi/) library.

Sample usage:

```java
WebServer webServer = new NettyWebServer(9991);
Rest rest = new Rest(webServer);
rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
    @Override
    public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
        String name = Rest.param(req, "name");
        String petName = Rest.param(req, "petName");
        res.content(String.format("Name: %s\nPet: %s\n", name, petName)).end();
    }
});
webServer.start().get();
System.out.println("Try this: curl -i localhost:9991/people/Mickey/pets/Pluto");
```

Redirecting:

```java
rest.GET("/people/{name}/animals/{petName}", new HttpHandler() {
    @Override
    public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) throws Exception {
        Rest.redirect(res, "/people/{name}/pets/{petName}",
                "name", param(req, "name"),
                "petName", param(req, "petName")
        );
    }
});
```