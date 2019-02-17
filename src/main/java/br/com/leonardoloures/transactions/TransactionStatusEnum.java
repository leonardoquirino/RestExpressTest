package br.com.leonardoloures.transactions;

public enum TransactionStatusEnum {

    // Constants -----------------------------------------------------
    INITIAL(0, "Initial"), PENDING(1, "pending"), APPLIED(2, "applied");

    // Attributes ----------------------------------------------------
    private int type;
    private String description;

    // Constructors --------------------------------------------------
    private TransactionStatusEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionStatusEnum fromValue(int value) throws IllegalArgumentException {
        try {
            return TransactionStatusEnum.values()[value];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unknown enum value : " + value);
        }
    }
}
