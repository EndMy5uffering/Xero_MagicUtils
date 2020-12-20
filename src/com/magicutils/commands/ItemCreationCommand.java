package com.magicutils.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.magicutils.database.ItemDatabase;
import com.magicutils.easygui.InventoryManager;
import com.magicutils.enchants.EnchantWrapper;
import com.magicutils.guis.EnchantUI;
import com.magicutils.guis.ListGUI;
import com.magicutils.main.MagicUtilsMain;
import com.magicutils.potionitemutils.ItemEffect;
import com.magicutils.potionitemutils.PotionItems;
import com.magicutils.potionitemutils.PotionItems.Slot;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ItemCreationCommand implements TabExecutor{

	List<String> PotionEffects;
	
	public ItemCreationCommand(){
		this.PotionEffects = Lists.newArrayList(PotionEffectType.values()).stream().map(x -> {
			return x.getName();
			}).collect(Collectors.toList());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			MagicUtilsMain.plugin.getLogger().log(Level.INFO, "Command only usable from ingame");
			return true;
		}
		// /potionarmor apply <effect> <lvl>
		// /potionarmor openlist
		Player player = (Player) sender;
		
		if(!player.hasPermission("potionitems.edititems")) {
			player.sendMessage(ChatColor.RED + "You don't have permissin to use this command!");
			return true;
		}
		
		if(args.length <= 0) {
			player.sendMessage(ChatColor.RED + "More then 0 arguments are required!");
			return true;
		}
		
		ItemStack inHand = player.getInventory().getItemInMainHand();
		PotionItems potionItem;
		switch(args[0]) {
		case "create":
			if(!checkArgs(player, args, 2, "/magicutils create slot")) return true;

			if(inHand == null || inHand.getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
				return true;
			}

			PotionItems.Slot slot = Slot.valueOf(args[1]);
			
			if(slot == null) {
				player.sendMessage(ChatColor.RED + "Not a valid slot!");
				return true;
			}
			
			potionItem = new PotionItems(inHand, slot);
			
			MagicUtilsMain.pim.addPotionItem(potionItem);
			
			player.getInventory().setItemInMainHand(potionItem.getItem());
			player.sendMessage(ChatColor.GREEN + "HAVE FUN WITH YOUR NEW ITEM");
			return true;
		case "addeffect":
			if(!checkArgs(player, args, 3, "/magicutils addeffect <effect> <lvl>")) return true;
			
			if(!PotionItems.isValidEffect(args[1])) {
				player.sendMessage(ChatColor.RED + args[1] + " <- Is not a valid PotionEffectType!");
				return true;
			}
			
			if(!PotionItems.isValidEffectLvl(args[2])) {
				player.sendMessage(ChatColor.RED + args[2] + " <- Is not a valid level for the potion effect!");
				return true;
			}
			
			if(inHand == null || inHand.getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
				return true;
			}
			potionItem = MagicUtilsMain.pim.getPotionItem(inHand);
			
			if(potionItem == null) {
				player.sendMessage(ChatColor.RED + "The item you are currently trying to add an effect to is not yet a potion item!");
				player.sendMessage(ChatColor.RED + "Use: [/magicutils create] first");
				return true;
			}
			
			potionItem.addItemEffect(PotionItems.getItemEffectFromArgs(args[1], args[2]));

			player.sendMessage(ChatColor.GREEN + "Effect added to item!");
			return true;
		case "addenchant":
			
			EnchantUI ui = new EnchantUI(player);
			if(ui.OpenGUI()) InventoryManager.addGUI(ui);
			
			return true;
		case "set":
			if(!checkArgs(player, args, 3, "/magicutils set <name|description> [text]")) return true;
			
			if(inHand == null || inHand.getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
				return true;
			}
			
			potionItem = MagicUtilsMain.pim.getPotionItem(inHand);
			
			if(potionItem == null) {
				player.sendMessage(ChatColor.RED + "The item you are currently trying to add an effect to is not yet a potion item!");
				player.sendMessage(ChatColor.RED + "Use: [/magicutils create] first");
				return true;
			}
			
			
			
			switch(args[1].toLowerCase()) {
			case "name":
				String name = "";
				for(int i = 2; i < args.length; i++) {
					name += args[i] + " ";
				}
				name = name.substring(0, name.length()-1);
				
				if(name.length() >= 64) {
					player.sendMessage(ChatColor.RED + "Item name must not exceed 64 characters!");
					return true;
				}
				
				potionItem.setItemName(name);

				player.getInventory().setItemInMainHand(potionItem.getItem());
				player.sendMessage(ChatColor.GREEN + "Item name set.");
				return true;
			case "description":
				List<String> lore = new ArrayList<>();
				String lines = "";
				for(int i = 2; i < args.length; i++) {
					lines += args[i] + " ";
				}
				lines = lines.substring(0, lines.length()-1);
				for(String line : lines.split(";")) {
					lore.add(line);
				}
				if(lines.length() >= 128) {
					player.sendMessage(ChatColor.RED + "Item lore must not exceed 128 characters!");
					return true;
				}
				
				potionItem.setItemDescription(lore);
				player.getInventory().setItemInMainHand(potionItem.getItem());
				player.sendMessage(ChatColor.GREEN + "Item description set.");
				return true;
			default:
				player.sendMessage(ChatColor.RED + "No sutch command!");
				return true;
			}
		case "save":
			
			if(inHand == null || inHand.getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
				return true;
			}
			
			potionItem = MagicUtilsMain.pim.getPotionItem(inHand);
			
			if(potionItem == null) {
				player.sendMessage(ChatColor.RED + "The item you are currently trying to add an effect to is not yet a potion item!");
				player.sendMessage(ChatColor.RED + "Use: [/magicutils create] first");
				return true;
			}
			
			ItemDatabase.savePotionItem(potionItem);
			player.sendMessage(ChatColor.GREEN + "Item was saved.");
			return true;
			
		case "list":
			if(args.length == 1) {
				ListGUI gui = new ListGUI(player,0);
				if(gui.OpenGUI()) InventoryManager.addGUI(gui);
			}

			if(args.length == 2) {
				switch(args[1].toLowerCase()) {
				case "effects":
					if(inHand == null || inHand.getType().equals(Material.AIR)) {
						player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
						return true;
					}
					
					potionItem = MagicUtilsMain.pim.getPotionItem(inHand);
					
					if(potionItem == null) {
						player.sendMessage(ChatColor.RED + "The item you are currently trying to add an effect to is not yet a potion item!");
						player.sendMessage(ChatColor.RED + "Use: [/magicutils create] first");
						return true;
					}
					
					potionItem.getItemEffects().forEach(x -> {
						String preCommand = ChatColor.GOLD + "Effect: " + ChatColor.BLUE + x.getType().getName() + ChatColor.GOLD + " LVL: " + ChatColor.BLUE + x.getLevel() + " ";
						TextComponent message = new TextComponent(preCommand);
						TextComponent remove = new TextComponent("[REMOVE]");
						remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/magicutils remove effect " + x.getType().getName()));
						remove.setColor(ChatColor.RED);
						player.spigot().sendMessage(message, remove);
					});
					return true;
				case "enchants":
					
					ListGUI gui = new ListGUI(player,1);
					if(gui.OpenGUI()) InventoryManager.addGUI(gui);
					
					return true;
					default: 
						player.sendMessage("/magicutils list effects");
						return true;
				}
			}
			
			return true;
			
		case "remove":
			
			if(inHand == null || inHand.getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
				return true;
			}
			
			potionItem = MagicUtilsMain.pim.getPotionItem(inHand);
			
			if(potionItem == null) {
				player.sendMessage(ChatColor.RED + "The item you are currently trying to add an effect to is not yet a potion item!");
				player.sendMessage(ChatColor.RED + "Use: [/magicutils create] first");
				return true;
			}
			
			if(args.length == 1) {
				MagicUtilsMain.pim.removePotionItem(inHand);
				ItemDatabase.removePotionItem(potionItem);
				player.sendMessage(ChatColor.GREEN + "Item was removed.");
			}
			
			if(args.length == 3 && args[1].equalsIgnoreCase("effect")) {
				ItemEffect effect = null;
				for(ItemEffect ieff : potionItem.getItemEffects()) {
					if(ieff.getType().getName().equals(args[2])) effect = ieff;
				}
				
				if(effect != null) {
					potionItem.getItemEffects().remove(effect);
				}else {
					player.sendMessage(ChatColor.RED + "No Effect was found with the name: " + args[2]);
				}
			}
			
			return true;
			 
		case "update":
			
			if(inHand == null || inHand.getType().equals(Material.AIR)) {
				player.sendMessage(ChatColor.RED + "You have to have the item, you want to add a potion effect to, in your hand!");
				return true;
			}
			
			potionItem = MagicUtilsMain.pim.getPotionItem(inHand);
			
			if(potionItem == null) {
				player.sendMessage(ChatColor.RED + "The item you are currently trying to add an effect to is not yet a potion item!");
				player.sendMessage(ChatColor.RED + "Use: [/magicutils create] first");
				return true;
			}
			
			ItemDatabase.updatePotionItem(potionItem);
			player.sendMessage(ChatColor.GREEN + "Item was updated.");
			return true;
			
		case "sync":
			
			MagicUtilsMain.pim.reset();
			
			ItemDatabase.LoadItems();
			
			player.sendMessage("Data has been synced.");
			return true;
			
			default:
				player.sendMessage(ChatColor.RED + "No sutch command!");
				return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return null;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 1) {
			return Lists.newArrayList("create", "addeffect", "set", "list", "save", "remove", "update", "sync", "addenchant");
		}
		
		if(args.length == 2) {
			switch(args[0].toLowerCase()) {
			case "addenchant":
				return List.of(EnchantWrapper.STAFFWEAPON.getKey().toString());
			case "addeffect":
				return PotionEffects;
			case "set":
				return Lists.newArrayList("name", "description");
			case "create":
				return Lists.newArrayList("hand", "armor");
			case "list":
				return Lists.newArrayList("effects", "enchants");
			case "remove":
				return Lists.newArrayList("effect");
				default:
					return null;
			}
		}
		
		if(args.length == 3) {
			switch(args[0].toLowerCase()) {
			case "addeffect":
				return Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
			case "remove":
				return PotionEffects;
				default:
					return null;
			}
		}
		
		return null;
	}
	
	private boolean checkArgs(Player p, String[] args, int expected, String sampleCommand) {
		if(args.length < expected) {
			p.sendMessage(ChatColor.RED + "Missing Arguments!");
			if(sampleCommand != null)p.sendMessage(sampleCommand);
			return false;
		}
		return true;
	}
	
	private void textCommand(Player p, String msg, String command, ChatColor c) {
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		message.setColor(c);
		p.spigot().sendMessage(message);
	}
	
}
