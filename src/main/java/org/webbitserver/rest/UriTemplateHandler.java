package org.webbitserver.rest;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import java.net.URI;
import java.util.Map;

class UriTemplateHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final String uriTemplate;
    private final UriTemplateProcessor uriTemplateProcessor;

    public UriTemplateHandler(String uriTemplate, HttpHandler httpHandler, UriTemplateProcessor uriTemplateProcessor) {
        this.uriTemplate = uriTemplate;
        this.uriTemplateProcessor = uriTemplateProcessor;
        this.httpHandler = httpHandler;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        String path = URI.create(request.uri()).getPath();

        Map<String, Object> variables = uriTemplateProcessor.extract(uriTemplate, path);
        if (variables != null) {
            request.data(Rest.URI_TEMPLATE_VARIABLES, variables);
            httpHandler.handleHttpRequest(request, response, control);
        } else {
            control.nextHandler();
        }
    }
}
