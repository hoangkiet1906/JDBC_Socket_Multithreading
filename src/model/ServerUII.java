package model;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import socket.model.hh.HangHoaChuyen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;

public class ServerUII extends JFrame implements ActionListener {	

	// Socket Related
//	public static SimpleDateFormat formatter = new SimpleDateFormat("[MM/hh/yy hh:mm a]");
	public static SimpleDateFormat formatter = new SimpleDateFormat("[hh:mm a]");
	private static HashMap<String, ObjectInputStream> connectedClients = new HashMap<>();
	private static final int MAX_CONNECTED = 50;
	private static int PORT;
	private static ServerSocket server,server2;
	private Socket sockett;
	private static volatile boolean exit = false;


	// JFrame related
	private JPanel contentPane;
	private JTextArea txtAreaLogs;
	private JButton btnStart;
	private JLabel lblChatServer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerUII frame = new ServerUII();
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(frame);
					//Logs
					System.setOut(new PrintStream(new TextAreaOutputStream(frame.txtAreaLogs)));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerUII() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 570, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		lblChatServer = new JLabel("QUAN LY");
		lblChatServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatServer.setFont(new Font("Tahoma", Font.PLAIN, 40));
		contentPane.add(lblChatServer, BorderLayout.NORTH);

		btnStart = new JButton("START");
		btnStart.addActionListener(this);
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 30));
		contentPane.add(btnStart, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		txtAreaLogs = new JTextArea();
		txtAreaLogs.setBackground(Color.BLACK);
		txtAreaLogs.setForeground(Color.WHITE);
		txtAreaLogs.setLineWrap(true);
		scrollPane.setViewportView(txtAreaLogs);
		setLocation(10,10);
		setSize(450,550);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnStart) {
			if(btnStart.getText().equals("START")) {
				exit = false;
				getRandomPort();
				start();
				new QLHangHoa("Quan Ly Hang Hoa Xuat Nhap Kho");
				
				
				btnStart.setText("STOP");
			}else {
				addToLogs("Chat server stopped...");
				exit = true;
				btnStart.setText("START");
			}
		}
		
		//Refresh UI
		refreshUIComponents();
	}
	
	public void refreshUIComponents() {
		lblChatServer.setText("SERVER" + (!exit ? ": "+PORT:""));
	}

	public static void start() {
		new Thread(new ServerHandler()).start();
	}

	public static void stop() throws IOException {
		if (!server.isClosed()) server.close();
	}

	private static void broadcastMessage(HangHoa message) throws ClassNotFoundException, IOException {
		for (ObjectInputStream p: connectedClients.values()) {
			p.readObject();
		}
	}
	
	public static void addToLogs(String message) {
		System.out.printf("%s %s\n", formatter.format(new Date()), message);
	}

	private static int getRandomPort() {
		int port = 8888;
		PORT = port;
		return port;
	}
	
	private static class ServerHandler implements Runnable{
		@Override
		public void run() {
			try {
				server = new ServerSocket(PORT);
				
				
				
				addToLogs("Bat dau quan ly port: " + PORT);
				addToLogs("Dang cho ket noi...");
				while(!exit) {
					if (connectedClients.size() <= MAX_CONNECTED){
						new Thread(new ClientHandler(server.accept())).start();
					}
				}
			}
			catch (Exception e) {
				addToLogs("\nError occured: \n");
				addToLogs(Arrays.toString(e.getStackTrace()));
				addToLogs("\nExiting...");
			}
		}
	}
	
	// Start of Client Handler
	private static class ClientHandler implements Runnable {
		private Socket socket;
		private ObjectInputStream out;
		
		private BufferedReader in;
		private String name;
		
		
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run(){
			
			addToLogs("1 Client da ket noi: " + socket.getInetAddress());
			try {
				
				ObjectInputStream out2 = new ObjectInputStream(socket.getInputStream());
				HangHoa hhc;
				for(;;) {
					hhc = (HangHoa) out2.readObject();
					synchronized (connectedClients) {
						if (!name.isEmpty() && !connectedClients.keySet().contains(name)) break;
						
					}
				}
//				out.println("Thanh cong , " + name.toUpperCase() + "!");
				if(name.equals("Nhap Kho")) {
					if(new JDBCConnection().addHangHoa(hhc)){
						addToLogs(name.toUpperCase() + " START ");
					}
				}
				else if(name.equals("Xuat Kho")) {
					if(new JDBCConnection().xuatHangHoa(hhc, hhc.getId(), hhc.getSoLuong(), hhc.getNgayXuat())){
						addToLogs(name.toUpperCase() + " START ");
					}
				}
				else if(name.equals("Chinh Sua TT")) {
					if(new JDBCConnection().chinhSuaHangHoa(hhc, hhc.getId())){
						addToLogs(name.toUpperCase() + " START ");
					}
				}
				
				
				connectedClients.put(name, out);
				
				String message;
//				out.println("You may join the chat now...");
				while ((message = in.readLine()) != null && !exit) {
//					System.out.println(new Kho().getHhkc().getId());
					if (!message.isEmpty()) {
						if (message.toLowerCase().equals("/quit")) break;
						addToLogs(String.format("[%s] %s", name, message));
					}
				}
				
				
			} catch (Exception e) {
				addToLogs(e.getMessage());
			} finally {
				if (name != null) {
					addToLogs(name + " da thoat");
					connectedClients.remove(name);
//					broadcastMessage(name + " has left");
				}
			}
			addToLogs("So client la: "+String.valueOf((connectedClients.size())));
		}
	}
	
	public void NhanData() {
		try {
			ObjectInputStream out = new ObjectInputStream(sockett.getInputStream());
			HangHoaChuyen hhc = (HangHoaChuyen) out.readObject();
			System.out.println(hhc.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
