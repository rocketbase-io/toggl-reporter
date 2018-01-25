package io.rocketbase.toggl.backend.service;

import io.rocketbase.toggl.backend.model.Worker;
import io.rocketbase.toggl.backend.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkerService {

    @Resource
    private WorkerRepository workerRepository;

    public List<Worker> findAll() {
        return workerRepository.findAll();
    }

    public Worker updateWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public void deleteWorker(Worker worker) {
        workerRepository.delete(worker);
    }
}
