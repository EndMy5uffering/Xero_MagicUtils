package com.magicutils.packages;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class Bullet {

	private Vector dir, loc, prefLoc;
	private World w;
	private boolean alive = true;
	private double damage;
	private Entity firedBy;
	private int lifeTime;
	private Particle particle, impactParticle;
	private static final double BlockRadius = Math.sqrt(2.0)/2.0;
	
	public Bullet(Particle particle, Particle impectParticle, double speed, double damage, Vector dir, Vector loc, World w, Entity firedBy) {
		this.dir = dir;
		this.loc = loc.clone();
		this.prefLoc = loc.clone();
		this.w = w;
		this.damage = damage;
		this.firedBy = firedBy;
		this.lifeTime = 50;
		this.particle = particle;
		this.impactParticle = impectParticle;
		
		this.dir = this.dir.normalize().multiply(speed);
	}
	
	public void updateLocation() {
		if(!alive) return;
		if(lifeTime <= 0) {
			this.alive = false;
			this.impect(loc);
			return;
		}
		this.lifeTime--;
		this.prefLoc = loc.clone();
		this.loc = this.loc.add(dir);
		Block b = IntersectedBlock();
		if(b != null) {
			this.alive = false;
			this.impect(getImpactOnEntity(b.getBoundingBox(), prefLoc.getX(), prefLoc.getY(), prefLoc.getZ(), dir.getX(), dir.getY(), dir.getZ()));
//			w.createExplosion(new Location(w, loc.getX(), loc.getY(), loc.getZ()), 100);
			return;
		}
		
		w.getEntities().forEach(x -> {
			if(!isAlive()) return;
			if(!(x instanceof LivingEntity)) return;
			if(x.equals(firedBy)) return;
			if(slabAABB(x.getBoundingBox())) {
				this.alive = false;
				LivingEntity e = (LivingEntity) x;
//				e.setHealth((e.getHealth() - damage < 0 ? 0 : e.getHealth() - damage));
				e.damage(damage, firedBy);
//				Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(firedBy, e, DamageCause.ENTITY_ATTACK, this.damage));
				this.impect(getImpactOnEntity(e.getBoundingBox(), prefLoc.getX(), prefLoc.getY(), prefLoc.getZ(), dir.getX(), dir.getY(), dir.getZ()));
//				w.createExplosion(new Location(w, loc.getX(), loc.getY(), loc.getZ()), 10);
				return;
			}
		});
		
		w.getPlayers().forEach(x -> {
			if(x.getLocation().toVector().subtract(loc).length() <= 100 && alive) {
				PackageManager.SendParticalPackage(x, particle, loc, new Vector(0.05,0.05,0.05), 0.0f,3);
				PackageManager.SendParticalPackage(x, particle, loc.clone().add(dir.clone().multiply(-0.5)), new Vector(0,0,0), 0f,0);
			}
		});
		
	}
	
	private boolean checkIntersectWithEntity(Entity e, double x1, double y1, double z1, double vx, double vy, double vz) {
		double ePosX = e.getBoundingBox().getCenterX();
		double ePosY = e.getBoundingBox().getCenterY();
		double ePosZ = e.getBoundingBox().getCenterZ();
		double a = dot(ePosX-x1, ePosY-y1, ePosZ-z1, vx, vy, vz)/dot(vx, vy, vz, vx, vy, vz);
		
		double x = a * vx + x1;
		double y = a * vy + y1;
		double z = a * vz + z1;
		
		
		return e.getBoundingBox().contains(x, y, z) && a <= 1 && a >= -1;
	}
	
	private boolean slabAABB(BoundingBox b) {
		double tx1 = (b.getMinX() - this.prefLoc.getX())/dir.getX();
	    double tx2 = (b.getMaxX() - this.prefLoc.getX())/dir.getX();
	    double ty1 = (b.getMinY() - this.prefLoc.getY())/dir.getY();
	    double ty2 = (b.getMaxY() - this.prefLoc.getY())/dir.getY();
	    double tz1 = (b.getMinZ() - this.prefLoc.getZ())/dir.getZ();
	    double tz2 = (b.getMaxZ() - this.prefLoc.getZ())/dir.getZ();

	    double tmin = Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
	    double tmax = Math.min(Math.min(Math.max(tx1, tx2), Math.max(ty1, ty2)), Math.max(tz1, tz2));
	    
	    return tmax >= tmin && tmin < 1 && tmax < 1 && tmin > 0 && tmax > 0;
	}
	
	private Vector getImpactOnEntity(BoundingBox b, double x1, double y1, double z1, double vx, double vy, double vz) {
		double ePosX = b.getCenterX();
		double ePosY = b.getCenterY();
		double ePosZ = b.getCenterZ();
		double a = dot(ePosX-x1, ePosY-y1, ePosZ-z1, vx, vy, vz)/dot(vx, vy, vz, vx, vy, vz);
		
		double x = a * vx + x1;
		double y = a * vy + y1;
		double z = a * vz + z1;
		return new Vector(x, y, z);
	}
	
	private Block IntersectedBlock() {
		
		ArrayList<Block> possibleIntersect = new ArrayList<>();
		double l = dir.length();
		double a = 0;
		double x = 0;
		double y = 0;
		double z = 0;
		
		for(double i = 0; i <= l; i += 1/l) {
			x = (i * dir.getX() + this.prefLoc.getX());
			y = (i * dir.getY() + this.prefLoc.getY());
			z = (i * dir.getZ() + this.prefLoc.getZ());

			possibleIntersect.add(w.getBlockAt((int)x, (int)y, (int)z));
			possibleIntersect.add(w.getBlockAt((int)x+1, (int)y, (int)z));
			possibleIntersect.add(w.getBlockAt((int)x, (int)y+1, (int)z));
			possibleIntersect.add(w.getBlockAt((int)x, (int)y, (int)z+1));
			possibleIntersect.add(w.getBlockAt((int)x-1, (int)y, (int)z));
			possibleIntersect.add(w.getBlockAt((int)x, (int)y-1, (int)z));
			possibleIntersect.add(w.getBlockAt((int)x, (int)y, (int)z-1));
			
		}
		
		for(Block b : possibleIntersect) {
//			double ePosX = b.getBoundingBox().getCenterX();
//			double ePosY = b.getBoundingBox().getCenterY();
//			double ePosZ = b.getBoundingBox().getCenterZ();
//			a = dot(ePosX-this.prefLoc.getX(), ePosY-this.prefLoc.getY(), ePosZ-this.prefLoc.getZ(), dir.getX(), dir.getY(), dir.getZ())/dot(dir.getX(), dir.getY(), dir.getZ(), dir.getX(), dir.getY(), dir.getZ());
//			
//			x = a * this.dir.getX() + this.prefLoc.getX();
//			y = a * this.dir.getY() + this.prefLoc.getY();
//			z = a * this.dir.getZ() + this.prefLoc.getZ();
//			
////			PackageManager.SendBlockChangePackage(b, Material.GOLD_BLOCK, (Player) this.firedBy);
//			if(!b.getType().equals(Material.AIR) && b.getBoundingBox().contains(x, y, z) && a <= 1 && a >= 0) {
////				PackageManager.SendBlockChangePackage(b, Material.GOLD_BLOCK, (Player) this.firedBy);
//				return b;
//			}
//			
////			double vx = ePosX - x;
////			double vy = ePosY - y;
////			double vz = ePosZ - z;
////			
////			if(Math.sqrt(vx*vx + vy * vy + vz * vz) < BlockRadius) {
////				return b;
////			}
			if(slabAABB(b.getBoundingBox())) {
				return b;
			}
		}
		
		return null;
	}
	
	public void impect(Vector loc) {
		w.getPlayers().forEach(x -> {
			if(x.getLocation().toVector().subtract(loc).length() <= 100) {
				PackageManager.SendParticalPackage(x, impactParticle, loc, new Vector(0,0,0), 0.5f,500);
			}
		});
	}
	
	public double dot(double x1, double x2, double x3, double y1, double y2, double y3) {
		return (x1*y1)+(x2*y2)+(x3*y3);
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
}