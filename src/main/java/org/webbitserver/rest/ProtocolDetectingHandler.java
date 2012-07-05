package org.webbitserver.rest;

import org.webbitserver.EventSourceHandler;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebSocketHandler;
import org.webbitserver.handler.HttpToEventSourceHandler;
import org.webbitserver.handler.HttpToWebSocketHandler;

class ProtocolDetectingHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final HttpHandler webSocketHandler;
    private final HttpHandler eventSourceHandler;

    public ProtocolDetectingHandler(HttpHandler httpHandler, EventSourceHandler eventSourceHandler, WebSocketHandler webSocketHandler) {
        this.httpHandler = httpHandler;
        this.eventSourceHandler = new HttpToEventSourceHandler(eventSourceHandler);
        this.webSocketHandler = new HttpToWebSocketHandler(webSocketHandler);
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        if (isWebSocket(request)) {
            if (webSocketHandler != null) {
                webSocketHandler.handleHttpRequest(request, response, control);
            } else {
                response.status(500).end();
            }
        } else if (isEventSource(request)) {
            if (eventSourceHandler != null) {
                eventSourceHandler.handleHttpRequest(request, response, control);
            } else {
                response.status(500).end();
            }
        } else if (httpHandler != null) {
            httpHandler.handleHttpRequest(request, response, control);
        } else {
            response.status(500).end();
        }
    }

    private boolean isWebSocket(HttpRequest request) {
        return request.hasHeader("Sec-WebSocket-Version");
    }

    private static boolean isEventSource(HttpRequest request) {
        String accept = request.header("Accept");
        return accept != null && accept.contains("text/event-stream");
    }
}
