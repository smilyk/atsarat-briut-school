package atsaratbriut.school.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/ping")
public class PingController {
    private String currentDate = LocalDateTime.now().toLocalDate().toString();

    @Value("${server.port}")
    String serverPort;
    @GetMapping()
    public String ping() {
        return " School-Service working " + currentDate + " on port " +  serverPort;
    }

}
