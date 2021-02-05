package com.oop.intelimenus.actionable;

import java.util.Optional;
import org.bukkit.entity.Player;

public interface Viewable {

    Optional<Player> getViewer();
}
