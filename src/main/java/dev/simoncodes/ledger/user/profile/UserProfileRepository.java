package dev.simoncodes.ledger.user.profile;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends CrudRepository<UserProfile, UUID> {
    Optional<UserProfile> findUserProfileByUserId(UUID userId);

    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN TRUE ELSE FALSE END FROM user_profiles up WHERE user_id = :userId")
    boolean existsByUserId(UUID userId);
}
