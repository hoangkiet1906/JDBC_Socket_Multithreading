package model;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class SapXep extends JFrame implements ActionListener{
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
	
	String namee;
	JScrollPane sp;
	JPanel p,TieuDe;
	JTable tb;
	DefaultTableModel model;
	ArrayList<HangHoa> hh;
	JButton ok, b3;
	JTextField port;
	public SapXep(String s, ArrayList<HangHoa> h, String name) {
		super(s);
		namee=name;
		hh=h;
		setLayout(new BorderLayout());
		tb = new JTable();
		model = new DefaultTableModel();
		model.addColumn("Id");
		model.addColumn("Ten");
		model.addColumn("Gia");
		model.addColumn("So Luong");
		model.addColumn("Da Nhap");
		model.addColumn("Ngay Nhap");
		model.addColumn("Da Xuat");
		model.addColumn("Ngay Xuat");
		if(name=="Ten") {
			Collections.sort(h, new Comparator<HangHoa>() {
				public int compare(HangHoa h1, HangHoa h2) {

					return h1.getTen().compareTo(h2.getTen());
				}
			});
		}
		else
			if (name=="Gia"){
				Collections.sort(h, new Comparator<HangHoa>() {
					public int compare(HangHoa h1, HangHoa h2) {
						Double g1 = Double.valueOf(h1.getGia());
						Double g2 = Double.valueOf(h2.getGia());
						return g1.compareTo(g2);
					}
				});
			}
			else 
				if(name=="So Luong") {
					Collections.sort(h, new Comparator<HangHoa>() {
						public int compare(HangHoa h1, HangHoa h2) {
							Integer s1 = h1.getSoLuong();
							Integer s2 = h2.getSoLuong();
							return s1.compareTo(s2);
						}
					});
				}
				else
					if(name=="Ngay Nhap Kho") {
						 Collections.sort(h, new Comparator<HangHoa>() {
						        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
						        public int compare(HangHoa h1, HangHoa h2) {
						        	String o1 = new String(h1.getNgayNhap());
						        	String o2 = new String(h2.getNgayNhap());
						            try {
						                return f.parse(o1).compareTo(f.parse(o2));
						            } catch (ParseException e) {
						                throw new IllegalArgumentException(e);
						            }
						        }
						    });
					}
//						Collections.sort(h, new Comparator<HangHoa>() {
//							public int compare(HangHoa h1, HangHoa h2) {
//								String s1 = h1.getNgayNhap();
//								String d1 = new String(s1).substring(0, 2);
//								String m1 = new String(s1).substring(3, 5);
//								String y1 = new String(s1).substring(6, 10);
//								
//								String s2 = h2.getNgayNhap();
//								String d2 = new String(s2).substring(0, 2);
//								String m2 = new String(s2).substring(3, 5);
//								String y2 = new String(s2).substring(6, 10);
//								if(y1==y2) {
//									if(m1==m2) {
//										return d1.compareTo(d2);
//									}
//									else
//										return m1.compareTo(m2);
//								}
//								else return y1.compareTo(y2);
//						});
					else
						if(name=="Ngay Xuat Kho") {
							Collections.sort(h, new Comparator<HangHoa>() {
						        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
						        public int compare(HangHoa h1, HangHoa h2) {
						        	String o1 = new String(h1.getNgayXuat());
						        	String o2 = new String(h2.getNgayXuat());
						            try {
						                return f.parse(o1).compareTo(f.parse(o2));
						            } catch (ParseException e) {
						                throw new IllegalArgumentException(e);
						            }
						        }
						    });
						}
		
		for (HangHoa e : h) {
			model.addRow(new Object[] {e.getId(),e.getTen(),e.getGia(),e.getSoLuong(),
					e.getDaNhap(),e.getNgayNhap(),e.getDaXuat(),e.getNgayXuat()});
		}
		
		sp = new JScrollPane(tb);
		
		b3 = new JButton("Connect");
		b3.addActionListener(this);
		port = new JTextField();
		port.setColumns(8);
		
		this.add(sp,BorderLayout.CENTER);
		
		TieuDe = new JPanel();
		TieuDe.setLayout(new FlowLayout());
		JLabel ten = new JLabel("Sap Xep Theo "+name);
		ten.setFont(new Font("Sitka Text", Font.BOLD, 20));
		TieuDe.add(ten,BorderLayout.CENTER);
		
		this.add(TieuDe,BorderLayout.NORTH);
		ok = new JButton("Cancel");
		ok.addActionListener(this);
		p = new JPanel();
		p.add(port);
		p.add(b3);
		p.add(ok);
		p.setLayout(new FlowLayout());
		this.add(p,BorderLayout.SOUTH);
		setLocation(400,200);
		setSize(600,300);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Connect")) {
			if(port.getText().equals("8888")) {
				start();
				tb.setModel(model);
			}
			else JOptionPane.showMessageDialog(rootPane, "Port sai rui ko the ket noi!");
		}
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
			clientName = "Sap Xep Theo "+namee;
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
