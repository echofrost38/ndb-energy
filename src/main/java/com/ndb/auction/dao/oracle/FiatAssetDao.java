package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.models.FiatAsset;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name="TBL_FIAT_ASSET")
public class FiatAssetDao {
    
    private static FiatAsset extract(ResultSet rs) throws SQLException {
		FiatAsset model = new FiatAsset();
		model.setId(rs.getInt("ID"));
        model.setFiatId(rs.getInt("FIAT_ID"));
        model.setFiatSymbol(rs.getString("FIAT_SYMBOL"));
        model.setSymbol(rs.getString("SYMBOL"));
		return model;
	}

    

}
