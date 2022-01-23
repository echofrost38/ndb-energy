package com.ndb.auction.dao.oracle.presale;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.presale.PreSaleCondition;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_PRESALE_CONDITION")
public class PreSaleConditionDao extends BaseOracleDao {
    
    private static PreSaleCondition extract(ResultSet rs) throws SQLException {
		PreSaleCondition m = new PreSaleCondition();
		m.setId(rs.getInt("ID"));
        m.setPresaleId(rs.getInt("PRESALE_ID"));
        m.setTask(rs.getString("TASK"));
        m.setUrl(rs.getString("URL"));
		return m;
	}

    public int insert(PreSaleCondition m) {
        String sql = "INSERT INTO TBL_PRESALE_CONDITION(ID,PRESALE_ID,TASK,URL)"
            + "VALUES(SEQ_PRESALE_CONDITION.NEXTVAL,?,?,?)";
        return jdbcTemplate.update(sql, m.getPresaleId(), m.getTask(), m.getUrl());
    }

    public List<PreSaleCondition> selectById(int presaleId) {
        String sql = "SELECT * FROM TBL_PRESALE_CONDITION WHERE PRESALE_ID = ?";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleCondition>() {
			@Override
			public PreSaleCondition mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, presaleId);
    }

    public int udpate(PreSaleCondition m) {
        String sql = "UPDATE TBL_PRESALE_CONDITION SET TASK=?, URL=? WHERE ID=?";
        return jdbcTemplate.update(sql, m.getTask(), m.getUrl(), m.getId());
    }

    public int deleteTask(int id) {
        String sql = "DELETE FROM TBL_PRESALE_CONDITION WHERE ID=?";
        return jdbcTemplate.update(sql, id);
    }

    public int insertConditionList(List<PreSaleCondition> list) {
        int result = 0;
        for (PreSaleCondition preSaleCondition : list) {
            result += insert(preSaleCondition);
        }
        return result;
    }

}
