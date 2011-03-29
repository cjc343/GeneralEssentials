package cjc.generalessentials;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import com.nijikokun.bukkit.Permissions.Permissions;

public class GeneralEssentials extends JavaPlugin {
	private GeneralEssentialsPlayerListener gepl = new GeneralEssentialsPlayerListener();
	private GeneralEssentialsServerListener gesl = new GeneralEssentialsServerListener(this);
	private Logger log = Logger.getLogger("Minecraft");
	public static Permissions Permissions = null;
	public static String path;
	
	public void onEnable() {
		try {
			path = getServer().getPluginManager().getPlugin("General").getDataFolder().getPath();
		} catch (Exception e) {
			log.warning(getDescription().getName() + " requires that 'General' be installed as well.");
		}

		// Loading GE files
		if (gepl.reload() == false)
			return;
		// Add help
		try {
			File g_help = new File(path + "/general.help");
			String[] gehelp = { "/gereload &5-&3 Reload any " + getDescription().getName() + "  settings", "/kit &b(name) &5-&3 List or receive kit" };
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
			log.warning("Failed to find " + path + "/general.help; help entries will not be entered");
			
		}

		// Permissions
		setupPermissions();
		getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, gesl, Priority.Low, this);
		// Assume success
		log.info("Launched " + getDescription().getFullName());
	}

	public void onDisable() {
		// unless I actually do something here, I'm keeping this clutter free.
		// log.info("General Essentials plugins is disabled");
	}	
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		Player p;
		if (sender instanceof Player) {
			p = (Player) sender;
		} else {
			return false;
		}
		gepl.onPlayerCommand(p, command, args);
		
		return true;
		
	}
	
    private void setupPermissions() {
    	//setup permissions and iconomy
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");
        if (GeneralEssentials.Permissions == null) {
            if (plugin != null) {
            	GeneralEssentials.Permissions = (Permissions)plugin;
                System.out.println("[GeneralEssentials] hooked into Permissions.");
            }
        }
    }
}