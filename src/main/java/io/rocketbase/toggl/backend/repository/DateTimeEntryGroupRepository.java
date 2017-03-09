package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.model.DateTimeEntryGroupModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by marten on 08.03.17.
 */

public interface DateTimeEntryGroupRepository extends MongoRepository<DateTimeEntryGroupModel, String> {

    Long deleteByWorkspaceIdAndDateBetween(long workspaceId, Date from, Date to);

    List<DateTimeEntryGroupModel> findByWorkspaceIdAndDateBetween(long workspaceId, Date from, Date to);

}
