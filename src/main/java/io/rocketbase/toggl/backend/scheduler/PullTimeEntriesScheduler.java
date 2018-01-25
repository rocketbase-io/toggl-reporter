package io.rocketbase.toggl.backend.scheduler;

import io.rocketbase.toggl.backend.config.TogglService;
import io.rocketbase.toggl.backend.model.ApplicationSetting.SchedulingConfig;
import io.rocketbase.toggl.backend.service.FetchAndStoreService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class PullTimeEntriesScheduler {

    @Resource
    private TogglService togglService;

    @Resource
    private FetchAndStoreService fetchAndStoreService;

    // each hour, 30sec initial delay
    @Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 1000 * 30)
    public void schedule() {
        SchedulingConfig schedulingConfig = togglService.getSchedulingConfig();
        if (schedulingConfig != null && schedulingConfig.isEnableScheduling()) {
            log.info("start scheduler");
            long startTime = System.currentTimeMillis();
            LocalDate start = schedulingConfig.getLastFinishedDate() != null ? schedulingConfig.getLastFinishedDate() : schedulingConfig.getStartSchedulingFrom();

            fetchAndStoreService.fetchBetween(start, LocalDate.now());

            schedulingConfig.setLastFinishedDate(LocalDate.now());
            togglService.updateSchedulingConfig(schedulingConfig);
            log.info("finished scheduling - took: {} ms", System.currentTimeMillis() - startTime);
        }
    }
}
