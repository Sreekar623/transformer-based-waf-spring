package com.iomp.WAF.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class WafController {


    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String input) {
        return ResponseEntity.ok(Map.of("status","ALLOW","input",input));
    }


    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestParam String input) {


        return ResponseEntity.ok("Processed ");
    }
}