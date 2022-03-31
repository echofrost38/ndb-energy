package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.BuyNamePrice;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_NAME_PRICE")
public class BuyNamePriceDao extends BaseOracleDao {

    private static BuyNamePrice extract(ResultSet rs) throws SQLException {
		BuyNamePrice m = new BuyNamePrice();
		m.setId(rs.getInt("ID"));
		m.setNumOfChars(rs.getInt("CHARS"));
        m.setPrice(rs.getDouble("PRICE"));
		return m;
	}

    public int insert(BuyNamePrice m) {
        String sql = "INSERT INTO TBL_NAME_PRICE(ID,CHARS,PRICE)VALUES(SEQ_NAME_PRICE.NEXTVAL,?,?)";
        return jdbcTemplate.update(sql, m.getNumOfChars(), m.getPrice());
    }

    public List<BuyNamePrice> selectAll() {
        String sql = "SELECT * FROM TBL_NAME_PRICE ORDER BY CHARS";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public BuyNamePrice select(int chars) {
        String sql = "SELECT * FROM TBL_NAME_PRICE WHERE CHARS=?";
        return jdbcTemplate.query(sql, rs -> {
            if(!rs.next()) return null;
            return extract(rs);
        }, chars);
    }

    public int update(int id, int chars, double price) {
        String sql = "UPDATE TBL_NAME_PRICE SET CHARS=?,PRICE=? WHERE ID=?";
        return jdbcTemplate.update(sql, chars, price, id);
    }

    public int delete(int id) {
        String sql = "DELETE FROM TBL_NAME_PRICE WHERE ID=?";
        return jdbcTemplate.update(sql, id);
    }
}
