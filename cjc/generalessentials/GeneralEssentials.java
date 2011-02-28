package cjc.generalessentials;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import java.util.logging.Logger;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.Messaging;
import com.nijiko.permissions.PermissionHandler;

public class GeneralEssentials extends JavaPlugin {
	private GeneralEssentialsPlayerListener gepl = new GeneralEssentialsPlayerListener();
	private Logger log = Logger.getLogger("Minecraft");
	private PluginManager pm;
	public static PermissionHandler Permissions = null;
	public static String path;
	
	public void onEnable() {
		path = getServer().getPluginManager().getPlugin("General").getDataFolder().getPath();

		setupPermissions();
		// Loading GE files
		if (gepl.reload() == false)
			return;
		// Get the plugin manager
		pm = getServer().getPluginManager();
		// Add help
		try {
			File g_help = new File(path + "/general.help");
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
		log.info("Launched General Essentials plugin v. 1.0.5.2 -- 1.0.5 w/o constructor compiled with bukkit 409");
	}

	public void onDisable() {
		// unless I actually do something here, I'm keeping this clutter free.
		// log.info("General Essentials plugins is disabled");
	}	
	
	//new setupPermissions courtesy of Acru
	//http://forums.bukkit.org/posts/79813/
	//changed this to TimeShift
	private void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
        if (GeneralEssentials.Permissions == null) {
            if (test != null) {
                this.getServer().getPluginManager().enablePlugin(test); // This line.
                GeneralEssentials.Permissions = ((Permissions)test).getHandler();
            } else {
				log.info(Messaging.bracketize("GeneralEssentials") + " Permission system not enabled. Disabling plugin.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
        }
    }//modified setup method from Permissions thread by Niji
}