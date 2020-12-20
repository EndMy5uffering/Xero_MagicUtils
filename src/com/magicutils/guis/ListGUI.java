package com.magicutils.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.google.common.collect.Lists;
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
			String[] roman = {""," I", " II", " III", " IV", " V", " VI", " VII", " VIII", " IX", " X"};
			List<ItemStack> items = new ArrayList<ItemStack>();
			for(int i = 0; i < 11; i++) {
				items.add(getBook("Staff-Weapon", Lists.newArrayList("Staff-Weapon" + (i <= 11 ? roman[i] : i)), EnchantWrapper.STAFFWEAPON, i));
			}
			for(int i = 0; i < 11; i++) {
				items.add(getBook("Ori-Staff-Weapon", Lists.newArrayList("Ori-Staff-Weapon" + (i <= 11 ? roman[i] : i)), EnchantWrapper.ORI, i));
			}
			for(int i = 0; i < 11; i++) {
				items.add(getBook("Zat", Lists.newArrayList("Zat" + (i <= 11 ? roman[i] : i)), EnchantWrapper.ZATGUN, i));
			}
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
	
	public ItemStack getBook(String name, List<String> lore, Enchantment e, int EnchantLvl) {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
		bookMeta.addStoredEnchant(e, EnchantLvl, true);
		bookMeta.setDisplayName(name);
		bookMeta.setLore(lore);
		book.setItemMeta(bookMeta);
		return book;
	}
	
}
