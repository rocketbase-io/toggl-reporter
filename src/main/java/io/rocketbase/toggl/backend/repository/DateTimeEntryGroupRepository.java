package io.rocketbase.toggl.backend.repository;

import io.rocketbase.toggl.backend.model.DateTimeEntryGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by marten on 08.03.17.
 */

public interface DateTimeEntryGroupRepository extends MongoRepository<DateTimeEntryGroup, String> {

    Long deleteByWorkspaceIdAndDateBetween(long workspaceId, Date from, Date to);

    List<DateTimeEntryGroup> findByWorkspaceIdAndDateBetween(long workspaceId, Date from, Date to);

}
