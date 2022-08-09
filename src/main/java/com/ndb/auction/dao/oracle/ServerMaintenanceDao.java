package com.ndb.auction.dao.oracle;

import org.springframework.stereotype.Repository;

@Repository
@Table(name = "TBL_SERVER_MAINTENANCE")
public class ServerMaintenanceDao extends BaseOracleDao {

    public String check() {
        String sql = "SELECT MESSAGE FROM TBL_SERVER_MAINTENANCE WHERE ENABLED=1 AND (EXPIRE_DATE IS NULL OR EXPIRE_DATE>SYSDATE)";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return rs.getString(1);
        });
    }

}
