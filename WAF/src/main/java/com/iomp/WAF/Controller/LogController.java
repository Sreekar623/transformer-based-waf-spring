package com.iomp.WAF.Controller;

import com.iomp.WAF.Entity.RequestLog;
import com.iomp.WAF.Repository.RequestLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class LogController {

    private final RequestLogRepository repository;

    public LogController(RequestLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/logs")
    public List<RequestLog> getLogs() {

        List<RequestLog> allLogs = repository.findAll();


        Map<String, RequestLog> uniqueMap = new LinkedHashMap<>();

        for (RequestLog log : allLogs) {
            String key = (log.getInput() + "_" + log.getStatus());


            uniqueMap.put(key, log);
        }

        return new ArrayList<>(uniqueMap.values());
    }
}