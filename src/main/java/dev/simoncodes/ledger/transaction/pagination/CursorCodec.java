package dev.simoncodes.ledger.transaction.pagination;

import dev.simoncodes.ledger.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class CursorCodec {
    private final ObjectMapper mapper;

    public String encode(TransactionCursor cursor) {
        try {
            String json = mapper.writeValueAsString(cursor);
            return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JacksonException e) {
            log.warn("Failed to encode cursor: {}", cursor, e);
            throw new IllegalStateException("Error in encoding transaction cursor: " + e.getMessage(), e);
        }
    }

    public TransactionCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            throw new IllegalArgumentException("Cursor cannot be null or blank");
        }
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encoded);
            String json = new String(decodedBytes, StandardCharsets.UTF_8);
            return mapper.readValue(json, TransactionCursor.class);
        } catch (IllegalArgumentException | JacksonException e) {
            log.warn("Failed to decode cursor: {}", encoded, e);
            throw new BadRequestException("Invalid cursor for decoding");
        }
    }
}
