/*
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package auctionsystem.exception;

/** This is the exception which is thrown
 * when the credit card cannot be used (like
 * for example overdrawing).
 */
public class CreditCardException extends AuctionException {
    public CreditCardException() {}
    
    public CreditCardException(String msg) {
        super(msg);
    }
}
