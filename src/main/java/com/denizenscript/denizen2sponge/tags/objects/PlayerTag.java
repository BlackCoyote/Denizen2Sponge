package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractTagObject {

    // <--[object]
    // @Type PlayerTag
    // @SubType EntityTag
    // @Group Entities
    // @Description Represents an online player on the server. Identified by UUID.
    // -->

    private Player internal;

    public PlayerTag(Player player) {
        internal = player;
    }

    public Player getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name PlayerTag.name
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the name of the player.
        // @Example "Bob" .name returns "Bob".
        // -->
        handlers.put("name", (dat, obj) -> new TextTag(((PlayerTag) obj).internal.getName()));
    }

    public static PlayerTag getFor(Action<String> error, String text) {
        try {
            Optional<Player> oplayer = Sponge.getServer().getPlayer(UUID.fromString(text));
            if (!oplayer.isPresent()) {
                error.run("Invalid PlayerTag UUID input!");
                return null;
            }
            return new PlayerTag(oplayer.get());
        }
        catch (IllegalArgumentException e) {
            Optional<Player> oplayer = Sponge.getServer().getPlayer(text);
            if (!oplayer.isPresent()) {
                error.run("Invalid PlayerTag named input!");
                return null;
            }
            return new PlayerTag(oplayer.get());
        }
    }

    public static PlayerTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof PlayerTag) ? (PlayerTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new EntityTag(internal).handle(data);
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }
}
