package dev.simoncodes.ledger.transaction;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByIdAndAccountId(UUID id, UUID accountId);

    /**
     * Cursor parameters must all be non-null together or all be null together.
     * Partial cursors (some null, some not) will silently return empty results.
     */
    @Query("""
        SELECT t.* FROM transactions t
        WHERE t.account_id = :accountId
          AND (CAST(:cursorDate AS DATE) IS NULL OR (t.transaction_date, t.created_at, t.id) < (:cursorDate, :cursorCreatedAt, :cursorId))
          AND (CAST(:fromDate AS DATE) IS NULL OR t.transaction_date >= :fromDate)
          AND (CAST(:toDate AS DATE) IS NULL OR t.transaction_date <= :toDate)
          AND (CAST(:direction AS VARCHAR) IS NULL OR t.direction = :direction)
          AND (CAST(:categoryId AS UUID) IS NULL OR t.category_id = :categoryId)
        ORDER BY t.transaction_date DESC, t.created_at DESC, t.id DESC
        LIMIT :limit
        """)
    List<Transaction> findPage(
            @Param("accountId") UUID accountId,
            @Param("cursorDate")LocalDate cursorDate,
            @Param("cursorCreatedAt") Instant cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("direction") TransactionType direction,
            @Param("categoryId") UUID categoryId,
            @Param("limit") int limit);
}
