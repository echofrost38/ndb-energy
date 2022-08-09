package com.ndb.auction.controllers;

import com.ndb.auction.config.AppConfig;
import com.ndb.auction.hooks.BaseController;
import com.ndb.auction.service.ServerManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerManageController extends BaseController {

    private final ServerManageService serverManageService;

    public ServerManageController(ServerManageService serverManageService) {
        this.serverManageService = serverManageService;
        AppConfig.maintenanceMessage = serverManageService.checkMaintenance();
    }

    @GetMapping(value = "/admin/refresh", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object refresh() {
        AppConfig.maintenanceMessage = serverManageService.checkMaintenance();
        if (AppConfig.maintenanceMessage == null)
            return "Maintenance Mode is OFF.";
        else
            return "Maintenance Mode is ON.\nMessage = " + AppConfig.maintenanceMessage;
    }

}
