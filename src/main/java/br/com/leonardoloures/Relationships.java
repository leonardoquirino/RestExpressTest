package br.com.leonardoloures;

import br.com.leonardoloures.account.AccountEntity;
import br.com.leonardoloures.transactions.TransactionEntity;
import com.strategicgains.hyperexpress.HyperExpress;
import com.strategicgains.hyperexpress.RelTypes;
import org.restexpress.RestExpress;
import org.restexpress.common.exception.ConfigurationException;

import java.util.Map;

public abstract class Relationships
{
	private static Map<String, String> ROUTES;

	public static void define(RestExpress server)
	{
		ROUTES = server.getRouteUrlsByName();

		HyperExpress.relationships()
		.forCollectionOf(AccountEntity.class)
			.rel(RelTypes.SELF, href(Constants.Routes.ACCOUNT_COLLECTION))
				.withQuery("limit={limit}")
				.withQuery("offset={offset}")
			.rel(RelTypes.NEXT, href(Constants.Routes.ACCOUNT_COLLECTION) + "?offset={nextOffset}")
				.withQuery("limit={limit}")
				.optional()
			.rel(RelTypes.PREV, href(Constants.Routes.ACCOUNT_COLLECTION) + "?offset={prevOffset}")
				.withQuery("limit={limit}")
				.optional()
		.forClass(AccountEntity.class)
			.rel(RelTypes.SELF, href(Constants.Routes.SINGLE_ACCOUNT))
		.forClass(TransactionEntity.class)
			.rel(RelTypes.SELF, href(Constants.Routes.SINGLE_ACCOUNT));

	}

	private static String href(String name)
	{
		String href = ROUTES.get(name);
		if (href == null) throw new ConfigurationException("Route name not found: " + name);
		return href;
	}
}
