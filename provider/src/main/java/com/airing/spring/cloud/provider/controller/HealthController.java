package com.airing.spring.cloud.provider.controller;

import com.airing.spring.cloud.provider.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("health")
public class HealthController {

    @Autowired
    private HealthStatusService healthStatusService;

    @PostMapping("set/status")
    public Object setStatus(boolean status) {
        this.healthStatusService.setStatus(status);
        return this.healthStatusService.isStatus();
    }

}
