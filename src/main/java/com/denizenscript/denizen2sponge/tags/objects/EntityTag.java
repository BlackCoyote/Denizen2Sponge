package com.denizenscript.denizen2sponge.tags.objects;

import com.denizenscript.denizen2core.tags.AbstractTagObject;
import com.denizenscript.denizen2core.tags.TagData;
import com.denizenscript.denizen2core.tags.objects.NullTag;
import com.denizenscript.denizen2core.tags.objects.TextTag;
import com.denizenscript.denizen2core.utilities.Action;
import com.denizenscript.denizen2core.utilities.Function2;
import com.denizenscript.denizen2sponge.utilities.DataKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EntityTag extends AbstractTagObject {

    // <--[object]
    // @Type EntityTag
    // @SubType TextTag
    // @Group Entities
    // @Description Represents an entity on the server. Identified by UUID.
    // -->

    private Entity internal;

    public EntityTag(Entity ent) {
        internal = ent;
    }

    public Entity getInternal() {
        return internal;
    }

    public final static HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> handlers = new HashMap<>();

    static {
        // <--[tag]
        // @Name EntityTag.uuid
        // @Updated 2016/08/26
        // @Group Identification
        // @ReturnType TextTag
        // @Returns the unique ID of the entity.
        // -->
        handlers.put("uuid", (dat, obj) -> new TextTag(((EntityTag) obj).internal.getUniqueId().toString()));
        // <--[tag]
        // @Name EntityTag.location
        // @Updated 2016/08/26
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the location of the entity.
        // -->
        handlers.put("location", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getLocation()));
        // <--[tag]
        // @Name EntityTag.velocity
        // @Updated 2016/08/26
        // @Group General Information
        // @ReturnType LocationTag
        // @Returns the velocity of the entity (The vector it is is currently moving in).
        // -->
        handlers.put("velocity", (dat, obj) -> new LocationTag(((EntityTag) obj).internal.getVelocity()));
        // <--[tag]
        // @Name EntityTag.data
        // @Updated 2016/08/28
        // @Group General Information
        // @ReturnType MapTag
        // @Returns a list of all data keys and their values for the entity.
        // -->
        handlers.put("data", (dat, obj) -> DataKeys.getAllKeys(((EntityTag) obj).internal));
        // <--[tag]
        // @Name EntityTag.get[<TextTag>]
        // @Updated 2016/08/28
        // @Group General Information
        // @ReturnType Dynamic
        // @Returns the value of the specified key on the entity.
        // -->
        handlers.put("get", (dat, obj) -> {
            String keyName = dat.getNextModifier().toString();
            Key key = DataKeys.getKeyForName(keyName);
            if (key == null) {
                dat.error.run("Invalid key '" + keyName + "'!");
                return new NullTag();
            }
            return DataKeys.getValue(((EntityTag) obj).internal, key, dat.error);
        });
    }

    public static EntityTag getFor(Action<String> error, String text) {
        UUID id = UUID.fromString(text);
        for (World world : Sponge.getServer().getWorlds()) {
            Optional<Entity> e = world.getEntity(id);
            if (e.isPresent()) {
                return new EntityTag(e.get());
            }
        }
        error.run("Invalid EntityTag UUID input!");
        return null;
    }

    public static EntityTag getFor(Action<String> error, AbstractTagObject text) {
        return (text instanceof EntityTag) ? (EntityTag) text : getFor(error, text.toString());
    }

    @Override
    public HashMap<String, Function2<TagData, AbstractTagObject, AbstractTagObject>> getHandlers() {
        return handlers;
    }

    @Override
    public AbstractTagObject handleElseCase(TagData data) {
        return new TextTag(toString()).handle(data);
    }

    @Override
    public String toString() {
        return internal.getUniqueId().toString();
    }
}
