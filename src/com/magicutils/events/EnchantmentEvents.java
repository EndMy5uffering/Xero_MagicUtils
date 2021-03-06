package com.magicutils.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.magicutils.enchants.EnchantWrapper;
import com.magicutils.packages.Bullet;
import com.magicutils.packages.BulletHandler;
import com.magicutils.packages.ZatGunBullet;
import com.magicutils.potionitemutils.PotionItems;


public class EnchantmentEvents implements Listener {

	private static Map<Player, Long> PlayerFired = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			ItemStack i = e.getItem();
			
			if(i != null && i.hasItemMeta()) {
				if(canFire(e.getPlayer())) {
					Vector dir = e.getPlayer().getLocation().getDirection();
					Vector loc = e.getPlayer().getLocation().toVector().add(new Vector(0,e.getPlayer().getEyeHeight(),0)).add(dir.clone());
					
					if(i.getItemMeta().hasEnchant(EnchantWrapper.STAFFWEAPON)) {
						BulletHandler.add(new Bullet(Particle.FLAME, Particle.FLAME, 1, i.getItemMeta().getEnchantLevel(EnchantWrapper.STAFFWEAPON)+1, 30, dir, loc, e.getPlayer().getWorld(), e.getPlayer()));
						PlayerFired.put(e.getPlayer(), System.currentTimeMillis()+500);
						e.setCancelled(true);
					}else if(i.getItemMeta().hasEnchant(EnchantWrapper.ORI)) {
						BulletHandler.add(new Bullet(Particle.CRIT_MAGIC, Particle.CRIT_MAGIC, 1, i.getItemMeta().getEnchantLevel(EnchantWrapper.ORI)+2, 30, dir, loc, e.getPlayer().getWorld(), e.getPlayer()));
						PlayerFired.put(e.getPlayer(), System.currentTimeMillis()+1000);
						e.setCancelled(true);
					}else if(i.getItemMeta().hasEnchant(EnchantWrapper.ZATGUN)) {
						BulletHandler.add(new ZatGunBullet(Particle.FLAME, Particle.FLAME, 1, i.getItemMeta().getEnchantLevel(EnchantWrapper.ZATGUN)+1, 30, dir, loc, e.getPlayer().getWorld(), e.getPlayer()));
						PlayerFired.put(e.getPlayer(), System.currentTimeMillis());
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		ItemStack inHand = e.getItemInHand();
		if(inHand == null || inHand.getType().equals(Material.AIR)) return;
		String tag = PotionItems.getNbtTag(inHand, PotionItems.IDTag);
		if(tag != null && tag != "" && tag != "null") {
			e.setCancelled(true);
		}
	}
	
	private boolean canFire(Player p) {
		if(PlayerFired.get(p) == null) return true;
		return PlayerFired.get(p) - System.currentTimeMillis() < 0;
	}
	
}
