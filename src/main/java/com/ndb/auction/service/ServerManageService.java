package com.ndb.auction.service;

import com.ndb.auction.dao.oracle.ServerMaintenanceDao;
import com.ndb.auction.models.ServerMaintenance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerManageService extends BaseService {

    private final ServerMaintenanceDao serverMaintenanceDao;

    public ServerMaintenance checkMaintenance() {
        return serverMaintenanceDao.get();
    }

}
