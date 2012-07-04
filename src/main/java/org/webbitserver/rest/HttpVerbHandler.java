package org.webbitserver.rest;

import org.webbitserver.EventSourceHandler;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.handler.HttpToEventSourceHandler;

class HttpVerbHandler implements HttpHandler {
    private final String verb;
    private final HttpHandler httpHandler;
    private final HttpHandler eventSourceHandler;

    public HttpVerbHandler(String verb, HttpHandler httpHandler, EventSourceHandler eventSourceHandler) {
        this.verb = verb;
        this.httpHandler = httpHandler;
        this.eventSourceHandler = new HttpToEventSourceHandler(eventSourceHandler);
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        if (request.method().equalsIgnoreCase(verb)) {
            if (acceptsEventStream(request)) {
                eventSourceHandler.handleHttpRequest(request, response, control);
            } else {
                httpHandler.handleHttpRequest(request, response, control);
            }
        } else {
            control.nextHandler();
        }
    }

    private static boolean acceptsEventStream(HttpRequest request) {
        String accept = request.header("Accept");
        return accept != null && accept.contains("text/event-stream");
    }
}
