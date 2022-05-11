package net.unicraft.skyresources.Listeners;

import net.unicraft.skyresources.SkyResources;
import net.unicraft.skyresources.utilities.RandomGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;

public class PlayerInteractionListener implements Listener {

	double crushChance;

	public PlayerInteractionListener(SkyResources plugin) {
		getCrushChance(plugin);
	}

	void getCrushChance(Plugin plugin) {
		try {
			crushChance = plugin.getConfig().getDouble("crushChance");
		} catch (ClassCastException cce) {
			crushChance = plugin.getConfig().getInt("crushChance");
		}
	}

	@EventHandler
	public void playerInteraction(PlayerInteractEvent event) {
		Action eventAction = event.getAction();
		EquipmentSlot slot = event.getHand();
		if (eventAction.equals(Action.RIGHT_CLICK_BLOCK) && slot.equals(EquipmentSlot.HAND)) {
			ItemStack itemStack = event.getItem();
			if (itemStack != null) {
				Material item = itemStack.getType();
				if (SkyResources.isWorldAllowed(event.getClickedBlock().getWorld())) {
					switch (item) {
					case DIAMOND_HOE:
					case GOLDEN_HOE:
					case IRON_HOE:
					case NETHERITE_HOE:
					case STONE_HOE:
					case WOODEN_HOE:
						crushMaterial(event);
						break;

					default:
						break;
					}
					switch (event.getClickedBlock().getType()) {
					case WATER_CAULDRON:
						useOnCauldron(event);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	private void crushMaterial(PlayerInteractEvent event) {
		if (event.getClickedBlock().getType().equals(Material.COBBLESTONE)
				|| event.getClickedBlock().getType().equals(Material.GRAVEL)) {
			double c = RandomGenerator.getRandomChanceDouble();
			if (crushChance >= c) {
				if (event.getClickedBlock().getType().equals(Material.COBBLESTONE)) {
					event.getClickedBlock().setType(Material.GRAVEL);
				} else if (event.getClickedBlock().getType().equals(Material.GRAVEL)) {
					event.getClickedBlock().setType(Material.SAND);
				}
			}
			Damageable itemDamage = (Damageable) event.getItem();
			itemDamage.setDamage(itemDamage.getDamage() + 4);
		}
	}

	private void useOnCauldron(PlayerInteractEvent event) {
		ItemStack itemStack = event.getItem();
		Block cauldron = event.getClickedBlock();
		switch (itemStack.getType()) {
		case SAND:
			Levelled cauldronData = (Levelled) cauldron.getBlockData();
			cauldronData.setLevel(cauldronData.getLevel() - 1);
			cauldron.setBlockData(cauldronData);

			event.getItem().setAmount(event.getItem().getAmount() - 1);
			Location spawnLocation = new Location(cauldron.getWorld(), cauldron.getX(), cauldron.getY() + 1,
					cauldron.getZ());
			cauldron.getWorld().dropItem(spawnLocation, new ItemStack(Material.CLAY, 1));
			break;
		default:
			break;
		}
	}
}
