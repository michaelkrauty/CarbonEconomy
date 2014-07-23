package me.michaelkrauty.CarbonEconomy;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created on 7/22/2014.
 *
 * @author michaelkrauty
 */
public class Config {

	private final Main main;

	File configFile;

	public Config(Main main) {
		this.main = main;
		configFile = new File(main.getDataFolder() + "/config.yml");
		checkFile();
		reload();
	}
	YamlConfiguration config = new YamlConfiguration();

	private void checkFile() {
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				InputStream input = main.getResource("config.yml");
				OutputStream output = new FileOutputStream(configFile);
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = input.read(buffer)) > 0) {
					output.write(buffer, 0, bytesRead);
				}
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void reload() {
		try {
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDBHost() {
		return config.getString("db_host");
	}

	public int getDBPort() {
		return config.getInt("db_port");
	}

	public String getDBDatabase() {
		return config.getString("db_database");
	}

	public String getDBUser() {
		return config.getString("db_user");
	}

	public String getDBPass() {
		return config.getString("db_pass");
	}

	public String getDBPrefix() {
		return config.getString("db_prefix");
	}

	public double getDefaultBalance() {
		return config.getDouble("default_balance");
	}
}
