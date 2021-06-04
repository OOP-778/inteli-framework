package com.oop.inteliframework.message.actionbar;

import com.oop.inteliframework.message.ComponentUtil;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.message.api.InteliMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class InteliActionBarMessage implements InteliMessage<InteliActionBarMessage> {

  @Setter @Getter private Component content;

  @Override
  public Component toComponent() {
    return content;
  }

  @Override
  public void send(Audience receiver) {
    receiver.sendActionBar(content);
  }

  @Override
  public InteliActionBarMessage clone() {
    return new InteliActionBarMessage(content);
  }

  @Override
  public InteliActionBarMessage replace(Replacer replacer) {
    this.content = ComponentUtil.colorizeFromBukkit(replacer.accept(ComponentUtil.contentFromComponent(content)));
    return this;
  }
}
