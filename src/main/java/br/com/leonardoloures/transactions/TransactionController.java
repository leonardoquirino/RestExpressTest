package br.com.leonardoloures.transactions;

import br.com.leonardoloures.Constants;
import com.strategicgains.hyperexpress.builder.DefaultTokenResolver;
import com.strategicgains.hyperexpress.builder.DefaultUrlBuilder;
import com.strategicgains.hyperexpress.builder.UrlBuilder;
import com.strategicgains.repoexpress.domain.Identifier;
import io.netty.handler.codec.http.HttpMethod;
import org.bson.types.ObjectId;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;
import org.restexpress.query.QueryFilters;
import org.restexpress.query.QueryOrders;
import org.restexpress.query.QueryRanges;

import java.util.List;

public class TransactionController {
    private static final UrlBuilder LOCATION_BUILDER = new DefaultUrlBuilder();
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        super();
        this.transactionService = transactionService;
    }

    public TransactionEntity create(Request request, Response response) {
        TransactionDTO transactionDTO = request.getBodyAs(TransactionDTO.class, "Resource details not provided");
        TransactionEntity entity = TransactionEntity.fromDTO(transactionDTO);
        TransactionEntity saved = transactionService.create(entity);

        // Construct the response for create...
        response.setResponseCreated();
        // Include the Location header...
        String locationPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.SINGLE_ACCOUNT);
        response.addLocationHeader(LOCATION_BUILDER.build(locationPattern, new DefaultTokenResolver()));
        // Return the newly-created item...
        return saved;
    }

    public TransactionEntity read(Request request, Response response) {
        String id = request.getHeader(Constants.Url.ACCOUNT_ID, "No Account ID supplied");
        Identifier identifier = new Identifier(new ObjectId(id));
        TransactionEntity transactionEntity = transactionService.read(identifier);
        return transactionEntity;
    }

    public TransactionEntity readInitial(Request request, Response response) {

        TransactionEntity transactionEntity = transactionService.readOldestTransactionByStatus(TransactionStatusEnum.INITIAL);
        return transactionEntity;
    }

    public TransactionEntity readTransactionResult(Request request, Response response) {
        String id = request.getHeader(Constants.Url.TRANSACTION_ID, "No Account ID supplied");
        Identifier identifier = new Identifier(new ObjectId(id));
        TransactionEntity transactionEntity = transactionService.read(identifier);
        transactionEntity = transactionService.transfer(transactionEntity);
        return transactionEntity;
    }

    public List<TransactionEntity> readAll(Request request, Response response) {
        QueryFilter filter = QueryFilters.parseFrom(request);
        QueryOrder order = QueryOrders.parseFrom(request);
        QueryRange range = QueryRanges.parseFrom(request, 20);
        List<TransactionEntity> entities = transactionService.readAll(filter, range, order);
        response.setCollectionResponse(range, entities.size(), transactionService.count(filter));

        return entities;
    }
}
