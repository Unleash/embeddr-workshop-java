package io.getunleash.embeddr.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Embeddr's payment provider, which is down. The outage is scripted: the
 * workshop ships a paid feature against this provider and rescues it with a
 * flag flip, so the failure below is load-bearing. Do not fix, stub, or
 * route around it.
 */
@RestController
class PaymentController {

    record PaymentRequest(String sessionId) {}

    private static final long PROVIDER_DELAY_MS = 900;

    @PostMapping("/payment")
    ResponseEntity<Map<String, String>> payment(@RequestBody PaymentRequest request) throws InterruptedException {
        Requests.requireSessionId(request.sessionId());

        // Pretend to think, then fail the way real providers do: slowly.
        Thread.sleep(PROVIDER_DELAY_MS);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "error", "payment provider not responding",
                "detail", "you have not been charged. probably."));
    }
}
