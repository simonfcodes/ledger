package dev.simoncodes.ledger.category;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends CrudRepository<Category, UUID> {
    @Query("""
        SELECT
            c.id,
            c.user_id,
            c.display_name,
            c.parent_id,
            c.color,
            c.icon,
            c.code,
            uhc.category_id IS NOT NULL AS hidden
        FROM categories c
        LEFT JOIN user_hidden_category uhc ON (c.id = uhc.category_id AND uhc.user_id = :userId)
        WHERE (c.id = :categoryId)
          AND (c.user_id IS NULL OR c.user_id = :userId)
        
""")
    Optional<CategoryWithHiddenStatus> getSingleCategory(UUID userId, UUID categoryId);

    @Query("SELECT CASE WHEN COUNT(C) > 0 THEN TRUE ELSE FALSE END FROM categories C WHERE id = :id AND (user_id = :userId OR user_id IS NULL)")
    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT CASE WHEN c.user_id IS NULL THEN TRUE ELSE FALSE END FROM categories c WHERE id = :id")
    boolean isSystemCategory(UUID id);

    @Query("SELECT CASE WHEN COUNT(C) >0 THEN TRUE ELSE FALSE END FROM categories C WHERE id = :id AND (user_id = :userId)")
    boolean existsByIdAndOwnedByUserId(UUID id, UUID userId);

    @Query("""
        INSERT INTO user_hidden_category (user_id, category_id)
        SELECT :userId, c.id
        FROM categories c
        WHERE user_id IS NULL
        AND (c.id = :categoryId OR parent_id = :categoryId)
        ON CONFLICT (user_id, category_id) DO NOTHING
""")
    void hideSystemCategoryAndChildrenForUser(UUID userId, UUID categoryId);

    @Query("""
        DELETE FROM user_hidden_category
        WHERE user_id = :userId
        AND category_id = :categoryId
""")
    void unhideSystemCategoryForUser(UUID userId, UUID categoryId);

    @Query("""
        SELECT
            c.id,
            c.user_id,
            c.display_name,
            c.parent_id,
            c.color,
            c.icon,
            c.code,
            uhc.category_id IS NOT NULL AS hidden
        FROM categories c
        LEFT JOIN user_hidden_category uhc ON (c.id = uhc.category_id AND uhc.user_id = :userId)
        WHERE (c.user_id IS NULL OR c.user_id = :userId)
""")
    List<CategoryWithHiddenStatus> getAllWithHiddenStatusByUserId(UUID userId);

    @Query("SELECT CASE WHEN c.parent_id IS NOT NULL THEN TRUE ELSE FALSE END FROM categories c WHERE id = :id")
    boolean hasParentCategory(UUID id);
}
