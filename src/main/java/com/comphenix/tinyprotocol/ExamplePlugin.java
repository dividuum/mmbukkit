package com.comphenix.tinyprotocol;

import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		new MinersMoviesCapture(this);
	}
}
