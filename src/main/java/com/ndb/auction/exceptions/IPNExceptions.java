package com.ndb.auction.exceptions;

public class IPNExceptions extends RuntimeException {
	private String message;
	
	public IPNExceptions(String message) {
        super(message);
        this.message = message;
    }
	
	public String getMessage() {
		return message;
	}

    public IPNExceptions() {
    	
    }
}
