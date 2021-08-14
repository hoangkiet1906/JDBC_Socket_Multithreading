package model;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;



public class XuatKho extends JFrame implements ActionListener{
	private static Socket clientSocket;
	private static int PORT;
	private PrintWriter out;

	// JFrame related
	private JPanel contentPane;
	private JTextArea txtAreaLogs;
	private JButton btnStart;
	private JPanel panelNorth;
	private JLabel lblChatClient;
	private JPanel panelNorthSouth;
	private JLabel lblPort;
	private JLabel lblName;
	private JPanel panelSouth;
	private JButton btnSend;
	private JTextField txtMessage;
	private JTextField txtNickname;
	private JTextField txtPort;
	private String clientName;
	
	
	
	JPanel p1,p2;
	JLabel l1,l2,l3;
	JTextField t1,t2,t3,t6;
	JButton b1,b2,b3;
	DefaultTableModel dataModel;
	ArrayList<HangHoa> hh = new ArrayList<HangHoa>();
	JDateChooser ng;
	public XuatKho(String s, ArrayList<HangHoa> h, DefaultTableModel model) {
		super(s);
		dataModel = model;
		hh = h;
		p1 = new JPanel();
		p2 = new JPanel();
		l1 = new JLabel("ID");
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l2 = new JLabel("SoLuong");
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		t1 = new JTextField();
		t2 = new JTextField();
		l3 = new JLabel("NgayXuat");
		l3.setHorizontalAlignment(SwingConstants.CENTER);
		t3 = new JTextField("00-00-0000");
		p1.setLayout(new GridLayout(4,2));
		p1.add(l1);
		p1.add(t1);
		p1.add(l2);
		p1.add(t2);
		p1.add(l3);
		ng = new JDateChooser();
		ng.setDateFormatString("dd-MM-yyyy");
		p1.add(ng);
		this.add(p1,"North");
		
		b3 = new JButton("Connect");
		b3.addActionListener(this);
		t6 = new JTextField();
		p1.add(t6);
		p1.add(b3);
		
		p2.setLayout(new FlowLayout());
		b1 = new JButton("OK");
		b1.addActionListener(this);
		b2 = new JButton("Cancel");
		b2.addActionListener(this);
		p2.add(b1);
		p2.add(b2);
		this.add(p2,"South");
		setSize(350,250);
		setLocation(525,350);
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Connect")) {
			if (t6.getText().equals("8888"))
			{
				start();
				JOptionPane.showMessageDialog(rootPane, "OK port dung cho phep ket noi!");
			}
			else {
				JOptionPane.showMessageDialog(rootPane, "Port sai rui xem lai port!");
			}
		}
		if(e.getActionCommand().equals("OK")) {
			if (t6.getText().equals("8888"))
			{
			
				
			
			
			int id = Integer.valueOf(t1.getText());
			int soLuong = Integer.valueOf(t2.getText());
			//String ngayXuat = t3.getText();
			DateFormat gg = new SimpleDateFormat("dd-MM-yyyy");
			
			String ngayXuat = gg.format(ng.getDate());
			
			String message = "Xuat Kho ID:"+id+ " So luong : "+soLuong;
			out.println(message);
			
			if(hh.get(id-1).getId().equals(String.valueOf(id)) 
						&& (hh.get(id-1).getSoLuong()>=soLuong)) {
				if(new JDBCConnection().xuatHangHoa(hh.get(id-1), String.valueOf(id), soLuong, ngayXuat)) {
					hh.set(id-1, new HangHoa(hh.get(id-1).getId()
							, hh.get(id-1).getTen(), hh.get(id-1).getGia()
							, hh.get(id-1).getSoLuong()-soLuong
							, hh.get(id-1).getDaNhap(),hh.get(id-1).getNgayNhap()
							, hh.get(id-1).getDaXuat()+soLuong, ngayXuat));
					this.dataModel.insertRow(id-1, new Object[] {hh.get(id-1).getId(),hh.get(id-1).getTen()
							,hh.get(id-1).getGia(),hh.get(id-1).getSoLuong()
							,hh.get(id-1).getDaNhap(),hh.get(id-1).getNgayNhap()
							,hh.get(id-1).getDaXuat(),hh.get(id-1).getNgayXuat()
					});
					this.dataModel.removeRow(id);
					this.dataModel.fireTableDataChanged();
					JOptionPane.showMessageDialog(rootPane, "Xuat hang hoa thanh cong!");
				}
			}
			else JOptionPane.showMessageDialog(rootPane, "Khong co hang hoa trong kho!");
		}
			else {
				JOptionPane.showMessageDialog(rootPane, "Port sai rui xem lai port!");
			}
		}
		else 
			if(e.getActionCommand().equals("Cancel")) {
				stop();
				this.dispose();
			}
	}
	
	public void start() {
		try {
//			PORT = Integer.parseInt(txtPort.getText().trim());
			PORT = 8888;
//			clientName = txtNickname.getText().trim();
			clientName = "Xuat Kho";
			clientSocket = new Socket("localhost", PORT);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			new Thread(new Listener()).start();
			//send name
			out.println(clientName);
		} catch (Exception err) {
			addToLogs("[ERROR] "+err.getLocalizedMessage());
		}
	}
	
	public void stop(){
		if(!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e1) {}
		}
	}
	
	public static void addToLogs(String message) {
		System.out.printf("%s %s\n", ServerUI.formatter.format(new Date()), message);
	}

	private static class Listener implements Runnable {
		private BufferedReader in;
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String read;
				for(;;) {
					read = in.readLine();
					if (read != null && !(read.isEmpty())) addToLogs(read);
				}
			} catch (IOException e) {
				return;
			}
		}

	}
}
