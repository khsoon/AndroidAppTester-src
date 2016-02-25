package kr_ac_yonsei_mobilesw_UI;

import kr_ac_yonsei_mobilesw_shell.ExecuteShellCommand;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
//import java.awt.Point;
//import java.awt.Rectangle;

//import javax.swing.DebugGraphics;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import javax.swing.JScrollBar;
import javax.swing.UIManager;
//import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import kr_ac_yonsei_mobilesw_lcs.GroupingResult;

//import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//import kr_ac_yonsei_mobilesw_shell.ExecuteShellCommand;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;

//import java.awt.event.ItemListener;
//import java.awt.event.ItemEvent;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
import java.util.Vector;
//import java.util.concurrent.locks.ReentrantLock;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;

import java.io.File;
//import java.io.IOException;
//import java.util.logging.FileHandler;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import javax.swing.table.TableModel;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

//import javax.swing.JSplitPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JProgressBar;
import javax.swing.JCheckBox;

public class BenchResult extends JFrame {

	private JPanel contentPane;

	private JScrollPane scrollLTop;
	DefaultTableModel modelLTop;
	private JTable tableLTop;

	private JTextArea intentTestInfo;
	private JScrollPane infoTextScroll;

	private JScrollPane scrollRTop;
	private DefaultTableModel modelRTop;
	private JTable tableRTop;

	private JTextField adbText;

	private JScrollPane scrollRDown;
	private DefaultTableModel modelRDown;
	private JTable tableRDown;

	private JTextField txtAdbPath;

	private JTextField typeTextField;
	private JTextField sizeTextField;

	private JComboBox cboDeviceID;
	private JCheckBox chckbxGrouping;

	private JButton btnRun;
	private JButton btnPause;
	private JProgressBar progressBar;
	
	private static Benchmark benchmarkUI;
	private boolean isBusy = false;
	private JFileChooser fc = new JFileChooser();
	
	public List<String> apkFilePathList; // APK file List
	private static String adbPathString; // reserved ADB path // ADB path :
											// txtAdbPath.getText();
	private char iList[] = new char[100]; // is Intent Test complete
	private char gList[] = new char[100]; // is Grouping complete

	public ReadInfo temp; //
	public String adb; // read adb
	public Vector<Vector<String>> log; //
	private Object mutex;

	static {
		adbPathString = Config.getAdbPath();

		if (adbPathString == null) {
			String osName = System.getProperty("os.name");
			String osNameMatch = osName.toLowerCase();
			if (osNameMatch.contains("linux")) {
				adbPathString = "/home/";
			} else if (osNameMatch.contains("windows")) {
				adbPathString = "C:/users/";
			} else
				if (osNameMatch.contains("mac os") || osNameMatch.contains("macos") || osNameMatch.contains("darwin")) {
				adbPathString = "/Users/";
			} else {
				adbPathString = "C:/users/"; // Windows OS by default
			}
		}
	}

	public static void main(final String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BenchResult frame = new BenchResult(args);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public String[] d;

	public BenchResult(String[] args) {

		if (args.length >= 1 && "-EXIT_ON_CLOSE".equals(args[0])) {
			String[] new_args = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				new_args[i] = args[i + 1];
			}

			args = new_args;
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		d = args;

		// window size
		setBounds(100, 100, 1259, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		apkFilePathList = new ArrayList<String>();

		//////////////////////////////////////////////////////
		scrollLTop = new JScrollPane();
		scrollLTop.setBounds(31, 84, 303, 201);
		scrollLTop.getViewport().setBackground(Color.white);
		scrollLTop.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollLTop.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollLTop);

		scrollRTop = new JScrollPane();
		scrollRTop.setBounds(701, 84, 523, 201);
		scrollRTop.getViewport().setBackground(Color.white);
		scrollRTop.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollRTop.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollRTop);

		scrollRDown = new JScrollPane();
		scrollRDown.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollRDown.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollRDown.setBounds(242, 415, 989, 239);
		scrollRDown.getViewport().setBackground(Color.white);
		contentPane.add(scrollRDown);
//		//////////////////////////////////////////////////////
//		JButton callMain = new JButton("Intent Test");
//		callMain.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//
//				Benchmark frame = new Benchmark(d);
//				frame.setVisible(true);
//			}
//		});
//		callMain.setBounds(24, 6, 117, 29);
//		contentPane.add(callMain);
//		//////////////////////////////////////////////////////

		// ========================================================================
		// //
		// Create Component to be not changed //
		// ========================================================================
		// //
		JLabel lblApkList = new JLabel("APK List");
		lblApkList.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblApkList.setBounds(31, 47, 130, 25);
		contentPane.add(lblApkList);

		JLabel lblIntentTestInfo = new JLabel("Intent Test Info");
		lblIntentTestInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblIntentTestInfo.setBounds(370, 42, 255, 30);
		contentPane.add(lblIntentTestInfo);

		JLabel lblGroupInfo = new JLabel("Group Info");
		lblGroupInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		lblGroupInfo.setBounds(701, 42, 180, 30);
		contentPane.add(lblGroupInfo);

		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setBounds(31, 338, 130, 16);
		contentPane.add(lblSettings);

		JLabel lblAdbCommand = new JLabel("ADB command");
		lblAdbCommand.setBounds(242, 306, 108, 16);
		contentPane.add(lblAdbCommand);

		JLabel lblAndoirdLog = new JLabel("Andoird Log");
		lblAndoirdLog.setBounds(242, 387, 92, 16);
		contentPane.add(lblAndoirdLog);

		JLabel lblAdbPath = new JLabel("ADB path : ");
		lblAdbPath.setBounds(31, 371, 67, 15);
		contentPane.add(lblAdbPath);

		JLabel lblDeviceId = new JLabel("Device ID : ");
		lblDeviceId.setBounds(31, 433, 85, 15);
		contentPane.add(lblDeviceId);

		JLabel lblOption = new JLabel("Option:");
		lblOption.setBounds(31, 503, 61, 16);
		contentPane.add(lblOption);

		JLabel lblType = new JLabel("Type");
		lblType.setBounds(65, 531, 61, 16);
		contentPane.add(lblType);

		typeTextField = new JTextField();
		typeTextField.setBounds(133, 525, 34, 28);
		contentPane.add(typeTextField);
		typeTextField.setColumns(10);

		JLabel lblSize = new JLabel("size");
		lblSize.setBounds(65, 556, 61, 16);
		contentPane.add(lblSize);

		sizeTextField = new JTextField();
		sizeTextField.setBounds(133, 553, 34, 28);
		contentPane.add(sizeTextField);
		sizeTextField.setColumns(10);

		JLabel lblProgress = new JLabel("Progress");
		lblProgress.setBounds(33, 650, 108, 20);

		contentPane.add(lblProgress);
		
		
		// ========================================================================
		// //

		// ========================================================================
		// //
		// Create Component to be changed //
		// ========================================================================
		// //
		createAPKList();
		createIntentTestInfo();
		createGroupInfo();
		createAndroidLog();

		adbText = new JTextField();
		adbText.setBounds(242, 334, 989, 43);
		contentPane.add(adbText);

		progressBar = new JProgressBar();
		progressBar.setBounds(31, 671, 199, 20);
		
		
		contentPane.add(progressBar);
		// ========================================================================
		// //

		// ========================================================================
		// //
		// Create Component to be received //
		// ========================================================================
		// //
		JButton addBtn = new JButton("Add APK");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (apkFilePathList.size() != 0) {
					int noexpansionIndex = apkFilePathList.get(apkFilePathList.size() - 1).lastIndexOf('/');
					String apkpath = apkFilePathList.get(apkFilePathList.size() - 1).substring(0, noexpansionIndex);

					if (apkpath != null || "".equals(apkpath)) {
						fc.setCurrentDirectory(new File(apkpath));
					}
				}
				int returnVal = fc.showOpenDialog(BenchResult.this);

				File file;

				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}

				file = fc.getSelectedFile();
				boolean input = true;
				for (int i = 0; i < apkFilePathList.size(); i++) {
					if (apkFilePathList.get(i).equals(file.getAbsolutePath())) {
						input = false;
						break;
					}
				}
				if (input == true) {
					apkFilePathList.add(file.getAbsolutePath());
					apkTableUpdate(apkFilePathList);
				}
			}
		});
		addBtn.setBounds(31, 297, 117, 29);
		contentPane.add(addBtn);

		settings();
		// ========================================================================
		// //

	}

	public void createIntentTestInfo() {

		infoTextScroll = new JScrollPane();
		infoTextScroll.setBounds(371, 84, 303, 201);
		contentPane.add(infoTextScroll);
		intentTestInfo = new JTextArea();
		infoTextScroll.setViewportView(intentTestInfo);
		intentTestInfo.setFont(UIManager.getFont("TextField.font"));
	}

	public void createAPKList() {

		modelLTop = new DefaultTableModel();
		modelLTop.addColumn("Num");
		modelLTop.addColumn("G");
		modelLTop.addColumn("I");
		modelLTop.addColumn("APK File");

		tableLTop = new JTable(modelLTop) {
			private static final long serialVersionUID = 1L;
		};
		tableLTop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				// System.out.println(row + " , " + column);

				// System.out.println(apkFilePathList.get(row));

				temp = new ReadInfo();
				temp.start(apkFilePathList.get(row));
				Vector<String> fileInfo = temp.getResultInfo();
				String line = "";
				// System.out.println(fileInfo.size());
				for (int i = 0; i < fileInfo.size(); i++) {
					line += fileInfo.get(i) + "\n";

				}
				intentTestInfo.setText(line);

				modelRTop.getDataVector().removeAllElements();
				modelRTop.setColumnCount(0);
				createGroupInfo();
				int max = temp.getGroupInfo().get(0).size();
				for (int i = 1; i < temp.getGroupInfo().size(); i++) {
					if (max < temp.getGroupInfo().get(i).size())
						max = temp.getGroupInfo().get(i).size();
				}
				for (int i = 0; i < max - 2; i++)
					modelRTop.addColumn("sub" + i);
				for (int i = 0; i < temp.getGroupInfo().size(); i++) {
					Object[] tt = new Object[temp.getGroupInfo().get(i).size() + 1];
					tt[0] = i;
					for (int j = 1; j < temp.getGroupInfo().get(i).size() + 1; j++) {
						tt[j] = temp.getGroupInfo().get(i).get(j - 1);
					}
					modelRTop.addRow(tt);
				}

			}
		});
		tableLTop.setShowVerticalLines(false);
		tableLTop.setShowHorizontalLines(false);
		tableLTop.setSelectionBackground(new Color(222, 237, 255));
		tableLTop.setRowHeight(23);
		tableLTop.setFont(new Font("Courier New", Font.PLAIN, 12));
		tableLTop.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableLTop.getColumnModel().getColumn(0).setPreferredWidth(50);
		tableLTop.getColumnModel().getColumn(1).setPreferredWidth(50);
		tableLTop.getColumnModel().getColumn(2).setPreferredWidth(50);
		tableLTop.getColumnModel().getColumn(3).setPreferredWidth(150);
		scrollLTop.setViewportView(tableLTop);
	}

	public void createGroupInfo() {

		modelRTop = new DefaultTableModel();
		modelRTop.addColumn("Index");
		modelRTop.addColumn("Top");
		modelRTop.addColumn("Sub");

		tableRTop = new JTable(modelRTop) {
			private static final long serialVersionUID = 1L;
		};
		tableRTop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				//System.out.println(row + "," + column);
				String logIndex = temp.getGroupInfo().get(row).get(column - 1);

				// temp.start(apk);

				log = temp.getLog(Integer.parseInt(logIndex));
				adb = temp.getADB();

				adbText.setText(adb);
				adbText.setCaretPosition(0);
				// modelRDown.getDataVector().removeAllElements();
				// modelRDown.setColumnCount(0);
				//System.out.println("change");
				createAndroidLog();

				for (int i = 0; i < log.size(); i++) {

					Object[] tt = new Object[log.get(i).size() + 1];
					tt[0] = i;
					for (int j = 1; j < log.get(i).size() + 1; j++) {
						tt[j] = log.get(i).get(j - 1);
					}

					modelRDown.addRow(tt);
				}

			}
		});
		tableRTop.setFont(new Font("Courier New", Font.PLAIN, 12));
		tableRTop.setSelectionBackground(new Color(222, 237, 255));
		tableRTop.setShowHorizontalLines(false);
		tableRTop.setShowVerticalLines(false);
		tableRTop.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableRTop.getColumnModel().getColumn(0).setPreferredWidth(50);
		tableRTop.getColumnModel().getColumn(1).setPreferredWidth(50);
		tableRTop.getColumnModel().getColumn(2).setPreferredWidth(700);
		tableRTop.setRowHeight(23);
		scrollRTop.setViewportView(tableRTop);
	}

	public void createAndroidLog() {

		modelRDown = new DefaultTableModel();
		modelRDown.addColumn("Line");
		modelRDown.addColumn("Level");
		modelRDown.addColumn("Application");
		modelRDown.addColumn("Tag");
		modelRDown.addColumn("Text");

		tableRDown = new JTable(modelRDown) {
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				JComponent jc = (JComponent) comp;

				if (getModel().getValueAt(row, 0) != null) {
					if (getModel().getValueAt(row, 0).toString().equals("V")) {
						comp.setForeground(Color.black);
					} else if (getModel().getValueAt(row, 0).toString().equals("D")) {
						comp.setForeground(new Color(0, 0, 127));
					} else if (getModel().getValueAt(row, 0).toString().equals("I")) {
						comp.setForeground(new Color(0, 127, 0));
					} else if (getModel().getValueAt(row, 0).toString().equals("W")) {
						comp.setForeground(new Color(255, 127, 0));
					} else if (getModel().getValueAt(row, 0).toString().equals("E")) {
						comp.setForeground(new Color(255, 0, 0));
					} else {
						comp.setForeground(Color.black);
					}
				}
				return comp;
			}
		};
		// tableRDown.setShowHorizontalLines(false);
		// tableRDown.setShowVerticalLines(false);
		tableRDown.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableRDown.getColumnModel().getColumn(0).setPreferredWidth(30);
		tableRDown.getColumnModel().getColumn(1).setPreferredWidth(50);
		tableRDown.getColumnModel().getColumn(2).setPreferredWidth(100);
		tableRDown.getColumnModel().getColumn(3).setPreferredWidth(100);
		tableRDown.getColumnModel().getColumn(4).setPreferredWidth(1000);
		tableRDown.setRowHeight(23);
		tableRDown.setFont(new Font("Courier New", Font.PLAIN, 12));
		tableRDown.setSelectionBackground(new Color(222, 237, 255));
		scrollRDown.setViewportView(tableRDown);
	}

	public void settings() {
		JButton btnFind = new JButton("Find ADB");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String adbpath = txtAdbPath.getText();
				if (adbpath != null || "".equals(adbpath)) {
					fc.setCurrentDirectory(new File(adbpath));
				}

				int returnVal = fc.showOpenDialog(BenchResult.this);

				File file;

				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}

				file = fc.getSelectedFile();

				String fullname = file.getAbsolutePath();
				String filename = file.getName();
				String adbPath = fullname.substring(0, fullname.length() - filename.length());
				txtAdbPath.setText(adbPath);

				Config.putAdbPath(adbPath);
			}
		});
		btnFind.setBounds(133, 368, 97, 23);
		contentPane.add(btnFind);

		txtAdbPath = new JTextField();
		txtAdbPath.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				txtAdbPath.setText(txtAdbPath.getText().replaceAll("\\\\", "/"));
			}
		});
		txtAdbPath.setText(adbPathString);
		txtAdbPath.setBounds(31, 393, 199, 28);
		txtAdbPath.setColumns(10);
		contentPane.add(txtAdbPath);

		JButton btnReadDevice = new JButton("ReadDevice");
		btnReadDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnReadDevice.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {

				String command = "adb devices -l";

				if (getisBusy() == true) // Already processing
				{
					JOptionPane.showMessageDialog(null, "Now Processing...");
					return;
				}

				setisBusy(true);

				cboDeviceID.removeAllItems();

				if (txtAdbPath.getText().trim().equals("")) {
					command = adbPathString + command;
				} else {
					command = txtAdbPath.getText().trim() + command;
				}

				ExecuteShellCommand.readDevice(BenchResult.this, command);

				setisBusy(false);

			}
		});
		btnReadDevice.setBounds(131, 426, 99, 30);
		contentPane.add(btnReadDevice);

		cboDeviceID = new JComboBox();
		cboDeviceID.setBounds(31, 461, 199, 30);
		contentPane.add(cboDeviceID);

		chckbxGrouping = new JCheckBox("Grouping");
		chckbxGrouping.setBounds(65, 584, 128, 23);
		contentPane.add(chckbxGrouping);

		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				progressBar.setMinimum(0);
				progressBar.setMaximum(apkFilePathList.size());
				progressBar.setValue(0);
				for (int i = 0; i < apkFilePathList.size(); i++) {

					File apk_file = new File(apkFilePathList.get(i));
					int noexpansionIndex = apkFilePathList.get(i).lastIndexOf('.');
					String noexpansion = apkFilePathList.get(i).substring(0, noexpansionIndex);
					String pkg_name = noexpansion + ".pkg_name";
					File intent_spec_file = new File(noexpansion + ".is");
					int random_type = Integer.valueOf(typeTextField.getText());
					int num_of_intents = Integer.valueOf(sizeTextField.getText());
					File adb_cmd_file = new File(noexpansion + ".adbcmd");
					String adb_path = txtAdbPath.getText();
					int exceptPathIndex = noexpansion.lastIndexOf('/');
					String path = noexpansion.substring(0, exceptPathIndex + 1);
					String real_name = noexpansion.substring(exceptPathIndex + 1);

					// System.out.println("apk_file : " + apk_file);
					// System.out.println("pkg_name : " + pkg_name);
					// System.out.println("intent_spec_file : " +
					// intent_spec_file);
					// System.out.println("random_type : " + random_type);
					// System.out.println("num_of_intents : " + num_of_intents);
					// System.out.println("adb_cmd_file : " + adb_cmd_file);
					// System.out.println("adb_path : " + adb_path);
					// System.out.println("path+Log_+real_name : " + path +
					// "Log_" + real_name);

					if (iList[i] == 'X') {
						System.out.println("Start of " + i + " Intent Test");
						new BatchRun().execute(apk_file, // File 클래스, APK 파일 경로
															// 및 이름
								pkg_name, //
								intent_spec_file, // File 클래스, APK 파일에서 자동 생성한
													// 인텐트 스펙을 저장할 파일 경로 및 이름
													// <--- apk_file과 동일 경로에
													// 생성하되, 확장자를 .is
								random_type, // int, 랜덤 타입 0, 1, 2 (By default,
												// 0) <--- UI에서 사용자가 선택하도록
								num_of_intents, // int, 생성할 인텐트의 수 (>0) <---
												// UI에서 사용자가 선택하도록
								adb_cmd_file, // File 클래스, 생성한 인텐트에 대한 ADB 명령어들을
												// 만들어 이 파일에 저장 <--- apk_file과
												// 동일 경로에 생성하되, 확장자를 .abdcmd
								adb_path, // String, 안드로이드 adb 명령어 경로
								path + "Log_" + real_name // 경로와 .apk를 뺀 나머지 이름
						);
						System.out.println("End of " + i + " Intent Test");
					}

					if (chckbxGrouping.isSelected() && gList[i] == 'X') {
						sema s = new sema(1);
						try {
							s.acquire();
							
							System.out.println("Start of " + i + " Grouping");
							GroupingResult grouping = new GroupingResult();
							int index = apkFilePathList.get(i).lastIndexOf('/');
							File directory = new File(apkFilePathList.get(i).substring(0, index + 1));
							File[] files = directory.listFiles();
							for (int j = 0; j < files.length; j++) {
								String str = files[j].getName();
								if (str.contains("Log_" + real_name)) {
									grouping.start(path + str);
									break;
								}
							}
							System.out.println("End of " + i + " Grouping");
							
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						s.release();
					}
					progressBar.setValue(i+1);
					apkTableUpdate(apkFilePathList);
				}
			}
		});
		btnRun.setBounds(24, 619, 93, 29);
		contentPane.add(btnRun);

		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPause.setBounds(133, 619, 97, 29);
		contentPane.add(btnPause);

	}

	public void apkTableUpdate(List<String> apkFilePathList) {

		createAPKList(); // 그냥 modelLTop만 리셋시켜주면되지않나?

		List<String> fileList = apkFilePathList;

		for (int i = 0; i < 100; i++) {
			iList[i] = 'X';
			gList[i] = 'X';
		}

		for (int i = 0; i < fileList.size(); i++) {
			int index = fileList.get(i).lastIndexOf("/");
			String filePath = fileList.get(i).substring(0, index);
			int index2 = fileList.get(i).lastIndexOf(".");
			String filename = fileList.get(i).substring(index + 1, index2);
			filename = "Log_" + filename;

			File directory = new File(filePath);
			File[] files = directory.listFiles();
			for (int j = 0; j < files.length; j++) {
				String str = String.valueOf(files[j]);
				int index3 = str.lastIndexOf("/");
				String filenamef = str.substring(index3 + 1); // filenamef =
																// Log_modoomable1.9.37_~.xlsx
				// T목록찾기
				if (filenamef.contains(filename)) // filename =
													// "Log_modoomable1.9.37"
				{
					iList[i] = 'O';
				}
				// G목록찾기
				if (filenamef.contains("G" + filename)) {
					gList[i] = 'O';
				}
			}
		}

		for (int i = 0; i < apkFilePathList.size(); i++) {
			int index = fileList.get(i).lastIndexOf("/");
			String fileName = fileList.get(i).substring(index + 1);
			modelLTop.addRow(new Object[] { i, gList[i], iList[i], fileName });
		}

	}

	public void showDeviceList(String deviceText) {
		if (deviceText.contains("List of devices attached")) {
			return;
		}

		String DevicesID = deviceText.substring(0, deviceText.indexOf(" "));
		String model = deviceText.substring(deviceText.indexOf("model:") + 6,
				deviceText.indexOf(" ", deviceText.indexOf("model:") + 5));

		// logger.info("showDeviceList => DevicesID : " + DevicesID + ", model :
		// " + model + ", raw : " + deviceText);

		cboDeviceID.addItem(model + ":" + DevicesID);
	}

	public void setisBusy(boolean bool) {
		isBusy = bool;
	}

	public boolean getisBusy() {
		return isBusy;
	}

	public class sema extends Semaphore{

		public sema(int permits) {
			super(permits);
			// TODO Auto-generated constructor stub
		}
		
	}
}
