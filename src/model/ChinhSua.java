package model;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;



public class ChinhSua extends JFrame implements ActionListener{
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
	JLabel l1,l2,l3,l4,l5,l55,l6,l66;
	JTextField t1,t2,t3,t4,t5,t55,t6,t66,tt;
	JButton b1,b2,b3;
	DefaultTableModel dataModel;
	ArrayList<HangHoa> hh = new ArrayList<HangHoa>();
	int Row;
	JDateChooser ng,ng1;
	public ChinhSua(String s, ArrayList<HangHoa> h, DefaultTableModel model, int getRow) {
		super(s);
		Row = getRow;
		dataModel=model;
		hh=h;
		p1 = new JPanel();
		p1.setLayout(new GridLayout(9,2));
		l1 = new JLabel("ID");
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		t1 = new JTextField(h.get(getRow).getId());
		t1.setEnabled(false);
		t1.setFont(new Font("Tahoma", Font.BOLD, 12));
		t1.setForeground(Color.RED);
		p1.add(l1);
		p1.add(t1);
		l2 = new JLabel("Ten");
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		t2 = new JTextField(h.get(getRow).getTen());
		p1.add(l2);
		p1.add(t2);		
		l3 = new JLabel("Gia");
		l3.setHorizontalAlignment(SwingConstants.CENTER);
		t3 = new JTextField(String.valueOf(h.get(getRow).getGia()));
		p1.add(l3);
		p1.add(t3);		
		l4 = new JLabel("SoLuong");
		l4.setHorizontalAlignment(SwingConstants.CENTER);
		t4 = new JTextField(String.valueOf(h.get(getRow).getSoLuong()));
		p1.add(l4);
		p1.add(t4);	
		l5 = new JLabel("DaNhap");
		l5.setHorizontalAlignment(SwingConstants.CENTER);
		t5 = new JTextField(String.valueOf(h.get(getRow).getDaNhap()));
		p1.add(l5);
		p1.add(t5);
		l55 = new JLabel("NgayNhap");
		l55.setHorizontalAlignment(SwingConstants.CENTER);
		t55 = new JTextField(String.valueOf(h.get(getRow).getNgayNhap()));
		ng = new JDateChooser();
		ng.setDateFormatString("dd-MM-yyyy");
		p1.add(l55);
		p1.add(ng);
		l6 = new JLabel("DaXuat");
		l6.setHorizontalAlignment(SwingConstants.CENTER);
		t6= new JTextField(String.valueOf(h.get(getRow).getDaXuat()));
		p1.add(l6);
		p1.add(t6);
		l66 = new JLabel("NgayXuat");
		l66.setHorizontalAlignment(SwingConstants.CENTER);
		t66 = new JTextField(String.valueOf(h.get(getRow).getNgayXuat()));
		ng1 = new JDateChooser();
		ng1.setDateFormatString("dd-MM-yyyy");
		p1.add(l66);
		p1.add(ng1);
		this.add(p1,"North");
		b3 = new JButton("Connect");
		b3.addActionListener(this);
		tt = new JTextField();
		p1.add(tt);
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
		
		setSize(350,300);
		setLocation(525,350);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Connect")) {
			if (tt.getText().equals("8888"))
			{
				start();
				JOptionPane.showMessageDialog(rootPane, "OK port dung cho phep ket noi!");
			}
			else {
				JOptionPane.showMessageDialog(rootPane, "Port sai rui xem lai port!");
			}
		}
		if(e.getActionCommand().equals("OK")) {
			if (tt.getText().equals("8888"))
			{
			
				
			
			
			String id = t1.getText();
			int row = Integer.valueOf(id);
			String ten = t2.getText();
			double gia = Double.valueOf(t3.getText());
			int soLuong = Integer.valueOf(t4.getText());
			int daNhap = Integer.valueOf(t5.getText());
			
			//String ngayNhap = t55.getText();
			DateFormat gg = new SimpleDateFormat("dd-MM-yyyy");			
			String ngayNhap = gg.format(ng.getDate());
			
			int daXuat = Integer.valueOf(t6.getText());
			
			//String ngayXuat = t66.getText();
			DateFormat gg1 = new SimpleDateFormat("dd-MM-yyyy");
			String ngayXuat = gg1.format(ng.getDate());
			
			String message = "Chinh Sua ID:"+id;
			out.println(message);
			
			HangHoa clone = new HangHoa(id, ten, gia, soLuong, daNhap, ngayNhap, daXuat, ngayXuat);
			if(new JDBCConnection().chinhSuaHangHoa(clone, clone.getId())) {
				hh.set(row-1, clone);
				this.dataModel.insertRow(row-1, new Object[] {
						clone.getId(),clone.getTen(),clone.getGia(),clone.getSoLuong(),
						clone.getDaNhap(),clone.getNgayNhap(),clone.getDaXuat(),clone.getNgayXuat()
				});
				this.dataModel.removeRow(row);
				this.dataModel.fireTableDataChanged();
				JOptionPane.showMessageDialog(rootPane, "Chinh sua hang hoa thanh cong!");
				
			}
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
			clientName = "Chinh Sua TT";
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
