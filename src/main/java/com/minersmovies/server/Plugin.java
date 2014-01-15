package com.minersmovies.server;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
	@Override
	public void onEnable() {
		new MinersMoviesCapture(this);
	}
}
