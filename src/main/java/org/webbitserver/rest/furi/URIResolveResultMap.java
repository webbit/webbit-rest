package org.webbitserver.rest.furi;

import org.weborganic.furi.URIResolveResult;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class URIResolveResultMap implements Map<String, Object> {
    private final URIResolveResult resolveResult;

    public URIResolveResultMap(URIResolveResult resolveResult) {
        this.resolveResult = resolveResult;
    }

    @Override
    public int size() {
        return resolveResult.names().size();
    }

    @Override
    public boolean isEmpty() {
        return resolveResult.names().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return resolveResult.names().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Object get(Object key) {
        return key instanceof String ? resolveResult.get((String) key) : null;
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return resolveResult.names();
    }

    @Override
    public Collection<Object> values() {
        Collection<Object> values = new ArrayList<Object>();
        for (String name : resolveResult.names()) {
            values.add(resolveResult.get(name));
        }
        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> result = new HashSet<Entry<String, Object>>();
        for (String name : resolveResult.names()) {
            result.add(new AbstractMap.SimpleEntry<String, Object>(name, resolveResult.get(name)));
        }
        return result;
    }
}
