package com.ashkiano.bin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Bin extends JavaPlugin implements CommandExecutor, Listener {
    private static final String BIN = "Bin";

    @Override
    public void onEnable() {
        this.getCommand("bin").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        // Initialize Metrics for plugin analytics
        Metrics metrics = new Metrics(this, 19234);

        this.getLogger().info("Thank you for using the Bin plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://paypal.me/josefvyskocil");

        checkForUpdates();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        Inventory binInventory = Bukkit.createInventory(player, 54, BIN);
        player.openInventory(binInventory);
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getViewers().get(0).getOpenInventory().getTitle().equals(BIN)) {
            event.getInventory().clear();
        }
    }

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://www.ashkiano.com/version_check.php?plugin=" + pluginName);
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
}
