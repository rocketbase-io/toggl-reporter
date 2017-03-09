package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.model.ApplicationSettingModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationSettingRepository extends MongoRepository<ApplicationSettingModel, String> {

}
