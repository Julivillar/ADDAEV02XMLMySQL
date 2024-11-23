package es.florida.AEV02Model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection{
	
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
	
	public static Connection loginDB(String user, String pw){
			
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
	
	public static String customSearch(Connection con, String querySearch, String username) {
		
		return "";
	}
	
	public static void exportToCSV(Connection con, String res) {
		
	}
}
