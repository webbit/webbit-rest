# Webbit-REST

Webbit-REST is a small [Sinatra](http://www.sinatrarb.com/)-inspired API for the [Webbit](https://github.com/webbit/webbit) web server.
It is based on [RFC 6570](http://tools.ietf.org/html/rfc6570) and the excellent [wo-furi](http://code.google.com/p/wo-furi/) library.

Webbit-REST also adds support for easy templating (currently only supporting [JMustache](https://github.com/samskivert/jmustache)). See jmustachedemo for an example.

## Sample usage:

```java
WebServer webServer = new NettyWebServer(9991);
Rest rest = new Rest(webServer);
rest.GET("/people/{name}/pets/{petName}", new HttpHandler() {
    @Override
    public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
        String name = Rest.param(req, "name");
        String petName = Rest.param(req, "petName");
        res.content(String.format("Name: %s\nPet: %s\n", name, petName)).end();
    }
});
webServer.start().get();
System.out.println("Try this: curl -i localhost:9991/people/Mickey/pets/Pluto");
```

## Redirecting:

```java
rest.GET("/people/{name}/animals/{petName}", new HttpHandler() {
    @Override
    public void handleHttpRequest(HttpRequest req, HttpResponse res, HttpControl ctl) {
        Rest.redirect(res, "/people/{name}/pets/{petName}",
                "name", param(req, "name"),
                "petName", param(req, "petName")
        );
    }
});
```

## Installation

### Maven

```xml
<dependency>
    <groupId>org.webbitserver</groupId>
    <artifactId>webbit-rest</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Not Maven

https://oss.sonatype.org/content/repositories/releases/org/webbitserver/webbit-rest/0.2.0/webbit-rest-0.2.0.jar
