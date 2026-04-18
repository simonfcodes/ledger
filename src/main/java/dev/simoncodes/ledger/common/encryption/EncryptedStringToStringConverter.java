package dev.simoncodes.ledger.common.encryption;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@WritingConverter
public class EncryptedStringToStringConverter implements Converter<EncryptedString, String> {

    private final EncryptionService encryptionSvc;

    @Override
    public String convert(EncryptedString encryptedString) {
        return encryptionSvc.encrypt(encryptedString.value());
    }

}
