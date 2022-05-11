package net.unicraft.skyresources.Listeners;

import net.unicraft.skyresources.SkyResources;
import net.unicraft.skyresources.utilities.RandomGenerator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public class CobbleGenListener implements Listener {

	TreeMap<Double,Material> materialChances;
	
	Collection<String> enabledWorlds;
	
	boolean enableOnlyForStone;
	
	public CobbleGenListener(SkyResources plugin) {
		generateChanceMap(plugin);
		getEnableOnlyStone(plugin);
	}
	
	void generateChanceMap(Plugin plugin) {
		materialChances = new TreeMap<Double, Material>();
		ConfigurationSection sec = plugin.getConfig().getConfigurationSection("cobbleGenChances");
		Map<String, Object> map = sec.getValues(false);
		
		double totalChance = 0.0000;
		for(String s : map.keySet()) {
			Material m = Material.getMaterial(s);
			if(m != null) {
				double chance= 0.000;
				try {
					chance = (double) map.get(s);
				}
				catch(ClassCastException cce) {
					int intChance = (int) map.get(s);
					chance = Double.valueOf(intChance);
				}
				totalChance += chance;
				materialChances.put(totalChance,m);
			}
		}
		if(totalChance > 100.0000) {
			plugin.getLogger().log(Level.SEVERE, "Chances sum for cobble-generation is higher than 100, disabling the CobbleGen-feature");
			materialChances = new TreeMap<Double, Material>();
		}
	}
	
	void getEnableOnlyStone(Plugin plugin) {
		enableOnlyForStone = plugin.getConfig().getBoolean("enableOnlyStone");
	}
	
	@EventHandler
	public void StoneFormed(BlockFormEvent event) {
		if(SkyResources.enabledWorlds.contains(event.getBlock().getWorld().getName())) {
			Material n = event.getNewState().getType();
			if((n.equals(Material.COBBLESTONE) && !enableOnlyForStone) || n.equals(Material.STONE)) {
				try {
					double c = RandomGenerator.getRandomChanceDouble();
					Material m = materialChances.higherEntry(c).getValue();
					if(m != null) {
						event.getNewState().setType(m);
					}
				}
				catch(NullPointerException npe) {
					//Do Nothing
				}
				
			}
		}
	}
}
