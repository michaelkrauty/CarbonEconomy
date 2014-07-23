package me.michaelkrauty.CarbonEconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

/**
 * Created on 7/22/2014.
 *
 * @author michaelkrauty
 */
public class SQL {

	private static String host;
	private static int port;
	private static String database;
	private static String user;
	private static String pass;
	private static String table;
	private static double defaultBalance;

	public SQL(Config config) {
		host = config.getDBHost();
		port = config.getDBPort();
		database = config.getDBDatabase();
		user = config.getDBUser();
		pass = config.getDBPass();
		table = config.getDBPrefix() + "CarbonEconomy";
		defaultBalance = config.getDefaultBalance();
		openConnection();
		checkTable();
	}

	private static Connection connection;

	private synchronized static void openConnection() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized static void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static boolean checkTable() {
		openConnection();
		boolean res = true;
		try {
			PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (player varchar(256) PRIMARY KEY, balance double);");
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			res = false;
		}
		return res;
	}

	public synchronized static void checkUser(UUID uuid) {
		openConnection();
		try {
			PreparedStatement stmt = connection
					.prepareStatement("SELECT * FROM `" + table + "` WHERE player=?;");
			stmt.setString(1, uuid.toString());
			ResultSet resultSet = stmt.executeQuery();
			if (!resultSet.next()) {
				stmt = connection.prepareStatement("INSERT INTO `" + table + "`(`player`, `balance`) VALUES (?,?);");
				stmt.setString(1, uuid.toString());
				stmt.setDouble(2, defaultBalance);
				stmt.execute();
			}

			stmt.close();
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static double getBalance(UUID uuid) {
		openConnection();
		try {
			checkUser(uuid);
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `" + table + "` WHERE player=?;");
				sql.setString(1, uuid.toString());
				ResultSet result = sql.executeQuery();
				result.next();
				return result.getDouble("balance");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public synchronized static void setBalance(UUID uuid, double balance) {
		openConnection();
		checkUser(uuid);
		try {
			PreparedStatement stmt = connection.prepareStatement("UPDATE `" + table + "` SET `balance`=? WHERE `player`=?;");
			stmt.setDouble(1, balance);
			stmt.setString(2, uuid.toString());
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}