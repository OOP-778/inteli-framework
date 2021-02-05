package com.oop.intelimenus.actionable;

import static com.oop.intelimenus.InteliMenus.getInteliMenus;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.bukkit.entity.Player;

public interface Actionable<T extends Actionable> extends Openable<T>, Moveable<T>, Viewable,
    Parentable<T>, Refreshable<T> {

    /*
    Get current action of the menu
    If action is not set it will return NONE
    */
    MenuAction getCurrentAction();

    /*
    Set current action
    */
    void setCurrentAction(MenuAction action);

    default void openAction(Runnable callback) {
        refreshAction(callback);
    }

    default void refreshAction(Runnable callback) {
        executeAction(MenuAction.REFRESH, callback);
    }

    default void closeAction(Runnable callback) {
        executeAction(MenuAction.CLOSE, callback);
    }

    default void returnAction(Runnable callback) {
        executeAction(MenuAction.RETURN, callback);
    }

    default void moveAction(@NonNull T where, Runnable callback) {
        setMoving(where);
        if (!where.getParent().isPresent()) {
            where.setParent(this);
        }

        executeAction(MenuAction.MOVE, callback);
    }

    default void executeAction(@NonNull MenuAction action, Runnable callback) {
        Player player = getViewer().orElseThrow(() -> new IllegalStateException(
            "Failed to execute action on menu, cause player isn't assigned!"));
        if (getCurrentAction() != MenuAction.NONE) {
            return;
        }

        setCurrentAction(action);
        if (action == MenuAction.REFRESH) {
            open((T) this, () -> {
                setCurrentAction(MenuAction.NONE);
                if (callback != null) {
                    callback.run();
                }
            });

        } else if (action == MenuAction.MOVE) {
            Preconditions
                .checkArgument(getCurrentMoving().isPresent(), "Moving object is not set!");
            open(getCurrentMoving().get(), () -> {
                setCurrentAction(MenuAction.NONE);
                setMoving(null);
                if (callback != null) {
                    callback.run();
                }
            });

        } else if (action == MenuAction.CLOSE) {
            getInteliMenus().getUtil().ensureSync(() -> {
                player.closeInventory();
                setCurrentAction(MenuAction.NONE);
                if (callback != null) {
                    callback.run();
                }
            });

        } else if (action == MenuAction.RETURN) {
            if (!getParent().isPresent()) {
                setCurrentAction(MenuAction.NONE);
                return;
            }

            open(getParent().get(), () -> {
                setCurrentAction(MenuAction.NONE);
                if (callback != null) {
                    callback.run();
                }
            });
        }
    }

    @Override
    default void refreshAction() {
        refreshAction(null);
    }
}
