import java.io.*;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.Properties;
import java.lang.Integer;

public class GeneralEssentials extends JavaPlugin{
	private static final Class[] param = new Class[]{URL.class};
	private ArrayList<String> kits = new ArrayList<String>();
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<ArrayList<String>> kitsreq = new ArrayList<ArrayList<String>>();
	private GeneralEssentials_player_listen gepl = new GeneralEssentials_player_listen();
	private Logger log = Logger.getLogger("Minecraft");
	private PluginManager pm;
	private Properties props = new Properties();

	public GeneralEssentials(PluginLoader pl, Server s, PluginDescriptionFile pdf, File f, File p, ClassLoader c){
		super(pl,s,pdf,f,p,c);
	}
	
	public void onEnable(){
		//Loading GroupUsers.jar
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		URL u;
		Method method;
		try{
			u = new File("GroupUsers.jar").toURI().toURL();
			method = sysclass.getDeclaredMethod("addURL", param);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[]{u});
		}catch(Exception e){
			log.warning("Failed to find or use GroupUsers.jar; users and groups will be ignored");
		}
		
		//Loading GE files
		if(gepl.reload() == false)
			return;
		
		//Get the plugin manager
		pm = getServer().getPluginManager();
		
		//Add help
		try{
			File g_help = new File("plugins/General/general.help");
			String[] gehelp = {"/gereload &5-&3 Reload any General Essentials settings","/kit &b(name) &5-&3 List or receive kit"};
			BufferedReader br = new BufferedReader(new FileReader(g_help));
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(g_help,true)));
			boolean toBeAdded;
			String thisString;
			for(String s : gehelp){
				toBeAdded = true;
				while((thisString = br.readLine()) != null){
					if(thisString.indexOf(s) != -1){
						toBeAdded = false;
						break;
					}
				}
				
				//If the line does not exist, add it
				//Also, if the last line is not blank, append a blank line
				if(toBeAdded == true)
					pw.print("\n"+s);
			}
			pw.close();
		}catch(Exception e){
			log.warning("Failed to find plugins/General/general.help; help entries will not be entered");
		}
		
		//Load plugin
		pm.registerEvent(Type.PLAYER_COMMAND,gepl,Priority.Low,this);
		
		//Assume success
		log.info("Launched General Essentials plugin v. 1.0.4");	
	}
	
	public void onDisable(){
		log.info("General Essentials plugins is disabled");
	}
	
	public class GeneralEssentials_player_listen extends PlayerListener{
		public void onPlayerCommand(PlayerChatEvent e){
			String[] m = e.getMessage().toLowerCase().split(" ");
			Player p = e.getPlayer();
			if(m[0].equals("/kit")){ //&& props.getProperty("kits-enable") == "true"){
				if(m.length == 1){
					String msg = "§cKits available: ";
					for(int i = 0;i<kits.size();i+=3)
						msg += kits.get(i)+" ";
					p.sendMessage(msg);
				}
				else if(m.length >= 2){
					int kPos = kits.indexOf(m[1]);
					if(kPos == -1)
						p.sendMessage("§cKit by the name of §e"+m[1]+"§c does not exist!");
					else{
						String pName = p.getName();
						int pPos = players.indexOf(pName);
						
						//Player did not request any kit previously
						if(pPos == -1){
							//Add the new player to the list
							int newPos = players.size();
							players.add(pName);
							kitsreq.add(new ArrayList<String>());
							
							//Add the kit and timestamp into the list
							InsertIntoPlayerList(m[1],newPos);
							
							//Receive the kit
							GetKit(kPos+1,p);
						}
						
						//Player did previously request a kit...
						else{
							ArrayList<String> al = kitsreq.get(pPos);
							int alPos = al.indexOf(m[1]);
							
							//...but not the selected one
							if(alPos == -1){
								InsertIntoPlayerList(m[1],pPos);
								GetKit(kPos+1,p);
							}
							
							//...and it is the selected one
							else{
								int left = Integer.parseInt(kits.get(kPos+2)) - ((int)(System.currentTimeMillis()/1000) -
									Integer.parseInt(al.get(alPos+1)));
								
								//Time did not expire yet
								if(left > 0)
									p.sendMessage("§cYou may not receive this kit so soon! Try again in §e"+left+"§c seconds.");
									
								//Time did expire
								else{
									al.remove(alPos);
									al.remove(alPos);
									InsertIntoPlayerList(m[1],pPos);
									GetKit(kPos+1,p);
								}
							}
						}
					}
				}
			}else if(m[0].equals("/gereload")){
				reload();
				log.info("General Essentials plugin reloaded");
			}
		}
		
		private void InsertIntoPlayerList(String cmd,int pos){
			ArrayList<String> al = kitsreq.get(pos);
			al.add(cmd);
			al.add(Integer.toString((int)(System.currentTimeMillis()/1000)));
		}
		
		private void GetKit(int pos,Player p){
			String items = kits.get(pos).trim().replaceAll(" ","");
			for(String i : items.split(",")){
				try{
					if(i.indexOf("-") == -1)
						p.getInventory().addItem(new ItemStack(Integer.parseInt(i),1));
					else if (i.indexOf("+") == -1) {
						String[] multiItem = i.split("-");
					
						p.getInventory().addItem(new ItemStack(Integer.parseInt(multiItem[0]),Integer.parseInt(multiItem[1])));
					} else {

					}
				}catch(NumberFormatException e){
					p.sendMessage("§cSyntax error in kit at substring §e"+i);
					p.sendMessage("§cPlease report to server admin!");
				}
			}
			p.sendMessage("§2Here you go! :D");
		}
		
		public boolean reload(){
			//Read the GE properties file
			/*try{
				props.load(new FileInputStream("General/general.essentials"));
			}catch(Exception e){
				log.info("An error occured: either General/general.essentials does not exist or could not be read; plugin disabled!");
				return false;
			}
			String conflict;
			String pluginConflict = "";*/
				
			//Read the kits file if kits are enabled
			/*if(props.getProperty("kits-enable") == "true"){
				conflict = props.getProperty("kits-conflict-with","");
				if(conflict != ""){
					for(String s : conflict.trim().replaceAll(" ","").split(",")){
						if(pm.isPluginEnabled(s)){
							pluginConflict = s;
							break;
						}
					}
				}
				
				//If there are no conflicting plugins
				if(pluginConflict == ""){*/
					try{
						BufferedReader br = new BufferedReader(new FileReader(new File("plugins/General/general.kits")));
						String l;
						int lineNumber = 1;
						kits.clear();
						String list;
						List<String> listing;
						while((l = br.readLine()) != null){
							list = l.trim();
							if(!list.startsWith("#")){
								listing = Arrays.asList(list.split(":"));
								/*if(listing.size() == 3)
									listing.add("");*/
								if(listing.size() >= 3){
									for(int i = 0;i<3;i++)
										kits.add(listing.get(i).toLowerCase());
								}else{
									log.info("Note: line "+lineNumber+" in general.kits is improperly defined and is ignored");
								}
							}
							lineNumber++;
						}
					}catch(Exception e){
						log.warning("An error occured: either plugins/General/general.kits does not exist or could not be read; kits ignored");
					}
				/*}else
					log.info("Note: kits are disabled because the plugin "+pluginConflict+" conflicts with General Essentials");
			//}else
				log.info("Note: kits are disabled. If you would like to enable them, set kits-enable to 'true'");*/
			
			//Return success
			return true;
		}
	}
}