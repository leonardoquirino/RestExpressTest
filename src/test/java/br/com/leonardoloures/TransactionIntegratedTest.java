package br.com.leonardoloures;

import br.com.leonardoloures.account.AccountEntity;
import br.com.leonardoloures.transactions.TransactionDTO;
import br.com.leonardoloures.transactions.TransactionStatusEnum;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.*;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class TransactionIntegratedTest {

    /**
     * The REST server that handles the test calls.
     */
    private static Server server;
    private HttpClient httpClient;
    private static final String BASE_URL = "http://localhost:8081";

    @BeforeClass
    public static void beforeClass() throws Exception {
        String[] env = {"dev"};
        Configuration configuration = Main.loadEnvironment(env);
        server = new Server(configuration);
    }

    @AfterClass
    public static void afterClass() {
        server.shutdown();
    }

    @Before
    public void beforeEach() {
        httpClient = new DefaultHttpClient();
    }

    @After
    public void afterEach() {
        httpClient = null;
    }

    @Test
    public void getTransactionRequestFail() throws IOException {
        given().when().get(BASE_URL + "/" + Constants.Url.PENDING_TRANSACTION + "/5c6a034e2682593728028e75")
                .then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void getPendingTransactionRequest() throws IOException {
        Response response;

        AccountEntity source = new AccountEntity();
        source.setNome("Source");
        source.addBalance(new Double(500));
        source.setAccountType(0);
        response = given().body(source)
                .when().post(BASE_URL + "/account")
                .then().contentType(ContentType.JSON).extract().response();
        String sourceId = response.path("id");

        AccountEntity destination = new AccountEntity();
        destination.setNome("Destination");
        destination.addBalance(new Double(500));
        destination.setAccountType(0);
        response = given().body(destination)
                .when().post(BASE_URL + "/account")
                .then().contentType(ContentType.JSON).extract().response();

        String destId = response.path("id");

        TransactionDTO transaction = new TransactionDTO();
        transaction.setSource(sourceId);
        transaction.setDestination(destId);
        transaction.setValue(100.0d);
        response = given().body(transaction)
                .when().post(BASE_URL + "/transaction")
                .then().contentType(ContentType.JSON).extract().response();

        transaction.setId(response.path("id"));

        given().when().get(BASE_URL + "/" + Constants.Url.INITIAL_TRANSACTION)
                .then().statusCode(HttpStatus.SC_OK)
                .and().assertThat()
                .body("value", equalTo(transaction.getValue().floatValue()))
                .body("status", equalTo(TransactionStatusEnum.INITIAL.name()));

        given().when().get(BASE_URL + "/" + Constants.Url.TRANSACTION + "/" + transaction.getId())
                .then().statusCode(HttpStatus.SC_OK)
                .and().assertThat()
                .body("status", equalTo(TransactionStatusEnum.DONE.name()));


        given().when().get(BASE_URL + "/" + Constants.Url.ACCOUNT + "/" + sourceId)
                .then().statusCode(HttpStatus.SC_OK)
                .and().assertThat()
                .body("balance", equalTo( new Float(source.getBalance() - transaction.getValue())));

        given().when().get(BASE_URL + "/" + Constants.Url.ACCOUNT + "/" + destId)
                .then().statusCode(HttpStatus.SC_OK)
                .and().assertThat()
                .body("balance", equalTo( new Float (source.getBalance() + transaction.getValue())));
    }

}
