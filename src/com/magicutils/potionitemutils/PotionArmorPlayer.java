package com.magicutils.potionitemutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.magicutils.easygui.InventoryManager;
import com.magicutils.main.MagicUtilsMain;
import com.magicutils.potionitemutils.PotionItems.Slot;

import net.minecraft.server.v1_16_R3.PlayerInventory;

public class PotionArmorPlayer {
	
	private final Player player;
	
	private ItemStack[] inHand = new ItemStack[2];
	
	private ItemStack[] armorContent = null;
	public PotionArmorPlayer(Player p) {
		this.player = p;
	}
	
	public void updatePlayer() {
		ItemStack[] newInv = player.getInventory().getArmorContents();
		ItemStack newinHand = player.getInventory().getItemInMainHand();
		ItemStack newinOffHand = player.getInventory().getItemInOffHand();
		
		if(inHand[0] != null && !inHand[0].getType().equals(Material.AIR) &&
				MagicUtilsMain.pim.hasPotionItem(inHand[0]) &&
				MagicUtilsMain.pim.getPotionItem(inHand[0]).getSlot().equals(Slot.hand)) {
			MagicUtilsMain.pim.getPotionItem(inHand[0]).remove(this.player);
		}
		
		if(!newinHand.getType().equals(Material.AIR)) {
			if(MagicUtilsMain.pim.hasPotionItem(newinHand) &&
					MagicUtilsMain.pim.getPotionItem(newinHand).getSlot().equals(Slot.hand)) {
				MagicUtilsMain.pim.getPotionItem(newinHand).apply(this.player);
			}
		}
		
		if(inHand[1] != null && !inHand[1].getType().equals(Material.AIR) &&
				MagicUtilsMain.pim.hasPotionItem(inHand[1]) &&
				MagicUtilsMain.pim.getPotionItem(inHand[1]).getSlot().equals(Slot.hand)) {
			MagicUtilsMain.pim.getPotionItem(inHand[1]).remove(this.player);
		}
		
		if(!newinOffHand.getType().equals(Material.AIR)) {
			if(MagicUtilsMain.pim.hasPotionItem(newinOffHand) &&
					MagicUtilsMain.pim.getPotionItem(newinOffHand).getSlot().equals(Slot.hand)) {
				MagicUtilsMain.pim.getPotionItem(newinOffHand).apply(this.player);
			}
		}
		
		getAddedItems(newInv).forEach(x -> {
			if(MagicUtilsMain.pim.hasPotionItem(x)) {
				MagicUtilsMain.pim.getPotionItem(x).apply(this.player);
			}
		});
		
		getRemovetItems(newInv).forEach(x -> {
			if(MagicUtilsMain.pim.hasPotionItem(x)) {
				MagicUtilsMain.pim.getPotionItem(x).remove(this.player);
			}
		});
		
		this.armorContent = newInv;
		this.inHand = new ItemStack[2];
		this.inHand[0] = newinHand;
		this.inHand[1] = newinOffHand;
	}
	
	public void removeAllEffects() {
		if(armorContent == null) return;
		Arrays.stream(armorContent).forEach(x -> {
			if(MagicUtilsMain.pim.hasPotionItem(x)) {
				MagicUtilsMain.pim.getPotionItem(x).remove(this.player);
			}
		});
	}
	
	public void addOnJoin() {
		ItemStack[] newInv = player.getInventory().getArmorContents();
		ItemStack newinHand = player.getInventory().getItemInMainHand();
		ItemStack newinOffHand = player.getInventory().getItemInOffHand();
		
		Arrays.stream(player.getInventory().getArmorContents()).forEach(x -> {
			if(MagicUtilsMain.pim.hasPotionItem(x)) {
				MagicUtilsMain.pim.getPotionItem(x).apply(player);
			}
		});
		
		this.armorContent = newInv;
		this.inHand = new ItemStack[2];
		this.inHand[0] = newinHand;
		this.inHand[1] = newinOffHand;
	}
	
	public boolean hasChanges(ItemStack[] currentArmorContent, ItemStack mainHand, ItemStack offHand) {
		return !Arrays.equals(this.armorContent, currentArmorContent) || !mainHand.equals(this.inHand[0]) || !offHand.equals(this.inHand[1]);
	}
	
	public List<ItemStack> getAddedItems(ItemStack[] currentArmorContent){
		ArrayList<ItemStack> out = new ArrayList<>();
		for(ItemStack current : currentArmorContent) {
			if(current == null) continue;
			out.add(current);  
		}
		return out;
	}
	
	public List<ItemStack> getRemovetItems(ItemStack[] currentArmorContent){
		ArrayList<ItemStack> out = new ArrayList<>();
		if(armorContent == null) return out;
		for(ItemStack old : armorContent) {
			if(old == null) continue;
			boolean match = false;
			for(ItemStack current : currentArmorContent) {
				if(current == null) continue;
				if(current.equals(old)) match = true;
			}
			if(!match) out.add(old); 
		}
		return out;
	}

	public Player getPlayer() {
		return player;
	}
	
}
