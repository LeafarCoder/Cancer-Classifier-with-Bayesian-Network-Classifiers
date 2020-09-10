package diagnosis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.UIManager;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import learner.learner_JFrame;
import tan.BN;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

public class diagnosis_JFrame extends JFrame implements Serializable{

	private String preferable_loading_path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
	private String[] records_header;
	private String[][] records_table;

	private BN bn;
	private boolean bn_uploaded = false;

	// 0 (none); 1 (individual); 2 (group)
	private int patientOrGroupLoad = 0;
	// patient(s) information
	private String[][] patient_info;
	// patient(s) symptoms
	private int[][] symptoms;
	// patient(s) results (probability for all outcomes)
	private double[][] results;

	private int resultClassIdx = 1;

	private static final long serialVersionUID = 1L;

	private String latest_version = "v.0.01";
	private String[] software_dev = {"André Miranda", "Rafael Correia", "Sofia Cotrim"};

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_2;
	private JPanel panel_4;
	private JPanel panel_5;
	private JList<String> patients_list;
	private JButton btnPersonalInformation;
	private JScrollPane scrollPanel_patients;
	private JRadioButton rdbtnIndividual;
	private JRadioButton rdbtnGroup;
	private JButton btnEditProfile;
	private JScrollPane table_scroll;

	private Object[][] optionsIcon;


	private String[] pat_info_headers = {"ID", "Name", "Age", "Gender", "Blood type"};
	private JTable table_results;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					diagnosis_JFrame frame = new diagnosis_JFrame();
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
	public diagnosis_JFrame() {


		// Set look and feel of components as the system's look and feel
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		// ************************************** MENU BAR ***************************************************
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// ****
		JMenuItem mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mnNew = new JMenuItem("New", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/new_file.png")));
		mnNew.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		mnFile.add(mnNew);


		JMenuItem mnSave = new JMenuItem("Save", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/save_icon.png")));
		mnFile.add(mnSave);


		JMenuItem mnClose = new JMenuItem("Close", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/close.png")));
		mnClose.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false); dispose();
			}
		});
		mnFile.add(mnClose);

		JMenu mnDiagnose = new JMenu("Diagnose");
		menuBar.add(mnDiagnose);

		JMenuItem mntmOptions = new JMenuItem("Options", new ImageIcon(getClass().getResource("/resources/options.png")));
		mntmOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				ImageIcon imageIcon = new ImageIcon(diagnosis_JFrame.class.getResource("/resources/death1_res.png"));
				final JLabel lblNewLabel = new JLabel();
				lblNewLabel.setIcon(imageIcon);
				
				JFrame f = new JFrame();
				f.setIconImage(Toolkit.getDefaultToolkit().getImage(diagnosis_JFrame.class.getResource("/resources/options.png")));
				f.setTitle("Diagnose Options");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setBounds(100, 100, 445, 428);
				f.getContentPane().setBackground(Color.WHITE);
				f.getContentPane().setLayout(null);
				f.setVisible(true);

				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				tabbedPane.setBounds(0, 0, 431, 389);
				f.getContentPane().add(tabbedPane);

				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				tabbedPane.addTab("Results categoring", null, panel, null);
				panel.setLayout(null);

				String[] header = {"Min. (%)","Max. (%)","Icon"};
				Object[][] data = new Object[optionsIcon.length][3];
				for(int i = 0; i < optionsIcon.length; i++){
					data[i][0] = (double)optionsIcon[i][1];
					data[i][1] = (double)optionsIcon[i][2];
					data[i][2] = optionsIcon[i][0];
				}

				DefaultTableModel model = new DefaultTableModel(data,  header){
					private static final long serialVersionUID = 1L;
					public Class getColumnClass(int column)
					{
						return getValueAt(0, column).getClass();
					}
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				};
				JTable table = new JTable(model);
				table.setBounds(10, 93, 409, 129);

				JScrollPane scroll = new JScrollPane(table);
				scroll.setSize(269, 238);
				scroll.setLocation(10, 11);
				scroll.setViewportView(table);
				panel.add(scroll);

				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "New category", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panel_1.setBackground(Color.WHITE);
				panel_1.setBounds(289, 60, 130, 175);
				panel.add(panel_1);
				panel_1.setLayout(null);

				JSlider slider = new JSlider();
				slider.setMinimum(0);
				slider.setValue(0);
				slider.setMaximum(100);
				slider.setBackground(Color.WHITE);
				slider.setBounds(35, 24, 85, 14);
				panel_1.add(slider);
				
				JSlider slider_1 = new JSlider();
				slider.setMinimum(0);
				slider_1.setMaximum(100);
				slider_1.setValue(100);
				slider_1.setBackground(Color.WHITE);
				slider_1.setBounds(35, 52, 85, 14);
				panel_1.add(slider_1);

				JLabel lblMax = new JLabel("Max:");
				lblMax.setBounds(10, 52, 46, 14);
				panel_1.add(lblMax);

				JLabel lblMin = new JLabel("Min:");
				lblMin.setBounds(10, 24, 46, 14);
				panel_1.add(lblMin);

				JButton btnSearchIco = new JButton("<html><center>Search<br>Icon</center></html>");
				btnSearchIco.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fc.addChoosableFileFilter(new FileFilter() {

							@Override
							public boolean accept(File f) {
								String name = f.getName().toLowerCase();
								// +- 600 (20x20 px tops)
								return (name.endsWith(".png") && f.length() <  600);
							}

							@Override
							public String getDescription() {
								return "PNG Images (16x16 / 20x20 px)";
							}
						});

						if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
							fc.getSelectedFile().getAbsolutePath();
							lblNewLabel.setIcon(new ImageIcon(fc.getSelectedFile().getAbsolutePath()));
						}
					}
				});
				btnSearchIco.setBounds(10, 80, 65, 37);
				panel_1.add(btnSearchIco);

				JButton btnAddCategory = new JButton("<html><center>Add<br>category</center></html>");
				btnAddCategory.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						model.addRow(new Object[]{(double)slider.getValue(), (double)slider_1.getValue(), lblNewLabel.getIcon()});
					}
				});
				btnAddCategory.setBounds(10, 128, 110, 37);
				panel_1.add(btnAddCategory);

				JPanel panel_2 = new JPanel(new GridBagLayout());
				panel_2.setBounds(85, 80, 35, 37);
				panel_1.add(panel_2);

				lblNewLabel.setVisible(true);
				GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
				gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
				gbc_lblNewLabel.gridx = 2;
				gbc_lblNewLabel.gridy = 1;
				panel_2.add(lblNewLabel, gbc_lblNewLabel);

				JTextArea txtrResultCategoriesAllow = new JTextArea();
				txtrResultCategoriesAllow.setFont(new Font("Tahoma", Font.PLAIN, 11));
				txtrResultCategoriesAllow.setText("'Result categories' allow a faster interpretation of the \r\nresults by the use of Icons. Min. and Max. define the \r\nrange of probabilities where to use the Icon.");
				txtrResultCategoriesAllow.setBounds(10, 260, 269, 46);
				panel.add(txtrResultCategoriesAllow);

				JButton btnRemove = new JButton("<html><center>Remove<br>category</center></html>");
				btnRemove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int[] sel = table.getSelectedRows();
						if(sel.length == 0)return;
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						for(int i = sel.length - 1; i >= 0; i--){
							System.out.println(sel[i]);
							model.removeRow(sel[i]);
						}
					}
				});
				btnRemove.setBounds(289, 11, 130, 38);
				panel.add(btnRemove);

				JButton btnApplyChanges = new JButton("Apply changes");
				btnApplyChanges.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						optionsIcon = new Object[table.getRowCount()][3];
						for(int i = 0; i < table.getRowCount(); i++){
							optionsIcon[i][0] = table.getValueAt(i, 2);
							optionsIcon[i][1] = table.getValueAt(i, 0);
							optionsIcon[i][2] = table.getValueAt(i, 1);
						}
					}
				});
				btnApplyChanges.setBounds(10, 317, 409, 33);
				panel.add(btnApplyChanges);

				String[] list_str = new String[bn.getSample().numberOfClasses()];
				for (int i = 0; i < bn.getSample().numberOfClasses(); i++) {
					list_str[i] = bn.getSample().getClassNames()[i];
				}

				JComboBox<String> comboBox = new JComboBox<String>(list_str);
				comboBox.setSelectedIndex(resultClassIdx);
				comboBox.setBounds(289, 286, 130, 20);
				panel.add(comboBox);

				JTextArea txtrU = new JTextArea();
				txtrU.setFont(new Font("Tahoma", Font.PLAIN, 11));
				txtrU.setText("Use following class \r\ncategory to evaluate \r\nresults:");
				txtrU.setBounds(289, 235, 127, 46);
				panel.add(txtrU);
			}


		});
		mnDiagnose.add(mntmOptions);


		// ****
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mnGetHelp = new JMenuItem("Get help", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/information.png")));
		mnGetHelp.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {

			}
		});
		mnHelp.add(mnGetHelp);

		JMenuItem mnAboutCli = new JMenuItem("About MDI", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/diagnosis.png")));
		mnAboutCli.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String msg = "The Medical Diagnosis Interface (MDI) is designed to facilitate \n"+
						" the use of the latest high-tech methods for various diseases diagnosis\n"+
						" by the medical professionals.\n\n"+
						" Latest version: " + latest_version + "\n"+
						"Software developers:";
				for (int i = 0; i < software_dev.length; i++) {
					msg += "\n   - "
							+ "" + software_dev[i];
				}
				JOptionPane optionPane = new JOptionPane(msg);
				optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog = optionPane.createDialog("");
				dialog.setTitle("About MDI");
				dialog.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/diagnosis.png")).getImage());
				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		mnHelp.add(mnAboutCli);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 50, 636, 623);
		setTitle("Medical Diagnosis Interface");
		setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/diagnosis.png")).getImage());
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Patient(s) symptoms", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 100, 296, 454);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblDiagnose = new JLabel("Diagnose:");
		lblDiagnose.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDiagnose.setBounds(52, 23, 54, 14);
		panel.add(lblDiagnose);

		ButtonGroup ind_group_choice = new ButtonGroup();

		rdbtnIndividual = new JRadioButton("Individual");
		rdbtnIndividual.setSelected(true);
		rdbtnIndividual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(patientOrGroupLoad == 1)return;
				changeIndivMode(1);
			}
		});
		rdbtnIndividual.setBackground(Color.WHITE);
		rdbtnIndividual.setBounds(112, 19, 71, 23);
		panel.add(rdbtnIndividual);
		ind_group_choice.add(rdbtnIndividual);

		rdbtnGroup = new JRadioButton("Group");
		rdbtnGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(patientOrGroupLoad == 2)return;
				changeIndivMode(2);
			}
		});
		rdbtnGroup.setBackground(Color.WHITE);
		rdbtnGroup.setBounds(188, 19, 55, 23);
		panel.add(rdbtnGroup);
		ind_group_choice.add(rdbtnGroup);

		panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual: <EMPTY>", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setBackground(Color.WHITE);
		panel_4.setBounds(10, 49, 276, 112);
		panel.add(panel_4);
		panel_4.setLayout(null);

		btnPersonalInformation = new JButton("Overview", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/overview.png")));
		btnPersonalInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {


				JFrame f2 = new JFrame();
				f2.setTitle("Overview (patient " + patient_info[0][0] + ")");
				f2.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/overview.png")).getImage());
				f2.setVisible(true);
				f2.setBounds(100, 100, 340, 500);
				f2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f2.getContentPane().setBackground(Color.WHITE);
				f2.getContentPane().setLayout(null);
				f2.setResizable(false);

				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Personal information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panel.setBounds(10, 11, 305, 103);
				f2.getContentPane().add(panel);
				panel.setLayout(null);

				JLabel lblId = new JLabel("ID: \t" + patient_info[0][0]);
				lblId.setBounds(31, 24, 264, 14);
				panel.add(lblId);

				JLabel lblName = new JLabel("Name: \t" + patient_info[0][1]);
				lblName.setBounds(31, 44, 264, 14);
				panel.add(lblName);

				JLabel lblAge = new JLabel("Age: " + patient_info[0][2]);
				lblAge.setBounds(31, 69, 59, 14);
				panel.add(lblAge);

				JLabel lblGender = new JLabel("Gender: " + patient_info[0][3]);
				lblGender.setBounds(100, 69, 92, 14);
				panel.add(lblGender);

				JPanel panel_2 = new JPanel();
				panel_2.setBorder(new TitledBorder(null, "Medical attributes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_2.setBackground(Color.WHITE);
				panel_2.setBounds(10, 125, 305, 60);
				f2.getContentPane().add(panel_2);
				panel_2.setLayout(null);

				JLabel lblBloodType = new JLabel("Blood type: " + patient_info[0][4]);
				lblBloodType.setBounds(31, 31, 264, 14);
				panel_2.add(lblBloodType);

				JPanel panel_1 = new JPanel();
				panel_1.setBackground(Color.WHITE);
				panel_1.setBorder(new TitledBorder(null, "Symptoms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_1.setBounds(10, 196, 305, 254);
				f2.getContentPane().add(panel_1);
				panel_1.setLayout(null);

				Object[][] data = new Object[bn.getSample().getNumFactors()][2];
				for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
					data[i][0] = bn.getSample().getHeaders()[i];
					data[i][1] = symptoms[0][i];
				}

				String[] header = {"Name", "Value"};
				JTable table = new JTable(data,header);

				DefaultTableModel tableModel = new DefaultTableModel(data, header) {
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				};
				table.setModel(tableModel);
				table.setFillsViewportHeight(true);
				JScrollPane table_scroll = new JScrollPane(table);
				table_scroll.setLocation(10, 15);
				table_scroll.setSize(285, 228);

				table_scroll.setVisible(true);
				panel_1.setLayout(null);
				table_scroll.setViewportView(table);
				panel_1.add(table_scroll);

			}
		});
		btnPersonalInformation.setBounds(22, 68, 110, 32);
		panel_4.add(btnPersonalInformation);

		btnEditProfile = new JButton("Edit profile", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/edit_record.png")));
		btnEditProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JTextField id_textField;
				JTextField name_textField;

				JFrame f1 = new JFrame();
				f1.setTitle("Edit record for single patient");
				f1.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/edit_record.png")).getImage());
				f1.setVisible(true);
				f1.setBounds(100, 100, 500, 500);
				f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f1.getContentPane().setBackground(Color.WHITE);
				f1.getContentPane().setLayout(null);
				f1.setResizable(false);

				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Personal information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panel.setBounds(10, 11, 227, 119);
				f1.getContentPane().add(panel);
				panel.setLayout(null);

				JLabel lblId = new JLabel("ID:");
				lblId.setBounds(31, 24, 15, 14);
				panel.add(lblId);

				id_textField = new JTextField();
				id_textField.setText(patient_info[0][0]);
				id_textField.setBounds(56, 21, 158, 20);
				panel.add(id_textField);
				id_textField.setColumns(10);

				JLabel lblName = new JLabel("Name:");
				lblName.setBounds(14, 52, 31, 14);
				panel.add(lblName);

				name_textField = new JTextField(patient_info[0][1]);
				name_textField.setBounds(56, 49, 158, 20);
				panel.add(name_textField);
				name_textField.setColumns(10);

				JLabel lblAge = new JLabel("Age:");
				lblAge.setBounds(24, 83, 23, 14);
				panel.add(lblAge);

				JSpinner age_spinner = new JSpinner();
				age_spinner.setModel(new SpinnerNumberModel(Integer.parseInt(patient_info[0][2]), 0, 150, 1));
				age_spinner.setBounds(56, 80, 40, 20);
				panel.add(age_spinner);

				JLabel lblGender = new JLabel("Gender:");
				lblGender.setBounds(104, 83, 46, 14);
				panel.add(lblGender);

				ButtonGroup gender_group = new ButtonGroup();

				JRadioButton rdbtnM = new JRadioButton("M");
				rdbtnM.setBackground(Color.WHITE);
				rdbtnM.setBounds(143, 79, 38, 23);
				panel.add(rdbtnM);
				gender_group.add(rdbtnM);

				JRadioButton rdbtnF = new JRadioButton("F");
				rdbtnF.setBackground(Color.WHITE);
				rdbtnF.setBounds(183, 79, 31, 23);
				panel.add(rdbtnF);
				gender_group.add(rdbtnF);

				rdbtnM.setSelected(patient_info[0][3].equals("Male"));
				rdbtnF.setSelected(patient_info[0][3].equals("Female"));

				Object[][] data = new Object[bn.getSample().getNumFactors()][3];
				for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
					data[i][0] = bn.getSample().getHeaders()[i];
					data[i][1] = symptoms[0][i];
					data[i][2] = bn.getSample().getVarDomain()[i][0] + " - " + bn.getSample().getVarDomain()[i][1];
				}

				JPanel panel_1 = new JPanel();
				panel_1.setBackground(Color.WHITE);
				panel_1.setBorder(new TitledBorder(null, "Symptoms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_1.setBounds(10, 141, 464, 263);
				f1.getContentPane().add(panel_1);
				panel_1.setLayout(null);

				String[] header = {"Name","Value","Range"};
				JTable table_sym = new JTable(data,header){
					public Class<? extends Object> getColumnClass(int c)
					{
						if(c == 1) return JScrollPane.class;
						else return getValueAt(0, c).getClass();
					}
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						// Just 'Value' column
						return column == 1;
					}
				};
				table_sym.setFillsViewportHeight(true);
				table_sym.getColumnModel().getColumn(0).setPreferredWidth(300);
				table_sym.getColumnModel().getColumn(1).setPreferredWidth(20);
				table_sym.getColumnModel().getColumn(2).setPreferredWidth(20);

				JComboBox<Integer> comboBoxG = new JComboBox<Integer>();
				//comboBoxG.set
				table_sym.getColumnModel().getColumn(1).setCellEditor(new MyEditor(comboBoxG));


				JScrollPane table_scroll = new JScrollPane(table_sym);
				table_scroll.setLocation(10, 15);
				table_scroll.setSize(444, 237);

				table_scroll.setVisible(true);
				panel_1.setLayout(null);
				//table_scroll.setBounds(0,0,100,100);
				table_scroll.setViewportView(table_sym);
				panel_1.add(table_scroll);

				JPanel panel_med = new JPanel();
				panel_med.setBorder(new TitledBorder(null, "Medical attributes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_med.setBackground(Color.WHITE);
				panel_med.setBounds(247, 11, 227, 119);
				f1.getContentPane().add(panel_med);
				panel_med.setLayout(null);

				JLabel lblBloodType = new JLabel("Blood type:");
				lblBloodType.setBounds(10, 23, 62, 14);
				panel_med.add(lblBloodType);

				JComboBox<String> comboBox = new JComboBox<String>();
				comboBox.setBackground(Color.WHITE);
				comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"O+", "A+", "B+", "AB+", "AB-", "A-", "O-", "B-"}));
				comboBox.setSelectedItem(patient_info[0][4]);
				comboBox.setBounds(79, 20, 53, 20);
				panel_med.add(comboBox);
				comboBox.setFocusable(true);

				JButton btnEditRecord = new JButton("Edit record");
				btnEditRecord.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setEnabled(panel_2, true);
						btnPersonalInformation.setEnabled(true);


						patient_info = new String[1][5];
						patient_info[0][0] = id_textField.getText();
						patient_info[0][1] = name_textField.getText();
						patient_info[0][2] = "" + age_spinner.getValue();
						patient_info[0][3] = rdbtnM.isSelected() ? "Male":"Female";
						patient_info[0][4] = comboBox.getSelectedItem().toString();

						panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual: " + patient_info[0][0], TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

						symptoms = new int[1][bn.getSample().getNumFactors()];
						for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
							symptoms[0][i] = (int) table_sym.getValueAt(i, 1);
						}

						f1.setVisible(false);
						f1.dispose();
					}
				});
				btnEditRecord.setBounds(10, 415, 464, 35);
				f1.getContentPane().add(btnEditRecord);

			}
		});
		btnEditProfile.setBounds(142, 68, 124, 33);
		panel_4.add(btnEditProfile);

		JButton btnLoadRecord = new JButton("Load record", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/upload.png")));
		btnLoadRecord.setBounds(22, 18, 110, 33);
		panel_4.add(btnLoadRecord);

		JButton btnCreateNewRecord = new JButton("<html>Create<br>record</html>", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/sp_add.png")));
		btnCreateNewRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				patient_info = new String[1][4];

				JTextField id_textField;
				JTextField name_textField;

				JFrame f1 = new JFrame();
				f1.setTitle("Create record for single patient");
				f1.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/sp_add.png")).getImage());
				f1.setVisible(true);
				f1.setBounds(100, 100, 500, 500);
				f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f1.getContentPane().setBackground(Color.WHITE);
				f1.getContentPane().setLayout(null);
				f1.setResizable(false);

				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Personal information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panel.setBounds(10, 11, 227, 119);
				f1.getContentPane().add(panel);
				panel.setLayout(null);

				JLabel lblId = new JLabel("ID:");
				lblId.setBounds(31, 24, 15, 14);
				panel.add(lblId);

				id_textField = new JTextField();
				id_textField.setText("00001");
				id_textField.setBounds(56, 21, 158, 20);
				panel.add(id_textField);
				id_textField.setColumns(10);

				JLabel lblName = new JLabel("Name:");
				lblName.setBounds(14, 52, 31, 14);
				panel.add(lblName);

				name_textField = new JTextField();
				name_textField.setBounds(56, 49, 158, 20);
				panel.add(name_textField);
				name_textField.setColumns(10);

				JLabel lblAge = new JLabel("Age:");
				lblAge.setBounds(24, 83, 23, 14);
				panel.add(lblAge);

				JSpinner age_spinner = new JSpinner();

				age_spinner.setModel(new SpinnerNumberModel(50, 0, 150, 1));
				age_spinner.setBounds(56, 80, 40, 20);
				panel.add(age_spinner);

				JLabel lblGender = new JLabel("Gender:");
				lblGender.setBounds(104, 83, 46, 14);
				panel.add(lblGender);

				ButtonGroup gender_group = new ButtonGroup();

				JRadioButton rdbtnM = new JRadioButton("M");
				rdbtnM.setBackground(Color.WHITE);
				rdbtnM.setBounds(143, 79, 38, 23);
				panel.add(rdbtnM);
				gender_group.add(rdbtnM);

				JRadioButton rdbtnF = new JRadioButton("F");
				rdbtnF.setBackground(Color.WHITE);
				rdbtnF.setBounds(183, 79, 31, 23);
				panel.add(rdbtnF);
				gender_group.add(rdbtnF);

				Object[][] data = new Object[bn.getSample().getNumFactors()][3];
				for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
					data[i][0] = bn.getSample().getHeaders()[i];
					data[i][1] = 0;
					data[i][2] = bn.getSample().getVarDomain()[i][0] + " - " + bn.getSample().getVarDomain()[i][1];
				}

				JPanel panel_1 = new JPanel();
				panel_1.setBackground(Color.WHITE);
				panel_1.setBorder(new TitledBorder(null, "Symptoms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_1.setBounds(10, 141, 464, 263);
				f1.getContentPane().add(panel_1);
				panel_1.setLayout(null);

				String[] header = {"Name","Value","Range"};
				JTable table_sym = new JTable(data,header){
					public Class<? extends Object> getColumnClass(int c)
					{
						if(c == 1) return JScrollPane.class;
						else return getValueAt(0, c).getClass();
					}
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						// Just 'Value' column
						return column == 1;
					}
				};
				table_sym.setFillsViewportHeight(true);
				table_sym.getColumnModel().getColumn(0).setPreferredWidth(300);
				table_sym.getColumnModel().getColumn(1).setPreferredWidth(20);
				table_sym.getColumnModel().getColumn(2).setPreferredWidth(20);

				JComboBox<Integer> comboBoxG = new JComboBox<Integer>();
				//comboBoxG.set
				table_sym.getColumnModel().getColumn(1).setCellEditor(new MyEditor(comboBoxG));


				JScrollPane table_scroll = new JScrollPane(table_sym);
				table_scroll.setLocation(10, 15);
				table_scroll.setSize(444, 237);

				table_scroll.setVisible(true);
				panel_1.setLayout(null);
				//table_scroll.setBounds(0,0,100,100);
				table_scroll.setViewportView(table_sym);
				panel_1.add(table_scroll);

				JPanel panel_med = new JPanel();
				panel_med.setBorder(new TitledBorder(null, "Medical attributes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_med.setBackground(Color.WHITE);
				panel_med.setBounds(247, 11, 227, 119);
				f1.getContentPane().add(panel_med);
				panel_med.setLayout(null);

				JLabel lblBloodType = new JLabel("Blood type:");
				lblBloodType.setBounds(10, 23, 62, 14);
				panel_med.add(lblBloodType);

				JComboBox<String> comboBox = new JComboBox<String>();
				comboBox.setBackground(Color.WHITE);
				comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"O+", "A+", "B+", "AB+", "AB-", "A-", "O-", "B-"}));
				comboBox.setBounds(79, 20, 53, 20);
				panel_med.add(comboBox);
				comboBox.setFocusable(true);

				JButton btnAddRecord = new JButton("Add record");
				btnAddRecord.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						// changeIndivMode(1);
						setEnabled(panel_2, true);
						btnPersonalInformation.setEnabled(true);
						btnEditProfile.setEnabled(true);

						patient_info = new String[1][5];
						patient_info[0][0] = id_textField.getText();
						patient_info[0][1] = name_textField.getText();
						patient_info[0][2] = "" + age_spinner.getValue();
						patient_info[0][3] = rdbtnM.isSelected() ? "Male":"Female";
						patient_info[0][4] = comboBox.getSelectedItem().toString();

						panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual: " + patient_info[0][0], TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

						symptoms = new int[1][bn.getSample().getNumFactors()];
						for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
							symptoms[0][i] = (int) table_sym.getValueAt(i, 1);
						}

						f1.setVisible(false);
						f1.dispose();
					}
				});
				btnAddRecord.setBounds(10, 415, 464, 35);
				f1.getContentPane().add(btnAddRecord);
			}
		});
		btnCreateNewRecord.setBounds(142, 18, 124, 33);
		panel_4.add(btnCreateNewRecord);

		panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Group", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(10, 172, 276, 271);
		panel.add(panel_5);
		panel_5.setLayout(null);

		JButton btnUploadPatientsRecords = new JButton("<html>Upload<br>records</html>", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/records.png")));
		btnUploadPatientsRecords.setBounds(141, 36, 111, 37);
		panel_5.add(btnUploadPatientsRecords);

		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 21, 111, 239);
		panel_5.add(panel_3);
		panel_3.setBackground(Color.WHITE);
		panel_3.setBorder(new TitledBorder(null, "Patients ID's", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setLayout(null);
		scrollPanel_patients = new JScrollPane();
		scrollPanel_patients.setBounds(6, 16, 99, 216);
		panel_3.add(scrollPanel_patients);

		patients_list = new JList<String>();
		patients_list.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPanel_patients.setViewportView(patients_list);
		patients_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		patients_list.setValueIsAdjusting(true);
		patients_list.setEnabled(false);
		patients_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Empty"};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});

		JButton btnSeeAll = new JButton("<html><center>Group<br>overview</center></html>", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/list.png")));
		btnSeeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFrame f3 = new JFrame("Group overview (" + patient_info.length + " individuals)");
				f3.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/list.png")).getImage());
				f3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f3.setBounds(100, 100, 656, 423);
				f3.getContentPane().setBackground(Color.WHITE);
				f3.setVisible(true);
				f3.getContentPane().setLayout(null);

				JTable table3 = new JTable(patient_info, pat_info_headers);
				DefaultTableModel tableModel = new DefaultTableModel(patient_info, pat_info_headers) {
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				};
				table3.setModel(tableModel);
				table3.getColumnModel().getColumn(0).setPreferredWidth(50);
				table3.getColumnModel().getColumn(1).setPreferredWidth(300);
				JScrollPane scroll_table = new JScrollPane(table3);
				scroll_table.setBounds(0, 0, 630, 373);
				scroll_table.setViewportView(table3);
				f3.getContentPane().add(scroll_table);
			}
		});
		btnSeeAll.setBounds(141, 84, 111, 37);
		panel_5.add(btnSeeAll);

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Action over selected ID", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_6.setBackground(Color.WHITE);
		panel_6.setBounds(131, 132, 135, 128);
		panel_5.add(panel_6);
		panel_6.setLayout(null);

		JButton btnSeeIndividual = new JButton("Overview", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/overview.png")));
		btnSeeIndividual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int idx = patients_list.getSelectedIndex();
				if(idx == -1)return;

				JFrame f4 = new JFrame();
				f4.setTitle("Overview (patient " + patient_info[idx][0] + ")");
				f4.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/overview.png")).getImage());
				f4.setVisible(true);
				f4.setBounds(100, 100, 340, 500);
				f4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f4.getContentPane().setBackground(Color.WHITE);
				f4.getContentPane().setLayout(null);
				f4.setResizable(false);

				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Personal information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panel.setBounds(10, 11, 305, 103);
				f4.getContentPane().add(panel);
				panel.setLayout(null);

				JLabel lblId = new JLabel("ID: \t" + patient_info[idx][0]);
				lblId.setBounds(31, 24, 264, 14);
				panel.add(lblId);

				JLabel lblName = new JLabel("Name: \t" + patient_info[idx][1]);
				lblName.setBounds(31, 44, 264, 14);
				panel.add(lblName);

				JLabel lblAge = new JLabel("Age: " + patient_info[idx][2]);
				lblAge.setBounds(31, 69, 59, 14);
				panel.add(lblAge);

				JLabel lblGender = new JLabel("Gender: " + patient_info[idx][3]);
				lblGender.setBounds(100, 69, 92, 14);
				panel.add(lblGender);

				JPanel panel_2 = new JPanel();
				panel_2.setBorder(new TitledBorder(null, "Medical attributes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_2.setBackground(Color.WHITE);
				panel_2.setBounds(10, 125, 305, 60);
				f4.getContentPane().add(panel_2);
				panel_2.setLayout(null);

				JLabel lblBloodType = new JLabel("Blood type: " + patient_info[idx][4]);
				lblBloodType.setBounds(31, 31, 264, 14);
				panel_2.add(lblBloodType);

				JPanel panel_1 = new JPanel();
				panel_1.setBackground(Color.WHITE);
				panel_1.setBorder(new TitledBorder(null, "Symptoms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_1.setBounds(10, 196, 305, 254);
				f4.getContentPane().add(panel_1);
				panel_1.setLayout(null);

				Object[][] data = new Object[bn.getSample().getNumFactors()][2];
				for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
					data[i][0] = bn.getSample().getHeaders()[i];
					data[i][1] = symptoms[idx][i];
				}

				String[] header = {"Symptom", "Value"};
				JTable table = new JTable(data,header);

				DefaultTableModel tableModel = new DefaultTableModel(data, header) {
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				};
				table.setModel(tableModel);
				table.setFillsViewportHeight(true);
				JScrollPane table_scroll = new JScrollPane(table);
				table_scroll.setLocation(10, 15);
				table_scroll.setSize(285, 228);

				table_scroll.setVisible(true);
				panel_1.setLayout(null);
				table_scroll.setViewportView(table);
				panel_1.add(table_scroll);

			}
		});
		btnSeeIndividual.setBounds(10, 24, 115, 41);
		panel_6.add(btnSeeIndividual);

		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int idx = patients_list.getSelectedIndex();
				if(idx == -1)return;

				JTextField id_textField;
				JTextField name_textField;

				JFrame f1 = new JFrame();
				f1.setTitle("Edit record for single patient");
				f1.setIconImage(new ImageIcon(diagnosis_JFrame.class.getResource("/resources/edit_record.png")).getImage());
				f1.setVisible(true);
				f1.setBounds(100, 100, 500, 500);
				f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f1.getContentPane().setBackground(Color.WHITE);
				f1.getContentPane().setLayout(null);
				f1.setResizable(false);

				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Personal information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panel.setBounds(10, 11, 227, 119);
				f1.getContentPane().add(panel);
				panel.setLayout(null);

				JLabel lblId = new JLabel("ID:");
				lblId.setBounds(31, 24, 15, 14);
				panel.add(lblId);

				id_textField = new JTextField();
				id_textField.setText(patient_info[idx][0]);
				id_textField.setBounds(56, 21, 158, 20);
				panel.add(id_textField);
				id_textField.setColumns(10);

				JLabel lblName = new JLabel("Name:");
				lblName.setBounds(14, 52, 31, 14);
				panel.add(lblName);

				name_textField = new JTextField(patient_info[idx][1]);
				name_textField.setBounds(56, 49, 158, 20);
				panel.add(name_textField);
				name_textField.setColumns(10);

				JLabel lblAge = new JLabel("Age:");
				lblAge.setBounds(24, 83, 23, 14);
				panel.add(lblAge);

				JSpinner age_spinner = new JSpinner();
				age_spinner.setModel(new SpinnerNumberModel(Integer.parseInt(patient_info[idx][2]), 0, 150, 1));
				age_spinner.setBounds(56, 80, 40, 20);
				panel.add(age_spinner);

				JLabel lblGender = new JLabel("Gender:");
				lblGender.setBounds(104, 83, 46, 14);
				panel.add(lblGender);

				ButtonGroup gender_group = new ButtonGroup();

				JRadioButton rdbtnM = new JRadioButton("M");
				rdbtnM.setBackground(Color.WHITE);
				rdbtnM.setBounds(143, 79, 38, 23);
				panel.add(rdbtnM);
				gender_group.add(rdbtnM);

				JRadioButton rdbtnF = new JRadioButton("F");
				rdbtnF.setBackground(Color.WHITE);
				rdbtnF.setBounds(183, 79, 31, 23);
				panel.add(rdbtnF);
				gender_group.add(rdbtnF);

				rdbtnM.setSelected(patient_info[idx][3].equals("Male"));
				rdbtnF.setSelected(patient_info[idx][3].equals("Female"));

				Object[][] data = new Object[bn.getSample().getNumFactors()][3];
				for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
					data[i][0] = bn.getSample().getHeaders()[i];
					data[i][1] = symptoms[idx][i];
					data[i][2] = bn.getSample().getVarDomain()[i][0] + " - " + bn.getSample().getVarDomain()[i][1];
				}

				JPanel panel_1 = new JPanel();
				panel_1.setBackground(Color.WHITE);
				panel_1.setBorder(new TitledBorder(null, "Symptoms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_1.setBounds(10, 141, 464, 263);
				f1.getContentPane().add(panel_1);
				panel_1.setLayout(null);

				String[] header = {"Name","Value","Range"};
				JTable table_sym = new JTable(data,header){
					public Class<? extends Object> getColumnClass(int c)
					{
						if(c == 1) return JScrollPane.class;
						else return getValueAt(0, c).getClass();
					}
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						// Just 'Value' column
						return column == 1;
					}
				};
				table_sym.setFillsViewportHeight(true);
				table_sym.getColumnModel().getColumn(0).setPreferredWidth(300);
				table_sym.getColumnModel().getColumn(1).setPreferredWidth(20);
				table_sym.getColumnModel().getColumn(2).setPreferredWidth(20);

				JComboBox<Integer> comboBoxG = new JComboBox<Integer>();
				//comboBoxG.set
				table_sym.getColumnModel().getColumn(1).setCellEditor(new MyEditor(comboBoxG));


				JScrollPane table_scroll = new JScrollPane(table_sym);
				table_scroll.setLocation(10, 15);
				table_scroll.setSize(444, 237);

				table_scroll.setVisible(true);
				panel_1.setLayout(null);
				//table_scroll.setBounds(0,0,100,100);
				table_scroll.setViewportView(table_sym);
				panel_1.add(table_scroll);

				JPanel panel_med = new JPanel();
				panel_med.setBorder(new TitledBorder(null, "Medical attributes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_med.setBackground(Color.WHITE);
				panel_med.setBounds(247, 11, 227, 119);
				f1.getContentPane().add(panel_med);
				panel_med.setLayout(null);

				JLabel lblBloodType = new JLabel("Blood type:");
				lblBloodType.setBounds(10, 23, 62, 14);
				panel_med.add(lblBloodType);

				JComboBox<String> comboBox = new JComboBox<String>();
				comboBox.setBackground(Color.WHITE);
				comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"O+", "A+", "B+", "AB+", "AB-", "A-", "O-", "B-"}));
				comboBox.setSelectedItem(patient_info[idx][4]);
				comboBox.setBounds(79, 20, 53, 20);
				panel_med.add(comboBox);
				comboBox.setFocusable(true);

				JButton btnEditRecord = new JButton("Edit record");
				btnEditRecord.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setEnabled(panel_2, true);
						btnPersonalInformation.setEnabled(true);

						patient_info[idx][0] = id_textField.getText();
						patient_info[idx][1] = name_textField.getText();
						patient_info[idx][2] = "" + age_spinner.getValue();
						patient_info[idx][3] = rdbtnM.isSelected() ? "Male":"Female";
						patient_info[idx][4] = comboBox.getSelectedItem().toString();

						panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual: " + patient_info[idx][0], TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

						for (int i = 0; i < bn.getSample().getNumFactors(); i++) {
							symptoms[idx][i] = (int) table_sym.getValueAt(i, 1);
						}

						f1.setVisible(false);
						f1.dispose();
					}
				});
				btnEditRecord.setBounds(10, 415, 464, 35);
				f1.getContentPane().add(btnEditRecord);




			}
		});
		btnEdit.setBounds(10, 76, 115, 41);
		panel_6.add(btnEdit);
		btnUploadPatientsRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// if input argument is 'null' ask for file:

				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(preferable_loading_path));
				// select only .csv and .txt (filter)
				fc.setFileFilter(new FileNameExtensionFilter("Comma separated values file types (.csv, .txt, ...)", "csv", "txt"));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				File records_file;
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					records_file = fc.getSelectedFile();
				}else{
					return;
				}


				BufferedReader br = null;
				String line = "";

				try {
					br = new BufferedReader(new FileReader(records_file));

					// read first line (header)
					line = br.readLine();
					String[] records_header = line.split(",");

					ArrayList<String[]> rec = new ArrayList<String[]>();

					patients_list.removeAll();
					patients_list.setEnabled(true);

					String[] values, IDs;

					// iterate for every other line
					while ((line = br.readLine()) != null) {
						// use comma as separator
						values = line.split(",");
						// System.out.println(Arrays.toString(values));
						rec.add(values);
					}

					IDs = new String[rec.size()];

					patient_info = new String[rec.size()][records_header.length];
					symptoms = new int[rec.size()][bn.getSample().getNumFactors()];

					for(int i = 0; i < rec.size(); i++){
						IDs[i] = rec.get(i)[0];
						for(int j = 0; j < records_header.length; j++){
							switch (j) {
							case 3:
								patient_info[i][j] = rec.get(i)[j].equals("M") ? "Male" : "Female";
								break;

							default:
								patient_info[i][j] = rec.get(i)[j];
								break;
							}

						}

						for (int j = 0; j < bn.getSample().getNumFactors(); j++) {
							// randomize symptoms
							Random rand = new Random();
							symptoms[i][j] = rand.nextInt(bn.getSample().getMaxValues()[j] + 1);
							// symptoms[i][j] =  bn.getSample().element(i)[j];
						}
					}

					patients_list.setModel(new AbstractListModel<String>() {
						private static final long serialVersionUID = 1L;
						String[] values = IDs;
						public int getSize() {
							return values.length;
						}
						public String getElementAt(int index) {
							return values[index];
						}
					});


					changeIndivMode(2);
					setEnabled(panel_2, true);
					panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Group: " + patient_info.length + " patients", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));


				} catch (FileNotFoundException er) {
					er.printStackTrace();
				} catch (IOException er) {
					er.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException er) {
							er.printStackTrace();	
						}
					}
				}
			}
		});

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Load Classifier", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(10, 11, 296, 78);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JButton btnLoadNewClassifier = new JButton("<html><center>Load<br>Classifier</center></html>", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/diag_graph.png")));
		btnLoadNewClassifier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					// if preferable loading path is different than this ask if user wants to change
					String path = fc.getCurrentDirectory().getAbsolutePath();
					if(path != preferable_loading_path){
						String message = "The current preferable path is " + preferable_loading_path + ".\n\nWould you like to change it to " + path + " ?";
						int dialogResult = JOptionPane.showConfirmDialog(null, message, "Set preferable loading path", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(dialogResult == JOptionPane.YES_OPTION){
							preferable_loading_path = path;
						}
					}

					FileInputStream fis;
					try {
						fis = new FileInputStream(fc.getSelectedFile());
						ObjectInputStream ois;
						ois = new ObjectInputStream(fis);
						bn = (BN)ois.readObject();
						fis.close();
						ois.close();

						JOptionPane.showMessageDialog(null, "The Classifier has been uploaded successfully!", "Info", JOptionPane.PLAIN_MESSAGE, new ImageIcon(diagnosis_JFrame.class.getResource("/resources/diagnosis.png")));

						changeIndivMode(1);

					} catch (FileNotFoundException er) {
						er.printStackTrace();
					} catch (IOException er) {
						er.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				}


			}
		});
		btnLoadNewClassifier.setBounds(10, 20, 133, 42);
		panel_1.add(btnLoadNewClassifier);

		JButton btnCreateNewClassifier = new JButton("<html><center>Create new<br>Classifier</center></html>", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/diag_add_graph.png")));
		btnCreateNewClassifier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				learner_JFrame learn_Classifier = new learner_JFrame(JFrame.DISPOSE_ON_CLOSE);
				learn_Classifier.setVisible(true);
			}
		});
		btnCreateNewClassifier.setBounds(153, 20, 133, 42);
		panel_1.add(btnCreateNewClassifier);

		panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Patients diagnosis", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(316, 11, 296, 543);
		contentPane.add(panel_2);
		panel_2.setLayout(null);


		// **** define optionsIcon
		// icons
		optionsIcon = new Object[3][3];
		optionsIcon[0][0] = new ImageIcon(getClass().getResource("/resources/death1_res.png"));
		optionsIcon[1][0] = new ImageIcon(getClass().getResource("/resources/warn1_res.png"));
		optionsIcon[2][0] = new ImageIcon(getClass().getResource("/resources/health1_res.png"));
		// min
		optionsIcon[0][1] = 0.;
		optionsIcon[1][1] = 30.;
		optionsIcon[2][1] = 70.;
		// max
		optionsIcon[0][2] = 30.;
		optionsIcon[1][2] = 70.;
		optionsIcon[2][2] = 100.;

		JButton btnMakeDiagnose = new JButton("<html><center>Make<br>diagnose</center></html>", new ImageIcon(diagnosis_JFrame.class.getResource("/resources/doc.png")));
		btnMakeDiagnose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				results = new double[symptoms.length][bn.getSample().numberOfClasses()];

				int numFactors = bn.getSample().getNumFactors();
				int numClasses = bn.getSample().numberOfClasses();

				Object[][] data = new Object[symptoms.length][3];
				String[] header = {"ID",bn.getSample().getClassNames()[resultClassIdx], ""};


				FileWriter fileWriter = null;
				try {
					fileWriter = new FileWriter("C:\\Users\\Rafael\\Desktop\\teste1.csv",true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				PrintWriter printWriter = new PrintWriter(fileWriter);


				for (int i = 0; i < symptoms.length; i++) {
					data[i][0] = patient_info[i][0];

					int[] vec = new int[numFactors + 1];
					for (int j = 0; j < numFactors; j++) {
						vec[j] = symptoms[i][j];
					}

					double sum = 0;
					for (int j = 0; j < numClasses; j++) {
						vec[numFactors] = j;
						results[i][j] = bn.prob(bn, vec);
						sum += results[i][j];
					}
					for (int j = 0; j < numClasses; j++) {
						results[i][j] /= sum;
						results[i][j] *= 100;
					}
					data[i][1] = String.format("%.2f", results[i][1]) + " %";

					for(int j = 0; j < optionsIcon.length; j++){
						if(((double)optionsIcon[j][1] <= (double)results[i][resultClassIdx]) && ((double)optionsIcon[j][2] > (double)results[i][resultClassIdx])){
							data[i][2] = optionsIcon[j][0];
							break;
						}
					}


					printWriter.println(results[i][1]);

					// System.out.println("\n\n  " + sum2 + ": "+(results[i][0] < results[i][1] ? "Cancro" : "Sem") + "\n");
				}

				printWriter.close();

				DefaultTableModel model = new DefaultTableModel(data,  header){
					private static final long serialVersionUID = 1L;
					public Class getColumnClass(int column)
					{
						return getValueAt(0, column).getClass();
					}
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				};
				table_results = new JTable(model);
				table_results.setRowHeight(20);
				table_results.setPreferredScrollableViewportSize(table_results.getPreferredSize());
				table_scroll.setViewportView(table_results);

				TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table_results.getModel());
				table_results.setRowSorter(sorter);

				ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
				//sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
				sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
				sorter.setSortKeys(sortKeys);

			}
		});
		btnMakeDiagnose.setBounds(83, 31, 128, 47);
		panel_2.add(btnMakeDiagnose);

		table_results = new JTable();
		table_results.setBounds(10, 110, 276, 411);
		table_results.setFillsViewportHeight(true);
		table_scroll = new JScrollPane(table_results);
		table_scroll.setLocation(10, 89);
		table_scroll.setSize(276, 443);
		table_scroll.setVisible(true);
		table_scroll.setViewportView(table_results);
		panel_2.add(table_scroll);


		// ---
		changeIndivMode(0);

}


// *************************************** AUXILIAR FUNCTION *******************************************

public void changeIndivMode(int mode){
	patientOrGroupLoad = mode;
	setEnabled(panel,true);
	setEnabled(panel_2,false);

	switch (mode) {
	case 0:
		setEnabled(panel,false);
		setEnabled(panel_2,false);
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		break;

		// INDIVIDUAL
	case 1:
		rdbtnIndividual.setSelected(true);
		setEnabled(panel,true);
		setEnabled(panel_2,false);
		setEnabled(panel_4,true);
		setEnabled(panel_5, false);
		btnPersonalInformation.setEnabled(false);
		btnEditProfile.setEnabled(false);

		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Group", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual: <EMPTY>", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		// reset group list
		patients_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Empty"};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});



		break;

		// GROUP
	case 2:
		rdbtnGroup.setSelected(true);
		setEnabled(panel,true);
		setEnabled(panel_2,false);
		setEnabled(panel_4,false);
		setEnabled(panel_5, true);

		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Group: <EMPTY>", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Individual", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		break;

	}
}

public void setEnabled(Component component, boolean enabled) {
	component.setEnabled(enabled);
	if (component instanceof Container) {
		for (Component child : ((Container) component).getComponents()) {
			setEnabled(child, enabled);
		}
	}
}

// ******************************** CLASSES TO HANDLE JTABLE *****************************

class MyEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;

	public MyEditor(JComboBox<Integer> num) {
		super(num);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
			int row, int column) {
		@SuppressWarnings("unchecked")
		JComboBox<Integer> editor = (JComboBox<Integer>) super.getTableCellEditorComponent(table, value, isSelected, row, column);

		editor.removeAllItems();
		for(int i = 0; i <= bn.getSample().getMaxValues()[row]; i++){
			editor.addItem(i);
		}
		editor.setSelectedIndex(0);
		return editor;
	}
}	
}
