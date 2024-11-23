package es.florida.AEV02XMLMySQL;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import es.florida.AEV02Model.DBConnection;
import es.florida.AEV02Model.User;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField querySearchField;

	public UserView(String username, String pw) throws ClassNotFoundException, SQLException {
		System.out.println("this is user view");
		Connection con = User.clientLogin(username, pw);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Search in population table");
		lblNewLabel.setBounds(42, 31, 214, 14);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(42, 90, 685, 270);
		contentPane.add(scrollPane);
		
		JTextArea resultTextArea = new JTextArea();
		scrollPane.setViewportView(resultTextArea);
		
		JButton searchBtn = new JButton("Search");
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBConnection.customSearch(con, querySearchField.getText(), username);
			}
		});
		searchBtn.setBounds(396, 56, 89, 23);
		contentPane.add(searchBtn);
		
		JButton logoutBtn = new JButton("Log out");
		logoutBtn.setBounds(638, 387, 89, 23);
		contentPane.add(logoutBtn);
		
		querySearchField = new JTextField();
		querySearchField.setBounds(42, 56, 322, 23);
		contentPane.add(querySearchField);
		querySearchField.setColumns(10);
		
		JButton exportQueryBtn = new JButton("Export results");
		exportQueryBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String resText = resultTextArea.getText();
				DBConnection.exportToCSV(con, resText);
			}
		});
		exportQueryBtn.setBounds(597, 56, 130, 23);
		contentPane.add(exportQueryBtn);
		
		setVisible(true);
	}
}
