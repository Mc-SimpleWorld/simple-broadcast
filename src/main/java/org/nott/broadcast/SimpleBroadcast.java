package org.nott.broadcast;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.StringUtil;
import org.nott.executor.SbExecutor;
import org.nott.global.GlobalFactory;
import org.nott.utils.SwUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public final class SimpleBroadcast extends JavaPlugin {

    public static YamlConfiguration CONFIG;
    public static YamlConfiguration MESSAGE;
    public static BukkitScheduler SCHEDULER;

    private static BukkitAudiences adventure;

    private Integer msgIndex = 0;

    public @NonNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        adventure = BukkitAudiences.create(this);
        initConfigYml();
        SCHEDULER = this.getServer().getScheduler();
        Objects.requireNonNull(this.getCommand("simplebroadcast")).setExecutor(new SbExecutor(this));
        SCHEDULER.scheduleSyncRepeatingTask(this, () -> {
            this.printBcMessage();
//            System.out.println(123);
//            SwUtil.log("123");
        }, 300L, CONFIG.getLong("broadcast.interval", 5) * 100L);

    }

    private void printBcMessage() {
        ConfigurationSection section = MESSAGE.getConfigurationSection("auto-broadcasts");
        if (SwUtil.isNull(section)) {
            return;
        }
        Set<String> keys = section.getKeys(false);
        if (SwUtil.isEmpty(keys)) {
            return;
        }
        List<String> keyList = keys.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
        if (SwUtil.isEmpty(onlinePlayers)) return;
        Integer index = getMsgIndex();
        onlinePlayers.forEach(player -> {
            String msgKey = keyList.get(index);
            String fullKey = msgKey + ".message";
            Audience audience = adventure.player(player);
            List<String> msgs = section.getStringList(fullKey);
            if (SwUtil.isEmpty(msgs)) {
                return;
            }
            for (String msg : msgs) {
                String text = PlaceholderAPI.setBracketPlaceholders(player, msg);
                audience.sendMessage(MiniMessage.miniMessage().deserialize(text));
            }
            int nextIndex = index + 1;
            if (nextIndex == keyList.size()) {
                setMsgIndex(0);
            }else{
                setMsgIndex(nextIndex);
            }
        });
    }

    public void initConfigYml() {
        saveConfig();
        CONFIG = (YamlConfiguration) this.getConfig();
        YamlConfiguration message = new YamlConfiguration();
        String path = this.getDataFolder() + File.separator + GlobalFactory.MESSAGE_YML;
        File file = new File(path);
        if (!file.exists()) {
            this.saveResource(GlobalFactory.MESSAGE_YML, false);
            try {
                message.load(Objects.requireNonNull(this.getTextResource(GlobalFactory.MESSAGE_YML)));
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                message.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        MESSAGE = message;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        SwUtil.log(GlobalFactory.MESSAGE_YML + "disabled");
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
