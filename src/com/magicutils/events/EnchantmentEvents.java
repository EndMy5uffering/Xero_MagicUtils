package com.magicutils.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.magicutils.enchants.EnchantWrapper;
import com.magicutils.packages.Bullet;
import com.magicutils.packages.BulletHandler;

public class EnchantmentEvents implements Listener {

	private static Map<Player, Long> PlayerFired = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			ItemStack i = e.getItem();
			
			if(i != null && i.hasItemMeta()) {
				if(canFire(e.getPlayer())) {
					Vector dir = e.getPlayer().getLocation().getDirection();
					Vector loc = e.getPlayer().getLocation().toVector().add(e.getPlayer().isSneaking() ? new Vector(0,1.2,0) : new Vector(0,1.6,0)).add(dir.clone());
					
					if(i.getItemMeta().hasEnchant(EnchantWrapper.STAFFWEAPON)) {
						BulletHandler.add(new Bullet(Particle.FLAME, Particle.SWEEP_ATTACK, 3, 500, 50, dir, loc, e.getPlayer().getWorld(), e.getPlayer()));
						PlayerFired.put(e.getPlayer(), System.currentTimeMillis());
						e.setCancelled(true);
						return;
					}else if(i.getItemMeta().hasEnchant(EnchantWrapper.ORI)) {
						BulletHandler.add(new Bullet(Particle.CRIT_MAGIC, Particle.CRIT_MAGIC, 3, 500, 50, dir, loc, e.getPlayer().getWorld(), e.getPlayer()));
						PlayerFired.put(e.getPlayer(), System.currentTimeMillis());
						e.setCancelled(true);
						return;
					}else if(i.getItemMeta().hasEnchant(EnchantWrapper.ZATGUN)) {
						BulletHandler.add(new Bullet(Particle.FLAME, Particle.FLAME, 3, 500, 50, dir, loc, e.getPlayer().getWorld(), e.getPlayer()));
						PlayerFired.put(e.getPlayer(), System.currentTimeMillis());
						e.setCancelled(true);
						return;
					}
					
				}
			}
			
		}
	}
	
	private boolean canFire(Player p) {
		if(PlayerFired.get(p) == null) return true;
		return Math.abs(PlayerFired.get(p) - System.currentTimeMillis()) > 500;
	}
	
}
