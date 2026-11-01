package io.getunleash.embeddr.payment;

import org.springframework.stereotype.Service;

/**
 * Embeddr's payment provider, which is down. The outage is scripted: the
 * workshop ships a paid feature against this provider and rescues it with a
 * flag flip, so the failure below is load-bearing. Do not fix, stub, or
 * route around it in here. Code that needs to charge calls {@link #charge}
 * and decides what to do when it fails.
 */
@Service
public class PaymentProvider {

    /** The provider's answer. {@code charged} is never true today. */
    public record Receipt(boolean charged, String detail) {}

    private static final long PROVIDER_DELAY_MS = 900;

    public Receipt charge(String sessionId) {
        // Pretend to think, then fail the way real providers do: slowly.
        try {
            Thread.sleep(PROVIDER_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new Receipt(false, "payment provider not responding");
    }
}
