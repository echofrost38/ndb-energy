package com.ndb.auction.dao.oracle.user;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserDetail;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@NoArgsConstructor
@Table(name = "TBL_USER_DETAIL")
public class UserDetailDao extends BaseOracleDao {

    private static UserDetail extract(ResultSet rs) throws SQLException {
        UserDetail m = new UserDetail();
        m.setId(rs.getInt("ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setFirstName(rs.getString("FIRST_NAME"));
        m.setLastName(rs.getString("LAST_NAME"));
        m.setDob(rs.getDate("BIRTHDAYck"));
        m.setAddress(rs.getString("ADDRESS"));
        m.setIssueDate(rs.getDate("ISSUE_DATE"));
        m.setExpiryDate(rs.getDate("EXPIRY_DATE"));
        m.setNationality(rs.getString("NATIONALITY"));
        m.setPersonalNumber(rs.getString("PERSONAL_NUMBER"));
        m.setDocumentNumber(rs.getString("DOCUMENT_NUMBER"));
        m.setAge(rs.getInt("AGE"));
        m.setAuthority(rs.getString("AUTHORITY"));
        m.setCountryCode(rs.getString("COUNTRY_CODE"));
        m.setCountry(rs.getString("COUNTRY"));
        m.setDocumentType(rs.getString("DOCUMENT_TYPE"));
        m.setPlaceOfBirth(rs.getString("PLACE_OF_BIRTH"));
        m.setGender(rs.getString("GENDER"));

        return m;
    }

    public UserDetail selectById(int id) {
        String sql = "SELECT * FROM TBL_USER_DETAIL WHERE ID=? AND DELETED=0";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    public UserDetail selectByUserId(int userId) {
        String sql = "SELECT * FROM TBL_USER_DETAIL WHERE USER_ID=? AND DELETED=0";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, userId);
    }

    public List<UserDetail> selectAll(String orderby) {
        String sql = "SELECT * FROM TBL_USER_DETAIL";
        if (orderby == null)
            orderby = "ID";
        sql += " ORDER BY " + orderby;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public UserDetail insert(UserDetail m) {
        String sql = "INSERT INTO TBL_USER(ID, USER_ID, FIRST_NAME, LAST_NAME, BIRTHDAY, ADDRESS, ISSUE_DATE, EXPIRY_DATE," +
                " NATIONALITY, PERSONAL_NUMBER, DOCUMENT_NUMBER, AGE, AUTHORITY, COUNTRY_CODE, COUNTRY, DOCUMENT_TYPE," +
                " PLACE_OF_BIRTH, GENDER)"
                + "VALUES(SEQ_USER_DETAIL.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "ID" });
                    int i = 1;
                    ps.setLong(i++, m.getUserId());
                    ps.setString(i++, m.getFirstName());
                    ps.setString(i++, m.getLastName());
                    ps.setDate(i++, m.getDob());
                    ps.setString(i++, m.getAddress());
                    ps.setDate(i++, m.getIssueDate());
                    ps.setDate(i++, m.getExpiryDate());
                    ps.setString(i++, m.getNationality());
                    ps.setString(i++, m.getPersonalNumber());
                    ps.setString(i++, m.getDocumentNumber());
                    ps.setInt(i++, m.getAge());
                    ps.setString(i++, m.getAuthority());
                    ps.setString(i++, m.getCountryCode());
                    ps.setString(i++, m.getCountry());
                    ps.setString(i++, m.getDocumentType());
                    ps.setString(i++, m.getPlaceOfBirth());
                    ps.setString(i, m.getGender());
                    return ps;
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

}
