package com.magicutils.potionitemutils;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PotionItemManager {

	public HashMap<String, PotionItems> createdItems = new HashMap<>();
	public HashMap<Player, PotionArmorPlayer> playerArmorMap = new HashMap<>();
	
	public void addPlayer(Player p) {
		playerArmorMap.put(p, new PotionArmorPlayer(p));
	}
	
	public PotionArmorPlayer getPlayer(Player p) {
		return playerArmorMap.get(p);
	}
	
	public boolean hasPlayer(Player p) {
		return playerArmorMap.get(p) != null;
	}
	
	public void removePlayer(Player p) {
		PotionArmorPlayer potionPlayer = playerArmorMap.get(p);
		if(potionPlayer != null) potionPlayer.removeAllEffects();
		playerArmorMap.remove(p);
	}
	
	public void reset() {
		createdItems = new HashMap<>();
	}
	
	public void addPotionItem(PotionItems item) {
		createdItems.put(item.getId(), item);
	}
	
	public void removePotionItem(ItemStack item) {
		PotionItems i = getPotionItem(item);
		if(i != null) createdItems.remove(i.getId());
	}
	
	public boolean hasPotionItem(ItemStack item) {
		return createdItems.get(PotionItems.getIDFromItem(item)) != null;
	}
	
	public PotionItems getPotionItem(ItemStack item) {
		return createdItems.get(PotionItems.getIDFromItem(item));
	}
	
	public boolean hasPotionItem(String id) {
		return createdItems.get(id) != null;
	}
	
	public PotionItems getPotionItem(String id) {
		return createdItems.get(id);
	}
	
}
