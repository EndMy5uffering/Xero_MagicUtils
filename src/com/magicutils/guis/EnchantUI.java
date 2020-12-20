package com.magicutils.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.magicutils.easygui.GUIBase;

public class EnchantUI extends GUIBase{

	public EnchantUI(Player p) {
		super(p, 9, "<TEMP>", "EnchantUI");

		this.setItem(0, this.createItem("", null, Material.BLACK_STAINED_GLASS_PANE));
		this.setItem(2, this.createItem("", null, Material.BLACK_STAINED_GLASS_PANE));
		this.setItem(4, this.createItem("", null, Material.BLACK_STAINED_GLASS_PANE));
		this.setItem(5, this.createItem("", null, Material.BLACK_STAINED_GLASS_PANE));
		this.setItem(7, this.createItem("", null, Material.BLACK_STAINED_GLASS_PANE));
		this.setItem(8, this.createItem("", null, Material.BLACK_STAINED_GLASS_PANE));

		this.setItem(1, this.createItem("", null, Material.WHITE_STAINED_GLASS_PANE));
		this.setItem(3, this.createItem("", null, Material.WHITE_STAINED_GLASS_PANE));
		
		this.addNoCancleRange(6);
		
		this.setGeneralFunction(x -> {
			if(x.index == 1) {
				ItemStack item = x.event.getCursor().clone();
				if(item != null && !item.getType().equals(Material.AIR)) {
					this.setItem(1, item);
				}else if(item != null && item.getType().equals(Material.AIR)){
					this.setItem(1, this.createItem("", null, Material.WHITE_STAINED_GLASS_PANE));
				}
				this.refreshItems();
			}
			
			if(x.index == 3) {
				ItemStack item = x.event.getCursor().clone();
				if(item != null && !item.getType().equals(Material.AIR)) {
					this.setItem(3, item);
				}else if(item != null && item.getType().equals(Material.AIR)){
					this.setItem(3, this.createItem("", null, Material.WHITE_STAINED_GLASS_PANE));
				}
				this.refreshItems();
			}

			if((this.getItem(1) != null && this.getItem(3) != null) && this.getItem(3).getType().equals(Material.ENCHANTED_BOOK)) {
				ItemStack book = this.getItem(3);
				EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) book.getItemMeta();
				ItemStack i = this.getItem(1).clone();
				ItemMeta im = i.getItemMeta();
				
				bookmeta.getStoredEnchants().keySet().forEach(y -> {
					im.addEnchant(y, bookmeta.getStoredEnchants().get(y), true);
				});
				
				
				im.getEnchants().keySet().forEach(k -> {
					List<String> lore = im.getLore();
					List<String> BookLore = bookmeta.getLore();
					if(BookLore == null) return;
					if(lore == null) {
						lore = new ArrayList<>();
					}
					if(!lore.containsAll(BookLore)) {
						lore.addAll(BookLore);
					}
					im.setLore(lore);
				});
				
				i.setItemMeta(im);
				
				this.setItem(6, i);
				this.refreshItems();
			}

		});
		
	}

}
