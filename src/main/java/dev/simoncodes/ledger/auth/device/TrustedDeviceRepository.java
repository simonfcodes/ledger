package dev.simoncodes.ledger.auth.device;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrustedDeviceRepository extends CrudRepository<TrustedDevice, UUID> {
    Optional<TrustedDevice> findByDeviceTokenHash(String deviceTokenHash);
    void deleteAllByUserId(UUID userId);
    List<TrustedDevice> findAllByUserId(UUID userId);
}
