package com.ndb.auction.dao.oracle.auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.AuctionStats;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AUCTION")
public class AuctionDao extends BaseOracleDao {

	private static Auction extract(ResultSet rs) throws SQLException {
		Auction m = new Auction();
		m.setId(rs.getInt("ID"));
		m.setRound(rs.getInt("ROUND"));
		m.setStartedAt(rs.getTimestamp("START_DATE").getTime());
		m.setEndedAt(rs.getTimestamp("END_DATE").getTime());
		m.setTotalToken(rs.getLong("TOTAL_TOKEN"));
		m.setMinPrice(rs.getLong("MIN_PRICE"));
		m.setSold(rs.getLong("SOLD"));
		m.setStats(new AuctionStats(
				rs.getLong("QTY"), rs.getLong("WIN"), rs.getLong("FAIL")));
		m.setToken(rs.getLong("TOKEN"));
		m.setStatus(rs.getInt("STATUS"));
		return m;
	}

	public Auction createNewAuction(Auction m) {
		String sql = "INSERT INTO TBL_AUCTION(ID, ROUND, START_DATE, END_DATE, TOTAL_TOKEN, MIN_PRICE, SOLD, QTY, WIN, FAIL, TOKEN, STATUS)"
				+ "VALUES(SEQ_AUCTION.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?)";
		AuctionStats stats = new AuctionStats();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql.toString(),
							new String[] { "ID" });
					int i = 1;
					ps.setInt(i++, m.getRound());
					ps.setTimestamp(i++, new Timestamp(m.getStartedAt()));
					ps.setTimestamp(i++, new Timestamp(m.getEndedAt()));
					ps.setLong(i++, m.getTotalToken());
					ps.setLong(i++, m.getMinPrice());
					ps.setLong(i++, m.getSold());
					ps.setLong(i++, stats.getQty());
					ps.setLong(i++, stats.getWin());
					ps.setLong(i++, stats.getFail());
					ps.setLong(i++, m.getToken());
					ps.setInt(i++, m.getStatus());
					return ps;
				}
			}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	public List<Auction> getAuctionList() {
		String sql = "SELECT * FROM TBL_AUCTION ORDER BY ROUND";
		return jdbcTemplate.query(sql, new RowMapper<Auction>() {
			@Override
			public Auction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public Auction getAuctionById(int id) {
		String sql = "SELECT * FROM TBL_AUCTION WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Auction>() {
			@Override
			public Auction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public Auction getAuctionByRound(int round) {
		String sql = "SELECT * FROM TBL_AUCTION WHERE ROUND=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Auction>() {
			@Override
			public Auction extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, round);
	}

	public Auction updateAuctionByAdmin(Auction m) {
		String sql = "UPDATE TBL_AUCTION SET ROUND=?, START_DATE=?, END_DATE=?, TOTAL_TOKEN=? ,MIN_PRICE=?, SOLD=?, QTY=?, WIN=?, FAIL=?, TOKEN=?, STATUS=? WHERE ID=?";
		AuctionStats stats = m.getStats();
		jdbcTemplate.update(sql, m.getRound(), new Timestamp(m.getStartedAt()), new Timestamp(m.getEndedAt()),
				m.getTotalToken(), m.getMinPrice(), m.getSold(), stats.getQty(), stats.getWin(), stats.getFail(),
				m.getToken(), m.getStatus(), m.getId());
		return m;
	}

	public Auction startAuction(Auction m) {
		String sql = "UPDATE TBL_AUCTION SET STATUS=? WHERE ID=?";
		m.setStatus(Auction.STARTED);
		jdbcTemplate.update(sql, m.getStatus(), m.getId());
		return m;
	}

	public Auction endAuction(Auction m) {
		String sql = "UPDATE TBL_AUCTION SET STATUS=? WHERE ID=?";
		m.setStatus(Auction.ENDED);
		jdbcTemplate.update(sql, m.getStatus(), m.getId());
		return m;
	}

	public Auction updateAuctionStats(Auction m) {
		String sql = "UPDATE TBL_AUCTION SET STATUS=? WHERE ID=?";
		jdbcTemplate.update(sql, m.getStatus(), m.getId());
		return m;
	}

	public List<Auction> getAuctionByStatus(int status) {
		String sql = "SELECT * FROM TBL_AUCTION WHERE STATUS=? ORDER BY ROUND";
		return jdbcTemplate.query(sql, new RowMapper<Auction>() {
			@Override
			public Auction mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, status);
	}

}
