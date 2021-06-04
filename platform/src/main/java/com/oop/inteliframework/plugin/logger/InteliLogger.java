package com.oop.inteliframework.plugin.logger;

import com.oop.inteliframework.commons.util.StringFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.logging.Logger;

@Accessors(chain = true, fluent = true)
@Getter
public class InteliLogger {

  private final Logger logger;
  @Setter private @NonNull LoggerStyle style = LoggerStyle.defaultStyle();
  private String name;

  public InteliLogger(@NonNull String name) {
    this.name = name;
    this.logger = Logger.getLogger(name);
  }

  public void error(String message, Object... args) {
    log(
        StringUtils.replace(
            style.getErrorFormat(), "%message%", StringFormat.format(message, args)));
  }

  public void info(String message, Object... args) {
    log(
        StringUtils.replace(
            style.getInfoFormat(), "%message%", StringFormat.format(message, args)));
  }

  public void debug(String message, Object... args) {
    log(
        StringUtils.replace(
            style.getDebugFormat(), "%message%", StringFormat.format(message, args)));
  }

  public void log(String message) {
    logger.info(StringUtils.replace(message, "%name%", name) + IntelIColor.RESET);
  }
}
