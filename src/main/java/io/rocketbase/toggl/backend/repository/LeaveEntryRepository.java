package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.model.LeaveEntry;
import io.rocketbase.toggl.backend.model.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeaveEntryRepository extends MongoRepository<LeaveEntry, String> {

    List<LeaveEntry> findAllByWorker(Worker worker);

}
