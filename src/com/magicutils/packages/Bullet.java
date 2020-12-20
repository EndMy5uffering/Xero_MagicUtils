package com.magicutils.packages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
	
//	private boolean debug = false;
	
	public Bullet(Particle particle, Particle impectParticle, double speed, double damage, int liveSpan, Vector dir, Vector loc, World w, Entity firedBy) {
		this.dir = dir;
		this.loc = loc.clone();
		this.prefLoc = loc.clone();
		this.w = w;
		this.damage = damage;
		this.firedBy = firedBy;
		this.lifeTime = liveSpan;
		this.particle = particle;
		this.impactParticle = impectParticle;
		
		this.dir = this.dir.normalize().multiply(speed);
		
		w.playSound(new Location(w, prefLoc.getX(), prefLoc.getY(), prefLoc.getZ()), Sound.ENTITY_BLAZE_SHOOT, 1f, 1.5f);
		
	}
	
	public void updateLocation() {
		if(!alive) return;
		if(lifeTime <= 0) {
			this.alive = false;
			this.impact(loc);
			return;
		}

		this.lifeTime--;
		this.prefLoc = loc.clone();
		this.loc = this.loc.add(dir);
		Block b = IntersectedBlock();
		Entity entity = getIntersectedEntity();
		
		
		if(b != null && entity != null) {
			if(getClosestTmin(b.getBoundingBox()) < getClosestTmin(entity.getBoundingBox())) {
				impactBlock(b);
			}else {
				impactEntity(entity);
			}
			return;
		}else if(b != null && entity == null) {
			impactBlock(b);
			return;
		}else if(b == null && entity != null) {
			impactEntity(entity);
			return;
		}else if(b == null && entity == null) {
			drawProjectile();
		}
//		debug = true;
		
	}
	
	private void impactBlock(Block b) {
		this.impact(slabAABBImpactPoint(b.getBoundingBox()));
	}
	
	private void impactEntity(Entity entity) {
		LivingEntity e = (LivingEntity) entity;
		e.damage(damage, firedBy);
		Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(firedBy, e, DamageCause.ENTITY_ATTACK, this.damage));
		this.impact(slabAABBImpactPoint(e.getBoundingBox()));
	}
	
	private void impact(Vector loc) {
		this.alive = false;
		w.getPlayers().forEach(x -> {
			if(x.getLocation().toVector().subtract(loc).length() <= 100) {
				PackageManager.SendParticalPackage(x, impactParticle, loc, new Vector(0,0,0), 0.1f,20);
			}
		});
		
		for(Entity e : w.getEntities()) {
			if(e instanceof LivingEntity) {
				double d = e.getBoundingBox().getCenter().distance(loc);
				if(d < 50000) {
					LivingEntity entity = (LivingEntity) e;
					entity.damage(damage*1/d, firedBy);
					Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(firedBy, entity, DamageCause.ENTITY_ATTACK, this.damage));
					
				}
			}
		}
		
	}
	
	private void drawProjectile() {
		w.getPlayers().forEach(x -> {
			if(x.getLocation().toVector().subtract(loc).length() <= 100 && alive) {
				double d = 0.5;
//				PackageManager.SendParticalPackage(x, particle, loc.clone(), dir, (float)dir.length(),0);
				for(int i = 0; i < 5; i++) {
					double offx = (new Random().nextDouble()-0.5)*2;
					double offy = (new Random().nextDouble()-0.5)*2;
					double offz = (new Random().nextDouble()-0.5)*2;
					PackageManager.SendParticalPackage(x, particle, loc.clone().add(new Vector(d*offx,d*offy,d*offz)), dir, (float)dir.length()*0.5f,0);
				}
			}
		});
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
	    
	    return tmax >= tmin && tmin < 2 && tmax < 2 && tmin > -2 && tmax > -2;
	}
	
	private double getClosestTmin(BoundingBox b) {
		double tx1 = (b.getMinX() - this.prefLoc.getX())/dir.getX();
	    double tx2 = (b.getMaxX() - this.prefLoc.getX())/dir.getX();
	    double ty1 = (b.getMinY() - this.prefLoc.getY())/dir.getY();
	    double ty2 = (b.getMaxY() - this.prefLoc.getY())/dir.getY();
	    double tz1 = (b.getMinZ() - this.prefLoc.getZ())/dir.getZ();
	    double tz2 = (b.getMaxZ() - this.prefLoc.getZ())/dir.getZ();
	    
	    return Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
	}
	
	private Vector slabAABBImpactPoint(BoundingBox b) {
		double tx1 = (b.getMinX() - this.prefLoc.getX())/dir.getX();
	    double tx2 = (b.getMaxX() - this.prefLoc.getX())/dir.getX();
	    double ty1 = (b.getMinY() - this.prefLoc.getY())/dir.getY();
	    double ty2 = (b.getMaxY() - this.prefLoc.getY())/dir.getY();
	    double tz1 = (b.getMinZ() - this.prefLoc.getZ())/dir.getZ();
	    double tz2 = (b.getMaxZ() - this.prefLoc.getZ())/dir.getZ();

	    double tmin = Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
	    
	    return this.prefLoc.clone().add(this.dir.clone().multiply(tmin));
	}
	
	private Block IntersectedBlock() {
		
		Set<Block> possibleIntersect = new HashSet<>();
		double l = dir.length();
		double x = 0;
		double y = 0;
		double z = 0;
		
		for(double i = -0.5; i <= 1; i += 1/l) {
			x = (i * dir.getX() + this.prefLoc.getX());
			y = (i * dir.getY() + this.prefLoc.getY());
			z = (i * dir.getZ() + this.prefLoc.getZ());
			for(int ix = -1; ix < 1; ix++) {
				for(int iy = -1; iy < 1; iy++) {
					for(int iz = -1; iz < 1; iz++) {
						possibleIntersect.add(w.getBlockAt((int)x+ix, (int)y+iy, (int)z+iz));
					}
				}
			}
			
		}
		
		for(Block b : possibleIntersect) {
//			if(debug)PackageManager.SendBlockChangePackage(b, Material.GLASS, (Player)this.firedBy);
			if(slabAABB(b.getBoundingBox())) {
				return b;
			}
		}
		
		return null;
	}
	
	private Entity getIntersectedEntity() {
		for(Entity e : w.getEntities()) {
			if(!isAlive()) return null;
			if(!(e instanceof LivingEntity)) continue;
			if(e.equals(firedBy)) continue;
			if(slabAABB(e.getBoundingBox())) {
				return e;
			}
		}
		return null;
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
