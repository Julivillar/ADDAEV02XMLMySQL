package es.florida.AEV02Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.florida.AEV02Controller.XMLController;

public class User {

	public String username;
	public String pw;
	public String type;

	public User(String username, char[] pw) throws NoSuchAlgorithmException {
		this.username = username;
		this.pw = DBConnection.hashPw(pw);
	}


	public String login() throws ClassNotFoundException, SQLException {
		Connection con = DBConnection.loginDB(this.username, this.pw);
		if (con == null) {
			System.out.println("Invalid connection");
			return "false";
		}
		con.close();
		if (this.username.equals("admin")) {
			return "admin";
		} else {
			return "client";
		}
	}

	public static Connection adminLogin(String username, String pw) throws ClassNotFoundException, SQLException {
		Connection con = DBConnection.loginDB(username, pw);
		if (con == null) {
			return con;
		}

		return con;
	}

	public static Connection clientLogin(String username, String pw) throws ClassNotFoundException, SQLException {
		Connection con = DBConnection.loginDB(username, pw);
		if (con == null) {
			return con;
		}

		return con;
	}

	public static String handleNewUser(Connection con, String usernameField, char[] passwordField,
			char[] repeatPasswordField) throws NoSuchAlgorithmException {
		String hashedPassword = DBConnection.hashPw(passwordField);
		String hashedRepeatedPassword = DBConnection.hashPw(repeatPasswordField);

		if (!hashedPassword.equals(hashedRepeatedPassword)) {
			return "passwordKO";
		}
		try {

			PreparedStatement psInsert = con.prepareStatement(
					"INSERT INTO users (login, password, type) VALUES (?, ?, ?)");
			psInsert.setString(1, usernameField);
			psInsert.setString(2, hashedPassword);
			psInsert.setString(3, "client");
			psInsert.executeUpdate();

			String createUserSQL = String.format("CREATE USER '%s'@'localhost' IDENTIFIED BY ?", usernameField);
			PreparedStatement psCreate = con.prepareStatement(createUserSQL);
			psCreate.setString(1, hashedPassword);
			psCreate.executeUpdate();

			String grantSQL = String.format("GRANT SELECT ON population.population TO '%s'@'localhost';",
					usernameField);
			PreparedStatement psGrant = con.prepareStatement(grantSQL);
			psGrant.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return "error";
		}

		return "ok";
	}

	/**
	 * 
	 * @param selectedDBFile
	 * @return the string with all xml's
	 */
	public static List<String> importDatabase(File selectedDBFile, Connection con) {

		List<String> XMLContent = readCSVLines(selectedDBFile);
		XMLController.importXMLIntoDatabase(con);
		return XMLContent;

	}

	/**
	 * 
	 * @param selectedDBFile the csv file that will generate and import into DB
	 * @return the list of strings containing ALL the generated XML's
	 */
	private static List<String> readCSVLines(File selectedDBFile) {
		String[] tempArr = null;
		List<String> allXMLGenerated = new ArrayList<String>();

		try {
			FileReader fr = new FileReader(selectedDBFile);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			br.readLine();
			while ((line = br.readLine()) != null) {
				tempArr = line.split(";");

				String xmlGenerated = XMLController.generateXMLFromFile(tempArr);
				allXMLGenerated.add(xmlGenerated);

			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return allXMLGenerated;
	}

}
