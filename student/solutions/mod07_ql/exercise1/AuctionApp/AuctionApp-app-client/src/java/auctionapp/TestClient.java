/*
 * TestClient.java
 * Mod4 version
 * Created on Oct 3, 2006, 4:11 PM  
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package auctionapp;

import auctionsystem.ejb.*;
import auctionsystem.entity.*;
import auctionsystem.exception.*;

import javax.ejb.*;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

//imports for BidStatusMessageReceiving
import java.io.*;
import javax.jms.*;
import javax.naming.*;
import javax.annotation.Resource;
import auctionsystem.dto.BidStatusMessage;

//imports for autoBid functionality
import auctionsystem.dto.PlaceBidMessage;

/**
 *
 * @author asokp
 */
public class TestClient
       implements MessageListener
{
   @EJB
   private static AuctionManagerRemote auctionManager;
   
   //For messageStatusBidReceiver
   //** @Resource(mappedName = "jms/bidsPlacedTopic")
   private static Topic bidsPlacedTopic;
   //** @Resource(mappedName = "jms/TopicConnectionFactory")
   private static ConnectionFactory consumerConnectionFactory;
   private Connection consumerConnection = null;
   private Session consumerSession = null;
   private MessageConsumer consumer = null;
   private Hashtable auctionRegister = new Hashtable();
   
   // on the queue "placeBidQueue" place
   // bid messages are sent
   //** @Resource(mappedName = "jms/placeBidQueue")
   private static Queue placeBidQueue = null;
   //** @Resource(mappedName = "jms/QueueConnectionFactory")  
   private static ConnectionFactory producerConnectionFactory;
   private Connection producerConnection = null;
   private Session producerSession = null;
   private MessageProducer producer = null;
   private Hashtable autobidRegister = new Hashtable();
   
   boolean communicationEstablished = false;
   static TestClient client;
   static AuctionGui auctionGui;
   /** Creates a new instance of TestClient */
   public TestClient() 
   {
      try 
      {
        /*
         producerConnection = producerConnectionFactory.createConnection("guest", "guest");
         producerSession = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         producer = producerSession.createProducer(placeBidQueue);
         System.out.println("TestClient has created an Auction Message Producer.");
        */
        
        /*
         consumerConnection = consumerConnectionFactory.createConnection("guest", "guest");
         consumerSession = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         consumer = consumerSession.createConsumer(bidsPlacedTopic);
         consumer.setMessageListener(this);
         consumerConnection.start();
         System.out.println("TestClient has registered to listen for message.");
        */
      }
      catch (Exception je) 
      {
         je.printStackTrace();
         System.exit(-1);
      }
      auctionGui = new AuctionGui(this);
   } // End Constructor
  
   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      client = new TestClient();
      String reply = auctionManager.communicationTest("hello on startup");
      auctionGui.displayMessage(reply);
      if (args.length != 0) {
         auctionGui.displayMessage(client.processUserInput(args));
      }
   } // end method main

   
   public String processUserInput(String args[]) 
   {
      String result = null;

      if (args.length == 0 || args[0].equalsIgnoreCase("hello")) {
         result = auctionManager.communicationTest("client <-> server ");
         System.out.println(result);
         return result;
      }

      // switch on use case
      if ((args[0].compareTo("addItem") == 0)) {
         if (args.length != 3) {
            return ("addItem description image");
         }
         return addItem(args[1], args[2]);
      } else if ((args[0].compareTo("addBookItem") == 0)) {
         if (args.length != 5) {
            return ("addBookItem description image title author");
         }
         return addBookItem(args[1], args[2], args[3], args[4]);
      } else if ((args[0].compareTo("addAuction") == 0)) {
         if (args.length != 7) {
            return ("addAuction startAmount increment numberOfDays +" +
                   " description image sellerID");
         }
         Double startAmount = new Double(args[1]);
         Double increment = new Double(args[2]);
         Integer numberOfDays = new Integer(args[3]);
         Integer sellerID = new Integer(args[6]);
         return addAuction(startAmount.doubleValue(), increment.doubleValue(),
                numberOfDays.intValue(), args[4], args[5], sellerID);
      } else if ((args[0].compareTo("addAuctionForSeconds") == 0)) {
         if (args.length != 7) {
            return ("TestClient needs the following parameters" +
                   "to be specified: addAuctionForSeconds startAmount " +
                   "increment numberOfSeconds description " +
                   "image sellerID");
         }
         Double startAmount = new Double(args[1]);
         Double increment = new Double(args[2]);
         Integer numberOfSeconds = new Integer(args[3]);
         Integer sellerID = new Integer(args[6]);
         int auctionID = addAuctionForSeconds(startAmount.doubleValue(),
                increment.doubleValue(),
                numberOfSeconds.intValue(), args[4],
                args[5], sellerID);
         if (auctionID == -1) {
            return "addAuctionForSeconds failed";
         }
         else {
            return "added AuctionForSeconds actionID: " + auctionID;
         }
      } else if ((args[0].compareTo("closeAuctionBeforeTimeout") == 0)) {
         if (args.length != 7) {
            return ("TestClient needs the following parameters" +
                   "to be specified: closeAuctionBeforeTimeout startAmount " +
                   "increment numberOfSeconds description " +
                   "image sellerID");
         }
         Double startAmount = new Double(args[1]);
         Double increment = new Double(args[2]);
         Integer numberOfSeconds = new Integer(args[3]);
         Integer sellerID = new Integer(args[6]);
         int auctionID = addAuctionForSeconds(startAmount.doubleValue(),
                increment.doubleValue(),
                numberOfSeconds.intValue(), args[4],
                args[5], sellerID);
         if (auctionID == -1) {
            return "addAuctionForSeconds failed";
         }
         else {
            closeAuction(auctionID);
            return "added then closed auction: actionID=" + auctionID;
         }
      } else if ((args[0].compareTo("closeAuction") == 0)) {
         if (args.length != 2) {
            return ("closeAuction auctionID");
         }
         Integer auctionID = new Integer(args[1]);
         closeAuction(auctionID);
         return "closed auction: actionID=" + auctionID;
      } else if ( (args[0].compareTo("placeBid") == 0)) {
         if (args.length != 4) {
            return("placeBid auctionID bidderID bidAmount");
         }
         Integer auctionID = new Integer(args[1]);
         Integer bidderID = new Integer(args[2]);
         Double bidAmount = new Double(args[3]);
         return placeBid(auctionID, bidderID, bidAmount.doubleValue());
      } else if ((args[0].compareTo("showAllOpenAuctions") == 0)) {
         return (showAllOpenAuctions());
      } else if ((args[0].compareTo("showAuctionsOfSeller") == 0)) {
         if (args.length != 2) {
            return ("showAuctionsOfSeller sellerID");
         }
         Integer sellerID = new Integer(args[1]);
         return (showAuctionsOfSeller(sellerID));
      } else if ( (args[0].compareTo("showSeller") == 0)) {
         if (args.length != 2) {
            return ("showSeller displayName");
         }
         return (showSeller(args[1]));
      }  else if ( (args[0].compareTo("receiveBidStatusMessage") == 0)) {
         System.out.println("rBSM argsLen = " + args.length);
         if (args.length != 2) {
            return ("receiveBidStatusMessage auctionID");
         }
         Integer auctionID = new Integer(args[1]);
         try {
           auctionRegister.put(auctionID, "");
         } catch (Exception e) {
             System.out.println("Null pointer exception 1");
         }
         return "\t registered to participate in auction # " + auctionID.intValue();
      }  else if ( (args[0].compareTo("autoBid") == 0)) {
         System.out.println("autoBid argsLen = " + args.length);
         if (args.length != 6) {
            return ("autoBid auctionID bidderID bidAmount maxAmount increment");
         }
         System.out.println("Starting autoBidder service ...");
         // processing command line arguments:
         Integer auctionID    = new Integer(args[1]);
         Integer bidderID     = new Integer(args[2]);
         Double  bidAmt       = new Double(args[3]);
         Double  maxBidAmt    = new Double(args[4]);
         Double  bidIncrement = new Double(args[5]);
         return autoBid(auctionID, bidderID, bidAmt, maxBidAmt, bidIncrement);
      }
      else {
         return ("Invalid use case. \nValid use cases: [addItem|" +
                "  addAuction|" +
                "  showAllAuctions|" +
                "  showAuctionsOfSeller|" +
                "  showSeller|" +
                "  placeBid|" +
                "  closeAuction]");
      }
   } /* End processUserInput */

   
   //addItem method
   public String addItem(String description, String image) 
   {
      Object obj = null;
      try
      {
         obj = auctionManager.addItem(description, image);
         return ("Created " + obj);
      }
      catch (EJBException ee) 
      {
         ee.printStackTrace();
         return ee.getMessage();
      }
   } /* End addItem */

  
   public String addBookItem(String description, String image, String title, String author)
   {
      Object obj = null;
      try
      {
         obj = auctionManager.addBookItem(description, image, title, author);
         return ("Created " + obj);
      }
      catch (EJBException ee) 
      {
         ee.printStackTrace();
         return ee.getMessage();
      }
   }
  
  
  // addAuction method
  public String addAuction(double startAmount, double increment,
    int numberOfDays, String description, String image, Integer sellerID) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, numberOfDays);
    Date closeTime = calendar.getTime();
    int auctionID = 0;
    try {
      auctionID = auctionManager.addAuction(startAmount, increment, closeTime, 
        description, image, sellerID);
      return ("     User #" + sellerID.intValue() +
        " added an auction with the ID = " + auctionID);
    /*} catch (CreateException ce) {
      ce.printStackTrace();
      System.exit(-1);
    } */
    } catch (EJBException re) {
      re.printStackTrace();
      return re.getMessage();
    }
  }
  
  
  public int addAuctionForSeconds(double startAmount, double increment,
    int numberOfSeconds, String description, String image, Integer sellerID) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, numberOfSeconds);
    Date closeTime = calendar.getTime();
    int auctionID = 0;
    try {
      auctionID = auctionManager.addAuction(startAmount, increment, closeTime, 
        description, image, sellerID);
    } catch (EJBException re) {
      re.printStackTrace();
      return -1;
    }

    System.out.println(" User #" + sellerID.intValue() +
      " added an auction with the ID = " + auctionID);
    return auctionID;
  } // end method addAuctionforSeconds


   /** places a bid via the session facade
    * <code>AuctionManager</code>.
    */
   public String placeBid(Integer auctionID, Integer bidderID, double amount) 
   {
      try {
         auctionManager.placeBid(auctionID, bidderID, amount);
      } catch (AuctionException ae) {
         ae.printStackTrace();
         return ae.getMessage();
      } catch (Exception ee) {
         ee.printStackTrace();
         return ee.getMessage();
      }
      
      return (" User #" + bidderID.intValue() + " has placed a bid");
   } // end method placeBid
 
  
  public String showAllOpenAuctions() {
    Collection auctionDTOs = null;
    String result = null;
    // **action AuctionOutDTO auctionDTO = null;
    try {
      auctionDTOs = auctionManager.getAllOpenAuctions();      
    } catch (Exception re) {
      re.printStackTrace();
      System.exit(-1);
    }
    result = " Open Auctions are:";
    Iterator i = auctionDTOs.iterator();
    Auction auction = null;
    int openAuctionCount = 0;
    while (i.hasNext()) {
      openAuctionCount++;      
      // **action auctionDTO = (AuctionDTO) i.next();
      auction = (Auction)i.next();
      // **action System.out.println(auctionDTO.toString());
      result = result + "\nAuction " + auction.getId() + "  is open";
    }
    return result + "\nOpen auction count = " + openAuctionCount;        
  } // end method showAllOpenAuctions();

  
  /** displays all auctions of a specific seller
   * by querying the database via the
   * <code>AuctionManager</code> session
   * facade.
   *
   * @param sellerID the ID of the seller whose
   *        auctions shall be displayed
   */
  public String showAuctionsOfSeller(Integer sellerID) {
    Collection auctionDTOs = null;
    String result = null;     
    try {
      auctionDTOs = auctionManager.getAllAuctionsOfSeller(sellerID);
    } catch (Exception ee) {
      System.out.println("TC.showAuctionsOfSeller: Exception");
      ee.printStackTrace();
      return ee.getMessage();
    }
 
    if (auctionDTOs.isEmpty()) {
      result = ("The user with the ID " + sellerID +
        " does not offer an auction");
    } else {
      result = (" The seller with the ID " + sellerID +
        " has the following open auctions:");
      Iterator i = auctionDTOs.iterator();
      Auction auction = null;
      while (i.hasNext()) {
        auction = (Auction) i.next();
        result = result + "\nAuction " + auction.getId();
      }
    } // end else
    return result;
  } // end method showAuctionsOfSeller();
  
  
  public String showSeller(String displayName) {
    AuctionUser sellerDTO = null;
    
    try {
      sellerDTO = (AuctionUser) auctionManager.getSeller(displayName);
    } catch (EJBException ee) {
      return ("TC.getSeller: EJBException " + ee.getMessage() + "  " 
        + ee.getCausedByException() + "  " + ee);
      //ee.printStackTrace();
    } catch (Exception e) {
      return ("TC.getSeller: Exception");
    }
    return " Seller's " + displayName + " personal data: " + sellerDTO;
  } // end method showSeller

  
  public String closeAuction(Integer auctionID) {
        System.out.println(" Closing the auction " + auctionID);
        try {
            auctionManager.closeAuction(auctionID);
        } catch (AuctionException ae) {
            ae.printStackTrace();
            return ae.getMessage();
        } 
        return "Closed auction " + auctionID.intValue();
    } // end method closeAuction

  
   public void auctionMessageRegister(Integer auctionID)
   {
      auctionGui.displayMessage("   --> Registering for Auction Status Messages: Auction #" + auctionID);
   }

   
   public String autoBid(Integer auctionID, Integer bidderID, Double bidAmt, Double maxBidAmt, Double bidIncrement)
   {
      try
      {
         Object[] bid = { bidderID, maxBidAmt, bidIncrement };
         auctionGui.displayMessage("   --> Attempting to start an auto-bid session\n"
                +"   \tAuction: " + auctionID
                +"   \tBidder : " + bid[0]
                +"   \tMax Bid: " + bid[1]
                +"   \tBid Imt: " + bid[2]);

         System.out.println(" \n \t participating in auction # " + auctionID.intValue());
         System.out.println(" \t placing a bid ... ");
         
         // create place bid message:
         PlaceBidMessage placeBidMessage = new PlaceBidMessage(auctionID, bidderID, bidAmt);
         System.out.println(" \t sending object " + placeBidMessage.toString());
         
         // send the place bid message:
         ObjectMessage message = producerSession.createObjectMessage(placeBidMessage);
         producer.send(message);
         try {
           autobidRegister.put(auctionID, bid);
         } catch (Exception e) {
             System.out.println("Null pointer exception 2");
         }
         return " Auto-Bid created Successfully.";
      }
      catch (JMSException je)
      {
         je.printStackTrace();
         return " FAILED: Auto-Bid Session could not be created.";
      }
   }

  
   public void onMessage(Message message) 
   {
      auctionGui.displayMessage("   --> Heard there was a message in TestClient!!");
      try {
         if (auctionRegister.containsKey(new Integer(message.getIntProperty("auctionID"))))
         {
            // process the received message:
            ObjectMessage objectMessage = (ObjectMessage) message;
            BidStatusMessage bidStatusUpdateMessage = (BidStatusMessage) objectMessage.getObject();

            System.out.println("  \t a message was received " + bidStatusUpdateMessage.toString() );
            auctionGui.displayMessage("  \t You are registered for bid status messages on auction #" + message.getIntProperty("auctionID"));
            auctionGui.displayMessage("  \t\t a bid message was received: " + bidStatusUpdateMessage.toString());
         }
         else if (autobidRegister.containsKey(new Integer(message.getIntProperty("auctionID"))))
         {
            Object[] myBid = (Object[]) autobidRegister.get(new Integer(message.getIntProperty("auctionID")));
            Integer myBidderID     = (Integer) myBid[0];
            Double  myMaxBidAmt    = (Double)  myBid[1];
            Double  myBidIncrement = (Double)  myBid[2];

            // process the received message:
            ObjectMessage objectMessage = (ObjectMessage) message;
            BidStatusMessage bidStatusUpdateMessage = (BidStatusMessage) objectMessage.getObject();

            System.out.println("  \t a message was received " + bidStatusUpdateMessage.toString() );
            auctionGui.displayMessage("  \t You are registered to autobid on auction #" + message.getIntProperty("auctionID"));
            auctionGui.displayMessage("  \t\t a bid message was received: " + bidStatusUpdateMessage.toString());

            double  bidAmt = bidStatusUpdateMessage.getBidAmount();
            Integer bidder = bidStatusUpdateMessage.getBidderID();
            String  status = bidStatusUpdateMessage.getStatus();
            auctionGui.displayMessage("  \t   New Bid: " + bidAmt
                   +"   \tBidder: " + bidder
                   +"   \tStatus: " + status);
            auctionGui.displayMessage("  \t   Your Auto-Bid Definition\n"
                   +"   \tBidder : " + myBidderID
                   +"   \tMax Bid: " + myMaxBidAmt
                   +"   \tBid Imt: " + myBidIncrement);
            
            // check business constraints against message that was received:
            if (status.compareTo("OPEN") == 0 ) {
               // check if it's my placed bid:
               if (bidder.intValue() != myBidderID.intValue()) {
                  System.out.println(" \n \t somebody else placed a bid");
                  // check if another bid can be placed:
                  if (bidAmt + myBidIncrement.doubleValue() <= myMaxBidAmt.doubleValue()) {
                     double bidAmount = bidAmt + myBidIncrement.doubleValue();
                     // create place bid message:
                     PlaceBidMessage placeBidMessage = new PlaceBidMessage(
                            new Integer(message.getIntProperty("auctionID")),
                            myBidderID, bidAmount);
                     System.out.println(" \t sending object " + placeBidMessage.toString());
         
                     // send the place bid message:
                     ObjectMessage bidMessage = producerSession.createObjectMessage(placeBidMessage);
                     producer.send(bidMessage);
                  } else {
                     System.out.println("\n \t over bid limit !");
                  }
               }
               else {
                  System.out.println("\n \t it's my own bid");
               }
            } // end if(auctionStatus.compareTo("OPEN") == 0 )
         }
         else
         {
            System.out.println("  \t not registered to listen to auction #" + message.getIntProperty("auctionID"));
            auctionGui.displayMessage("  \t not registered to listen to auction #" + message.getIntProperty("auctionID"));
         }
      } catch (ClassCastException cce) {
         cce.printStackTrace();
         System.exit(-1);
      } catch (JMSException je) {
         je.printStackTrace();
         System.exit(-1);
      }
   }
}
