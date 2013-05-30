/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionapp;

import auctionsystem.ejb.*;
import javax.ejb.EJB;

/**
 *
 * @author mis
 */
public class TestClient {

    @EJB
    private static AuctionManagerRemote auctionManager;

    public static void main(String[] args) {
        String message = "hello";
        System.out.println("Sending " + message);
        String reply = auctionManager.communicationTest(message);
        System.out.println(reply);
    }
}
