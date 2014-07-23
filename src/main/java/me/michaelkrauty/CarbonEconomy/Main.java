package me.michaelkrauty.CarbonEconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

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
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		setupEconomy();
		config = new Config(this);
		sql = new SQL(config);
		getServer().getPluginManager().registerEvents(this, this);
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
		final UUID uuid = event.getPlayer().getUniqueId();
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			public void run() {
				sql.setBalance(uuid, economy.getBalance(getServer().getOfflinePlayer(uuid)));
			}
		});
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		final OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(event.getPlayer().getUniqueId());
		double ecoBalance = economy.getBalance(offlinePlayer);
		double sqlBalance = sql.getBalance(event.getPlayer().getUniqueId());
		if (ecoBalance != sqlBalance) {
			final double difference = sqlBalance - ecoBalance;
			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {
					economy.depositPlayer(offlinePlayer, difference);
				}
			});
		}
	}
}
