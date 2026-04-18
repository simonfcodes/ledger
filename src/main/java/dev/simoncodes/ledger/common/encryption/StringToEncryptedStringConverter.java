package dev.simoncodes.ledger.common.encryption;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ReadingConverter
public class StringToEncryptedStringConverter implements Converter<String, EncryptedString> {

    private final EncryptionService encryptionSvc;
    @Override
    public EncryptedString convert(String source) {
        return new EncryptedString(
                encryptionSvc.decrypt(source)
        );
    }
}
