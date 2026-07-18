package com.hms.api_gateway.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Lets us add extra headers (the resolved user identity) onto an incoming
 * request before it is forwarded to a downstream service.
 */
public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> extraHeaders = new HashMap<>();

    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void putHeader(String name, String value) {
        extraHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String value = extraHeaders.get(name);
        return value != null ? value : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (extraHeaders.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(extraHeaders.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(extraHeaders.keySet());
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            names.add(original.nextElement());
        }
        return Collections.enumeration(names);
    }
}
