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

public class Bin extends JavaPlugin implements CommandExecutor, Listener {
    private static final String BIN = "Bin";

    @Override
    public void onEnable() {
        this.getCommand("bin").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        // Initialize Metrics for plugin analytics
        Metrics metrics = new Metrics(this, 19234);

        this.getLogger().info("Thank you for using the Bin plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://paypal.me/josefvyskocil");
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
}
