package com.magicutils.packages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.Vector3f;
import net.minecraft.server.v1_16_R3.World;

import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class PackageManager {

	public static EntityArmorStand CreateArmorStand(Location loc, boolean small,float rx, float ry, float rz, float rb) {
		World s = ((CraftWorld)loc.getWorld()).getHandle();
		EntityArmorStand stand = new EntityArmorStand(EntityTypes.ARMOR_STAND ,s);
        
        stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), rb, 0);
        stand.setCustomNameVisible(false);
        stand.setNoGravity(true);
        stand.setHeadPose(new Vector3f(rx, ry, rz));
        stand.setInvisible(true);
        stand.setBasePlate(false);
        stand.setSmall(small);
        return stand;
	}
	
	public static void SendSpawnPackage(EntityArmorStand stand, Player p, Material m) {
		PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(stand);
//        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(m)));
		PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), Lists.newArrayList(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(m)))));

		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(spawn);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(equip);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(metadata);
	}
	
	public static void SendDespawnPackage(int id, Player p) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(id);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(destroy);
	}
	
	public static void SendBlockChangePackage(Block block, Material m, Player p) {
		IBlockData blockdata = blockData(m);
		PacketPlayOutBlockChange changeEvent = new PacketPlayOutBlockChange(blockPosition(block), blockdata);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(changeEvent);
	}
	
	private static IBlockData blockData(Material m) {
	    net.minecraft.server.v1_16_R3.Block nmsBlock = CraftMagicNumbers.getBlock(m);
	    return nmsBlock.getBlockData();
	}
//	
	private static BlockPosition blockPosition(Block b) {
	    BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
	    return bp;
	}
	
	public static void SendParticalPackage(Player p, Particle partical, Vector pos, Vector box,float particalVel, int extraParticalCount) {
		/*(ParticleParam Partical,
		 *  boolean var1,
		 *  double LocX,
		 *  double LocY,
		 *  double LocZ,
		 *  float SpawnInBoxX,
		 *  float SpawnInBoxY,
		 *  float SpawnInBoxZ,
		 *  float particalVelocity,
		 *  int extraParticalCount)*/
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(CraftParticle.toNMS(partical), true, pos.getX(),  pos.getY(),  pos.getZ(), (float)box.getX(), (float)box.getY(), (float)box.getZ(), particalVel, extraParticalCount);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void SendUpdate(EntityArmorStand stand, Player p, Material m) {
//        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(m)));
		PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), Lists.newArrayList(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(m)))));

		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);

        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(equip);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(metadata);
	}
	
	public static void SendTeleport(EntityArmorStand stand, Player p) {
		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
        PacketPlayOutEntityTeleport telport = new PacketPlayOutEntityTeleport(stand);
        
        
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(metadata);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(telport);
	}
	
}
