package io.getunleash.embeddr.api;

import io.getunleash.embeddr.payment.PaymentProvider;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The payment provider's HTTP face. It answers 502 because the provider is
 * down on purpose; see {@link PaymentProvider}. Leave this endpoint as is.
 */
@RestController
class PaymentController {

    record PaymentRequest(String sessionId) {}

    private final PaymentProvider provider;

    PaymentController(PaymentProvider provider) {
        this.provider = provider;
    }

    @PostMapping("/payment")
    ResponseEntity<Map<String, String>> payment(@RequestBody PaymentRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());
        PaymentProvider.Receipt receipt = provider.charge(sessionId);
        if (receipt.charged()) {
            return ResponseEntity.ok(Map.of("status", "charged"));
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "error", receipt.detail(),
                "detail", "you have not been charged. probably."));
    }
}
