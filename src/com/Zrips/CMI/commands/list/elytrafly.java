package com.Zrips.CMI.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class elytrafly implements Cmd {

    @Override
    public void getExtra(ConfigReader var1) {
        var1.get("ElytraRequired", "{gcw}&cYou must wear an elytra to use /efly"); // Zrips, put msgs that you need here
        var1.get("flyToggled", "{gcp}Elytra flight [status]&p!"); // Zrips, put msgs that you need here
    }

    @Override
    @CAnnotation(
            info = "Toggle flight mode specifically for Elytra users",
            args = "([on/off])",
            tab = {"on", "off"},
            explanation = {"Enables or disables flight, but only if the player is wearing an Elytra."},
            others = true
    )
    public Boolean perform(CMI plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        CMIUser user = plugin.getPlayerManager().getUser(player);

        ItemStack chest = player.getInventory().getChestplate();
        CMIItemStack cmiChest = new CMIItemStack(chest);

        if (chest == null || cmiChest.getCMIType() != CMIMaterial.ELYTRA) {
            com.Zrips.CMI.Locale.CMILC.info(this, sender, "ElytraRequired");
            return false;
        }

        boolean newState = !player.getAllowFlight();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("on")) {
                newState = true;
            } else if (args[0].equalsIgnoreCase("off")) {
                newState = false;
            }
        }

        player.setAllowFlight(newState);
        if (!newState) {
            player.setFlying(false);
        }

        String status = newState ? CMIMessages.getMsg(LC.info_variables_Enabled) : CMIMessages.getMsg(LC.info_variables_Disabled);

        com.Zrips.CMI.Locale.CMILC.info(this, sender, "flyToggled", "[status]", status, "[playerName]", user.getDisplayName());

        return true;
    }
}