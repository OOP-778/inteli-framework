package com.oop.intelimenus.config;

import com.oop.intelimenus.button.IButton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ConfigButton {

    private IButton button;
    private String letter;
    private String identifier;
    public ConfigButton() {
    }

    public IButton build() {
        return button;
    }
}
