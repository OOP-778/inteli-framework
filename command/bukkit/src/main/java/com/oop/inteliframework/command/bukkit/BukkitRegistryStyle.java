package com.oop.inteliframework.command.bukkit;

import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import com.oop.inteliframework.command.style.RegistryStyle;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class BukkitRegistryStyle implements RegistryStyle {
    @Override
    public void handleError(@NotNull @NonNull CommandError[] errors, @NonNull CommandRegistry registry, @NonNull CommandParseHistory history) {
        BukkitCommandExecutor executor = history.getExecutor().as(BukkitCommandExecutor.class);
    }
}
