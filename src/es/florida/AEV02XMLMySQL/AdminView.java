package es.florida.AEV02XMLMySQL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import es.florida.AEV02Model.DBConnection;
import es.florida.AEV02Model.User;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class AdminView extends JFrame {
	private JFrame frame;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField repeatPasswordField;
	private JTextField databaseTextField;
	private JTextField querySearchField;

	public AdminView(String username, String pw) throws ClassNotFoundException, SQLException {
		
		frame = new JFrame();
		Connection con = User.adminLogin(username, pw);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel adminViewLabel = new JLabel("Admin view");
		adminViewLabel.setBounds(58, 11, 76, 22);
		contentPane.add(adminViewLabel);
		
		usernameField = new JTextField();
		usernameField.setBounds(647, 45, 86, 20);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(647, 85, 86, 20);
		contentPane.add(passwordField);
		
		repeatPasswordField = new JPasswordField();
		repeatPasswordField.setBounds(647, 122, 86, 20);
		contentPane.add(repeatPasswordField);
		
		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(503, 48, 86, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(503, 88, 62, 14);
		contentPane.add(lblPassword);
		
		JLabel lblRepeatPassword = new JLabel("Repeat password");
		lblRepeatPassword.setBounds(503, 125, 106, 14);
		contentPane.add(lblRepeatPassword);
		
		JButton createUserBtn = new JButton("Create user");
		createUserBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		createUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String newUserResult = User.handleNewUser(con, usernameField.getText(), passwordField.getPassword(), repeatPasswordField.getPassword());
					if(newUserResult.equals("ok")) {
						JOptionPane.showMessageDialog(contentPane, "Client created succesfully");
					}else if(newUserResult.equals("passwordKO")){
						JOptionPane.showMessageDialog(contentPane, "Passwords don't match");
					}else {
						JOptionPane.showMessageDialog(contentPane, "Something went wrong");
					}
					
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
			}
		});
		createUserBtn.setBounds(503, 165, 230, 23);
		contentPane.add(createUserBtn);
		
		JButton logoutBtn = new JButton("Log out");
		logoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				new LoginView();
			}
		});
		logoutBtn.setBounds(685, 387, 89, 23);
		contentPane.add(logoutBtn);
		
		databaseTextField = new JTextField();
		databaseTextField.setColumns(10);
		databaseTextField.setBounds(200, 45, 151, 20);
		contentPane.add(databaseTextField);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(58, 164, 382, 234);
		contentPane.add(scrollPane);
		
		JTextArea resultTextArea = new JTextArea();
		resultTextArea.setEditable(false);
		scrollPane.setViewportView(resultTextArea);
		
		JButton importDatabaseBtn = new JButton("Import Database");
		importDatabaseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView());
				j.setCurrentDirectory(new File("."));
				j.setFileSelectionMode(JFileChooser.FILES_ONLY);
				j.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
				
				j.showOpenDialog(importDatabaseBtn);
				
				databaseTextField.setText(j.getSelectedFile().getName());
				List<String> fileIntoXML = User.importDatabase(j.getSelectedFile(), con);
				resultTextArea.setText(fileIntoXML.toString());
			}
		});
		importDatabaseBtn.setBounds(58, 44, 132, 23);
		contentPane.add(importDatabaseBtn);
		
		querySearchField = new JTextField();
		querySearchField.setColumns(10);
		querySearchField.setBounds(58, 119, 230, 20);
		contentPane.add(querySearchField);
		
		JLabel lblMakeAQuery = new JLabel("Search");
		lblMakeAQuery.setBounds(58, 95, 106, 22);
		contentPane.add(lblMakeAQuery);
		
		JButton exportQueryBtn = new JButton("Export results");
		exportQueryBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String resText = resultTextArea.getText();
				DBConnection.exportToCSV(con, resText);
			}
		});
		exportQueryBtn.setBounds(450, 375, 136, 23);
		contentPane.add(exportQueryBtn);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBConnection.customSearch(con, querySearchField.getText(), username);
				
			}
		});
		btnSearch.setBounds(304, 118, 136, 23);
		contentPane.add(btnSearch);
		
		JLabel lblCreateNewUser = new JLabel("Create new user");
		lblCreateNewUser.setBounds(503, 15, 151, 22);
		contentPane.add(lblCreateNewUser);
		
		
		setVisible(true);
	}
}
