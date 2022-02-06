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

package me.xneox.epicguard.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.xneox.epicguard.core.EpicGuard;
import me.xneox.epicguard.core.Platform;
import me.xneox.epicguard.core.util.VersionUtils;
import me.xneox.epicguard.velocity.listener.DisconnectListener;
import me.xneox.epicguard.velocity.listener.PlayerSettingsListener;
import me.xneox.epicguard.velocity.listener.PostLoginListener;
import me.xneox.epicguard.velocity.listener.PreLoginListener;
import me.xneox.epicguard.velocity.listener.ServerPingListener;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Plugin(
    id = "epicguard",
    name = "EpicGuard",
    version = VersionUtils.CURRENT_VERSION,
    description = "Bot protection system for Minecraft servers.",
    url = "https://github.com/xxneox/EpicGuard",
    authors = "neox")
public class EpicGuardVelocity implements Platform {
  private final ProxyServer server;
  private final Logger logger;

  private EpicGuard epicGuard;

  @Inject
  public EpicGuardVelocity(ProxyServer server, Logger logger) {
    this.server = server;
    this.logger = logger;
  }

  @Subscribe
  public void onEnable(ProxyInitializeEvent e) {
    this.epicGuard = new EpicGuard(this);

    var commandManager = this.server.getCommandManager();
    var meta = commandManager
        .metaBuilder("epicguard")
        .aliases("guard", "epicguardvelocity", "guardvelocity")
        .build();

    commandManager.register(meta, new VelocityCommandHandler(this.epicGuard));

    var eventManager = this.server.getEventManager();
    eventManager.register(this, new PostLoginListener(this.epicGuard));
    eventManager.register(this, new PreLoginListener(this.epicGuard));
    eventManager.register(this, new DisconnectListener(this.epicGuard));
    eventManager.register(this, new ServerPingListener(this.epicGuard));
    eventManager.register(this, new PlayerSettingsListener(this.epicGuard));
  }

  @Subscribe
  public void onDisable(ProxyShutdownEvent e) {
    this.epicGuard.shutdown();
  }

  @Override
  public @NotNull String platformVersion() {
    return this.server.getVersion().toString();
  }

  @Override
  public @NotNull Logger logger() {
    return this.logger;
  }

  @Override
  public Audience audience(@NotNull UUID uuid) {
    return this.server.getPlayer(uuid).orElse(null);
  }

  @Override
  public void disconnectUser(@NotNull UUID uuid, @NotNull Component message) {
    this.server.getPlayer(uuid).ifPresent(player -> player.disconnect(message));
  }

  @Override
  public void runTaskLater(@NotNull Runnable task, long seconds) {
    this.server.getScheduler().buildTask(this, task).delay(seconds, TimeUnit.SECONDS).schedule();
  }

  @Override
  public void scheduleRepeatingTask(@NotNull Runnable task, long seconds) {
    this.server.getScheduler().buildTask(this, task).repeat(seconds, TimeUnit.SECONDS).schedule();
  }
}
