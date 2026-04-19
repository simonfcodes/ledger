package dev.simoncodes.ledger.auth.mfa;

public class MfaException extends RuntimeException {
    public MfaException(String message) {
        super(message);
    }
}
