package com.magicutils.enchants;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import com.magicutils.main.MagicUtilsMain;

public class EnchantWrapper extends Enchantment{

	private final String name;
	private final int maxlvl;
	private final Set<ItemStack> canEnchant;
	
	public static final Enchantment STAFFWEAPON = new EnchantWrapper("staff-weapon", 1);
	public static final Enchantment ZATGUN = new EnchantWrapper("zatniktel", 1);
	public static final Enchantment ORI = new EnchantWrapper("ori-weapon", 1);
	
	public EnchantWrapper(String name, int maxLvl) {
		super(new NamespacedKey(MagicUtilsMain.plugin, name));
		this.name = name;
		this.maxlvl = maxLvl;
		this.canEnchant = null;
	}
	
	public EnchantWrapper(String namespace, String name, int maxLvl, Set<ItemStack> canEnchant) {
		super(new NamespacedKey(MagicUtilsMain.plugin, name));
		this.name = name;
		this.maxlvl = maxLvl;
		this.canEnchant = canEnchant;
	}
	
	public static void register() {
		List<Enchantment> enchants = Arrays.stream(Enchantment.values()).collect(Collectors.toList());
		
		if(!enchants.contains(STAFFWEAPON))
			registerEnchantment(STAFFWEAPON);
		if(!enchants.contains(ZATGUN))
			registerEnchantment(ZATGUN);
		if(!enchants.contains(ORI))
			registerEnchantment(ORI);
		
	}
	
	public static void registerEnchantment(Enchantment e) {
		
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			Enchantment.registerEnchantment(e);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return (canEnchant != null ? canEnchant.contains(item) : true);
	}

	@Override
	public boolean conflictsWith(Enchantment enchant) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return this.maxlvl;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

}
