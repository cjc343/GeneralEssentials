package com.bukkit.cjc.generalessentials;

import java.lang.Integer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;




public class GeneralEssentialsPlayerListener extends PlayerListener {
	private ArrayList<String> kits = new ArrayList<String>();
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<ArrayList<String>> kitsreq = new ArrayList<ArrayList<String>>();
	private Logger log = Logger.getLogger("Minecraft");
	
	
	
	public void onPlayerCommand(PlayerChatEvent e) {
		String[] m = e.getMessage().toLowerCase().split(" ");
		Player p = e.getPlayer();
		if (m[0].equals("/kit"))  { // && (GeneralEssentials.Permissions.has(p, "generalessentials.kit")))
			// props.getProperty("kits-enable")
			// ==
			// "true"){
			if (m.length == 1) {
				String msg = "�cKits available: ";
				for (int i = 0; i < kits.size(); i += 3)
					if (GeneralEssentials.Permissions.has(p, "generalessentials.kit." + kits.get(i).toString())) {
						msg += kits.get(i) + " ";
					}
				p.sendMessage(msg);
			} else if (m.length >= 2) {
				int kPos = kits.indexOf(m[1]);
				if (kPos == -1)
					p.sendMessage("�cKit by the name of �e" + m[1] + "�c does not exist!");
				else {

						if (!GeneralEssentials.Permissions.has(p, "generalessentials.kit." + m[1].toLowerCase())) {
							p.sendMessage("You do not have permission for that kit.");
							return;
						}

					String pName = p.getName();
					int pPos = players.indexOf(pName);

					// Player did not request any kit previously
					if (pPos == -1) {
						// Add the new player to the list
						int newPos = players.size();
						players.add(pName);
						kitsreq.add(new ArrayList<String>());

						// Add the kit and timestamp into the list
						InsertIntoPlayerList(m[1], newPos);

						// Receive the kit
						GetKit(kPos + 1, p);
					}

					// Player did previously request a kit...
					else {
						ArrayList<String> al = kitsreq.get(pPos);
						int alPos = al.indexOf(m[1]);

						// ...but not the selected one
						if (alPos == -1) {
							InsertIntoPlayerList(m[1], pPos);
							GetKit(kPos + 1, p);
						}

						// ...and it is the selected one
						else {
							int left = Integer.parseInt(kits.get(kPos + 2)) - ((int) (System.currentTimeMillis() / 1000) - Integer.parseInt(al.get(alPos + 1)));

							// Time did not expire yet
							if (left > 0)
								p.sendMessage("�cYou may not receive this kit so soon! Try again in �e" + left + "�c seconds.");

							// Time did expire
							else {
								al.remove(alPos);
								al.remove(alPos);
								InsertIntoPlayerList(m[1], pPos);
								GetKit(kPos + 1, p);
							}
						}
					}
				}
			}
		} else if (m[0].equals("/gereload") && (GeneralEssentials.Permissions.has(p, "generalessentials.reload"))) {
			reload();
			log.info("General Essentials plugin reloaded");
			p.sendMessage("GeneralEssentials has been reloaded.");
		} else if (m[0].equals("/gereload") || m[0].equals("/kit")) {
			p.sendMessage("You do not have permission to do that. (GeneralEssentials)");
		}
	}

	private void InsertIntoPlayerList(String cmd, int pos) {
		ArrayList<String> al = kitsreq.get(pos);
		al.add(cmd);
		al.add(Integer.toString((int) (System.currentTimeMillis() / 1000)));
	}

	private void GetKit(int pos, Player p) {
		String items = kits.get(pos).trim().replaceAll(" ", "");
		for (String i : items.split(",")) {
			try {
				if (i.indexOf("-") == -1)
					p.getInventory().addItem(new ItemStack(Integer.parseInt(i), 1));
				else if (i.indexOf("+") == -1) {
					String[] multiItem = i.split("-");
					p.getInventory().addItem(new ItemStack(Integer.parseInt(multiItem[0]), Integer.parseInt(multiItem[1])));
				} else {
					String[] itemVal = i.split("\\+");
					String[] decCount = itemVal[1].split("-");
					p.getInventory().addItem(new ItemStack(Integer.parseInt(itemVal[0]), Integer.parseInt(decCount[1]), Short.parseShort(decCount[0])));
				}
			} catch (NumberFormatException e) {
				p.sendMessage("�cSyntax error in kit at substring �e" + i);
				p.sendMessage("�cPlease report to server admin!");
			}
		}
		p.sendMessage("�2Here you go! :D");
	}

	public boolean reload() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("plugins/General/general.kits")));
			String l;
			int lineNumber = 1;
			kits.clear();
			String list;
			List<String> listing;
			while ((l = br.readLine()) != null) {
				list = l.trim();
				if (!list.startsWith("#")) {
					listing = Arrays.asList(list.split(":"));
					if (listing.size() >= 3) {
						for (int i = 0; i < 3; i++)
							kits.add(listing.get(i).toLowerCase());
					} else {
						log.info("Note: line " + lineNumber + " in general.kits is improperly defined and is ignored");
					}
				}
				lineNumber++;
			}
		} catch (Exception e) {
			log.warning("An error occured: either plugins/General/general.kits does not exist or could not be read; kits ignored");
		}
		// Return success
		return true;
	}
}