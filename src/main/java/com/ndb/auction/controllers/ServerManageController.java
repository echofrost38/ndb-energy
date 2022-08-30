package com.ndb.auction.controllers;

import com.ndb.auction.config.AppConfig;
import com.ndb.auction.hooks.BaseController;
import com.ndb.auction.models.ServerMaintenance;
import com.ndb.auction.service.ServerManageService;
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
        ServerMaintenance serverMaintenance = serverManageService.checkMaintenance();
        AppConfig.maintenanceMessage = serverMaintenance;
        if (serverMaintenance == null)
            return "Maintenance Mode is OFF.";
        else
            return "Maintenance Mode is ON.\nMessage = " + serverMaintenance.getMessage() + "\nExpire = " + serverMaintenance.getExpireDate();
    }

}
