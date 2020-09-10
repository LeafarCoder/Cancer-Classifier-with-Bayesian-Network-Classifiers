package learner;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import tan.*;

import javax.swing.border.LineBorder;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.SystemColor;
import javax.swing.JTextField;

public class learner_JFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	// Bayes Network
	BN bn;
	Graph<String, String> graph;
	double pseudo_counts = 0.5;

	// sample variable
	Sample sample = null;

	// window
	private JPanel contentPane;
	private int height = 600;
	// help extension
	private int short_width = 700;
	private int help_width = 400;
	private int full_width = short_width + help_width;

	// log history
	private int logHeight = 250;
	private int margins = 5;

	// load_info box
	private int load_info_box_width = 100;
	private int load_info_box_height = 130;

	// other global variables for components
	static JTextPane logText;
	JLabel empty_file_img;
	JTextPane loaded_sample_text;
	JPanel load_file_info;
	JTable table;
	JScrollPane scrollDataTable;
	JButton btnLockSidePanel;
	static JTabbedPane tabbedPane;
	JTextArea table_msg;
	static JTabbedPane tabbedPane_down;
	CardLayout cardLayout;
	JPanel pane_DataTable;
	JButton btnLearn;
	BasicVisualizationServer<String, String> bvs;
	JPanel pane_graph;
	JMenuItem mnViewGraph;
	JPanel panel_saveClassifier;
	JCheckBox chckbxDefaultExtension;
	static JTextPane errorlogText;
	JTextArea console_text_area;

	// user hacks
	private String preferable_loading_path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
	boolean tabbedPane_locked = false;
	boolean BN_generated = false;
	Console console;

	// Software developers:
	String[] software_dev = {"André Miranda", "José Correia", "Sofia Cotrim"};

	// Latest version:
	String latest_version = "v0.01";
	private JTextField textField;
	private JTextField save_name;
	private JTextField save_path;
	private JTextField txtclf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					learner_JFrame frame = new learner_JFrame(JFrame.EXIT_ON_CLOSE);
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
	public learner_JFrame(int close_mode) {
		// Set look and feel of components as the system's look and feel
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex) {
			ex.printStackTrace();
		}


		setTitle("Classifier Learner Interface");
		setResizable(false);
		setDefaultCloseOperation(close_mode);
		setBounds(100, 50, short_width, height);
		setIconImage(new ImageIcon(getClass().getResource("/resources/learner_icon.png")).getImage());

		// ************************************** MENU BAR ***************************************************
		// UIManager.getLookAndFeelDefaults().put("Menu.arrowIcon", null);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// ****
		JMenuItem mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mnNew = new JMenuItem("New", new ImageIcon(getClass().getResource("/resources/new_file.png")));
		mnNew.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendToLogHistory("nice", Color.BLACK, false);
			}
		});
		mnFile.add(mnNew);


		JMenuItem mnSave = new JMenuItem("Save", new ImageIcon(getClass().getResource("/resources/save_icon.png")));
		mnFile.add(mnSave);


		JMenuItem mnClose = new JMenuItem("Close", new ImageIcon(getClass().getResource("/resources/close.png")));
		mnClose.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false); dispose();
			}
		});
		mnFile.add(mnClose);


		// ****
		JMenuItem mnView = new JMenu("View");

		menuBar.add(mnView);

		mnViewGraph = new JMenuItem("Graph", new ImageIcon(getClass().getResource("/resources/graph.png")));
		mnViewGraph.setEnabled(false);
		mnViewGraph.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if(!mnViewGraph.isEnabled())return;
				open_ViewGraph_window(true);
			}
		});
		mnView.add(mnViewGraph);

		// ****
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mnGetHelp = new JMenuItem("Get help", new ImageIcon(getClass().getResource("/resources/information.png")));
		mnGetHelp.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				expand_tabbedPane();
				tabbedPane_locked = true;
				// always last one
				tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
				btnLockSidePanel.setIcon(new ImageIcon(getClass().getResource("/resources/open_lock.png")));
			}
		});
		mnHelp.add(mnGetHelp);

		JMenuItem mnAboutCli = new JMenuItem("About CLI", new ImageIcon(getClass().getResource("/resources/learner_icon.png")));
		mnAboutCli.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String msg = "The Classifier Learner Interface (CLI) is designed to facilitate \n"+
						" the use of the latest high-tech methods for various diseases diagnosis.\n"+
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
				dialog.setTitle("About CLI");
				dialog.setIconImage(new ImageIcon(getClass().getResource("/resources/learner_icon.png")).getImage());
				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		mnHelp.add(mnAboutCli);

		// ************************************** Menu bar END ***************************************************

		// contentPane variable
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);


		// *************************************** Loaded sample info END *****************************************		



		// ***************************************** LOCK BUTTON ********************************************
		btnLockSidePanel = new JButton(new ImageIcon(getClass().getResource("/resources/close_lock.png")));
		final int btnLockSidePanel_width = 25;
		btnLockSidePanel.setBounds(short_width - btnLockSidePanel_width - 40, 10, btnLockSidePanel_width, btnLockSidePanel_width);
		btnLockSidePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){

				if(!tabbedPane_locked){
					expand_tabbedPane();
					tabbedPane_locked = true;
					btnLockSidePanel.setIcon(new ImageIcon(getClass().getResource("/resources/open_lock.png")));
				}else{
					retract_tabbedPane();
					tabbedPane_locked = false;
					btnLockSidePanel.setIcon(new ImageIcon(getClass().getResource("/resources/close_lock.png")));
				}
			}
		});
		contentPane.add(btnLockSidePanel);
		// ***************************************** Lock button END ********************************************


		// ***************************************** TABBED PANE RIGHT ********************************************
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				expand_tabbedPane();

			}
			@Override
			public void mouseExited(MouseEvent e) {

				Point mouse_pnt = MouseInfo.getPointerInfo().getLocation();
				if(	(mouse_pnt.x > getX() + short_width - 30 &&
						mouse_pnt.y > getY() + 55 &&
						mouse_pnt.x < getX() + full_width - 30 &&
						mouse_pnt.y < getY() + height - 50) || tabbedPane_locked)return;
				retract_tabbedPane();
			}
		});

		tabbedPane.setBounds(short_width - 35, margins, full_width - short_width + margins, height - margins - 70);
		contentPane.add(tabbedPane);

		// table pane
		pane_DataTable = new JPanel(new CardLayout());
		cardLayout = (CardLayout)(pane_DataTable.getLayout());
		JPanel table_msg_panel = new JPanel(new GridBagLayout());
		table_msg = new JTextArea("No data available to preview.\r\nUpload a file to see data.");
		table_msg.setEditable(false);
		table_msg.setBackground(SystemColor.control);
		table_msg.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table_msg_panel.add(table_msg);
		table = new JTable();
		table.setColumnSelectionAllowed(true);
		scrollDataTable = new JScrollPane(table);
		pane_DataTable.add(scrollDataTable);
		pane_DataTable.add(table_msg_panel);
		tabbedPane.addTab("", null, pane_DataTable, null);
		cardLayout.last(pane_DataTable);

		// help pane
		JPanel pane_instructions = new JPanel();
		pane_instructions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Instructions", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pane_instructions.setBackground(Color.WHITE);
		pane_instructions.setLayout(null);
		JTextArea txtrClickOn = new JTextArea();
		txtrClickOn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtrClickOn.setText("\r\n\r\n1. Click on \u201CUpload sample\u201D and \r\nchoose the file with the database\r\n on your computer.\r\n\r\n2. Click on \u201CCreate Classifier\u201C.\r\n\r\n3. In order to save the classifier,\r\n enter the name of the file you\r\n wish to save and choose the path.\r\n\r\n4. Press Save.");
		txtrClickOn.setEditable(false);
		txtrClickOn.setBounds(10, 15, full_width - short_width - 2*margins - 80, height - 2*margins - 100);
		pane_instructions.add(txtrClickOn);
		tabbedPane.addTab("", null, pane_instructions, null);



		// ************************************************************************************
		// Create vertical labels to render tab titles
		JLabel labTab1 = new JLabel(" Preview data ");
		labTab1.setIcon(new ImageIcon(getClass().getResource("/resources/data.png")));
		labTab1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labTab1.setUI(new VerticalLabelUI(false)); // true/false to make it upwards/downwards
		tabbedPane.setTabComponentAt(0, labTab1); // For component1

		JLabel labTab2 = new JLabel(" Help ");
		labTab2.setIcon(new ImageIcon(getClass().getResource("/resources/question_mark_rot.png")));
		labTab2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		labTab2.setUI(new VerticalLabelUI(false));
		tabbedPane.setTabComponentAt(1, labTab2); // For component2

		// ***************************************** Tabbed pane right END ********************************************


		// ***************************************** TABBED PANE DOWN ********************************************
		tabbedPane_down = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_down.setBounds(margins, (int)(height - logHeight - 10*margins), short_width - 10*margins, logHeight);
		contentPane.add(tabbedPane_down);

		// CONSOLE
		console = new Console();

		console_text_area = new JTextArea();
		Document console_document = console_text_area.getDocument();
		console_text_area.setText("Classifier Learner Interface CONSOLE (v0.01)\r\n\r\nType 'help' command for help.\r\n\r\n> ");
		((AbstractDocument)console_document).setDocumentFilter(new NonEditableLineDocumentFilter());
		DefaultCaret caret = (DefaultCaret) console_text_area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane_console = new JScrollPane();
		scrollPane_console.setViewportView(console_text_area);
		tabbedPane_down.addTab("Console", new ImageIcon(getClass().getResource("/resources/console_terminal.png")), scrollPane_console, "Prompt for execution of commands");

		// LOG HISTORY
		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setBounds(margins, height - logHeight - margins - 50, short_width - 2*margins - 6, logHeight);
		tabbedPane_down.addTab("Log History", new ImageIcon(getClass().getResource("/resources/log_history.png")), scrollPane, "Keeps the records of all the operations.");

		logText = new JTextPane();
		logText.setMargin(new Insets(5,5,5,5));
		DefaultCaret caret2 = (DefaultCaret) logText.getCaret();
		caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(logText);
		logText.setFont(new Font("Tahoma", Font.PLAIN, 13));
		logText.setEditable(false);

		// ERROR LOG
		JScrollPane scrollPane_errorLog = new JScrollPane();
		//scrollPane.setBounds(margins, height - logHeight - margins - 50, short_width - 2*margins - 6, logHeight);
		tabbedPane_down.addTab("Error log", new ImageIcon(getClass().getResource("/resources/error_log.png")), scrollPane_errorLog, "Warns about errors.");

		errorlogText = new JTextPane();
		errorlogText.setMargin(new Insets(5,5,5,5));
		DefaultCaret caret3 = (DefaultCaret) errorlogText.getCaret();
		caret3.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane_errorLog.setViewportView(errorlogText);
		errorlogText.setFont(new Font("Tahoma", Font.PLAIN, 13));
		errorlogText.setEditable(false);

		tabbedPane_down.setSelectedIndex(1);
		// ***************************************** Tabbed pane down END ********************************************



		// **************************************************** UPLOAD SAMPLE BUTTON ****************************************************

		JPanel panel_classifier = new JPanel();
		panel_classifier.setBorder(new TitledBorder(null, "Create Classifier", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_classifier.setBackground(Color.WHITE);
		panel_classifier.setBounds(10, 29, 307, 272);
		contentPane.add(panel_classifier);
		panel_classifier.setLayout(null);


		JPanel panel_uploadSample = new JPanel();
		panel_uploadSample.setBounds(23, 21, 274, 155);
		panel_classifier.add(panel_uploadSample);
		panel_uploadSample.setBorder(new TitledBorder(null, "Upload sample", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_uploadSample.setBackground(Color.WHITE);
		panel_uploadSample.setLayout(null);

		JButton btnUploadSample = new JButton("<html>Upload <br>sample</html>");
		btnUploadSample.setBounds(20, 56, 75, 35);
		panel_uploadSample.add(btnUploadSample);
		btnUploadSample.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upload_sample("");
			}
		});
		btnUploadSample.setToolTipText("Upload a sample from an existing file");
		// **************************************************** Upload sample button END ****************************************************

		// **************************************************** CREATE CLASSIFIER BUTTON ****************************************************

		btnLearn = new JButton("Create Classifier");
		btnLearn.setBounds(100, 213, 111, 36);
		panel_classifier.add(btnLearn);
		btnLearn.setEnabled(false);
		btnLearn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
 
				learn();

			}
		});


		// **************************************************** Create classifier button END ************************************************


		// ********************************************** LOADED SAMPLE INFO **********************************************

		// image
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource("/resources/empty_file.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// scale image
		double img_scale_factor = 0.65;
		int w = (int)(image.getWidth() * img_scale_factor);
		int h = (int)(image.getHeight() * img_scale_factor);
		BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(img_scale_factor, img_scale_factor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		image2 = scaleOp.filter(image, image2);

		load_file_info = new JPanel();
		load_file_info.setBounds(105, 11, 154, 133);
		panel_uploadSample.add(load_file_info);
		load_file_info.setBorder(null);
		load_file_info.setBackground(Color.WHITE);
		empty_file_img = new JLabel(new ImageIcon(image2));
		empty_file_img.setVisible(true);
		load_file_info.add(empty_file_img);

		// info text
		loaded_sample_text = new JTextPane();
		loaded_sample_text.setBounds(5, 5, load_info_box_width - 10, load_info_box_height - 10);
		loaded_sample_text.setEditable(false);
		load_file_info.add(loaded_sample_text);

		loaded_sample_text.setVisible(false);
		// ********************************************** Loaded sample info END **********************************************

		// *************************** PSEUDO-COUNTS **************************************
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String text = textField.getText();
				double value = 0;
				try
				{
					value = Double.parseDouble(text);
					sendToLogHistory("Pseudo-count defined: S = "+value, Color.BLACK, false);
					pseudo_counts = value;
				}
				catch(NumberFormatException e)
				{
					sendToLogHistory("Pseudo-counts must be a positive real number!", Color.RED, true);
					textField.setText(""+pseudo_counts);
				}
			}
		});
		textField.setText("0.5");
		textField.setToolTipText("<html>Pseudo-counts (parameter S): \r\n<br>\r\nValue greater than zero that provides flexibility over a small data set.\r\n<br>\r\nIn such a data set, value that don't show up too often are still considered <br>\r\nas possible.<br>\r\n<b>[Default value = 0.5]</b>\r\n</html>");
		textField.setBounds(111, 180, 41, 20);
		panel_classifier.add(textField);
		textField.setColumns(10);

		JLabel lblPseudoCounts = new JLabel("Pseudo-counts:");
		lblPseudoCounts.setToolTipText("<html>Pseudo-counts (parameter S): \r\n<br>\r\nValue greater than zero that provides flexibility over a small data set.\r\n<br>\r\nIn such a data set, value that don't show up too often are still considered <br>\r\nas possible.<br>\r\n<b>[Default value = 0.5]</b>\r\n</html>");
		lblPseudoCounts.setBounds(33, 183, 79, 14);
		panel_classifier.add(lblPseudoCounts);
		// *************************** Pseudo-counts END **************************************

		
		// ************************************* SAVE PANEL *************************************
		panel_saveClassifier = new JPanel();
		panel_saveClassifier.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Save Classifier", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_saveClassifier.setBackground(Color.WHITE);
		panel_saveClassifier.setBounds(343, 29, 284, 272);
		contentPane.add(panel_saveClassifier);
		panel_saveClassifier.setEnabled(false);
		panel_saveClassifier.setLayout(null);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save("");
			}
		});
		btnSave.setBounds(104, 220, 83, 29);
		panel_saveClassifier.add(btnSave);

		JLabel lblClassifierName = new JLabel("Classifier name:");
		lblClassifierName.setBounds(22, 40, 83, 14);
		panel_saveClassifier.add(lblClassifierName);

		save_name = new JTextField();
		save_name.setBounds(32, 65, 228, 20);
		panel_saveClassifier.add(save_name);
		save_name.setColumns(10);

		save_path = new JTextField();
		save_path.setBounds(32, 147, 228, 20);
		panel_saveClassifier.add(save_path);
		save_path.setColumns(10);

		JLabel lblNewLabel = new JLabel("Save path:");
		lblNewLabel.setBounds(22, 127, 62, 14);
		panel_saveClassifier.add(lblNewLabel);

		JButton btnSaveBrowse = new JButton("Browse...");
		btnSaveBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				if(preferable_loading_path != ""){
					fc.setCurrentDirectory(new File(preferable_loading_path));
				}
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					save_path.setText(""+fc.getSelectedFile());
				}
			}
		});
		btnSaveBrowse.setBounds(32, 178, 89, 23);
		panel_saveClassifier.add(btnSaveBrowse);

		txtclf = new JTextField();
		txtclf.setText(".clf");
		txtclf.setBounds(155, 93, 35, 20);
		panel_saveClassifier.add(txtclf);
		txtclf.setColumns(10);
		txtclf.setEditable(true);

		chckbxDefaultExtension = new JCheckBox("Use default extension");
		chckbxDefaultExtension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtclf.setEnabled(!chckbxDefaultExtension.isSelected());
				if(chckbxDefaultExtension.isSelected())
					txtclf.setText(".clf");
			}
		});
		chckbxDefaultExtension.setSelected(true);
		chckbxDefaultExtension.setBackground(Color.WHITE);
		chckbxDefaultExtension.setBounds(24, 92, 131, 23);
		panel_saveClassifier.add(chckbxDefaultExtension);

		for(Component c: panel_saveClassifier.getComponents()){c.setEnabled(false);}
		// ************************************* Save panel END *************************************
		
		
		sendToLogHistory("Initializing Classifier Learner Interface...", Color.BLACK, false);
		String msg = "Learner Interface has initialized successfully.\n" +
				"\t--------------------------------------------------------\n"+
				"\t  Program version: 0.01 (Beta)\n" +
				"\t  Last update date: 12/05/2018.\n" +
				"\t  Software developers: André Miranda / Rafael Correia / Sofia Cotrim\n"+
				"\t-------------------------------------------------------";
		sendToLogHistory(msg, Color.BLACK, false);
		
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++++++++++++++++++ UTILITY FUNCTIONS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Function that converts a vector of Strings (representing integers) into a vector of Integers (parsing element by element)
	private static int[] str2int(String[] str){
		int[] ans = new int[str.length];
		for (int i = 0; i < ans.length; i++) {
			ans[i] = Integer.parseInt(str[i]);			
		}
		return ans;
	}

	private static void sendToLogHistory(String msg, Color c, boolean bold){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time_info = "\n[" + sdf.format(cal.getTime()) + "]: \t";
		String final_msg = msg;

		JTextPane panel;
		// if error
		if(c == Color.RED){
			panel = errorlogText;
		}else{
			panel = logText;
		}
		StyledDocument doc = panel.getStyledDocument();
		Style style = panel.addStyle("Tahoma", null);
		StyleConstants.setForeground(style, Color.BLACK);
		StyleConstants.setBold(style, false);

		try {
			doc.insertString(doc.getLength(), time_info, style);
		}catch (BadLocationException e){}

		StyleConstants.setForeground(style, c);
		StyleConstants.setBold(style, bold);

		try {
			doc.insertString(doc.getLength(), final_msg, style);
		}catch (BadLocationException e){}

		//int index = (Color.RED == c) ? 2:1;
		// tabbedPane_down.setSelectedIndex(index);
	}

	public void learn(){
		if(sample == null){
			sendToLogHistory("No sample file has yet been uploaded!", Color.RED, true);
			return;
		}

		long total_time = 0;
		long start = System.nanoTime();
		
		int[] max = sample.getMaxValues();
		int var_num = sample.getNumFactors();
		int class_idx = var_num;

		// new weighted graph
		WGraph w_graph = new WGraph(var_num);

		// define weights: ****************************************
		// ADD TO SEPARATE FUNCTION (AUXILIAR)

		// for each pair of nodes
		for(int i = 1; i < var_num; i++){
			for(int j = 0; j < i; j++){
				// weight for i <-> j
				double weight = 0;

				// System.out.println("'"+i+";"+j+";"+class_idx+"'");
				// sum over all possible values for X, Y and C
				for(int x = 0; x <= max[i]; x++){
					for(int y = 0; y <= max[j]; y++){

						// Class is the last variable from sample, so index is the last one
						for(int c = 0; c <= max[class_idx]; c++){
							double cnt_xyc = sample.count(new int[]{i,j,class_idx},new int[]{x,y,c});
							// heuristic (no need for further calculation)
							if(cnt_xyc == 0.)continue;

							double cnt_xc = sample.count(new int[]{i,class_idx},new int[]{x,c});
							
							double cnt_yc = sample.count(new int[]{j,class_idx},new int[]{y,c});

							double cnt_c = sample.count(new int[]{class_idx},new int[]{c});

							// System.out.println("   ("+x+" / "+y+" / "+c+") : "+cnt_xyc+",  "+cnt_xc+",  "+cnt_yc+",  "+cnt_c);
							weight += cnt_xyc/(double)sample.length() * Math.log10((cnt_xyc * cnt_c) / (cnt_yc * cnt_xc));

						}
					}
				}

				w_graph.add_edge(i, j, weight);
				// System.out.println("Weight ("+i+" <--> "+j+") = "+weight);
			}
		}
		System.out.println("Weights calculation: " + (System.nanoTime() - start)/1000000000. + " seconds");
		total_time += System.nanoTime() - start;
		// define weights END ****************************************
		
		start = System.nanoTime();
		// Apply MST
		DGraph d_graph = w_graph.MST(0);
		System.out.println("MST: " + (System.nanoTime() - start)/1000000000. + " seconds");
		total_time += System.nanoTime() - start;
		
		
		// Define the Bayes Network
		start = System.nanoTime();
		
		bn = new BN(d_graph, sample, pseudo_counts);
		BN_generated = true;

		System.out.println("BN creation: " + (System.nanoTime() - start)/1000000000. + " seconds");
		total_time += System.nanoTime() - start;
		
		System.out.println("\nTotal time: " + total_time/1000000000. + " seconds");
		
		// enable graph view button (menu)
		mnViewGraph.setEnabled(true);

		// enable saving
		panel_saveClassifier.setEnabled(true);
		for(Component c: panel_saveClassifier.getComponents()){c.setEnabled(true);}
		txtclf.setEnabled(!chckbxDefaultExtension.isSelected());

		// Messages
		sendToLogHistory("The Classifier has been created successfully", Color.BLACK, false);
		JOptionPane.showMessageDialog(null, "The Classifier has been created successfully!", "Info", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/resources/learner_icon.png")));

	}

	public void upload_sample(String file_path){
		// sample reset
		sample = null;

		// disable save Classifier
		panel_saveClassifier.setEnabled(false);

		// enable saving
		for(Component c: panel_saveClassifier.getComponents()){c.setEnabled(false);}
		txtclf.setEnabled(!chckbxDefaultExtension.isSelected());
		panel_saveClassifier.setEnabled(false);

		// reset the BN
		BN_generated = false;

		// disable view graph menu button
		mnViewGraph.setEnabled(false);

		// enable Classifier button
		btnLearn.setEnabled(false);

		// change info for image meaning "no file uploaded"
		empty_file_img.setVisible(true);
		loaded_sample_text.setVisible(false);
		load_file_info.setBorder(null);

		// table data reset and hide
		table = new JTable();
		cardLayout.last(pane_DataTable);


		// file handle
		File csv_file;

		if(file_path == ""){
			// if input argument is 'null' ask for file:

			JFileChooser fc = new JFileChooser();
			if(preferable_loading_path != ""){
				fc.setCurrentDirectory(new File(preferable_loading_path));
			}
			// select only .csv and .txt (filter)
			fc.setFileFilter(new FileNameExtensionFilter("Comma separated values file types (.csv, .txt, ...)", "csv", "txt"));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){

				// if preferable loading path is different than this ask if user wants to change
				String path = fc.getCurrentDirectory().getAbsolutePath();
				if(path != preferable_loading_path){
					String message = "The current preferable path is " + preferable_loading_path + ".\n\nWould you like to change it to " + path + " ?";
					int dialogResult = JOptionPane.showConfirmDialog(null, message, "Set preferable loading path", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(dialogResult == JOptionPane.YES_OPTION){
						preferable_loading_path = path;
						sendToLogHistory("Preferable loading path set to " + path + ".", Color.BLACK, false);
					}
				}

				csv_file = fc.getSelectedFile();
			}else{
				return;
			}
		}else{
			// otherwise use the argument input
			csv_file = new File(file_path);
		}


		BufferedReader br = null;
		String line = "";

		// checks for errors during the reading of the file
		boolean error = false;
		int error_line = 1;

		try {
			String[] headers_keep = null;
			String[] classNames_keep = null;
			
			br = new BufferedReader(new FileReader(csv_file));

			// read first line (just to know the number of variables and/or know the names of each variable)
			line = br.readLine();
			String[] values = line.split(",");
			int sample_size = values.length;
			
			sample = new Sample(sample_size);
			
			if(isInteger(values[0])){
				sample.add(str2int(values));
			}else{
				// otherwise the first two lines of the file gives us the header names and the class classifications
				headers_keep = values;
				
				// read next line (class names)
				line = br.readLine();
				values = line.split(",");
				classNames_keep = values;
			}

			// iterate for every other line
			while ((line = br.readLine()) != null && !error) {
				// use comma as separator
				values = line.split(",");
				error = !sample.add(str2int(values));
				error_line++;
			}
			
			if(!error){
				sample.setHeaders(headers_keep);
				sample.setClassNames(classNames_keep);
				
				// enable Classifier button
				btnLearn.setEnabled(true);

				// *********  everything went well so update sample info  ************
				empty_file_img.setVisible(false);
				loaded_sample_text.setVisible(true);
				load_file_info.setBorder(new LineBorder(new Color(0, 0, 0)));
				table_msg.setVisible(false);

				String info = "";
				info += "File name:\n   "+csv_file.getName()+"\n";
				info += "Sample size:\n   "+sample.length()+"\n";
				info += "Variables:\n   "+sample.getNumFactors()+"\n";
				info += "Number of classes:\n   "+sample.numberOfClasses();
				loaded_sample_text.setText(info);

				// expand data
				expand_tabbedPane();
				tabbedPane_locked = true;
				btnLockSidePanel.setIcon(new ImageIcon(getClass().getResource("/resources/open_lock.png")));
				tabbedPane.setSelectedIndex(0);

				// fill table with file data
				int num_vars = sample.getNumFactors() + 1;
				String[] columnHeaders = new String[num_vars + 1];
				Object[][] data = new Object[sample.length()][num_vars + 1];
				for(int i = 0; i < sample.length(); i++){
					data[i][0] = "Pac. " + (i + 1);
					int[] element = sample.element(i);
					for(int j = 1; j < num_vars + 1; j++){
						data[i][j] = element[j - 1];
					}
				}
				columnHeaders[0] = "Pacient ID"; 
				for(int i = 0; i < num_vars; i++){
					columnHeaders[i + 1] = sample.getHeaders()[i];
				}
				columnHeaders[num_vars] = "Class";
				table = new JTable(data, columnHeaders);
				table.setBounds(0, 0 , tabbedPane.getWidth(), tabbedPane.getHeight());
				
				table.getColumnModel().getColumn(0).setPreferredWidth(70);
				table.getColumnModel().getColumn(0).setHeaderRenderer(new RowHeaderRenderer());
				
				table.getColumnModel().getColumn(0).setCellRenderer(new RowHeaderRenderer());
				
				for(int i = 1; i <= sample.getNumFactors() + 1; i++){
					// table.getColumnModel().getColumn(i).setPreferredWidth(50);
					table.getColumnModel().getColumn(i).setHeaderRenderer(new RowHeaderRenderer());
				}
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				//table.setBounds(200, 0, full_width - short_width - 500, full_width - short_width);
				scrollDataTable.setViewportView(table);
				cardLayout.first(pane_DataTable);

				// send info to log:
				String msg = "New sample uploaded (" + csv_file.getName() + ")" + "\n" + "\t" + sample.length() + " data points.";
				sendToLogHistory(msg,Color.BLACK, false);
			}else{
				// send info to log:
				String msg = "Error while reading file!\n\tProblem at line " + error_line + ".";
				sendToLogHistory(msg,Color.RED, true);
			}
			

		} catch (FileNotFoundException er) {
			sendToLogHistory("File requested was not found!", Color.RED, true);
			er.printStackTrace();
		} catch (IOException er) {
			sendToLogHistory("IO Exception!", Color.RED, true);
			er.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException er) {
					sendToLogHistory("IO Exception!", Color.RED, true);
					er.printStackTrace();	
				}
			}
		}

	}

	public void save(String file_path){
		// save Classifier in path
		
		String full_path;
		if(file_path == ""){
			String path = save_path.getText();
			String full_name = save_name.getText() + txtclf.getText();
			full_path = path + "\\" + full_name;
			
			if(!new File(path).isDirectory()){
				sendToLogHistory("The given path does not exist or could not be found!", Color.RED, true);
				return;
			}
		}else{
			full_path = file_path;
		}
		
		FileOutputStream fos;
		try {
			File file = new File(full_path);
			fos = new FileOutputStream(file);
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(fos);
			oos.writeObject(bn);
			oos.close();

			sendToLogHistory("The Classifier has been saved successfully!", Color.BLACK, false);
			JOptionPane.showMessageDialog(null, "The Classifier has been saved successfully!", "Info", JOptionPane.PLAIN_MESSAGE, new ImageIcon(getClass().getResource("/resources/learner_icon.png")));
		} catch (FileNotFoundException er) {
			er.printStackTrace();
		} catch (IOException er) {
			er.printStackTrace();
		}

	}
	
	public void open_ViewGraph_window(boolean seeClass){

		JFrame frame = new JFrame("Graph");

		// set graph to see
		int num_nodes = bn.getGraph().getNumNodes();
		graph = new DirectedSparseGraph<String, String>();
		for(int i = 0; i < num_nodes; i++){
			if(i == num_nodes - 1){
				if(!seeClass)continue;
				graph.addVertex("Class");
			}else{
				graph.addVertex(sample.getHeaders()[i]);
			}
		}
		for(int i = 0; i < bn.getGraph().getEdges().size(); i++){
			Edge edge = bn.getGraph().getEdges().get(i);
			String node1 = (edge.n1 < num_nodes - 1) ? (sample.getHeaders()[edge.n1]) : "Class";
			String node2 = (edge.n2 < num_nodes - 1) ? (sample.getHeaders()[edge.n2]) : "Class";
			if(!seeClass)
				if(node1 == "Class" || node2 == "Class")continue;
			graph.addEdge(new String(new char[i]).replace("\0", " "), node1, node2);
		}

		
		int size = 650;
		Layout<String, String> layout = new CircleLayout<String, String>(graph);
		layout.setSize(new Dimension(size, size));
		bvs = new BasicVisualizationServer<String, String>(layout);
		bvs.setPreferredSize(new Dimension(size, size));
		bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
		bvs.setVisible(true);

		JCheckBox see_Class = new JCheckBox("See Class node");
		see_Class.setSelected(seeClass);
		see_Class.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				open_ViewGraph_window(see_Class.isSelected());
				frame.setVisible(false);
				frame.dispose();
			}
		});
		see_Class.setBounds(20, 0, 200, 50);
		// see_Class.setFocusable(false);
		see_Class.setEnabled(true);
		see_Class.setVisible(true);
		
		// frame.getContentPane().setFocusable(true);
		frame.setIconImage(new ImageIcon(getClass().getResource("/resources/graph.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(50, 50, size, size);
		JPanel graph_panel = new JPanel();
		graph_panel.add(bvs);
		frame.getContentPane().add(see_Class);
		frame.getContentPane().add(graph_panel);
		frame.pack();
		frame.setVisible(true);
	}

	public static boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    for (int i = 0; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	private void expand_tabbedPane(){
		setSize(new Dimension(full_width, height));
	}

	private void retract_tabbedPane(){
		setSize(new Dimension(short_width, height));
	}


	 // **************************************************** Class for table rows look & feel ****************************************************
	static class RowHeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		public RowHeaderRenderer() {
	        setHorizontalAlignment(JLabel.CENTER);
	    }
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        if (table != null) {
	            JTableHeader header = table.getTableHeader();

	            if (header != null) {
	                setForeground(header.getForeground());
	                setBackground(header.getBackground());
	                setFont(header.getFont());
	            }
	        }

	        if (isSelected) {
	            setFont(getFont().deriveFont(Font.BOLD));
	        }

	        setValue(value);
	        return this;
	    }
	}
	
	// **************************************************** Class for console control ****************************************************
	class NonEditableLineDocumentFilter extends DocumentFilter {
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException{
			if(string == null) {
				return;
			}else{
				replace(fb, offset, 0, string, attr);
			}
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException{
			replace(fb, offset, length, "", null);
		}

		private static final String PROMPT = "> ";
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException{
			Document doc = fb.getDocument();
			Element root = doc.getDefaultRootElement();
			int count = root.getElementCount();
			int index = root.getElementIndex(offset);
			Element cur = root.getElement(index);
			int promptPosition = cur.getStartOffset()+PROMPT.length();
			if(index==count-1 && offset-promptPosition>=0) {
				if(text.equals("\n")) {
					String cmd = doc.getText(promptPosition, offset-promptPosition);
					if(cmd.isEmpty()) {
						text = "\n"+PROMPT;
					}else{
						text = "\n\n";

						String[] value = console.parseQuery(cmd).split("[.]");

						if(value[0] == "-1"){
							// through error
							text += "Unknown command! For help type 'help'.";
							
						}else{
							// if input is a valid command:
							
							switch (console.getCodeMapInv()[Integer.parseInt(value[0])]) {
							case "cd":
								if(value.length == 1){
									text += "Current path:\n" + console.getCurrentPath();
								}else{
									if(value[1].equals("1")){
										text += "Current path:\n" + console.getCurrentPath();
									}else{
										text += "Could not find the specified path!";
									}
								}

								break;

							case "cd..":
								text += "Current path:\n" + console.getCurrentPath();
								break;

							case "dir":
								ArrayList<String> dir = new ArrayList<String>();
								int max = 0;
								for (final File fileEntry : new File(console.getCurrentPath()).listFiles()) {
									switch (value[1]) {
									case "0":
										// do nothing
										break;

									case "1":
										if(fileEntry.isDirectory())continue;
										break;

									case "2":
										if(fileEntry.isFile())continue;
										break;
									}

									if(fileEntry.getName().length() > max)
										max = fileEntry.getName().length();
									dir.add(fileEntry.getName());

								}
								// tab space = 8 characthers
								int shortest_space = (int) Math.ceil((max + 1) / 8.);
								dir.sort(null);
								int current_size = 0;

								switch (value[1]) {
								case "0":
									// do nothing
									text += "Directory of " + console.getCurrentPath() + ":\n\n";
									break;

								case "1":
									text += "Directory of " + console.getCurrentPath() + " (files only):\n\n";
									break;

								case "2":
									text += "Directory of " + console.getCurrentPath() + " (folders only):\n\n";
									break;
								}

								for (int i = 0; i < dir.size(); i++) {
									int tab_times = shortest_space - (dir.get(i).length() ) / 8;
									text += dir.get(i) + new String(new char[tab_times]).replace("\0", "\t");

									current_size += 8 * tab_times + dir.get(i).length();
									if(i < dir.size() - 1 && current_size + dir.get(i+1).length() > 80){
										text += "\n";
										current_size = 0;
									}
								}
								break;

							case "upload_sample":
								if(console.getFileName() == ""){
									text += "Must add a file name from the current directory!";
								}else{
									String path = console.getCurrentPath() + "\\" + console.getFileName();
									File f = new File(path);
									if(f.exists() && !f.isDirectory()){
										upload_sample(path);
										text += "Sample uploaded successfully!";
									}else{
										text += "File doesn't exist in the current directory!";
									}
									console.setFileName("");
								}
								

								break;

							case "view_graph":
								if(BN_generated){
									open_ViewGraph_window(true);
								}else{
									text += "No graph available.\nFirst upload a sample and run the learner!";
								}

								break;
								
							case "learn":
								learn();
								text += "The Classifier has been created successfully!";
								break;

							case "save":
								if(console.getFileName() == ""){
									text += "Must provide a file name!";
								}else{
									if(BN_generated){
										String path = console.getCurrentPath() + "\\" + console.getFileName();
										
										if(console.getFileName().split("[.]").length == 1){
											text += "File extension assumed as default (.clf)\n";
											path += ".clf";
											save(path);
											text += "Classifier saved successfully!";
										}else{
											save(path);
											text += "Classifier saved successfully!";
											
										}
									}else{
										text += "No Classifier available to save!\nCreate one first!";
									}
									console.setFileName("");
								}

								break;

							case "goto_pref_path":
								console.setCurrentPath(preferable_loading_path);
								text += "Current path:\n" + console.getCurrentPath();
								break;
								
							case "cls":
								Document console_document1 = console_text_area.getDocument();
								((AbstractDocument)console_document1).setDocumentFilter(null);
								console_text_area.setText("> ");
								((AbstractDocument)console_document1).setDocumentFilter(new NonEditableLineDocumentFilter());
								return;
								
							case "help":
								// help
								text += console.giveHelp();
							default:
								break;
							}
						}
						text += "\n\n" + PROMPT;
					}

				}
				fb.replace(offset, length, text, attrs);
			}
		}
	}
}
