package model;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.JapaneseDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import socket.model.hh.HangHoaChuyen;





public class NhapKho extends JFrame implements ActionListener{
	private static Socket clientSocket;
	private static int PORT;
	private PrintWriter out;
	
//	private ObjectOutputStream out2;
	HangHoaChuyen hhc;
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
	JLabel l1,l2,l3,l4,l5,l6;
	JTextField t1,t2,t3,t4,t6;
	JComboBox t5;
	JDateChooser ng;
	JButton b1,b2,b3;
	DefaultTableModel dataModel;
	ArrayList<HangHoa> hh = new ArrayList<HangHoa>();
	HangHoa hhk = new HangHoa();
	
	
	public HangHoa getHhk() {
		return hhk;
	}
	public void setHhk(HangHoa hhk) {
		this.hhk = hhk;
	}

	
	public NhapKho(String s, ArrayList<HangHoa> h,DefaultTableModel model) {
		super(s);
		dataModel=model;
		hh=h;
		p1 = new JPanel();
		p1.setLayout(new GridLayout(6,2));
		l1 = new JLabel("ID");
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		t1 = new JTextField();
		t1.setText(String.valueOf(h.size()+1));
		t1.setEnabled(false);
		p1.add(l1);
		p1.add(t1);
		l2 = new JLabel("Ten");
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		t2 = new JTextField();
		p1.add(l2);
		p1.add(t2);		
		l3 = new JLabel("Gia");
		l3.setHorizontalAlignment(SwingConstants.CENTER);
		t3 = new JTextField();
		p1.add(l3);
		p1.add(t3);		
		l4 = new JLabel("SoLuong");
		l4.setHorizontalAlignment(SwingConstants.CENTER);
		t4 = new JTextField();
		p1.add(l4);
		p1.add(t4);	
		l5 = new JLabel("Ngay Nhap");
		l5.setHorizontalAlignment(SwingConstants.CENTER);
		ng = new JDateChooser();
		ng.setDateFormatString("dd-MM-yyyy");
		
		
		p1.add(l5);
		p1.add(ng);
		this.add(p1,"North");
		
		b3 = new JButton("Connect");
		b3.addActionListener(this);
		t6 = new JTextField();
		p1.add(t6);
		p1.add(b3);
		
		
		p2 = new JPanel();
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
			
				
//				GuiData();
				
			
			
			String id=t1.getText();
			String ten=t2.getText();
			float gia=Float.valueOf(t3.getText());
			int soLuong=Integer.valueOf(t4.getText());
			//String ngayNhap = t5.getSelectedItem().toString();
			DateFormat gg = new SimpleDateFormat("dd-MM-yyyy");
			
			String ngayNhap = gg.format(ng.getDate());
			
			HangHoa hanghoa = new HangHoa(id, ten, gia, soLuong, soLuong, ngayNhap, 0, "00-00-0000");
			
			String message = "Nhap Kho: "+hanghoa.getTen()+ " So luong : "+soLuong;
			out.println(message);
			if((new JDBCConnection().addHangHoa(hanghoa))) {
				hh.add(hanghoa);
				dataModel.addRow(new Object[] {hanghoa.getId(),hanghoa.getTen(),hanghoa.getGia(),
						hanghoa.getSoLuong(),hanghoa.getDaNhap(),hanghoa.getNgayNhap()
						,hanghoa.getDaXuat(),hanghoa.getNgayXuat()
				});
				JOptionPane.showMessageDialog(rootPane, "Them hang hoa thanh cong!");
				int clone = Integer.parseInt(t1.getText());
				t1.setText(String.valueOf(clone+1));

			}
			
		}
			else {
				JOptionPane.showMessageDialog(rootPane, "Port sai rui xem lai port!");
			}
		}
		else if(e.getActionCommand().equals("Cancel")) {
			stop();
					this.dispose();
		}
	}
	
	public void start() {
		try {
//			String id=t1.getText();
//			String ten=t2.getText();
//			float gia=Float.valueOf(t3.getText());
//			int soLuong=Integer.valueOf(t4.getText());
//			//String ngayNhap = t5.getSelectedItem().toString();
//			DateFormat gg = new SimpleDateFormat("dd-MM-yyyy");
//			
//			String ngayNhap = gg.format(ng.getDate());
			
//			hhk = new HangHoa(id, ten, gia, soLuong, soLuong, ngayNhap, 0, "00-00-0000");
//			new Kho().setHhkc(hhk);
//			PORT = Integer.parseInt(txtPort.getText().trim());
			PORT = 8888;
//			clientName = txtNickname.getText().trim();
			clientName = "Nhap Kho";
			clientSocket = new Socket("localhost", PORT);
			out = new PrintWriter(clientSocket.getOutputStream(), true);

			
			new Thread(new Listener()).start();
			//send name
			out.println(clientName);
			
			
//			out2.writeObject(hhclient);
//			out2.flush();
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
	
	public void GuiData() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.writeObject(hhc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

