/*
 * EpicGuard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EpicGuard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.xneox.epicguard.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.xneox.epicguard.core.EpicGuard;
import me.xneox.epicguard.core.command.sub.AnalyzeCommand;
import me.xneox.epicguard.core.command.sub.BlacklistCommand;
import me.xneox.epicguard.core.command.sub.HelpCommand;
import me.xneox.epicguard.core.command.sub.ReloadCommand;
import me.xneox.epicguard.core.command.sub.SaveCommand;
import me.xneox.epicguard.core.command.sub.StatusCommand;
import me.xneox.epicguard.core.command.sub.WhitelistCommand;
import me.xneox.epicguard.core.util.TextUtils;
import me.xneox.epicguard.core.util.VersionUtils;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

/**
 * This class holds all registered subcommands, and handles the user command/tab suggestion input.
 * TODO: Better command system
 */
public class CommandHandler {
  private final Map<String, SubCommand> commandMap = new HashMap<>();
  private final EpicGuard epicGuard;

  public CommandHandler(EpicGuard epicGuard) {
    this.epicGuard = epicGuard;

    this.commandMap.put("analyze", new AnalyzeCommand());
    this.commandMap.put("blacklist", new BlacklistCommand());
    this.commandMap.put("help", new HelpCommand());
    this.commandMap.put("reload", new ReloadCommand());
    this.commandMap.put("status", new StatusCommand());
    this.commandMap.put("whitelist", new WhitelistCommand());
    this.commandMap.put("save", new SaveCommand());
  }

  public void handleCommand(@NotNull String[] args, @NotNull Audience audience) {
    // No arguments provided - send the version message.
    if (args.length < 1) {
      audience.sendMessage(TextUtils.component("&#99ff00 You are running EpicGuard v" + VersionUtils.CURRENT_VERSION +
          " on " + this.epicGuard.platform().platformVersion()));
      audience.sendMessage(TextUtils.component("&#99ff00 Run &l/guard help &#99ff00to see available commands and statistics"));
      return;
    }

    var subCommand = this.commandMap.get(args[0]);
    if (subCommand == null) {
      audience.sendMessage(TextUtils.component(this.epicGuard.messages().command().prefix() + this.epicGuard.messages().command().unknownCommand()));
      return;
    }

    subCommand.execute(audience, args, this.epicGuard);
  }

  @NotNull
  public Collection<String> handleSuggestions(@NotNull String[] args) {
    // If no argument is specified, send all available subcommands.
    if (args.length <= 1) {
      return this.commandMap.keySet();
    }

    var subCommand = this.commandMap.get(args[0]);
    if (subCommand != null) {
      return subCommand.suggest(args, this.epicGuard);
    }
    return new ArrayList<>();
  }
}
