
package auctionapp;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.logging.*;
import java.io.*;

public class GuiLogPanel extends JPanel {
  
  /** Creates a new instance of LogPanel */
  Logger logger;
  // LogPanel Components
  protected JLabel logLb;
  protected JTextArea logTa= new JTextArea(9, 50);
  protected JScrollPane logSp = new JScrollPane(logTa);
  
  private String logName;
  
  public GuiLogPanel(String logName) {
    this.logName = logName;
    buildLogPanel();
    constructLogger();
  }
  
  void buildLogPanel() {
    logLb = new JLabel(logName, SwingConstants.CENTER);
    this.setLayout(new BorderLayout());
    this.add(logLb, BorderLayout.NORTH);
    this.add(logSp, BorderLayout.CENTER);
  }
  
  public void constructLogger(){
    logger=Logger.getLogger(logName);
    // logger.addHandler(new GuiLogHandler(this));
  }
  
  public void setLog(String log){
    logTa.setText(log);
  }
  
  public void addToLog(String newLogEvent){
    logTa.append(newLogEvent);
    logTa.setCaretPosition(logTa.getText().length());
  }
}
