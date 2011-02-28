package com.bukkit.cjc.generalessentials;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import java.util.logging.Logger; //import java.util.Properties;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;

public class GeneralEssentials extends JavaPlugin {

	private GeneralEssentialsPlayerListener gepl = new GeneralEssentialsPlayerListener();
	private Logger log = Logger.getLogger("Minecraft");
	private PluginManager pm;
	public static PermissionHandler Permissions = null;

	public GeneralEssentials(PluginLoader pl, Server s, PluginDescriptionFile pdf, File f, File p, ClassLoader c) {
		super(pl, s, pdf, f, p, c);
	}
	
	public void onEnable() {
		setupPermissions();

		// Loading GE files
		if (gepl.reload() == false)
			return;
		// Get the plugin manager
		pm = getServer().getPluginManager();
		// Add help
		try {
			File g_help = new File("plugins/General/general.help");
			String[] gehelp = { "/gereload &5-&3 Reload any General Essentials settings", "/kit &b(name) &5-&3 List or receive kit" };
			BufferedReader br = new BufferedReader(new FileReader(g_help));
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(g_help, true)));
			boolean toBeAdded;
			String thisString;
			for (String s : gehelp) {
				toBeAdded = true;
				while ((thisString = br.readLine()) != null) {
					if (thisString.indexOf(s) != -1) {
						toBeAdded = false;
						break;
					}
				}

				// If the line does not exist, add it
				// Also, if the last line is not blank, append a blank line
				if (toBeAdded == true)
					pw.print("\n" + s);
			}
			pw.close();
		} catch (Exception e) {
			log.warning("Failed to find plugins/General/general.help; help entries will not be entered");
		}

		// Load plugin
		pm.registerEvent(Type.PLAYER_COMMAND, gepl, Priority.Low, this);

		// Assume success
		log.info("Launched General Essentials plugin v. 1.0.5");
	}

	public void onDisable() {
		// unless I actually do something here, I'm keeping this clutter free.
		// log.info("General Essentials plugins is disabled");
	}

	// setupPermission method from Nijikokun's "Permissions v2.0" post
	public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if (GeneralEssentials.Permissions == null) {
			if (test != null) {
				GeneralEssentials.Permissions = ((Permissions) test).getHandler();
			} else {
				log.info(Messaging.bracketize("GeneralEssentials") + " Permission system not enabled. Disabling plugin.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

}