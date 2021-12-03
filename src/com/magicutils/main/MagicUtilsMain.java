package com.magicutils.main;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.magicutils.commands.ItemCreationCommand;
import com.magicutils.database.ItemDatabase;
import com.magicutils.easygui.InventoryManager;
import com.magicutils.enchants.EnchantWrapper;
import com.magicutils.events.EnchantmentEvents;
import com.magicutils.packages.BulletHandler;
import com.magicutils.potionitemutils.InventoryObserver;
import com.magicutils.potionitemutils.PotionItemManager;

public class MagicUtilsMain extends JavaPlugin implements Listener{

	public static Plugin plugin;
	public static MagicUtilsMain main;
	
	public static PotionItemManager pim = new PotionItemManager();
	public InventoryObserver obs;
	public BulletHandler bh;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		main = this;
		EnchantWrapper.register();
		init();
		
		this.getServer().getPluginManager().registerEvents(new InventoryManager(), this);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new EnchantmentEvents(), this);
		this.getCommand("mutils").setExecutor(new ItemCreationCommand());
		ConfigManager.CreateConfigFiles();
		String MySQLurl = "jdbc:mysql:" + ConfigManager.getConfigFile().getString("url");
		try {
			DriverManager.getConnection(MySQLurl, ConfigManager.getConfigFile().getString("user"), ConfigManager.getConfigFile().getString("pass"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			disable();
		}
		ItemDatabase.createTables();
		ItemDatabase.LoadItems();
	}
	
	public void disable() {
		obs.cancel();
		bh.cancel();
		this.getServer().getPluginManager().disablePlugin(this);
	}
	
	public void init() {
		obs = new InventoryObserver();
		obs.runTaskTimer(this, 0, 20*5);
		bh = new BulletHandler();
		bh.runTaskTimer(this, 0, 1);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		pim.removePlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerExit(PlayerKickEvent e) {
		pim.removePlayer(e.getPlayer());
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!pim.hasPlayer(e.getPlayer())) pim.addPlayer(e.getPlayer());
		pim.getPlayer(e.getPlayer()).addOnJoin();
		pim.getPlayer(e.getPlayer()).updatePlayer();
	}
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(!pim.hasPlayer(p)) pim.addPlayer(p);
		pim.getPlayer(p).updatePlayer();
	}
}
