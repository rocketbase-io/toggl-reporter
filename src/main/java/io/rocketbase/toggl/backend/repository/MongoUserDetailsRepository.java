package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.security.MongoUserDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserDetailsRepository extends MongoRepository<MongoUserDetails, String> {

    Optional<MongoUserDetails> findByUsername(String username);
}
