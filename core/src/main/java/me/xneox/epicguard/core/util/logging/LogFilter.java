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

package me.xneox.epicguard.core.util.logging;

import me.xneox.epicguard.core.EpicGuard;
import me.xneox.epicguard.core.util.ToggleState;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * This class adds a Log4J filter.
 * Filtered messages can be configured in the configuration.
 */
public class LogFilter extends AbstractFilter {
  private final EpicGuard epicGuard;

  public LogFilter(EpicGuard epicGuard) {
    this.epicGuard = epicGuard;
  }

  public void register() {
    var logger = (Logger) LogManager.getRootLogger();
    logger.addFilter(this);
  }

  @Override
  public Result filter(LogEvent event) {
    return event == null ? Result.NEUTRAL : isLoggable(event.getMessage().getFormattedMessage());
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
    return isLoggable(msg.getFormattedMessage());
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
    return isLoggable(msg);
  }

  @Override
  public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
    return msg == null ? Result.NEUTRAL : isLoggable(msg.toString());
  }

  private Result isLoggable(String message) {
    var state = this.epicGuard.config().consoleFilter().filterMode();
    if (state == ToggleState.ALWAYS || state == ToggleState.ATTACK && this.epicGuard.attackManager().isUnderAttack()) {
      for (String string : this.epicGuard.config().consoleFilter().filterMessages()) {
        if (message.contains(string)) {
          return Result.DENY;
        }
      }
    }
    return Result.NEUTRAL;
  }
}
