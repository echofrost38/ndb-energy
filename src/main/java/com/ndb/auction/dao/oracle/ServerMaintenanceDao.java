package com.ndb.auction.dao.oracle;

import com.ndb.auction.models.ServerMaintenance;
import org.springframework.stereotype.Repository;

@Repository
@Table(name = "TBL_SERVER_MAINTENANCE")
public class ServerMaintenanceDao extends BaseOracleDao {

    public ServerMaintenance get() {
        String sql = "SELECT * FROM TBL_SERVER_MAINTENANCE WHERE ENABLED=1 AND (EXPIRE_DATE IS NULL OR EXPIRE_DATE>SYSDATE)";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            ServerMaintenance model = new ServerMaintenance();
            model.setMessage(rs.getString("MESSAGE"));
            model.setExpireDate(rs.getTimestamp("EXPIRE_DATE"));
            return model;
        });
    }

}
