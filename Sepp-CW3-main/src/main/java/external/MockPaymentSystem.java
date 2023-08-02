package external;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A mock implementation of {@link PaymentSystem} for testing purposes.
 * In a real life application, this implementation would be making network requests to a payment service API.
 * However, networking is the topic of another course (if this sounds interesting, you may want to take
 * COMN - Computer Communications and Networks in year 3 or 4).
 * <p>
 * This class should keep track of payments made, so that when a refund is requested, it can check whether the
 * transactionAmount corresponds to a previously made payment (or if something fishy may be going on).
 * Watch out for transactions made between the same people for the same amount more than once!
 * <p>
 * Hint: you may find it helpful to use an inner Transaction class, overriding its equals and hashCode methods
 */
public class MockPaymentSystem implements PaymentSystem {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private static void printMessage(String prompt, String... arguments) {
        System.err.println(ANSI_CYAN + prompt + ": " + String.join(", ", arguments) + ANSI_RESET);
    }

    private static class Transaction {
        private final String buyer;
        private final String seller;
        private final double amount;

        public Transaction(String buyer, String seller, double amount) {
            this.buyer = buyer;
            this.seller = seller;
            this.amount = amount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(buyer, seller, amount);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof Transaction)) return false;
            Transaction other = (Transaction) obj;
            return buyer.equals(other.buyer) && seller.equals(other.seller) && Double.compare(amount, other.amount) == 0;
        }
    }

    // Key = Transaction, Value = Count of same transactions
    private final Map<Transaction, Integer> transactions;

    public MockPaymentSystem() {
        transactions = new HashMap<>();
    }

    public MockPaymentSystem(MockPaymentSystem other) {
        transactions = new HashMap<>(other.transactions);
    }

    @Override
    public boolean processPayment(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount) {
        printMessage("Processing payment", buyerAccountEmail, sellerAccountEmail, String.valueOf(transactionAmount));
        Transaction transaction = new Transaction(buyerAccountEmail, sellerAccountEmail, transactionAmount);
        transactions.put(transaction, transactions.getOrDefault(transaction, 0) + 1);
        return true;
    }

    @Override
    public boolean processRefund(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount) {
        printMessage("Processing refund", buyerAccountEmail, sellerAccountEmail, String.valueOf(transactionAmount));
        Transaction transaction = new Transaction(buyerAccountEmail, sellerAccountEmail, transactionAmount);
        int transactionCount = transactions.getOrDefault(transaction, 0);
        if (transactionCount <= 0) {
            return false;
        }
        transactions.put(transaction, transactionCount - 1);
        return true;
    }

    @Override
    public void close() {
        // No need to clean up any resources in the mock system. But it may be necessary in a real system,
        // e.g., we might want to close any open network sockets or files
    }
}