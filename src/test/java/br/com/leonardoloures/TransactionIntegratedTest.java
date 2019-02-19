package br.com.leonardoloures;

import br.com.leonardoloures.account.AccountEntity;
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
        AccountEntity source = new AccountEntity();
        source.setNome("Source");
        source.addBalance(new Double(500));
        source.setAccountType(0);
        given().body(source)
                .when().post(BASE_URL + "/account")
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().assertThat()
                .body("balance", equalTo(source.getBalance().floatValue()))
                .body("nome", equalTo(source.getNome()));

        AccountEntity destination = new AccountEntity();
        destination.setNome("Destination");
        destination.addBalance(new Double(500));
        destination.setAccountType(0);
        given().body(destination)
                .when().post(BASE_URL + "/account")
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().assertThat()
                .body("balance", equalTo(destination.getBalance().floatValue()))
                .body("nome", equalTo(destination.getNome()));

        given().when().get(BASE_URL + "/" + Constants.Url.PENDING_TRANSACTION + "/5c6a034e2682593728028e75")
                .then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

}
