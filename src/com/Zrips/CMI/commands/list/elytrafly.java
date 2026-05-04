package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager.CMIPerm;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class elytrafly implements Cmd {

    public elytrafly() {
    }

    @Override
    public void getExtra(ConfigReader reader) {
        reader.get("ElytraRequired", "{gcw}&cYou must wear an elytra to use this command!");
        reader.get("feedback", "{gcp}You have set elytra fly to {gcs}[boolean]{gcp} for {gcs}[playerDisplayName]{gcp}.");
        reader.get("targetFeedback", "{gcp}Your elytra fly mode set to {gcs}[boolean]{gcp} by {gcs}[senderDisplayName]{gcp}.");
    }

    @Override
    @CAnnotation(
            info = "Toggle flight mode specifically for Elytra users",
            args = "[playerName] (true/false) (-s)",
            regVar = {0, 1, 2},
            others = true
    )
    public Boolean perform(CMI plugin, CommandSender sender, String[] args) {
        boolean silent = false;
        String targetName = null;
        Boolean state = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                if (CMIPerm.command_silent.hasPermission(sender)) {
                    silent = true;
                }
                continue;
            }
            if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("on") || arg.equalsIgnoreCase("t")) {
                state = true;
                continue;
            }
            if (arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("off") || arg.equalsIgnoreCase("f")) {
                state = false;
                continue;
            }
            targetName = arg;
        }

        Player player = plugin.getTarget(sender, targetName, this);
        if (player == null) {
            return null;
        }

        ItemStack chest = player.getInventory().getChestplate();
        CMIItemStack cmiChest = new CMIItemStack(chest);
        if (chest == null || cmiChest.getCMIType() != CMIMaterial.ELYTRA) {
            CMILC.info(this, sender, "ElytraRequired");
            return false;
        }

        if (state == null) {
            state = !player.getAllowFlight();
        }

        player.setFallDistance(0.0F);
        player.setAllowFlight(state);
        if (!state) {
            player.setFlying(false);
        }

        CMIUser user = plugin.getPlayerManager().getUser(player);
        if (user != null) {
            user.setTfly(0L);
            if (!user.isOnline()) {
                user.setHadAllowFlight(state);
                user.setWasFlying(state);
                user.setFlying(state);
            }
        }

        plugin.save(player);

        Snd snd = new Snd().setSender(sender).setTarget(player);
        if (!silent) {
            CMILC.info(this, sender, "feedback", snd, "[boolean]", state);
        }

        if (!player.getName().equalsIgnoreCase(sender.getName()) && !silent) {
            CMILC.info(this, player, "targetFeedback", "[boolean]", state, snd);
        }

        return true;
    }
}
