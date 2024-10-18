package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Commandtppos extends EssentialsCommand
{
    public Commandtppos()
    {
        super("tppos");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
    {
        if (args.length < 3)
        {
            throw new NotEnoughArgumentsException();
        }

        World world;
        int index = 0;

        if (args.length == 4) {
            world = server.getWorld(args[0]);
            if (world == null) {
                throw new Exception(tl("invalidWorld", args[0]));
            }
            index = 1;
        } else {
            world = user.getWorld();
        }

        final double x = args[index].startsWith("~") ? user.getLocation().getX() + Integer.parseInt(args[index].substring(1)) : Integer.parseInt(args[index]);
        final double y = args[index + 1].startsWith("~") ? user.getLocation().getY() + Integer.parseInt(args[index + 1].substring(1)) : Integer.parseInt(args[index + 1]);
        final double z = args[index + 2].startsWith("~") ? user.getLocation().getZ() + Integer.parseInt(args[index + 2].substring(1)) : Integer.parseInt(args[index + 2]);
        final Location loc = new Location(world, x, y, z, user.getLocation().getYaw(), user.getLocation().getPitch());

        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000)
        {
            throw new NotEnoughArgumentsException(tl("teleportInvalidLocation"));
        }

        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        user.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.getTeleport().teleport(loc, charge, TeleportCause.COMMAND);
        throw new NoChargeException();
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
    {
        if (args.length < 3)
        {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, args, 0, true, false);
        World world;
        int index = 1;

        if (args.length == 5) {
            world = server.getWorld(args[1]);
            if (world == null) {
                throw new Exception(tl("invalidWorld", args[1]));
            }
            index = 2;
        } else {
            world = user.getWorld();
        }

        final double x = args[index].startsWith("~") ? user.getLocation().getX() + Integer.parseInt(args[index].substring(1)) : Integer.parseInt(args[index]);
        final double y = args[index + 1].startsWith("~") ? user.getLocation().getY() + Integer.parseInt(args[index + 1].substring(1)) : Integer.parseInt(args[index + 1]);
        final double z = args[index + 2].startsWith("~") ? user.getLocation().getZ() + Integer.parseInt(args[index + 2].substring(1)) : Integer.parseInt(args[index + 2]);
        final Location loc = new Location(world, x, y, z, user.getLocation().getYaw(), user.getLocation().getPitch());

        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000)
        {
            throw new NotEnoughArgumentsException(tl("teleportInvalidLocation"));
        }

        sender.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.getTeleport().teleport(loc, null, TeleportCause.COMMAND);
    }
}