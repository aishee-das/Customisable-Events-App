package external;

/**
 * Interface for the external {@link PaymentSystem} (e.g., PayPal, Transferwise, or similar). It allows requesting for
 * payments to be made from one party to another (both are assumed to be already registered on the payment system), or
 * for payments to be refunded. There is only one {@link PaymentSystem} and all users of this application use the same
 * system. Payments and refunds can succeed or fail, this is indicated by the return values.
 */
public interface PaymentSystem extends AutoCloseable {
    /**
     * Request a payment to be made from the buyer to the seller for a given transaction amount
     * @param buyerAccountEmail email address of the buyer's account on the payment system
     * @param sellerAccountEmail email address of the seller's account on the payment system
     * @param transactionAmount amount to be transferred in GBP pence
     * @return True if successful and false otherwise
     */
    boolean processPayment(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount);

    /**
     * Request a payment to be refunded from the seller to the buyer for a given transaction amount
     * @param buyerAccountEmail email address of the buyer's account on the payment system
     * @param sellerAccountEmail email address of the seller's account on the payment system
     * @param transactionAmount amount to be transferred in GBP pence
     * @return True if successful and false otherwise
     */
    boolean processRefund(String buyerAccountEmail, String sellerAccountEmail, double transactionAmount);
}