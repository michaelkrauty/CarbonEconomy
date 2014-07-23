package me.michaelkrauty.CarbonEconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 7/22/2014.
 *
 * @author michaelkrauty
 */
public class Main extends JavaPlugin implements Listener {

	public static Economy economy = null;

	public static Config config;
	public static SQL sql;

	public void onEnable() {
		setupEconomy();
		config = new Config(this);
		sql = new SQL(config);
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
	}

	public void onDisable() {
		sql.closeConnection();
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		sql.setBalance(event.getPlayer().getUniqueId(), economy.getBalance(getServer().getOfflinePlayer(event.getPlayer().getUniqueId())));
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(event.getPlayer().getUniqueId());
		double ecoBalance = economy.getBalance(offlinePlayer);
		double sqlBalance = sql.getBalance(event.getPlayer().getUniqueId());
		if (ecoBalance != sqlBalance) {
			double difference = sqlBalance - ecoBalance;
			economy.depositPlayer(offlinePlayer, difference);
		}
	}
}
