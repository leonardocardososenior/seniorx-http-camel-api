package br.com.senior.seniorx.http.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.spi.PropertiesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeniorXHTTPRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeniorXHTTPRouteBuilder.class);

    protected final RouteBuilder builder;
    protected String host = "{{seniorx.host}}";
    protected boolean anonymous = false;
    protected String method;
    protected String domain;
    protected String service;
    protected PrimitiveType primitiveType;
    protected String primitive;

    public SeniorXHTTPRouteBuilder(RouteBuilder builder) {
        this.builder = builder;
    }

    public SeniorXHTTPRouteBuilder method(String method) {
        this.method = method;
        return this;
    }

    public SeniorXHTTPRouteBuilder host(String host) {
        this.host = host;
        return this;
    }

    public SeniorXHTTPRouteBuilder anonymous(boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    public SeniorXHTTPRouteBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public SeniorXHTTPRouteBuilder service(String service) {
        this.service = service;
        return this;
    }

    public SeniorXHTTPRouteBuilder primitiveType(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
        return this;
    }

    public SeniorXHTTPRouteBuilder primitive(String primitive) {
        this.primitive = primitive;
        return this;
    }

    public String getRoute(Exchange exchange) {
        Message message = exchange.getMessage();
        if (message.getHeader("route") != null) {
            return null;
        }
        /*
         * PropertiesComponent properties = exchange.getContext().getPropertiesComponent();
         * String resolvedHost = resolve(properties, host);
         * if (resolvedHost == null) {
         * return null;
         * }
         * if (resolvedHost.endsWith("/")) {
         * resolvedHost = resolvedHost.substring(0, resolvedHost.length() - 1);
         * }
         */
        String route = "rest:";
        route += method + ':';
        if (anonymous) {
            route += "/anonymous";
        }
        route += "/rest/" + domain //
                + '/' + service //
                + '/' + primitiveType.path //
                + '/' + primitive //
        // + "?host=" + resolvedHost //
        ;

        message.setHeader("route", route);
        message.setHeader("Content-Type", "application/json");

        LOGGER.info("Routing to {}", route);

        return route;
    }

    public ValueBuilder route() {
        return builder.method(this, "getRoute");
    }

    private String resolve(PropertiesComponent properties, String value) {
        if (value != null && value.startsWith("{{") && value.endsWith("}}")) {
            return properties.resolveProperty(value.substring(2, value.length() - 2)).orElse(null);
        }
        return value;
    }

}
