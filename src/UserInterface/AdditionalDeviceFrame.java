package UserInterface;

import java.awt.EventQueue;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Color;

public class AdditionalDeviceFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField deviceNameField;
	private JTextField cpuNameField;
	private JTextField memoryNameField;
	private static JFrame mainFrame;

	/**
	 * Launch the application.
	 */
	public void showDeviceFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainFrame = new AdditionalDeviceFrame();		
					mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public AdditionalDeviceFrame() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("Add New Device");
		setBounds(100, 100, 450, 300);
		
		JLabel lblNewLabel = new JLabel("Device Name");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		deviceNameField = new JTextField();
		deviceNameField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("CPU Size");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		cpuNameField = new JTextField();
		cpuNameField.setColumns(10);
		
		JComboBox<String> cpuBox = new JComboBox<String>();
		cpuBox.addItem("KB");
		cpuBox.addItem("MB");
		cpuBox.addItem("GB");
		
		JLabel lblNewLabel_2 = new JLabel("Memory Size");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		memoryNameField = new JTextField();
		memoryNameField.setColumns(10);
		JCheckBox chckbxEncryptionSupport = new JCheckBox("Encryption Support");
		chckbxEncryptionSupport.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JComboBox<String> memoryBox = new JComboBox<String>();
		memoryBox.addItem("KB");
		memoryBox.addItem("MB");
		memoryBox.addItem("GB");
		
		JButton btnSave = new JButton("Save");
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String memorySize = (memoryNameField.getText()+" "+String.valueOf(memoryBox.getSelectedItem())).toUpperCase();
				String cpuSize = (cpuNameField.getText()+" "+String.valueOf(cpuBox.getSelectedItem())).toUpperCase();
				String encryptionSupport = "NO";
				String deviceName = deviceNameField.getText().toLowerCase().replaceAll(" ", "").toUpperCase();
				if(chckbxEncryptionSupport.isSelected())
					encryptionSupport="YES";
				try {
					FileWriter filewriter = new FileWriter(System.getProperty("user.dir")+File.separator+"devices"+File.separator+deviceName+".txt");
					filewriter.write("NAME : "+deviceName);
					filewriter.write("\nCPU : "+cpuSize);
					filewriter.write("\nRAM : "+memorySize);
					filewriter.write("\nENCRYPTION SUPPORT : "+ encryptionSupport);
					filewriter.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			
			}
			
		});
		
	
		
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(23)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxEncryptionSupport, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_2))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnSave, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(memoryNameField, Alignment.LEADING)
								.addComponent(cpuNameField, Alignment.LEADING)
								.addComponent(deviceNameField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(memoryBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(cpuBox, 0, 54, Short.MAX_VALUE))))
					.addContainerGap(116, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(50)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(deviceNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(cpuNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cpuBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(memoryNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(memoryBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxEncryptionSupport)
					.addGap(28)
					.addComponent(btnSave)
					.addContainerGap(69, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);

	}
}
