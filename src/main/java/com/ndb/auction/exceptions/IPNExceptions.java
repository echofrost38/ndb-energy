package com.ndb.auction.exceptions;

public class IPNExceptions extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1517545468154220665L;
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
