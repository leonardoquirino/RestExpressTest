package br.com.leonardoloures;

import io.netty.handler.codec.http.HttpMethod;

import org.restexpress.RestExpress;

public abstract class Routes {
    public static void define(Configuration config, RestExpress server) {

        server.uri(Constants.Url.ACCOUNT + "/{" + Constants.Url.ACCOUNT_ID + "}",
                config.getAccountController())
                .method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
                .name(Constants.Routes.SINGLE_ACCOUNT);

        server.uri(Constants.Url.ACCOUNT , config.getAccountController())
                .action("readAll", HttpMethod.GET)
                .method(HttpMethod.POST)
                .name(Constants.Routes.ACCOUNT_COLLECTION);

        server.uri(Constants.Url.PENDING_TRANSACTION,
                config.getAccountController())
                .method(HttpMethod.GET)
                .name(Constants.Routes.SINGLE_TRANSACTION);
        // or REGEX matching routes...
        // server.regex("/some.regex", config.getRouteController());
    }
}
