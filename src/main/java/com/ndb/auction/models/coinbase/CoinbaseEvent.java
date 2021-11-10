package com.ndb.auction.models.coinbase;

public class CoinbaseEvent {

    private String id;
    private String scheduled_for;
    private CoinbaseEventBody event;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getScheduled_for() {
        return scheduled_for;
    }
    public void setScheduled_for(String scheduled_for) {
        this.scheduled_for = scheduled_for;
    }
    public CoinbaseEventBody getEvent() {
        return event;
    }
    public void setEvent(CoinbaseEventBody event) {
        this.event = event;
    }

}
