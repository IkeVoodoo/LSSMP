package me.ikevoodoo.lssmp.feature;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface PlayerChatCallback {

    void add(Player player, Consumer<String> callback);

}
