package com.magicutils.guis;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.magicutils.easygui.GUIPages;
import com.magicutils.enchants.EnchantWrapper;
import com.magicutils.main.MagicUtilsMain;
import com.magicutils.potionitemutils.PotionItemManager;

public class ListGUI extends GUIPages{

	public ListGUI(Player p, int mode) {
		super(p, 9*6, ChatColor.GOLD + "<TEMP>", "ListGUI");
		setup(mode);
	}

	public void setup(int mode) {
		if(mode == 0) {
			List<ItemStack> items = MagicUtilsMain.pim.createdItems.values().stream().map(x -> {
				return x.getItem();
				}).sorted((x, y) -> {
					String name1, name2;
					if(x.hasItemMeta()) {
						name1 = x.getItemMeta().getDisplayName();
					}else {
						name1 = x.getType().toString();
					}
					
					if(y.hasItemMeta()) {
						name2 = y.getItemMeta().getDisplayName();
					}else {
						name2 = y.getType().toString();
					}
					return name1.compareTo(name2);
				}).collect(Collectors.toList());
			this.setSorceList(items);
		}else if(mode == 1) {
			ItemStack staffEnchant = new ItemStack(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta staffMeta = (EnchantmentStorageMeta) staffEnchant.getItemMeta();
			staffMeta.addStoredEnchant(EnchantWrapper.STAFFWEAPON, 1, true);
			staffMeta.setDisplayName("Staff-Weapon");
			staffMeta.setLore(List.of(ChatColor.WHITE+"Staff-Weapon"));
			staffEnchant.setItemMeta(staffMeta);
			List<ItemStack> items = List.of(staffEnchant);
			this.setSorceList(items);
		}
		
		this.setGeneralFunction(x -> {
			if(x.item != null && x.item.getType().equals(Material.BLACK_STAINED_GLASS_PANE)) return;
			if(x.index >= 0 && x.index < 45) {
				
				if(x.player.getInventory().firstEmpty() > -1) {
					x.player.getInventory().addItem(x.item);
				}
				
			}
		});
	}
	
}
