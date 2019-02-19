package br.com.leonardoloures;

public class Constants {
    /**
     * These define the URL parmaeters used in the route definition strings (e.g. '{userId}').
     */
    public class Url {
        public static final String ACCOUNT_ID = "accountId";
        public static final String ACCOUNT = "account";

        public static final String PENDING_TRANSACTION = "transaction/pending";
    }

    /**
     * These define the route names used in naming each route definitions.  These names are used
     * to retrieve URL patterns within the controllers by name to create links in responses.
     */
    public class Routes {
        //TODO: Your Route names here...
        public static final String SINGLE_ACCOUNT = "account.single.route.oid";
        public static final String ACCOUNT_COLLECTION = "account.collection.route.oid";
        public static final String SINGLE_TRANSACTION = "transaction.single.route.oid";
        public static final String TRANSACTION_COLLECTION = "transaction.collection.route.oid";
    }
}
