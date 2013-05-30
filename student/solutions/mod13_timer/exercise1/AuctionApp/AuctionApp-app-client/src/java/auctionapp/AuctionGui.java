
package auctionapp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.logging.*;

public class AuctionGui implements ActionListener{
  // Auction variables
  private TestClient client;
  // User panel variables
  private JPanel userPanel;
  private JComboBox useCaseCb;
  private String [] useCaseStrings = {
    "addItem  description image",
    "addBookItem description image title author",
    "addAuction  startAmount  increment  numberOfDays  description  image  sellerID",
    "addAuctionForSeconds  startAmount  increment  numberOfSeconds  description  image  sellerID",
    "closeAuctionBeforeTimeout  startAmount  increment  numberOfSeconds  description  image  sellerID",
    "placeBid  auctionID  bidderID  bidAmount",
    "showAllOpenAuctions",
    "showAuctionsOfSeller  sellerID",
    "showSeller  displayName",
    "receiveBidStatusMessage auctionID",
    "autoBid(auctionID, bidderID, bidAmt, maxBidAmt, bidIncrement",
    "hello"
  };
  private JTextField userInputTf;
  private JLabel commandFormatLb;
  private JLabel userInputLb;
  private String logName = "AuctionApp Log"; 
  private Logger logger = Logger.getLogger(logName);
  private GuiLogPanel logPanel;
  private JFrame frame;
  private Container contentPane;
  //protected PanelViewer viewDeck;
  //private AuctionPanel[] panels= new AuctionPanel[10];
  private JLabel templogLb = new JLabel("Place Holder for Log Panel", SwingConstants.CENTER);

  
  /** Creates a new instance of BrokerGUI */
  public AuctionGui(TestClient client) {
    this.client = client;
    buildDisplay();
  }
  
  /*
  public void addController(BrokerController controller){
    brokerController=controller;
    for(int i=0;panels[i]!=null;i++)
      panels[i].registerController(controller);
  }
  */
  protected void buildDisplay(){
    frame = new JFrame("AuctionApp TestClient");
    //viewDeck = new TabPanelViewer();
    //panels[0] = new AuctionPanel(model);
    //viewDeck.addView(panels[0].getPanel(), "Auction Use Cases");

    // Code to build AllCustomersPanel 
    // panels[1] = new AllCustomerTablePanel(model); // was new AllCustomerPanel(model);
    // viewDeck.addView(panels[1].getPanel(), "All Customers");

    // Code to build the other Panels goes here

    // build and display frame
    contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    // contentPane.add(viewDeck.getPanel(), BorderLayout.NORTH);
    // 
    // build userPanel
    userPanel = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    userPanel.setLayout(gridbag);
    commandFormatLb = new JLabel("Auction Use Case Format");
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1.0;
    gridbag.setConstraints(commandFormatLb, c);
    userPanel.add(commandFormatLb);
//    commandFormatLb = new JLabel("Auction Use Case Format");
//    c.weightx = 1.0;
//    gridbag.setConstraints(commandFormatLb, c);
    useCaseCb = new JComboBox(useCaseStrings);
    c.gridwidth = GridBagConstraints.REMAINDER; //end row
    gridbag.setConstraints(useCaseCb, c);
    userPanel.add(useCaseCb);
    userInputLb = new JLabel("Auction Use Case Input");
    c.gridwidth = 1;
    gridbag.setConstraints(userInputLb, c);
    userPanel.add(userInputLb);
    userInputTf = new JTextField (70);
    c.gridwidth = GridBagConstraints.REMAINDER; //end row
    gridbag.setConstraints(userInputTf, c);
    userPanel.add(userInputTf);
    userInputTf.addActionListener(this);
    contentPane.add(userPanel, BorderLayout.NORTH);

    // build and add log panel to contentPane
    JLabel templogLb = new JLabel("Place Holder for Log Panel", SwingConstants.CENTER);
    //contentPane.add(templogLb, BorderLayout.SOUTH);
    logPanel = new GuiLogPanel(logName);
    contentPane.add(logPanel, BorderLayout.CENTER); 
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible( true );
    //viewDeck.showView("All Customers");
  }
  
  public void displayMessage(String input){
    //logger.severe(input);
    logPanel.addToLog("\n" + input);
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals(""))command = " ";
    System.out.println("User input: " + command);
    displayMessage("User input: " + command);
    String commandArray[] = command.split(" ");
    // for (int i=0; i<commandArray.length; i++){
    //   displayMessage(commandArray[i]);
    // }
    String result = client.processUserInput(commandArray);
    displayMessage(result);
  }
  
  public static void main(String args[]){
    AuctionGui gui = new AuctionGui(null);
  }


}
