package org.nott.executor;

import lombok.Data;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nott.broadcast.SimpleBroadcast;
import org.nott.utils.SwUtil;

/**
 * @author Nott
 * @date 2024-10-15
 */
@Data
public class SbExecutor implements CommandExecutor {

    private SimpleBroadcast plugin;

    public SbExecutor(SimpleBroadcast plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1 && "reload".equals(args[0])) {
            if (commandSender.isOp()) {
                getPlugin().initConfigYml();
                boolean isConsole = "console".equalsIgnoreCase(commandSender.getName());
                if (isConsole) {
                    SwUtil.log(SimpleBroadcast.MESSAGE.getString("common.reloaded"));
                }else {
                    Audience audience = plugin.adventure().player((Player) commandSender);
                    TextComponent component = Component.text(SimpleBroadcast.MESSAGE.getString("common.reloaded"))
                            .color(NamedTextColor.YELLOW);
                    audience.sendMessage(component);
                }
                return true;
            } else {
                Audience audience = plugin.adventure().player((Player) commandSender);
                TextComponent component = Component.text(SimpleBroadcast.MESSAGE.getString("common.not_per"))
                        .color(NamedTextColor.DARK_RED);
                audience.sendMessage(component);
                return true;
            }
        }
        return false;
    }
}
