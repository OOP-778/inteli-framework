package com.oop.inteliframework.plugin.logger;

import com.oop.inteliframework.commons.util.StringFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Accessors(chain = true, fluent = true)
@Getter
public class InteliLogger {
  @Setter
  private @NonNull LoggerStyle style = LoggerStyle.defaultStyle();
  private String name;

  public InteliLogger(@NonNull String name) {
    this.name = name;
  }

  public void error(String message, Object... args) {
    log(
        StringUtils.replace(
            style.getErrorFormat(), "%message%", StringFormat.format(message, args)));
  }

  public void log(String message) {
    System.out.println(StringUtils.replace(message, "%name%", name) + IntelIColor.RESET);
  }
}
