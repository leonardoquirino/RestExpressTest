package br.com.leonardoloures;

import io.netty.handler.codec.http.HttpMethod;

import org.restexpress.RestExpress;

public abstract class Routes
{
	public static void define(Configuration config, RestExpress server)
	{
		// TODO: Your routes here...
		server.uri("/bank/account/{accountId}.{format}", config.getAccountController())
		    .method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
		    .name(Constants.Routes.SINGLE_ACCOUNT);

		server.uri("/artista", config.getAccountController())
				.action("readAll", HttpMethod.GET)
				.method(HttpMethod.POST)
				.name(Constants.Routes.ACCOUNT_COLLECTION);

		// or REGEX matching routes...
		// server.regex("/some.regex", config.getRouteController());
	}
}
