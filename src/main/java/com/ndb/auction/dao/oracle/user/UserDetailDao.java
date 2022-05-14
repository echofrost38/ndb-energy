package com.ndb.auction.dao.oracle.user;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.user.UserDetail;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        m.setDob(rs.getString("BIRTHDAY"));
        m.setAddress(rs.getString("ADDRESS"));
        m.setIssueDate(rs.getString("ISSUE_DATE"));
        m.setExpiryDate(rs.getString("EXPIRY_DATE"));
        m.setNationality(rs.getString("NATIONALITY"));
        m.setPersonalNumber(rs.getString("PERSONAL_NUMBER"));
        m.setDocumentNumber(rs.getString("DOCUMENT_NUMBER"));
        m.setAge(rs.getInt("AGE"));
        m.setHeight(rs.getString("HEIGHT"));
        m.setAuthority(rs.getString("AUTHORITY"));
        m.setCountryCode(rs.getString("COUNTRY_CODE"));
        m.setCountry(rs.getString("COUNTRY"));
        m.setDocumentType(rs.getString("DOCUMENT_TYPE"));
        m.setPlaceOfBirth(rs.getString("PLACE_OF_BIRTH"));
        m.setGender(rs.getString("GENDER"));
        m.setDeleted(rs.getInt("DELETED"));

        return m;
    }

    public UserDetail selectById(int id) {
        String sql = "SELECT * FROM TBL_USER_DETAIL WHERE ID=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, id);
    }

    public UserDetail selectByUserId(int userId) {
        String sql = "SELECT * FROM TBL_USER_DETAIL WHERE USER_ID=?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next())
                return null;
            return extract(rs);
        }, userId);
    }

    public List<UserDetail> selectAll(String orderby) {
        String sql = "SELECT * FROM TBL_USER_DETAIL";
        if (orderby == null || orderby.equals(""))
            orderby = "ID";
        sql += " ORDER BY " + orderby;
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs));
    }

    public UserDetail insert(UserDetail m) {
        String sql = "INSERT INTO TBL_USER_DETAIL(ID, USER_ID, FIRST_NAME, LAST_NAME, BIRTHDAY, ADDRESS, ISSUE_DATE, EXPIRY_DATE," +
                " NATIONALITY, PERSONAL_NUMBER, DOCUMENT_NUMBER, AGE, AUTHORITY, COUNTRY_CODE, COUNTRY, DOCUMENT_TYPE," +
                " PLACE_OF_BIRTH, GENDER, HEIGHT, DELETED, REG_DATE, UPDATE_DATE)"
                + "VALUES(SEQ_USER_DETAIL.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,SYSDATE,SYSDATE)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                    int i = 1;
                    ps.setLong(i++, m.getUserId());
                    ps.setString(i++, m.getFirstName());
                    ps.setString(i++, m.getLastName());

                    if (m.getDob() != null) {
                        ps.setDate(i++, Date.valueOf(LocalDate.parse(m.getDob())));
                    } else {
                        ps.setDate(i++, null);
                    }
                    ps.setString(i++, m.getAddress());

                    if (m.getIssueDate() != null) {
                        ps.setDate(i++, Date.valueOf(LocalDate.parse(m.getIssueDate())));
                    } else {
                        ps.setDate(i++, null);
                    }

                    if(m.getExpiryDate() != null) {
                        ps.setDate(i++, Date.valueOf(LocalDate.parse(m.getExpiryDate())));
                    } else {
                        ps.setDate(i++, null);
                    }
                    ps.setString(i++, m.getNationality());
                    ps.setString(i++, m.getPersonalNumber());
                    ps.setString(i++, m.getDocumentNumber());
                    ps.setInt(i++, m.getAge());
                    ps.setString(i++, m.getAuthority());
                    ps.setString(i++, m.getCountryCode());
                    ps.setString(i++, m.getCountry());
                    ps.setString(i++, m.getDocumentType());
                    ps.setString(i++, m.getPlaceOfBirth());
                    ps.setString(i++, m.getGender());
                    ps.setString(i, m.getHeight());
                    return ps;
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());

        return m;
    }

}
