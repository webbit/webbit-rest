package org.webbitserver.rest;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.weborganic.furi.URIPattern;
import org.weborganic.furi.URIResolveResult;
import org.weborganic.furi.URIResolver;

import java.net.URI;

class UriTemplateHandler implements HttpHandler {

    private final URIPattern uriPattern;
    private final HttpHandler httpHandler;
    public static final String URI_MATCH = "URI_MATCH";

    public UriTemplateHandler(String uriTemplate, HttpHandler httpHandler) {
        this.uriPattern = new URIPattern(uriTemplate);
        this.httpHandler = httpHandler;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        String path = URI.create(request.uri()).getPath();
        URIResolver uriResolver = new URIResolver(path);
        final URIResolveResult resolveResult = uriResolver.resolve(uriPattern);
        if (resolveResult.getStatus() == URIResolveResult.Status.RESOLVED) {
            request.data(URI_MATCH, new UriMatch() {
                @Override
                public String get(String name) {
                    return (String) resolveResult.get(name);
                }
            });
            httpHandler.handleHttpRequest(request, response, control);
        } else {
            control.nextHandler();
        }
    }
}
