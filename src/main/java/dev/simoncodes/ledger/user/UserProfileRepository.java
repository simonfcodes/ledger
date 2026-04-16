package dev.simoncodes.ledger.user;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserProfileRepository extends CrudRepository<UserProfile, UUID> { }
