package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.model.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkerRepository extends MongoRepository<Worker, String> {

}
