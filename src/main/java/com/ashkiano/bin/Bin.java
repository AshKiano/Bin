package com.ashkiano.bin;

import com.ashkiano.ashlib.PluginStatistics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Bin extends JavaPlugin implements CommandExecutor, Listener {
    private String binTitle;

    @Override
    public void onEnable() {
        //TODO tuto chybu vypisovat i OP hráčům do chatu
        if (!isAshLibPresent()) {
            getLogger().severe("AshLib plugin is missing! Please download and install AshLib to run Bin. (can be downloaded from: https://ashsource.ashkiano.com/resource-detail/30 )");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new PluginStatistics(this);

        this.saveDefaultConfig();
        binTitle = getConfig().getString("BinTitle", "Bin");

        boolean showDonateMessage = getConfig().getBoolean("ShowDonateMessage", true);

        this.getCommand("bin").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);

        Metrics metrics = new Metrics(this, 19234);

        if (showDonateMessage) {
            this.getLogger().info("Thank you for using the Bin plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
        }

        checkForUpdates();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfig().getString("only-player", "Only players can use this command!"));
            return true;
        }

        Player player = (Player) sender;
        Inventory binInventory = Bukkit.createInventory(player, 54, binTitle);
        player.openInventory(binInventory);
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        List<HumanEntity> viewers = event.getInventory().getViewers();
        if (!viewers.isEmpty() && viewers.get(0).getOpenInventory().getTitle().equals(binTitle)) {
            event.getInventory().clear();
        }
    }

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://plugins.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }

    private boolean isAshLibPresent() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AshLib");
        return plugin != null && plugin.isEnabled();
    }
}
