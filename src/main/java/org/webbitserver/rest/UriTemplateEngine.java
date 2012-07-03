package org.webbitserver.rest;

import java.util.Map;

/**
 * Abstract representation of an URI-template engine.
 */
public interface UriTemplateEngine {
    String expand(String uriTemplate, String[] keyValuePairs);

    String expand(String uriTemplate, Map<String, Object> parameters);

    Map<String, Object> extract(String uriTemplate, String uri);
}
