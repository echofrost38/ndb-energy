package com.ndb.auction.dao.oracle.transactions.stripe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.stripe.StripeCustomer;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_STRIPE_CUSTOMER")
public class StripeCustomerDao extends BaseOracleDao {
    
    private static StripeCustomer extract(ResultSet rs) throws SQLException {
		StripeCustomer m = new StripeCustomer();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setCustomerId(rs.getString("CUSTOMER_ID"));
        m.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
        m.setBrand(rs.getString("BRAND"));
        m.setCountry(rs.getString("COUNTRY"));
        m.setExpMonth(rs.getLong("EXP_MONTH"));
        m.setExpYear(rs.getLong("EXP_YEAR"));
        m.setLast4(rs.getString("LAST4"));
        return m;
	}

    public int insert(StripeCustomer m) {
        String sql = "INSERT INTO TBL_STRIPE_CUSTOMER(ID,USER_ID,CUSTOMER_ID,PAYMENT_METHOD,BRAND,COUNTRY,EXP_MONTH,EXP_YEAR,LAST4)"
            + "VALUES(SEQ_STRIPE_CUSTOMER.NEXTVAL,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getCustomerId(), m.getPaymentMethod(), m.getBrand(), m.getCountry(), m.getExpMonth(), m.getExpYear(), m.getLast4());
    }

    public List<StripeCustomer> selectByUser(int userId) {
        String sql = "SELECT * FROM TBL_STRIPE_CUSTOMER WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public StripeCustomer selectById(int id) {
        String sql = "SELECT * FROM TBL_STRIPE_CUSTOMER WHERE ID = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    public int update(StripeCustomer m) {
        String sql = "UPDATE TBL_STRIPE_CUSTOMER SET CUSTOMER_ID=?,PAYMENT_METHOD=?,BRAND=?,COUNTRY=?,EXP_MONTH=?,EXP_YEAR=?,LAST4=? WHERE ID = ?";
        return jdbcTemplate.update(sql, m.getCustomerId(),m.getPaymentMethod(), m.getBrand(), m.getCountry(), m.getExpMonth(), m.getExpYear(), m.getLast4(), m.getId());
    }
}
