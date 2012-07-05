package org.webbitserver.rest.furi;

import org.webbitserver.rest.UriTemplateProcessor;
import org.weborganic.furi.Parameters;
import org.weborganic.furi.URIParameters;
import org.weborganic.furi.URIPattern;
import org.weborganic.furi.URIResolveResult;
import org.weborganic.furi.URIResolver;
import org.weborganic.furi.URITemplate;

import java.util.Map;

public class FuriProcessor implements UriTemplateProcessor {
    @Override
    public String expand(String uriTemplate, String[] keyValuePairs) {
        return URITemplate.expand(uriTemplate, params(keyValuePairs));
    }

    @Override
    public String expand(String uriTemplate, Map<String, Object> parameters) {
        return URITemplate.expand(uriTemplate, params(parameters));
    }

    @Override
    public Map<String, Object> extract(String uriTemplate, String uri) {
        URIResolver uriResolver = new URIResolver(uri);
        URIPattern uriPattern = new URIPattern(uriTemplate);
        URIResolveResult resolveResult = uriResolver.resolve(uriPattern);
        if (resolveResult.getStatus() == URIResolveResult.Status.RESOLVED) {
            return new URIResolveResultMap(resolveResult);
        } else {
            return null;
        }
    }

    private Parameters params(String[] parameters) {
        Parameters params = new URIParameters();
        for (int i = 0; i < parameters.length; i += 2) {
            params.set(parameters[i], parameters[i + 1]);
        }
        return params;
    }

    private Parameters params(Map<String, Object> parameters) {
        Parameters params = new URIParameters();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            params.set(entry.getKey(), entry.getValue().toString());
        }
        return params;
    }
}
