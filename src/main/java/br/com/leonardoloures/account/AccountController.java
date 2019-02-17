package br.com.leonardoloures.account;

import br.com.leonardoloures.Constants;
import com.strategicgains.hyperexpress.builder.DefaultTokenResolver;
import com.strategicgains.hyperexpress.builder.DefaultUrlBuilder;
import com.strategicgains.hyperexpress.builder.UrlBuilder;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.syntaxe.ValidationEngine;
import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;
import org.restexpress.query.QueryFilters;
import org.restexpress.query.QueryOrders;
import org.restexpress.query.QueryRanges;

import java.util.List;

import static com.strategicgains.repoexpress.adapter.Identifiers.UUID;

public class AccountController {
    private static final UrlBuilder LOCATION_BUILDER = new DefaultUrlBuilder();
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        super();
        this.accountService = accountService;
    }

    public AccountEntity create(Request request, Response response) 	{
        AccountEntity entity = request.getBodyAs(AccountEntity.class, "Resource details not provided");
        AccountEntity saved = accountService.create(entity);

        // Construct the response for create...
        response.setResponseCreated();
        // Include the Location header...
        String locationPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.SINGLE_ACCOUNT);
        response.addLocationHeader(LOCATION_BUILDER.build(locationPattern, new DefaultTokenResolver()));
        // Return the newly-created item...
        return saved;
    }

    public AccountEntity read(Request request, Response response)
    {
        String id = request.getHeader(Constants.Url.ACCOUNT_ID, "No Account ID supplied");
        Identifier identifier = new Identifier(id);
        AccountEntity accountEntity = accountService.read(identifier);
        return accountEntity;
    }

    public List<AccountEntity> readAll(Request request, Response response)
    {
        QueryFilter filter = QueryFilters.parseFrom(request);
        QueryOrder order = QueryOrders.parseFrom(request);
        QueryRange range = QueryRanges.parseFrom(request, 20);
        List<AccountEntity> entities = accountService.readAll(filter, range, order);
        response.setCollectionResponse(range, entities.size(), accountService.count(filter));

        return entities;
    }

    public void update(Request request, Response response)
    {
        String id = request.getHeader(Constants.Url.ACCOUNT_ID);
        AccountEntity accountEntity = request.getBodyAs(AccountEntity.class, "Account details not provided");

        // Can't change the blod ID via update.
        accountEntity.setId(new Identifier(id));

        ValidationEngine.validateAndThrow(accountEntity);
        accountService.update(accountEntity);
        response.setResponseNoContent();
    }

    public void delete(Request request, Response response)
    {
        String id = request.getHeader(Constants.Url.ACCOUNT_ID, "No Blog ID supplied");
        accountService.delete(new Identifier(id));
        response.setResponseNoContent();
    }
}
