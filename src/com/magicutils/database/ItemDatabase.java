package com.magicutils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

import com.magicutils.main.ConfigManager;
import com.magicutils.main.MagicUtilsMain;
import com.magicutils.potionitemutils.ItemEffect;
import com.magicutils.potionitemutils.PotionItems;

public class ItemDatabase {

	private final static DatabaseInfo info = ConfigManager.getConfigDatabaseInfo();
	
	public static void createTables() {
		
		String sql = "CREATE TABLE IF NOT EXISTS potionitems ("
				+ " id VARCHAR(36) PRIMARY KEY,"
				+ " type VARCHAR(35),"
				+ " name VARCHAR(64),"
				+ " itemdesc VARCHAR(128),"
				+ " slot VARCHAR(10));";
		
		String effects = "CREATE TABLE IF NOT EXISTS potionitemeffects ("
				+ " id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
				+ " itemid VARCHAR(36),"
				+ " effect VARCHAR(36),"
				+ " lvl INTEGER);";
		
		String serialized = "CREATE TABLE IF NOT EXISTS potionitemserialized ("
				+ " id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
				+ " itemid VARCHAR(36),"
				+ " info VARCHAR(128),"
				+ " part INTEGER"
				+ ");";
		
		executeQuerry(sql);
		
		executeQuerry(effects);
		
		executeQuerry(serialized);
	}
	
	public static void savePotionItem(PotionItems item) {
		
		String out = "INSERT INTO potionitems(id,type,name,itemdesc,slot) VALUES('"
				+ item.getId() + "','"
				+ item.getItem().getType().toString() + "','"
				+ item.getItem().getItemMeta().getDisplayName() + "','"
				+ PotionItems.getDescriptionString(item.getItem()) + "','"
				+ item.getSlot().name() + "'"
				+ ");";
		
		for(ItemEffect e : item.getItemEffects()) {
			String effects = "INSERT INTO potionitemeffects(itemid, effect,lvl) VALUES('"
					+ item.getId() + "','"
					+ e.getType().getName() + "','"
					+ e.getLevel() + "'"
					+ ");";
		
			executeQuerry(effects);
		}
		
		SerializationPart.getListFromString(PotionItems.getSerializedString(item.getItem()), 128, item.getId()).forEach(x -> {
			executeQuerry(x.getSQLQuerry());
		});;
		
		executeQuerry(out);
	}
	
	public static void removePotionItem(PotionItems item) {
		String del = "DELETE FROM potionitems WHERE id='" + item.getId() + "';";
		String del2 = "DELETE FROM potionitemeffects WHERE itemid='" + item.getId() + "';";
		String del3 = "DELETE FROM potionitemserialized WHERE itemid='" + item.getId() + "';";
		

		executeQuerry(del);
		executeQuerry(del2);
		executeQuerry(del3);
	}
	
	public static void updatePotionItem(PotionItems item) {
		removePotionItem(item);
		savePotionItem(item);
	}
	
	public static void LoadItems() {
		
		try {
			ResultSet rs = DatabaseAccess.getData(info, "SELECT * FROM potionitems");
			
			while(rs.next()) {
				ResultSet serialization = DatabaseAccess.getData(info, "SELECT * FROM potionitemserialized WHERE itemid='" + rs.getString(1) + "';");
				
				ArrayList<SerializationPart> parts = new ArrayList<>();
				while(serialization.next()) {
					parts.add(new SerializationPart(serialization.getString(3), serialization.getInt(4), serialization.getString(2)));
				}
				
				ItemStack item = PotionItems.deserializeFromString(SerializationPart.getSerializationFromList(parts));
				MagicUtilsMain.pim.addPotionItem(new PotionItems(item, PotionItems.Slot.valueOf(rs.getString(5)), rs.getString(1)));
			
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			ResultSet rs = DatabaseAccess.getData(info, "SELECT * FROM potionitemeffects");
			
			while(rs.next()) {
				PotionItems item = MagicUtilsMain.pim.getPotionItem(rs.getString(2));
				if(item != null) item.addItemEffect(PotionItems.getItemEffectFromArgs(rs.getString(3), rs.getString(4)));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void executeQuerry(String q) {
		try {
			DatabaseAccess.executeQuerry(info, q);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
