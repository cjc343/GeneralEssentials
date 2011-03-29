package cjc.generalessentials;


import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;
public class GeneralEssentialsServerListener extends ServerListener {

    private GeneralEssentials origin;

    public GeneralEssentialsServerListener(GeneralEssentials thisPlugin) {
        this.origin = thisPlugin;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (GeneralEssentials.Permissions == null) {
            Plugin plugin = origin.getServer().getPluginManager().getPlugin("Permissions");

            if (plugin != null) {
                if (plugin.isEnabled()) {
                    GeneralEssentials.Permissions = (Permissions)plugin;
                    System.out.println("[" + origin.getDescription().getName() + "] hooked into Permissions.");
                }
            }
        }
    }
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (GeneralEssentials.Permissions != null) {
            String plugin = event.getPlugin().getDescription().getName();

            if (plugin.equals("Permissions")) {
                    GeneralEssentials.Permissions = null;
                    System.out.println("[" + origin.getDescription().getName() + "] cannot work without Permissions. Permissions has been unloaded.");
                }
            }
        }
}
