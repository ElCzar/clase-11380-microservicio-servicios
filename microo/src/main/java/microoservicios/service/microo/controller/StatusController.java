package microoservicios.service.microo.controller;

import microoservicios.service.microo.entity.Status;
import microoservicios.service.microo.repository.StatusRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/statuses")
public class StatusController {

    private final StatusRepository statusRepository;

    public StatusController(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    // Get all statuses
    @GetMapping
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }
}
