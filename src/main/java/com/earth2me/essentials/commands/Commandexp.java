package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.NumberUtil;
import java.util.List;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandexp extends EssentialsCommand
{
	public Commandexp()
	{
		super("exp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			showExp(user.getSource(), user);
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("set") && user.isAuthorized("essentials.exp.set"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.set.others"))
			{
				expMatch(server, user.getSource(), args[1], args[2], false);
			}
			else
			{
				setExp(user.getSource(), user, args[1], false);
			}
		}
		else if (args.length > 1 && args[0].equalsIgnoreCase("give") && user.isAuthorized("essentials.exp.give"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.give.others"))
			{
				expMatch(server, user.getSource(), args[1], args[2], true);
			}
			else
			{
				setExp(user.getSource(), user, args[1], true);
			}
		}
		else if (args[0].equalsIgnoreCase("show"))
		{
			if (args.length >= 2 && user.isAuthorized("essentials.exp.others"))
			{
				String match = args[1].trim();
				showMatch(server, user.getSource(), match);
			}
			else
			{
				showExp(user.getSource(), user);
			}
		}
		else
		{
			if (args.length >= 1 && NumberUtil.isInt(args[0].toLowerCase(Locale.ENGLISH).replace("l", "")) && user.isAuthorized("essentials.exp.give"))
			{
				if (args.length >= 2 && user.isAuthorized("essentials.exp.give.others"))
				{
					expMatch(server, user.getSource(), args[1], args[0], true);
				}
				else
				{
					setExp(user.getSource(), user, args[0], true);
				}
			}
			else if (args.length >= 1 && user.isAuthorized("essentials.exp.others"))
			{
				String match = args[0].trim();
				showMatch(server, user.getSource(), match);
			}
			else
			{
				showExp(user.getSource(), user);
			}
		}
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (args.length > 2 && args[0].equalsIgnoreCase("set"))
		{
			expMatch(server, sender, args[1], args[2], false);
		}
		else if (args.length > 2 && args[0].equalsIgnoreCase("give"))
		{
			expMatch(server, sender, args[1], args[2], true);
		}
		else
		{
			String match = args[0].trim();
			if (args.length >= 2 && NumberUtil.isInt(args[0].toLowerCase(Locale.ENGLISH).replace("l", "")))
			{
				match = args[1].trim();
				expMatch(server, sender, match, args[0], true);
			}
			else if (args.length == 1)
			{
				match = args[0].trim();
			}
			showMatch(server, sender, match);
		}
	}

	private void showMatch(final Server server, final CommandSource sender, final String match) throws PlayerNotFoundException
	{
		boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
		boolean foundUser = false;
		final List<Player> matchedPlayers = server.matchPlayer(match);
		for (Player matchPlayer : matchedPlayers)
		{
			final User player = ess.getUser(matchPlayer);
			if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(matchPlayer))
			{
				continue;
			}
			foundUser = true;
			showExp(sender, player);
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
		}
	}

	private void expMatch(final Server server, final CommandSource sender, final String match, String amount, final boolean give) throws NotEnoughArgumentsException, PlayerNotFoundException
	{
		boolean skipHidden = sender.isPlayer() && !ess.getUser(sender.getPlayer()).canInteractVanished();
		boolean foundUser = false;
		final List<Player> matchedPlayers = server.matchPlayer(match);
		for (Player matchPlayer : matchedPlayers)
		{
			final User player = ess.getUser(matchPlayer);
			if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(matchPlayer))
			{
				continue;
			}
			foundUser = true;
			setExp(sender, player, amount, give);
		}
		if (!foundUser)
		{
			throw new PlayerNotFoundException();
		}
	}

	private void showExp(final CommandSource sender, final User target)
	{
		sender.sendMessage(tl("exp", target.getDisplayName(), SetExpFix.getTotalExperience(target.getBase()), target.getBase().getLevel(), SetExpFix.getExpUntilNextLevel(target.getBase())));
	}

	//TODO: Limit who can give negative exp?
	private void setExp(final CommandSource sender, final User target, String strAmount, final boolean give) throws NotEnoughArgumentsException
	{
		long amount;
		strAmount = strAmount.toLowerCase(Locale.ENGLISH);
		if (strAmount.contains("l"))
		{
			strAmount = strAmount.replaceAll("l", "");
			int neededLevel = Integer.parseInt(strAmount);
			if (give)
			{
				neededLevel += target.getBase().getLevel();
			}
			amount = (long)SetExpFix.getExpToLevel(neededLevel);
			SetExpFix.setTotalExperience(target.getBase(), 0);
		}
		else
		{
			amount = Long.parseLong(strAmount);
			if (amount > Integer.MAX_VALUE || amount < Integer.MIN_VALUE)
			{
				throw new NotEnoughArgumentsException();
			}
		}

		if (give)
		{
			amount += SetExpFix.getTotalExperience(target.getBase());
		}
		if (amount > Integer.MAX_VALUE)
		{
			amount = (long)Integer.MAX_VALUE;
		}
		if (amount < 0l)
		{
			amount = 0l;
		}
		SetExpFix.setTotalExperience(target.getBase(), (int)amount);
		sender.sendMessage(tl("expSet", target.getDisplayName(), amount));
	}
}
