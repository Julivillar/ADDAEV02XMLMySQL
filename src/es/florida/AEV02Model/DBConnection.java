package es.florida.AEV02Model;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class DBConnection {

	private static String[] currentQueryHeader;
	private static String[][] currentQueryRows;

	public static String hashPw(char[] pw) throws NoSuchAlgorithmException {

		byte[] pwBytes = String.valueOf(pw).getBytes(StandardCharsets.UTF_8);
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] bytesToHash = messageDigest.digest(pwBytes);

		return parseByteToString(bytesToHash);
	}

	private static String parseByteToString(byte[] bytesToHash) {
		String result = "";
		// Format each byte as Hexadecimal integer with a minimum of 2 digits.
		// If needed to achieve 2 digits, precede it with 0
		for (byte b : bytesToHash) {
			result += String.format("%02x", b);
		}
		return result;
	}

	public static Connection loginDB(String user, String pw) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		Connection con;
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/population", user, pw);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.getMessage();
			return null;
		}
		return con;
	}

	public static String[][] customSearch(Connection con, String searchQuery) throws SQLException {
		String formattedSearchQuery;
		if (searchQuery.toLowerCase().equals("ALL") || searchQuery.equals("*")) {
			formattedSearchQuery = "SELECT * FROM population;";
		} else {
			formattedSearchQuery = searchQuery;
		}
		Statement searchQueryStatement = con.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet result = searchQueryStatement.executeQuery(formattedSearchQuery);
		ResultSetMetaData metadata = result.getMetaData();
		int numberOfColumns = metadata.getColumnCount();

		String[][] queryRows = getColumnNames(result, metadata);

		int currentRow = 1;

		while (result.next()) {
			for (int i = 0; i < numberOfColumns; i++) {
				queryRows[currentRow][i] = result.getString(i + 1);
			}
			currentRow++;
		}
		searchQueryStatement.close();

		currentQueryHeader = queryRows[0];
		currentQueryRows = Arrays.copyOfRange(queryRows, 1, queryRows.length);

		return queryRows;
	}

	private static String[][] getColumnNames(ResultSet result, ResultSetMetaData metadata) throws SQLException {

		String[][] columnNames = new String[getNumberOfRows(result) + 1][metadata.getColumnCount()];

		for (int i = 0; i < metadata.getColumnCount(); i++) {
			columnNames[0][i] = metadata.getColumnName(i + 1);
		}

		return columnNames;
	}

	private static int getNumberOfRows(ResultSet result) throws SQLException {

		int rows = 0;

		result.last();

		rows = result.getRow();

		result.beforeFirst();

		return rows;
	}

	public static void exportToCSV(String[][] queryRows, String[] queryHeader) {

		try {
			FileWriter fw = new FileWriter("customQuery.csv");
			String lineHeader = "";
			for (int i = 0; i < queryHeader.length; i++) {
				if (i < queryHeader.length - 1) {
					lineHeader += queryHeader[i]+";";
				}else{
					lineHeader += queryHeader[i];
				}
			}
			fw.write(lineHeader+ "\n");

			for (int i = 0; i < queryRows.length; i++) {
				for (int j = 0; j < queryRows[i].length; j++) {
					fw.write(queryRows[i][j].trim());
					if (j < queryRows[i].length - 1) {
						fw.write(";");
					}
				}
				if (i < queryRows.length - 1) {
					fw.write("\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[][] getCurrentQueryRows() {
		return currentQueryRows;
	}

	public static String[] getCurrentQueryHeader() {
		return currentQueryHeader;
	}
}
