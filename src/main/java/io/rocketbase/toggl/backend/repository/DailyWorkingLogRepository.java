package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.model.DailyWorkingLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyWorkingLogRepository extends MongoRepository<DailyWorkingLog, String> {

}
