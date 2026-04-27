package dev.simoncodes.ledger.account;

import dev.simoncodes.ledger.account.entity.Account;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    @Modifying
    @Query("UPDATE accounts SET current_balance = current_balance + :delta WHERE id = :id")
    int adjustBalance(@Param("id") UUID id, @Param("delta") BigDecimal delta);

    @Query("SELECT MAX(display_order) FROM accounts WHERE user_id = :userId")
    Integer findMaxDisplayOrderByUserId(@Param("userId") UUID userId);

    List<Account> findByUserIdOrderByDisplayOrder(UUID userId);

    @Query("SELECT CASE WHEN COUNT(A) > 0 THEN TRUE ELSE FALSE END FROM accounts A WHERE id = :id AND user_id = :userId")
    boolean existsByIdAndUserId(UUID id, UUID userId);
}