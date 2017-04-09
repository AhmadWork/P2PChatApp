

import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class clientInterface extends clientcontoller implements ActionListener {

	static JButton connectButton, sendButton;
	static JLabel  nameLabel, ipLabel, portLabel,partsLabel,yourMsgLabel,msgLabel;
	static JTextArea yourMsgText, msgText, partsText;
	static JTextField nameText, ipText, portText;
	public static int port = 9090;
	

	public clientInterface(){
		setSize(600,580);
		setLocation(600, 50);
		//setLocationRelativeTo(null);
		setResizable(true);
		setTitle("P2P Chat System");
		setLayout(null);
		
		connectButton = new JButton("Connect");
		connectButton.setBounds(400,20,150,30);
		add(connectButton);
		connectButton.addActionListener(this);
		
		msgLabel = new JLabel("messages");
		msgLabel.setBounds(20,100,100,30);
		add(msgLabel);
		
		yourMsgText = new JTextArea("");
		yourMsgText.setLineWrap(true);
		JScrollPane scrollInTxt = new JScrollPane(yourMsgText);
		scrollInTxt.setBounds(20,490,420,50);
		add(scrollInTxt);
		
		msgText = new JTextArea("");
		msgText.setEditable(false);
		msgText.setLineWrap(true);
		JScrollPane scrollOutTxt = new JScrollPane(msgText);
		scrollOutTxt.setBounds(20,125,420,350);
		add(scrollOutTxt);	
		
		partsText = new JTextArea("");
		partsText.setEditable(false);
		partsText.setLineWrap(true);
		JScrollPane scrollLoggedTxt = new JScrollPane(partsText);
		scrollLoggedTxt.setBounds(460,125,100,350);
		add(scrollLoggedTxt);
		
		sendButton = new JButton("Send");		
		sendButton.setBounds(460,490,100,50);
		add(sendButton);
		sendButton.addActionListener(this);
		
		nameLabel = new JLabel("Nick:");
		nameLabel.setBounds(20,20,100,30);
		add(nameLabel);
		ipLabel = new JLabel("IP:");
		ipLabel.setBounds(20,60,100,30);
		add(ipLabel);
		portLabel = new JLabel("port:");
		portLabel.setBounds(370,60,100,30);
		add(portLabel);
		
		nameText = new JTextField("");
		nameText.setBounds(50,20,300,30);
		
		add(nameText);
		ipText = new JTextField("localhost");
		ipText.setBounds(50,60,300,30);
		add(ipText);
		portText = new JTextField(Integer.toString(port));
		portText.setBounds(400,60,150,30);
		add(portText);
		
	}
}
 class clientcontoller  extends JFrame implements Runnable {

	public static void main(String[] args) {
		
		clientInterface cGUI = new clientInterface();
		cGUI.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		cGUI.setVisible(true);
		cGUI.run();
		Thread thread= new Thread();

	}
	// deal with the button that had been click.
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if (s==clientInterface.connectButton){					
			try {
				connectWithServer();
			} catch (Exception e1) {
				e1.printStackTrace();
			}	
		}else if (s==clientInterface.sendButton && connected == true){
			sendMessage();
		}
		
		else if (s==clientInterface.sendButton && connected == false){
			if (JOptionPane.showConfirmDialog(null, "Connect with the server?", "Not connected",
			    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				connectWithServer();
				sendMessage();
			}
		}
	
	}
	// create some variable to use it down 
	InetAddress IPAddress;
	DatagramSocket clientSocketSend;
	DatagramSocket clientSocketReceive;
	byte[] sendData = new byte[1024];
	DatagramPacket sendPacket;
	boolean portOpen = true;
	boolean connected = false;
	int clientListenPort = 8080;
	String usersInfo = "##&&***users***##";
	
	
// running the second part of the thread in the server.
	@Override
	
	public void run() {
		DatagramPacket receivePacket;
		String content;
		InetAddress ipInput;
		int portInput;
		// create socket with port number 
		try {
			clientSocketReceive = new DatagramSocket(8080);				
		} catch (SocketException e1) {
			System.err.println("Err03 client: No port number");
			e1.printStackTrace();
		}
		//receiving data 
		
while (portOpen) {
			
			byte[] receiveData = new byte[1024];			
			receivePacket = new DatagramPacket(receiveData, receiveData.length);			
			try {
				clientSocketReceive.receive(receivePacket);
			} catch (IOException e1) {
				System.out.println("Logged out...");	
				e1.printStackTrace();
			}                
			content = new String(receiveData, 0,receiveData.length);       
			ipInput = receivePacket.getAddress();						
			portInput = receivePacket.getPort();
			if(content.startsWith(usersInfo)){
				// if the contents coming from the server start with userInfo add the user into partsText in client interface
				//clientInterface.partsText.setText("");
				clientInterface.partsText.append(content.substring(usersInfo.length()).trim() + "\n");
			}
			else{
				// if the contents coming from the server start not with userInfo add the msg into msgText in client interface
				clientInterface.msgText.append(content.trim() + "\n");
				clientInterface.msgText.setCaretPosition(clientInterface.msgText.getDocument().getLength());
			}
		}
	
		
	}
	
	String witamInfo = "##&&***witam***##"; 
	
	 // Connecting with server
	 
	private void connectWithServer() {
		//tell him to enter his name if he did not do it.
		if (clientInterface.nameText.getText().equals("")){
			String nickString = JOptionPane.showInputDialog("Please enter your name: ");
			clientInterface.nameText.setText(nickString);
		}
		try {			
			IPAddress = InetAddress.getByName(clientInterface.ipText.getText());		
			clientSocketSend = new DatagramSocket();
			clientInterface.port = Integer.parseInt(clientInterface.portText.getText());
			sendData = (witamInfo + clientListenPort + clientInterface.nameText.getText()).getBytes();
			InetAddress ipHost = InetAddress.getLocalHost();
			sendPacket = new DatagramPacket(sendData, sendData.length, ipHost, clientInterface.port);
			clientSocketSend.send(sendPacket);
			
			clientInterface.msgLabel.setText("connected");
			clientInterface.msgLabel.setForeground(Color.green);
			portOpen = true;
			connected = true;
			clientInterface.connectButton.setEnabled(false);
			clientInterface.nameText.setEnabled(false);
			clientInterface.ipText.setEnabled(false);
			clientInterface.portText.setEnabled(false);
			
		} catch (UnknownHostException e) {
			System.out.println("Err01a: UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Err02a: No client input");
			e.printStackTrace();
		}		
	}
	

	
	/**
	 * Sending a message
	 */
	private void sendMessage() {
		try {			
			IPAddress = InetAddress.getByName(clientInterface.ipText.getText());		
			clientSocketSend = new DatagramSocket();
			clientInterface.port = Integer.parseInt(clientInterface.portText.getText());
			sendData = (usersInfo+clientInterface.yourMsgText.getText()).getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientInterface.port);
			clientSocketSend.send(sendPacket);		
			clientInterface.yourMsgText.setText("");
			
		} catch (UnknownHostException e) {
			System.out.println("Err01c: UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Err02c: No client input");
			e.printStackTrace();
		}		
	}

}
 class serverGuests {
		
		String guestName;
		InetAddress ipInput;						
		int portInput; 
		
		/**
		 * Constructs user connected to server
		 * 
		 * @param guestName users nick
		 * @param ipInput users IP adress
		 * @param portInput users listen-port number 
		 */
		public serverGuests(String guestNick, InetAddress ipInput, int portInput){
			this.guestName = guestNick;		
			this.ipInput = ipInput;
			this.portInput = portInput;
		}
		
	}

 class serverControl extends JFrame {

	public static void main(String[] args) {
		 ArrayList<serverGuests> guestList = new ArrayList<serverGuests>();

		 String witamInfo = "##&&***witam***##";  
		 String usersInfo = "##&&***users***##";
		 DatagramSocket clientSocketReceive = null;
		 try {
				 clientSocketReceive = new DatagramSocket(9090);
			} catch (SocketException e1) {
				System.err.println("Err03 server: No port number");
				e1.printStackTrace();
			}
		 DatagramPacket receivePacket;		
			String content;
			InetAddress ipInput;
			int portInput;
			
			boolean usersChanged = false;
			
			while (true) {
				
				byte[] receiveData = new byte[1024];			
				receivePacket = new DatagramPacket(receiveData, receiveData.length);			
				try {
					clientSocketReceive.receive(receivePacket);
				} catch (IOException e1) {
					System.err.println("Err03d server: No port number");
					e1.printStackTrace();
				}              

				content = new String(receiveData, 0, receiveData.length);   
				ipInput = receivePacket.getAddress();						
				portInput = receivePacket.getPort(); 
				
				String message = null;

				if (content.substring(7,10)=="wit"){
					String guestportTemp = content.substring(17,21); // cutting in port
					String guestNameTemp = content.substring(21).trim(); // cutting out witamInfo&port

					guestList.add(new serverGuests(guestNameTemp, ipInput, Integer.parseInt(guestportTemp)));
					usersChanged = true;
				}
				else if(content.substring(7,10)=="use"){
					for (int counter=0; counter < guestList.size(); counter++){
						if (guestList.get(counter).ipInput.equals(ipInput)){
	
							 message = "[" + guestList.get(counter).guestName + "]  " + content.substring(17).trim();
							
							break;
						}			
					}
					// message to every client
					
						InetAddress IPAddress2Send;
						int port2Send;
						byte[] sendData = new byte[1024];
						DatagramPacket sendPacket;
						byte[] sendDataUsers = new byte[1024];
						DatagramPacket sendPacketUsers;
						
					
				
					// sending message to every client
					for (int counter=0; counter < guestList.size(); counter++){
						try {
							DatagramSocket clientSocketSend = new DatagramSocket();
							
						} catch (SocketException e) {
							System.err.println("Err03g server: No port number");
							e.printStackTrace();
						}
						
						 IPAddress2Send = guestList.get(counter).ipInput;			
						 port2Send = guestList.get(counter).portInput;
						 sendData = message.getBytes();
						 sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress2Send, port2Send);
						DatagramSocket clientSocketSend = null;
						try {
							clientSocketSend.send(sendPacket);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						clientSocketSend.close();
						
					}
					
					
					
			}
				if(usersChanged){
					InetAddress IPAddress2Send;
					int port2Send;
					byte[] sendData = new byte[1024];
					DatagramPacket sendPacket;
					byte[] sendDataUsers = new byte[1024];
					DatagramPacket sendPacketUsers;
					String messageUsers;
					
					for (int counter=0; counter < guestList.size(); counter++){				
						messageUsers = usersInfo;
						DatagramSocket clientSocketSendUsers = null;
						try {
							clientSocketSendUsers = new DatagramSocket();
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						IPAddress2Send = guestList.get(counter).ipInput;			
						port2Send = guestList.get(counter).portInput;
						
						// all users (names)
						for (int j=0; j < guestList.size(); j++){
							messageUsers += guestList.get(j).guestName + "\n";
						}
						sendDataUsers = messageUsers.getBytes();
						sendPacketUsers = new DatagramPacket(sendDataUsers, sendDataUsers.length, IPAddress2Send, port2Send);
						try {
							clientSocketSendUsers.send(sendPacketUsers);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}clientSocketSendUsers.close();
				}
					 
	}
				usersChanged = false;
	}
}
}
