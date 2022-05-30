package com.ndb.auction.dao.oracle.withdraw;

import java.util.List;

import com.ndb.auction.models.withdraw.BaseWithdraw;

public interface IWithdrawDao {

    // create requset 
    public BaseWithdraw insert(BaseWithdraw baseWithdraw);

    // confirm(approve or denied) request by admin
    public int confirmWithdrawRequest(int requestId, int status, String reason);

    // get withdraw transactions by user
    public List<? extends BaseWithdraw> selectByUser(int userId);

    // get withdraw tx by status (penidng or approved)
    public List<? extends BaseWithdraw> selectByStatus(int userId, int status);

    // get pending requests by admin 
    public List<? extends BaseWithdraw> selectPendings();

    public BaseWithdraw selectById(int id);

}