package com.magicutils.potionitemutils;

import org.bukkit.scheduler.BukkitRunnable;

import com.magicutils.main.MagicUtilsMain;

public class InventoryObserver extends BukkitRunnable{

//	private Set<Player> tagged = new HashSet<>();
	
	public InventoryObserver() {
	
	}
	
	@Override
	public void run() {
		MagicUtilsMain.plugin.getServer().getOnlinePlayers().stream().forEach(x -> {
			if(!MagicUtilsMain.pim.hasPlayer(x)) MagicUtilsMain.pim.addPlayer(x);
			MagicUtilsMain.pim.getPlayer(x).updatePlayer();
		});
	}
}
