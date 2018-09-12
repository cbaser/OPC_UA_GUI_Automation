package userInterface;

import com.rm5248.serial.NoSuchPortException;
import com.rm5248.serial.NotASerialPortException;
import com.rm5248.serial.SerialPort;
import com.rm5248.serial.SerialPort.BaudRate;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class SerialPortFrame extends JFrame {
	private String [] portNames;
	private SerialPort port;
	private JCheckBox chckbxStatus;
	private JTextArea textArea;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	
	public void showDeviceFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SerialPortFrame frame = new SerialPortFrame();
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
	public SerialPortFrame() {
		setTitle("Serial Port");
		setBounds(100, 100, 597, 488);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblPort = new JLabel("Port");
		
		JComboBox<String> portBox = new JComboBox<>();
		getPorts(portBox);
		contentPane.repaint();
		contentPane.revalidate();
		 chckbxStatus = new JCheckBox("Status");
		chckbxStatus.setEnabled(false);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startConnection(String.valueOf(portBox.getSelectedItem()));
				startGettingInformation();
			}
		});
		
		 textArea = new JTextArea();
		
		JLabel lblOutput = new JLabel("Output");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(textArea, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblPort)
							.addGap(18)
							.addComponent(portBox, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnConnect)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxStatus)
							.addContainerGap(170, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblOutput)
							.addContainerGap(399, Short.MAX_VALUE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(29)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPort)
						.addComponent(portBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbxStatus)
						.addComponent(btnConnect))
					.addGap(52)
					.addComponent(lblOutput)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	
	private void getPorts(JComboBox<String> portBox) {
		try {
			 portNames = SerialPort.getSerialPorts();
			 for(String portname : portNames)
				 portBox.addItem(portname);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void startConnection(String portName) {
		try {
			port = new SerialPort(portName,SerialPort.NO_CONTROL_LINE_CHANGE);
			chckbxStatus.setSelected(true);
			chckbxStatus.setText("Connected");
		} catch (NoSuchPortException | NotASerialPortException | IOException e) {
			e.printStackTrace();
			chckbxStatus.setSelected(false);
			chckbxStatus.setText("Failed");
		}
	}
	private void startGettingInformation() {
		try {
		InputStream is = port.getInputStream();
		 BufferedReader br = new BufferedReader(new InputStreamReader(is));
		 String outputline;
		 
			while((outputline= br.readLine()) != null) 
					textArea.append(outputline+"\n");	
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
