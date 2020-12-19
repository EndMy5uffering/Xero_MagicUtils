package com.magicutils.packages;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

public class BulletHandler extends BukkitRunnable{

	public static ArrayList<Bullet> bullets = new ArrayList<>();
	public static ArrayList<Bullet> newbullets = new ArrayList<>();
	
	public BulletHandler() {
		
	}
	
	public static void add(Bullet b) {
		newbullets.add(b);
	}

	@Override
	public void run() {
		for(Bullet b : newbullets) {
			bullets.add(b);	
		}
		newbullets.clear();
		
		ArrayList<Bullet> remove = new ArrayList<>();
		for(Bullet b : bullets) {
			if(!b.isAlive())remove.add(b);
		}
		
		for(Bullet b : remove) {
			bullets.remove(b);
		}
		
		for(Bullet b : bullets) {
			b.updateLocation();
		}
		
	}
	
}
