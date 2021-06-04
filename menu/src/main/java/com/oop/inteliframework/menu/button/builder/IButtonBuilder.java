package com.oop.inteliframework.menu.button.builder;

import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.menu.animation.Animation;
import com.oop.inteliframework.menu.animation.AnimationComponent;
import com.oop.inteliframework.menu.attribute.Attribute;
import com.oop.inteliframework.menu.attribute.AttributeComponent;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.button.state.StateComponent;
import com.oop.inteliframework.menu.trigger.TriggerComponent;
import com.oop.inteliframework.menu.trigger.types.ButtonClickTrigger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IButtonBuilder {

  private IButton button = new IButton();

  public static IButtonBuilder of(ItemStack itemStack) {
    return new IButtonBuilder().item(itemStack);
  }

  public static IButtonBuilder of() {
    return new IButtonBuilder();
  }

  public static IButtonBuilder of(Supplier<ItemStack> displaySupplier) {
    IButtonBuilder buttonBuilder = new IButtonBuilder();
    buttonBuilder.button.setCurrentItem(displaySupplier);
    return buttonBuilder;
  }

  public IButtonBuilder item(ItemStack itemStack) {
    button.setCurrentItem(() -> itemStack);
    return this;
  }

  public IButtonBuilder addAttribute(Attribute attribute) {
    button.applyComponent(AttributeComponent.class, c -> c.addAttribute(attribute));
    return this;
  }

  public IButtonBuilder clickTrigger(Consumer<ButtonClickTrigger> onClick) {
    button.applyComponent(
        TriggerComponent.class,
        c -> c.addTrigger(ButtonClickTrigger.class, trigger -> trigger.onTrigger(onClick)));
    return this;
  }

  public IButtonBuilder addState(String stateId, AbstractInteliItem<?, ?> item) {
    button.applyComponent(
        StateComponent.class,
        stateComponent -> {
          stateComponent.addState(stateId, item);
        });
    return this;
  }

  public IButtonBuilder addState(String stateId, ItemStack itemStack) {
    return addState(stateId, new InteliItem(itemStack));
  }

  public IButtonBuilder addState(String stateId, Material material) {
    return addState(stateId, new ItemStack(material));
  }

  public IButtonBuilder addAnimation(Consumer<Animation<IButton>> animation) {
    Animation<IButton> animation1 = new Animation<>();
    animation.accept(animation1);
    return addAnimation(animation1);
  }

  public IButtonBuilder addAnimation(Animation<IButton> animation) {
    button.applyComponent(AnimationComponent.class, comp -> comp.add(animation));
    return this;
  }

  public IButton toButton() {
    return button;
  }

  public IButtonBuilder of(IButton button) {
    IButtonBuilder buttonBuilder = new IButtonBuilder();
    buttonBuilder.button = button;
    return buttonBuilder;
  }
}
