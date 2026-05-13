package com.iomp.WAF.Service;


import com.iomp.WAF.Entity.RequestLog;
import com.iomp.WAF.Repository.RequestLogRepository;
import org.springframework.stereotype.Service;

@Service
public class WafServiceImpl implements WafService {

    private final RequestLogRepository repository;

    public WafServiceImpl(RequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public String processInput(String input) {

        // Save request as ALLOW (for now)
        RequestLog log = new RequestLog();
        log.setInput(input);
        log.setStatus("ALLOW");

        repository.save(log);

        return "Processed: " + input;
    }

}