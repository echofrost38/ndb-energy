package com.ndb.auction.controllers;

import com.ndb.auction.config.AppConfig;
import com.ndb.auction.hooks.BaseController;
import com.ndb.auction.models.ServerMaintenance;
import com.ndb.auction.schedule.ScheduledTasks;
import com.ndb.auction.service.ServerManageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerManageController extends BaseController {

    private final ScheduledTasks scheduledTasks;
    private final ServerManageService serverManageService;

    public ServerManageController(ServerManageService serverManageService, ScheduledTasks scheduledTasks) {
        this.serverManageService = serverManageService;
        AppConfig.maintenanceMessage = serverManageService.checkMaintenance();
        this.scheduledTasks = scheduledTasks;
    }

    @GetMapping(value = "/admin/maintenance", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object refresh() {
        ServerMaintenance serverMaintenance = serverManageService.checkMaintenance();
        AppConfig.maintenanceMessage = serverMaintenance;
        if (serverMaintenance == null)
            return "Maintenance Mode is OFF.";
        else
            return "Maintenance Mode is ON.\nMessage = " + serverMaintenance.getMessage() + "\nExpire = " + serverMaintenance.getExpireDate();
    }

    @GetMapping(value = "/admin/auction/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object auctionStatus() {
        return "startedRound = " + scheduledTasks.getStartedRound()
                + "\n" + "startedCounter = " + scheduledTasks.getStartedCounter()
                + "\n" + "readyRound = " + scheduledTasks.getReadyRound()
                + "\n" + "readyCounter = " + scheduledTasks.getReadyCounter()
                + "\n" + "startedPresale = " + scheduledTasks.getStartedPresale()
                + "\n" + "startedPresaleCounter = " + scheduledTasks.getStartedPresaleCounter()
                + "\n" + "readyPresale = " + scheduledTasks.getReadyPresale()
                + "\n" + "readyPresaleCounter = " + scheduledTasks.getReadyPresaleCounter();
    }

    @GetMapping(value = "/admin/auction/checkAllRounds", produces = MediaType.TEXT_PLAIN_VALUE)
    public Object checkAllRounds() {
        scheduledTasks.checkAllRounds();
        return auctionStatus();
    }

}
