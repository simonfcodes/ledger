package dev.simoncodes.ledger.category;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends CrudRepository<Category, UUID> {
    @Query("SELECT CASE WHEN COUNT(C) > 0 THEN TRUE ELSE FALSE END FROM categories C WHERE id = :id AND (user_id = :userId OR user_id IS NULL)")
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
