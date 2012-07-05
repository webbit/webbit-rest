package org.webbitserver.rest;

import java.util.Map;

/**
 * Abstract representation of an URI-template engine.
 */
public interface UriTemplateProcessor {
    String expand(String uriTemplate, String[] keyValuePairs);

    String expand(String uriTemplate, Map<String, Object> parameters);

    /**
     * Extracts variables from a uri, matching against an URI template
     *
     * @param uriTemplate template to match against
     * @param uri         the URI
     * @return the matched variables, or null if there was no match
     */
    Map<String, Object> extract(String uriTemplate, String uri);
}
