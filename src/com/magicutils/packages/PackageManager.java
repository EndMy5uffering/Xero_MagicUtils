package com.magicutils.packages;

import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;

public class PackageManager {
	
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
		((CraftPlayer)p).getHandle().b.sendPacket(packet);
	}
	
}
