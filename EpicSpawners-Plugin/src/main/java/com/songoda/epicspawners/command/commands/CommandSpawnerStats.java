package com.songoda.epicspawners.command.commands;

import com.songoda.arconix.api.methods.formatting.TextComponent;
import com.songoda.epicspawners.EpicSpawnersPlugin;
import com.songoda.epicspawners.api.spawner.SpawnerData;
import com.songoda.epicspawners.command.AbstractCommand;
import com.songoda.epicspawners.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

public class CommandSpawnerStats extends AbstractCommand {

    public CommandSpawnerStats() {
        super("spawnerstats", null, true);
    }

    @Override
    protected ReturnType runCommand(EpicSpawnersPlugin instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        int size = 0;

        for (Map.Entry<EntityType, Integer> entry : instance.getPlayerActionManager().getPlayerAction(player).getEntityKills().entrySet()) {
            if (instance.getSpawnerManager().getSpawnerData(entry.getKey()).getKillGoal() != 0) {
                size++;
            }
        }

        String title = instance.getLocale().getMessage("interface.spawnerstats.title");

        Inventory i = Bukkit.createInventory(null, 54, title);
        if (size <= 9) {
            i = Bukkit.createInventory(null, 18, title);
        } else if (size <= 18) {
            i = Bukkit.createInventory(null, 27, title);
        } else if (size <= 27) {
            i = Bukkit.createInventory(null, 36, title);
        } else if (size <= 36) {
            i = Bukkit.createInventory(null, 45, title);
        }

        int num = 0;
        while (num != 9) {
            i.setItem(num, Methods.getGlass());
            num++;
        }
        ItemStack exit = new ItemStack(Material.valueOf(instance.getConfig().getString("Interfaces.Exit Icon")), 1);
        ItemMeta exitmeta = exit.getItemMeta();
        exitmeta.setDisplayName(instance.getLocale().getMessage("general.nametag.exit"));
        exit.setItemMeta(exitmeta);
        i.setItem(8, exit);

        short place = 9;
        player.sendMessage("");


        if (instance.getPlayerActionManager().getPlayerAction(player).getEntityKills().size() == 0) {
            player.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("interface.spawnerstats.nokills"));
            return ReturnType.SUCCESS;
        }

        player.sendMessage(instance.getReferences().getPrefix());
        player.sendMessage(instance.getLocale().getMessage("interface.spawnerstats.prefix"));
        for (Map.Entry<EntityType, Integer> entry : instance.getPlayerActionManager().getPlayerAction(player).getEntityKills().entrySet()) {
            int goal = instance.getConfig().getInt("Spawner Drops.Kills Needed for Drop");

            SpawnerData spawnerData = instance.getSpawnerManager().getSpawnerData(entry.getKey());

            int customGoal = spawnerData.getKillGoal();
            if (customGoal != 0) goal = customGoal;

            ItemStack it = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

            ItemStack item = instance.getHeads().addTexture(it, spawnerData);

            ItemMeta itemmeta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            itemmeta.setLore(lore);
            itemmeta.setDisplayName(TextComponent.formatText("&6" + spawnerData.getDisplayName() + "&7: &e" + entry.getValue() + "&7/&e" + goal));
            item.setItemMeta(itemmeta);
            i.setItem(place, item);

            place++;
            player.sendMessage(TextComponent.formatText("&7- &6" + spawnerData.getDisplayName() + "&7: &e" + entry.getValue() + "&7/&e" + goal));
        }
        player.sendMessage(instance.getLocale().getMessage("interface.spawnerstats.ongoal"));

        player.sendMessage("");

        player.openInventory(i);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicspawners.stats";
    }

    @Override
    public String getSyntax() {
        return "/SpawnerStats";
    }

    @Override
    public String getDescription() {
        return "This allows you to boost the amount of spawns that are got from placed spawners.";
    }
}
