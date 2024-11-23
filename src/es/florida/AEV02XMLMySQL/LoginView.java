package es.florida.AEV02XMLMySQL;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import es.florida.AEV02Model.User;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LoginView {

	private JFrame frame;
	private JTextField usernameField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView window = new LoginView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 489, 329);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		usernameField = new JTextField();
		usernameField.setBounds(240, 89, 86, 20);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		JLabel usernameLabel = new JLabel("Username");
		usernameLabel.setBounds(158, 92, 72, 14);
		frame.getContentPane().add(usernameLabel);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(158, 145, 72, 14);
		frame.getContentPane().add(passwordLabel);
		
		JLabel loginLabel = new JLabel("Login");
		loginLabel.setBounds(10, 11, 72, 14);
		frame.getContentPane().add(loginLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(240, 142, 86, 20);
		frame.getContentPane().add(passwordField);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				System.out.println("trying login");
				String username = usernameField.getText();
				char[] password = passwordField.getPassword();
				String loggedInUser = "";
				try {
					User user = new User(username, password);
					loggedInUser = user.login();
					
					if(loggedInUser.equals("false")) {
						JOptionPane.showMessageDialog(frame, "Invalid username or password");
					}else if(loggedInUser.equals("admin")){
						new AdminView(user.username, user.pw);
						frame.setVisible(false);
					}else {
						new UserView(user.username, user.pw);
						frame.setVisible(false);
					}
					
				} catch (NoSuchAlgorithmException e1) { 
					e1.printStackTrace(); 
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace(); 
				} catch (SQLException e1) {  
					e1.printStackTrace();
				}
				
				
				
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(158, 203, 168, 23);
		frame.getContentPane().add(btnNewButton);
	}
}
