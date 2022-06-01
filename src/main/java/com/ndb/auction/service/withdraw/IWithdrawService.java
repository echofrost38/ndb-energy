package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.models.withdraw.BaseWithdraw;

public interface IWithdrawService {
    // create requset 
    public BaseWithdraw createNewWithdrawRequest(BaseWithdraw baseWithdraw);

    // confirm(approve or denied) request by admin
    public int confirmWithdrawRequest(int requestId, int status, String reason) throws Exception;

    // get withdraw transactions by user
    public List<? extends BaseWithdraw> getWithdrawRequestByUser(int userId);

    // get withdraw tx by status (penidng or approved)
    public List<? extends BaseWithdraw> getWithdrawRequestByStatus(int userId, int status);

    // get pending requests by admin 
    public List<? extends BaseWithdraw> getAllPendingWithdrawRequests();

    public BaseWithdraw getWithdrawRequestById(int id, int status);
}
