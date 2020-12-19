package com.magicutils.database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class SerializationPart implements Comparable<SerializationPart>{

	private int part = 0;
	private String s = "";
	private String  itemID;
	
	public SerializationPart(String s, int part, String itemID) {
		this.part = part;
		this.s = s;
		this.itemID = itemID;
	}
	
	public String getSQLQuerry() {
		return "INSERT INTO potionitemserialized(itemid,info,part) VALUES('"
				+ itemID + "','"
				+ s + "','"
				+ part + "');";
	}
	
	@Override
	public int compareTo(SerializationPart o) {
		return Integer.compare(part, o.part);
	}
	
	@Override
	public String toString() {
		return "Part: " + this.part + " info: " + this.s + " length: " + this.s.length();
	}
	
	public static List<SerializationPart> getListFromString(String serializationString, int partSize, String itemID){
		
		if(serializationString.length() > partSize) {
			int k = serializationString.length() / partSize;
			List<SerializationPart> list = new ArrayList<>();
			for(int i = 0; i < k; i++) {
				list.add(new SerializationPart(serializationString.substring(i*partSize, i*partSize + partSize), i, itemID));
			}
			list.add(new SerializationPart(serializationString.substring(k*partSize, serializationString.length()), k, itemID));
			return list;
		}else {
			return Lists.newArrayList(new SerializationPart(serializationString, 0, itemID));
		}
	}
	
	public static String getSerializationFromList(List<SerializationPart> in) {
		List<SerializationPart> ordered = in.stream().sorted().collect(Collectors.toList());
		String out = "";
		for(SerializationPart p : ordered) {
			out += p.s;
		}
		return out;
	}

	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	
}
