package com.magicutils.potionitemutils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemEffect {

	private final PotionEffectType type;
	private final int Level;
	
	public ItemEffect(PotionEffectType type, int lvl){
		this.type = type;
		this.Level = lvl;
	}

	public PotionEffectType getType() {
		return type;
	}

	public int getLevel() {
		return Level;
	}
	
	public void apply(Player p) {
		p.addPotionEffect(new PotionEffect(type, 20*20, Level, true, false, true));
	}
	
	public void remove(Player p){
		p.removePotionEffect(type);
	}
	
}
