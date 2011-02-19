package unipi.p2p.chord.visualization;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import java.util.Hashtable;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Constants;

public class ChordRingCanvas extends JPanel  {
	private static final Logger log = Logger.getRootLogger();
	private static  int DIVISOR = 360;
	Point center;
	int size;
	int radius;
	//BigInteger[] ocInSlots = new BigInteger[DIVISOR];
	Hashtable nodes;
	boolean pointInRing = false;
	int numberOfNodesInPoint = 0;
	int indexInOcInSlots = -1;
	Vector vec;
	BigDecimal step;
	ChordViewer chordViewer;
	BufferedImage bufImage;
	private BigInteger localHostId;
	

	public ChordRingCanvas(ChordViewer chordViewer) {
		super();
		try {
		this.chordViewer = chordViewer;
		localHostId = chordViewer.getLocalHostID();
		setChordNetwork(chordViewer.getChordNetwork());
		
		/*Iterator i = vec.iterator();
		int k = 1;
		while(i.hasNext()) {
			BigInteger key = (BigInteger)i.next();
			for (; k <= ocInSlots.length; k++ ) {
				String temp = Integer.toString(k);
				if (key.compareTo(step.multiply(new BigInteger(temp))) <= 0) {
					ocInSlots[k-1] = ocInSlots[k-1].add(new BigInteger("1"));
					break;
				}
			}
			
			
		}
		*/
		} catch(Exception e) {
			log.fatal("Exception in ChordRingCanvas:" + e.toString());
		}
		//addMouseListener(this);
		//addMouseMotionListener(this);
	}
	
	public void setChordNetwork(ChordNetwork cnet) {
		try {
			this.nodes = chordViewer.getChordNetwork().getNodes();
			Set s = nodes.keySet();
			vec = new Vector(s);
			Collections.sort(vec);
			
			//DIVISOR = DIVISOR * vec.size();
			
			BigDecimal bd = new BigDecimal(Constants.TWOPOWERM);
			log.info("bbbb" + bd.toPlainString());
			log.info("bbb" + vec.size());
			BigDecimal temp = new BigDecimal(nodes.size());
			log.info("bbb" + temp.toPlainString());
			step = bd.divide(temp);
			log.info("bbb" + step.toPlainString());
				
		} catch(Exception e) {
			log.error("Exception in ChordRingCanvas:" + e.toString());
		}
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param bi An identifier
	 * @return The slot number that bi identifier belongs to.
	 */
	public double getSlotNumber(BigInteger bi) {
		try {
			BigDecimal bd = new BigDecimal(bi);
			return (bd.divide(step).doubleValue() * DIVISOR )/ nodes.size(); 
		} catch(Exception e) {
			log.error(":" + e.toString());
			return 1;
		}
	}
	
	
	public void drawNodeNumber(BigInteger bi, String message) {
		Graphics g = this.getGraphics();
		center = new Point((size / 2) + 10, (size / 2) + 10);
        radius = size / 2;
        double radian = (2 * Math.PI) / DIVISOR;
        log.info("11aaa" + bi.toString());
        log.info("11aaa" + getSlotNumber(bi));
        double myRadian = (Math.PI + Math.PI / 2) + (radian * getSlotNumber(bi));
        int xCircle = center.x + (int) (Math.cos(myRadian) * radius);
   	    int yCircle = center.y + (int) (Math.sin(myRadian) * radius);
        
   	    
   	    g.drawString(message, xCircle, yCircle);
       	
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Dimension d = this.getSize();
		int temp = d.width > d.height ? d.height : d.width;
        size = temp - 50;
       
        //this.bufImage = new BufferedImage(temp, temp, BufferedImage.TYPE_INT_ARGB);
        //Graphics2D gImage = bufImage.createGraphics();
        Graphics2D g2 = (Graphics2D)g;
        //g2.setStroke(new BasicStroke(2));
		//g2.drawOval(10, 10, size, size);
        //g2.setStroke(new BasicStroke(4));
		//displayDensityInRing(g2);
		//gImage.setStroke(new BasicStroke(2));
		g2.drawOval(10, 10, size, size);
		center = new Point((size / 2) + 10, (size / 2) + 10);
        radius = size / 2;
        double radian = (2 * Math.PI) / DIVISOR;
        double myRadian = (Math.PI + Math.PI / 2) + (radian * getSlotNumber(localHostId));
        int xCircle = center.x + (int) (Math.cos(myRadian) * radius);
   	    int yCircle = center.y + (int) (Math.sin(myRadian) * radius);
        
   	    
   	    g2.drawString("LocalHost", xCircle, yCircle);
		//gImage.setStroke(new BasicStroke(4));
		//displayDensityInRing(gImage);
		//g2.drawImage(bufImage, new AffineTransform(1f,0f,0f,1f,0,0), null);
        
		
	}
	
	

/*	private void displayDensityInRing(Graphics2D g) {
		try {
			
			int startAngle = 90;
			int endAngle = 360 / DIVISOR;
			
			int balancedNumOfNodesPerSlot = nodes.size() / DIVISOR;
			
			for(int counter = 0; counter < DIVISOR; counter++) {
				int temp = ocInSlots[counter].intValue();
				
				if (temp == 0) {
					g.setColor(Color.BLACK);
				} else if (0 < temp  && temp < (balancedNumOfNodesPerSlot/2)) {
					g.setColor(Color.BLUE);
				} else if ( (balancedNumOfNodesPerSlot/2) <= temp  && temp <= balancedNumOfNodesPerSlot) {
					g.setColor(Color.GREEN);
				}else if (balancedNumOfNodesPerSlot < temp  && temp <= (balancedNumOfNodesPerSlot * 3 / 2)) {
					g.setColor(Color.PINK);
				} else {
					g.setColor(Color.RED);
				}
				g.drawArc(10, 10, size, size, startAngle, -endAngle);
				startAngle = startAngle - endAngle;
				
				
			}
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	*/
	/*
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		
		if (pointInRing && numberOfNodesInPoint != 0) {
			BigInteger step = Constants.TWOPOWERM.divide(new BigInteger(Integer.toString(DIVISOR)));
			BigInteger start;
			if (this.indexInOcInSlots == 0) {
				start = BigInteger.ZERO;
			} else {
				start = step.multiply(new BigInteger(Integer.toString(indexInOcInSlots - 1)));
			}
			BigInteger end = step.multiply(new BigInteger(Integer.toString(indexInOcInSlots + 1)));
			Set set = nodes.keySet();
			TreeSet ts = new TreeSet(set);
			SortedSet ss = ts.subSet(start, end);
			Object[] bigInts = ss.toArray();
			int index = bigInts.length - numberOfNodesInPoint;
			if (index >= 0 && index < bigInts.length) {
				BigInteger bi = (BigInteger)bigInts[index];
				ChordNode cn = (ChordNode)nodes.get(bi);
				
				mainFrame.setInfoData(cn.getLocalConnection().toString(), 
						cn.getLocalConnection().bigIntValue().toString(),
						cn.successor().bigIntValue().toString(),
						cn.predecessor().bigIntValue().toString(), 
						cn.getFingerTable());
				
			}
			numberOfNodesInPoint--;
		}

	}

	public void mouseReleased(MouseEvent e) {
		

	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent e) {
		 center = new Point((size / 2) + 10, (size / 2) + 10);
         radius = size / 2;
         double radian = (2 * Math.PI) / DIVISOR;
         
         for(int i = 1; i <=DIVISOR; i++) {
        	 double myRadian = (Math.PI + Math.PI / 2) + (radian * i);
        	 int x = e.getX();
        	 int y = e.getY();
        	 int xCircle = center.x + (int) (Math.cos(myRadian) * radius);
        	 int yCircle = center.y + (int) (Math.sin(myRadian) * radius);
        	 if ((x == xCircle) && (y == yCircle)) {
        		 System.out.println("Found point in circle " + x + ",  " + y + " in " + (i-1));
        		 System.out.println("We have " + ocInSlots[i-1].toString() + " nodes in this slot");
        		 System.out.println(Color.GREEN.getRGB()+ " " + bufImage.getRGB(e.getX(), e.getY()));
        		 pointInRing = true;
        		 numberOfNodesInPoint = Integer.parseInt(ocInSlots[i-1].toString());
        		 this.setToolTipText("We have " + ocInSlots[i-1].toString() + " nodes in this slot");
        		 indexInOcInSlots = i-1;
        		 mainFrame.getRing().setData(mainFrame, indexInOcInSlots, numberOfNodesInPoint);
     			
        		 return;
        	 }
        	 
         }
        
        	// numberOfNodesInPoint = 0;
        //	 pointInRing = false;
        	// indexInOcInSlots = -1;
        
	}
*/
}
