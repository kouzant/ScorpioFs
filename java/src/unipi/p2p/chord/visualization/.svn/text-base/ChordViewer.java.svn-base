package unipi.p2p.chord.visualization;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Constants;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.RemoteChordNode;
import unipi.p2p.chord.util.Util;


public class ChordViewer extends JFrame implements ActionListener{
	private static final Logger log = Logger.getRootLogger();
	//SimulatorCanvas sc;
	JPanel infoPanel;
	JButton refreshButton = new JButton();
	private JTable routingTable = null;
	JTextField nodeIPTF;
	JTextField nodeIDTF;
	JTextField nodeSucTF;
	JTextField nodePreTF;
	Hashtable nodes;
	ChordNetwork cNet = null;
	RemoteChordNode startingNode = null;
	ChordNode cn = null;
	static ChordRingCanvas cr;
	JLabel infoLabel = new JLabel();
	//Ring ring;
	
	public ChordViewer(ChordNode cn) {
		super("Chord Viewer");
		log.info("starting the viewer");
		
		//this.nodes = cNet.getChordNodes();
		try {
			this.cn = cn;
			
			startingNode = cn.getRemoteChordNode(cn.getLocalNode());
			
			cNet = new ChordNetwork(startingNode);
			nodes = cNet.getNodes();
			
			initGui();
			
			cr.drawNodeNumber(startingNode.getLocalNode().getBigIntValue(), "LocalHost");
			
		} catch(Exception e) {
			log.error("Exception in viewer " + e.toString() + "   "+ e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	
	public ChordNetwork getChordNetwork() {
		return cNet;
	}
	
	public BigInteger getLocalHostID() {
		try {
			return startingNode.getLocalNode().getBigIntValue();
		} catch(Exception e) {
			return null;
		}
	}
	
	private void displayNodeToInfoPanel(RemoteChordNode cn) {
		try {
			nodeIPTF.setText(cn.getLocalNode().toString());
			nodeIDTF.setText(cn.getLocalNode().getBigIntValue().toString());
			nodeSucTF.setText(cn.successor().getBigIntValue().toString());
			if (cn.predecessor() != null) {
				nodePreTF.setText(cn.predecessor().getBigIntValue().toString());
			} else {
				nodePreTF.setText("NULL");
			}
			Finger[] fingerTable = cn.getFingers();
			TableModel tm = routingTable.getModel();
			for (int i = 0; i < fingerTable.length; i++) {
				if (fingerTable[i] != null) {
					tm.setValueAt(fingerTable[i].getBigIntValue().toString(), i, 1);
				} else {
					tm.setValueAt("Empty", i, 1);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void initGui() throws Exception {
		this.setSize(800, 600);
		cr = new ChordRingCanvas(this);
		initInfoPanel();
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		
		this.setLayout(new BorderLayout());
		//ring = new Ring("dummy number of slots", 34);
		this.infoLabel.setText("Remote Calls:" + 
				cn.getStatistics().getRemoteCalls() + 
				", Successor List Calls:" + 
				cn.getStatistics().getSuccessorListCalls() + 
				", Predecessor Calls:" + cn.getStatistics().getCheckPredecessorCalls() + 
				", Fix Fingers Calls:" + cn.getStatistics().getFixFingerCalls() + 
				", Stabilize Calls:" + cn.getStatistics().getStabilizeCalls());
		this.add(cr, BorderLayout.CENTER);
		this.add(infoPanel, BorderLayout.EAST);
		refreshButton.setText("Refresh");
		
		refreshButton.addActionListener(this);
		southPanel.add(refreshButton, BorderLayout.EAST);
		southPanel.add(infoLabel, BorderLayout.WEST);
		this.add(southPanel, BorderLayout.SOUTH);
		
		//this.add(ring, BorderLayout.NORTH);
		this.doLayout();
		this.displayNodeToInfoPanel(startingNode);
		this.setVisible(true);
		
	}
	
	public void initInfoPanel() throws Exception {
		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		JPanel nodeInfoPanel = new JPanel();
		JLabel l1 = new JLabel("Node:");
		nodeIPTF = new JTextField("");
		nodeIPTF.setEditable(false);
		nodeIPTF.setEnabled(false);
		JLabel l2 = new JLabel("ID:");
		nodeIDTF = new JTextField("");
		nodeIDTF.setEditable(false);
		nodeIDTF.setEnabled(false);
		JLabel l3 = new JLabel("Successor ID:");
		nodeSucTF = new JTextField("");
		nodeSucTF.setEditable(false);
		nodeSucTF.setEnabled(false);
		JLabel l4 = new JLabel("Predecessor ID:");
		nodePreTF = new JTextField("");
		nodePreTF.setEditable(false);
		nodePreTF.setEnabled(false);
		nodeInfoPanel.setLayout(new GridLayout(8, 1));
		nodeInfoPanel.add(l1);
		nodeInfoPanel.add(nodeIPTF);
		nodeInfoPanel.add(l2);
		nodeInfoPanel.add(nodeIDTF);
		nodeInfoPanel.add(l3);
		nodeInfoPanel.add(nodeSucTF);
		nodeInfoPanel.add(l4);
		nodeInfoPanel.add(nodePreTF);
		infoPanel.add(nodeInfoPanel);
		JPanel routingTablePanel = new JPanel();
		Vector columnNames = new Vector();
		columnNames.add("Index");
		columnNames.add("Finger");
		Vector rowData = getData(null);
		/*final JTable routingTable = new JTable(rowData, columnNames) {
	        public Component prepareRenderer(TableCellRenderer renderer,
	                                         int rowIndex, int vColIndex) {
	            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
	            if (c instanceof JComponent) {
	                JComponent jc = (JComponent)c;
	                jc.setToolTipText((String)getValueAt(rowIndex, vColIndex));
	            }
	            return c;
	        }
	    };*/
		routingTable = new JTable(rowData, columnNames);
		routingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		routingTable.getColumnModel().getColumn(1).setPreferredWidth(450);
		//routingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		routingTable.doLayout();
		JScrollPane scrollPane = new JScrollPane(routingTable);
		routingTablePanel.add(scrollPane);
		infoPanel.add(routingTablePanel);
		ListSelectionModel rowSM = routingTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	 if (e.getValueIsAdjusting()) return;

		         ListSelectionModel lsm =
		             (ListSelectionModel)e.getSource();
		         if (lsm.isSelectionEmpty()) {
		           
		         } else {
		             int selectedRow = lsm.getMinSelectionIndex();
		              System.out.println(selectedRow);
		              String temp = (String)routingTable.getValueAt(selectedRow, 1);
		              
		              try {
		            	  BigInteger bi = new BigInteger(temp);
		            	  cr.drawNodeNumber(bi, Integer.toString(selectedRow));
		            	
		              } catch(Exception ex){}
		         }
		    
		       
		    }
		});
		/*routingTable.addMouseListener(new MouseAdaptor() {
			public void mouseEntered(MouseEvent e) {}
			
			public void mousePressed(MouseEvent e) {
				routingTable.setToolTipText("agapios " + routingTable.getModel().getValueAt(routingTable.getSelectedRow(), 1));
				System.out.println(e.getX() + " " + e.getY() );
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			
		});
		//addSe.setToolTipText(routingTable.getSe)
		*/
	}
	
	public void setInfoData(String nodeIP, String nodeID, String successor, String predecessor, Finger[] fingerTable) {
		nodeIPTF.setText(nodeIP);
		nodeIDTF.setText(nodeID);
		nodeSucTF.setText(successor);
		nodePreTF.setText(predecessor);
		TableModel tm = routingTable.getModel();
		for (int i = 0; i < fingerTable.length; i++) {
			if (fingerTable[i] != null) {
				tm.setValueAt(fingerTable[i].getBigIntValue().toString(), i, 1);
			} else {
				tm.setValueAt("No value", i, 1);
			}
		}

	}
	private Vector getData(Hashtable data) {
		Vector rowData = new Vector();
		if (data == null) {
			for (int i = 0; i < Constants.IDENTIFIER_LENGTH; i++) {
				Vector temp = new Vector();
				temp.add(Integer.toString(i));
				temp.add("e_880284822444692635096307026421501630316665254817");
				rowData.add(temp);
			}
		}
		return rowData;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			cNet = new ChordNetwork(startingNode);
			nodes = cNet.getNodes();
			displayNodeToInfoPanel(startingNode);
			this.infoLabel.setText("Remote Calls:" + 
					cn.getStatistics().getRemoteCalls() + 
					", Successor List Calls:" + 
					cn.getStatistics().getSuccessorListCalls() + 
					", Predecessor Calls:" + cn.getStatistics().getCheckPredecessorCalls() + 
					", Fix Fingers Calls:" + cn.getStatistics().getFixFingerCalls() + 
					", Stabilize Calls:" + cn.getStatistics().getStabilizeCalls());
			cr.setChordNetwork(cNet);
			cr.repaint();
		} catch(Exception ex) {
			log.error("Exception in viewer " + e.toString());
			ex.printStackTrace();
		}
    }
	

}
