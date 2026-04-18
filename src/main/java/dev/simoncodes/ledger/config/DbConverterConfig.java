package dev.simoncodes.ledger.config;

import dev.simoncodes.ledger.common.encryption.EncryptedStringToStringConverter;
import dev.simoncodes.ledger.common.encryption.StringToEncryptedStringConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DbConverterConfig extends AbstractJdbcConfiguration {

    private final EncryptedStringToStringConverter writingConverter;
    private final StringToEncryptedStringConverter readingConverter;

    @Override
    @NonNull
    protected List<?> userConverters() {
        return List.of(writingConverter, readingConverter);
    }
}
