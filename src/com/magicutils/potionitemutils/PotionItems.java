package com.magicutils.potionitemutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class PotionItems {
	
	public enum Slot{
		armor,
		hand
	}
	
	public static final String IDTag = "POTION_ITEM_TAG";
	private final ItemStack item;
	private final String id;
	private final Slot slot;
	private ArrayList<ItemEffect> itemEffects = new ArrayList<>();
	
	public PotionItems(ItemStack item, Slot slot) {
		this.id = UUID.randomUUID().toString();
		this.item = PotionItems.setNbtTag(item, IDTag, id);
		this.slot = slot;
	}
	
	public PotionItems(ItemStack item, Slot slot, String id) {
		this.item = PotionItems.setNbtTag(item, IDTag, id);
		this.id = id;
		this.slot = slot;
	}
	
	public void addItemEffect(ItemEffect effect) {
		itemEffects.add(effect);
	}
	
	public void removeItemEffect(ItemEffect effect) {
		itemEffects.remove(effect);
	}
	
	public static boolean isValidEffect(String effect) {
		try {
			return PotionEffectType.getByName(effect) != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isValidEffectLvl(String lvl) {
		try {
			return Integer.valueOf(lvl) >= 0;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String getDescriptionString(ItemStack item) {
		if(!item.hasItemMeta()) return "";
		if(item.getItemMeta().getLore() == null) return "";
		String out = "";
		for(String s : item.getItemMeta().getLore()) {
			out += s + ";";
		}
		return out;
	}
	
	public static List<String> getFormatToItemLore(String in) {
		List<String> out = new ArrayList<>();
		for(String s : in.split(";")) {
			out.add(s);
		}
		return out;
	}
	
	public void setItemName(String name) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setDisplayName(name);
		this.item.setItemMeta(meta);
	}
	
	public void setItemDescription(List<String> lore) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setLore(lore);
		this.item.setItemMeta(meta);
	}
	
	public void apply(Player p) {
		for(ItemEffect effect : this.itemEffects) {
			effect.apply(p);
		}
	}
	
	public void remove(Player p) {
		for(ItemEffect effect : this.itemEffects) {
			effect.remove(p);
		}
	}
	
	public static String getIDFromItem(ItemStack item) {
		return getNbtTag(item, PotionItems.IDTag);
	}
	
	public static String getNbtTag(ItemStack item, String tag) {
		net.minecraft.world.item.ItemStack nbtItem = org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack.asNMSCopy(item);
		net.minecraft.nbt.NBTTagCompound nbtcomp = nbtItem.getTag();
		return nbtcomp == null ? "" : nbtcomp.getString(tag);
	}
	
	public static String getSerializedString(ItemStack stack) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(stack);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			System.out.println(e);
		}
		return "";
	}
	
	public static ItemStack deserializeFromString(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack item = (ItemStack) dataInput.readObject();
			return item;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	public static boolean hasNbtTag(ItemStack item, String tag) {
		String out = getNbtTag(item, tag);
		return !out.equals("null") && !out.equals("");
	}
	
	public static ItemStack setNbtTag(ItemStack item, String tag, String value) {
		net.minecraft.world.item.ItemStack nbtItem = org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack.asNMSCopy(item);
		net.minecraft.nbt.NBTTagCompound nbtcomp = (nbtItem.hasTag()) ? nbtItem.getTag() : new net.minecraft.nbt.NBTTagCompound();
		nbtcomp.set(tag, net.minecraft.nbt.NBTTagString.a(value));
		nbtItem.setTag(nbtcomp);
		return org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack.asBukkitCopy(nbtItem);
	}
	
	public static ItemEffect getItemEffectFromArgs(String effect, String lvl) {
		PotionEffectType type = PotionEffectType.getByName(effect);
		int level = Integer.valueOf(lvl);
		return new ItemEffect(type, level);
	}
	
	public static ItemStack getItem(String name, List<String> lore, Material m) {
		lore = lore == null ? new ArrayList<String>() : lore;
		ItemStack out = new ItemStack(m);
		ItemMeta meta = out.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName(name);
		out.setItemMeta(meta);
		return out;
	}

	public ArrayList<ItemEffect> getItemEffects() {
		return itemEffects;
	}

	public void setItemEffects(ArrayList<ItemEffect> itemEffects) {
		this.itemEffects = itemEffects;
	}

	public ItemStack getItem() {
		return item;
	}

	public String getId() {
		return id;
	}

	public Slot getSlot() {
		return slot;
	}

}
