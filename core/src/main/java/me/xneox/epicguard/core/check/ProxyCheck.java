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

package me.xneox.epicguard.core.check;

import me.xneox.epicguard.core.EpicGuard;
import me.xneox.epicguard.core.check.AbstractCheck;
import me.xneox.epicguard.core.user.ConnectingUser;
import org.jetbrains.annotations.NotNull;

/**
 * This checks if the user is using a VPN or a proxy.
 * The detection logic is located in {@link me.xneox.epicguard.core.proxy.ProxyManager}
 */
public class ProxyCheck extends AbstractCheck {
  public ProxyCheck(EpicGuard epicGuard) {
    super(epicGuard, epicGuard.messages().disconnect().proxy(), epicGuard.config().proxyCheck().priority());
  }

  @Override
  public boolean isDetected(@NotNull ConnectingUser user) {
    return this.evaluate(this.epicGuard.config().proxyCheck().checkMode(),
        this.epicGuard.proxyManager().isProxy(user.address()));
  }
}
