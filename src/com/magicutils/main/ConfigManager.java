package com.magicutils.main;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.magicutils.database.DatabaseInfo;

public class ConfigManager {
	private static FileConfiguration pluginConfig;
	private static File pluginConfigFile;

	public static void CreateConfigFiles() {
		if (!MagicUtilsMain.plugin.getDataFolder().exists()) {
			MagicUtilsMain.plugin.getDataFolder().mkdir();
		}
		pluginConfigFile = new File(MagicUtilsMain.plugin.getDataFolder(), "config.yml");
		
		if (!pluginConfigFile.exists()) {
			try {
				MagicUtilsMain.plugin.saveResource("config.yml", false);
				pluginConfigFile = new File(MagicUtilsMain.plugin.getDataFolder(), "config.yml");
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		pluginConfig = YamlConfiguration.loadConfiguration(pluginConfigFile);
	}
	
	public static FileConfiguration getConfigFile() {
		return pluginConfig;
	}
	
	public static DatabaseInfo getConfigDatabaseInfo() {
		return new DatabaseInfo("jdbc:mysql:" + pluginConfig.getString("url"),
				pluginConfig.getString("user"),
				pluginConfig.getString("pass"));
	}
}
