package org.webbitserver.rest;

import org.webbitserver.EventSourceHandler;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.WebSocketHandler;
import org.webbitserver.rest.furi.FuriProcessor;

import java.util.Map;

/**
 * Sinatra-style API around Webbit. Useful for defining RESTful APIs. Paths are defined according to the
 * <a href="http://tools.ietf.org/html/draft-gregorio-uritemplate-07">uritemplate</a> specification.
 */
public class Rest {
    public static final String URI_TEMPLATE_VARIABLES = "URI_TEMPLATE_VARIABLES";

    private final WebServer webServer;
    private final UriTemplateProcessor uriTemplateProcessor;

    public Rest(WebServer webServer) {
        this(webServer, new FuriProcessor());
    }

    public Rest(WebServer webServer, UriTemplateProcessor uriTemplateProcessor) {
        this.webServer = webServer;
        this.uriTemplateProcessor = uriTemplateProcessor;
    }

    // 1 handler

    public Rest GET(String uriTemplate, HttpHandler httpHandler) {
        return GET(uriTemplate, httpHandler, null, null);
    }

    public Rest GET(String uriTemplate, EventSourceHandler eventSourceHandler) {
        return GET(uriTemplate, null, eventSourceHandler, null);
    }

    public Rest GET(String uriTemplate, WebSocketHandler webSocketHandler) {
        return GET(uriTemplate, null, null, webSocketHandler);
    }

    // 2 handlers

    public Rest GET(String uriTemplate, HttpHandler httpHandler, EventSourceHandler eventSourceHandler) {
        return GET(uriTemplate, httpHandler, eventSourceHandler, null);
    }

    public Rest GET(String uriTemplate, HttpHandler httpHandler, WebSocketHandler webSocketHandler) {
        return GET(uriTemplate, httpHandler, null, webSocketHandler);
    }

    public Rest GET(String uriTemplate, EventSourceHandler eventSourceHandler, WebSocketHandler webSocketHandler) {
        return GET(uriTemplate, null, eventSourceHandler, webSocketHandler);
    }

    // 3 handlers

    public Rest GET(String uriTemplate, HttpHandler httpHandler, EventSourceHandler eventSourceHandler, WebSocketHandler webSocketHandler) {
        return verbHandler("GET", uriTemplate, httpHandler, eventSourceHandler, webSocketHandler);
    }

    public Rest PUT(String uriTemplate, HttpHandler httpHandler) {
        return verbHandler("PUT", uriTemplate, httpHandler, null, null);
    }

    public Rest POST(String uriTemplate, HttpHandler httpHandler) {
        return verbHandler("POST", uriTemplate, httpHandler, null, null);
    }

    public Rest DELETE(String uriTemplate, HttpHandler httpHandler) {
        return verbHandler("DELETE", uriTemplate, httpHandler, null, null);
    }

    public Rest HEAD(String uriTemplate, HttpHandler httpHandler) {
        return verbHandler("HEAD", uriTemplate, httpHandler, null, null);
    }

    private Rest verbHandler(String verb, String uriTemplate, HttpHandler httpHandler, EventSourceHandler eventSourceHandler, WebSocketHandler webSocketHandler) {
        HttpHandler protocolDetectingHandler = new ProtocolDetectingHandler(httpHandler, eventSourceHandler, webSocketHandler);
        HttpHandler httpVerbHandler = new HttpVerbHandler(verb, protocolDetectingHandler);
        HttpHandler uriTemplateHandler = new UriTemplateHandler(uriTemplate, httpVerbHandler, uriTemplateProcessor);
        webServer.add(uriTemplateHandler);
        return this;
    }

    /**
     * Get the resolved URI-template variables associated with {@code request}.
     *
     * @param request the request holding the params
     * @param name    named segment from the uri-template
     * @return the parameter value
     */
    public static Object param(HttpRequest request, String name) {
        return params(request).get(name);
    }

    public static String stringParam(HttpRequest request, String name) {
        Object param = param(request, name);
        return param == null ? null : param.toString();
    }

    public static Integer intParam(HttpRequest request, String name) {
        String param = stringParam(request, name);
        return param == null ? null : Integer.valueOf(param);
    }

    /**
     * Get the resolved URI-template variables associated with {@code request}.
     *
     * @param request the request holding the params
     * @return an object with all resolved variables
     */
    public static Map<String, Object> params(HttpRequest request) {
        return (Map<String, Object>) request.data(URI_TEMPLATE_VARIABLES);
    }

    /**
     * Perform a 302 Redirect
     *
     * @param response      the response to redirect
     * @param uriTemplate   where to redirect
     * @param keyValuePairs Example: ["name", "Mickey", "pet", "Pluto"]
     */
    public void redirect(HttpResponse response, String uriTemplate, String... keyValuePairs) {
        redirect(response, uriTemplateProcessor.expand(uriTemplate, keyValuePairs));
    }

    /**
     * Perform a 302 Redirect
     *
     * @param response    the response to redirect
     * @param uriTemplate where to redirect
     * @param parameters  Example: {"name": "Mickey", "pet": "Pluto"}
     */
    public void redirect(HttpResponse response, String uriTemplate, Map<String, Object> parameters) {
        redirect(response, uriTemplateProcessor.expand(uriTemplate, parameters));
    }

    public void redirect(HttpResponse response, String uri) {
        response.header("Location", uri).status(302).end();
    }
}
