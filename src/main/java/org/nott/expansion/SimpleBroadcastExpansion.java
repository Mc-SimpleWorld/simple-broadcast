package org.nott.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;
import org.nott.global.GlobalFactory;

/**
 * @author Nott
 * @date 2024-10-15
 */
public class SimpleBroadcastExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return GlobalFactory.IDENTIFIER;
    }

    @Override
    public @NotNull String getAuthor() {
        return GlobalFactory.AUTHOR;
    }

    @Override
    public @NotNull String getVersion() {
        return GlobalFactory.VERSION;
    }
}
