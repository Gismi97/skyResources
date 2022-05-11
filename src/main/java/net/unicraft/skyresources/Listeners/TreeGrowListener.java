package net.unicraft.skyresources.Listeners;

import net.unicraft.skyresources.SkyResources;
import net.unicraft.skyresources.utilities.RandomGenerator;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;


public class TreeGrowListener implements Listener {

	TreeMap<Double, Material> leavesChances;

	int maxLeavesToChange = 5;

	boolean doMaxAmountCheck;

	public TreeGrowListener(SkyResources plugin) {
		getMaxLeavesToChange(plugin);
		generateChanceMap(plugin);
	}

	void getMaxLeavesToChange(Plugin plugin) {
		maxLeavesToChange = plugin.getConfig().getInt("maxLeavesChanged");
		doMaxAmountCheck = maxLeavesToChange > -1;
	}

	void generateChanceMap(Plugin plugin) {
		leavesChances = new TreeMap<>();
		ConfigurationSection sec = plugin.getConfig().getConfigurationSection("leavesChances");
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
					chance = Double.valueOf((int) map.get(s));
				}
				totalChance += chance;
				leavesChances.put(totalChance,m);
			}
		}
		if(totalChance > 100.0000) {
			plugin.getLogger().log(Level.SEVERE, "Chances sum for leaves-generation is higher than 100, disabling the LeavesGen-feature");
			leavesChances = new TreeMap<>();
		}
	}

	@EventHandler
	public void treeGrown(StructureGrowEvent event) {
		if (event.isFromBonemeal() && event.getSpecies().equals(TreeType.TREE) && SkyResources.isWorldAllowed(event.getWorld())) {
			changeLeaves(event);
		}
	}

	private void changeLeaves(StructureGrowEvent event) {
		int leavesChanged = 0;
		for (BlockState s : event.getBlocks()) {
			if (s.getType().equals(Material.OAK_LEAVES)) {
				double c = RandomGenerator.getRandomChanceDouble();
				Material m = leavesChances.higherEntry(c).getValue();
				if (m != null) {
					s.setType(m);
					if (doMaxAmountCheck) {
						leavesChanged++;
						if (leavesChanged >= maxLeavesToChange) {
							break;
						}
					}
				}
			}
		}
	}
}
