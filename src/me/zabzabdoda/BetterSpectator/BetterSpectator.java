package me.zabzabdoda.BetterSpectator;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

/*
 * Written by Tyler Massey aka zabzabdoda
 */

public class BetterSpectator extends JavaPlugin implements Listener{
	
	private static Server server;

	
	static {
		server = Bukkit.getServer();
	}

	public void onEnable() {
		BetterSpectator.server.broadcastMessage("Enabling Better Spectator Plugin by zabzabdoda");
		BetterSpectator.server.getPluginManager().registerEvents((Listener) this, (Plugin) this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("bs")) {
				if(player.hasPermission("bs.view")) {
					if(args.length == 2) {
						if(args[0].equalsIgnoreCase("view")) {
							Player target = server.getPlayer(args[1]);
							if(target != null) {
								openPlayersInventory(player,target);
								return true;
							}
							sender.sendMessage(ChatColor.RED + "Player cannot be found, are they online?");
							return false;
						}
					}
					sender.sendMessage(ChatColor.RED + "Incorrect usage, type /help bs for help.");
					return false;
				}
				sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
				return false;
			}
			return false;
		}
		sender.sendMessage(ChatColor.RED + "Command can only be run in game.");
		return false;
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void playerclickplayer(PlayerInteractEntityEvent e) {
		if(e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
			if(e.getRightClicked() instanceof Player) {
				Player player = (Player) e.getRightClicked();
				openPlayersInventory(e.getPlayer(),player);
			}
		}
	}
	
	public void openPlayersInventory(Player player, Player player2) {
		Inventory spectatorInv = Bukkit.createInventory(null, 45,player2.getDisplayName()); 
		for(int i = 0; i < player2.getInventory().getSize()-1; i++) {
			if(player2.getInventory().getItem(i) != null) {
				spectatorInv.setItem(i,player2.getInventory().getItem(i));
			}else {
				spectatorInv.setItem(i,new ItemStack(Material.AIR));
			}
		}

		ItemStack healthItem = new ItemStack(Material.POTION,(int)player2.getHealth());
		ItemMeta healthMeta = healthItem.getItemMeta();
		healthMeta.setDisplayName(ChatColor.GREEN + "Health");
		ArrayList<String> activeEffectsList = new ArrayList<String>();
		for(PotionEffect effect : player2.getActivePotionEffects()) {
			activeEffectsList.add(ChatColor.AQUA + effect.getType().getName() + ", " + effect.getDuration() + ", " + effect.getAmplifier());
		}
		healthMeta.setLore(activeEffectsList);
		healthItem.setItemMeta(healthMeta);
		spectatorInv.setItem(40,healthItem);//health

		ItemStack foodItem = new ItemStack(Material.COOKED_BEEF,(int)player2.getFoodLevel());
		ItemMeta foodMeta = foodItem.getItemMeta();
		foodMeta.setDisplayName(ChatColor.GREEN + "Food");
		foodItem.setItemMeta(foodMeta);
		spectatorInv.setItem(41,foodItem);//food
		if(player2.getInventory().getItem(40) != null) {
			spectatorInv.setItem(42,player2.getInventory().getItem(40));//offhand
		}
		player.openInventory(spectatorInv);
	}

}
