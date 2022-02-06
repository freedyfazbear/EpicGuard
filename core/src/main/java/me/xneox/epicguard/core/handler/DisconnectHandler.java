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

package me.xneox.epicguard.core.handler;

import java.util.UUID;
import me.xneox.epicguard.core.EpicGuard;
import me.xneox.epicguard.core.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for the PlayerQuit/Disconnect listeners.
 * Used for removing the {@link OnlineUser} instance from cache.
 */
public abstract class DisconnectHandler {
  private final EpicGuard epicGuard;

  public DisconnectHandler(EpicGuard epicGuard) {
    this.epicGuard = epicGuard;
  }

  /**
   * Handling the player who has been online but quit the server.
   *
   * @param uuid UUID of the (offline) player.
   */
  public void onDisconnect(@NotNull UUID uuid) {
    this.epicGuard.userManager().removeUser(uuid);
  }
}
