package net.unicraft.skyresources;

import net.unicraft.skyresources.Listeners.CobbleGenListener;
import net.unicraft.skyresources.Listeners.PlayerInteractionListener;
import net.unicraft.skyresources.Listeners.TreeGrowListener;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkyResources extends JavaPlugin {

    private static SkyResources instance;

    static FileConfiguration config;

    static Logger logger;

    public static List<String> enabledWorlds;

    @Override
    public void onEnable() {
        instance = this;
        config = this.getConfig();
        logger = this.getLogger();
        saveDefaultConfig();

        getEnabledWorlds();

        if (config.getBoolean("enableRandomCobbleGen")) {
            getServer().getPluginManager().registerEvents(new CobbleGenListener(this), this);
        }
        if (config.getBoolean("enableRandomLeaves")) {
            getServer().getPluginManager().registerEvents(new TreeGrowListener(this), this);
        }
        boolean enableHoeCrushing = false;
        try {
            enableHoeCrushing = config.getDouble("crushChance") > 0;
        } catch (ClassCastException cce) {
            enableHoeCrushing = config.getInt("crushChance") > 0;
        }
        if (enableHoeCrushing) {
            getServer().getPluginManager().registerEvents(new PlayerInteractionListener(this), this);
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

            }
        }.runTaskTimer(this, 3L, 1L);
    }

    @SuppressWarnings("unchecked")
    static void getEnabledWorlds() {
        enabledWorlds = new ArrayList<>();
        enabledWorlds = (ArrayList<String>) config.get("SkyResourcesWorlds");
        if (enabledWorlds.isEmpty()) {
            logger.log(Level.SEVERE, "No worlds specified in the config.yml. Disabling SkyResources");
            JavaPlugin skyResources = SkyResources.getPlugin(SkyResources.class);
            skyResources.getServer().getPluginManager().disablePlugin(skyResources);
        }
    }

    @Override
    public void onDisable() {

    }

    public static boolean isWorldAllowed(World world) {
        return enabledWorlds.contains(world.getName());
    }

    public static SkyResources getPlugin() {
        return instance;
    }
}
