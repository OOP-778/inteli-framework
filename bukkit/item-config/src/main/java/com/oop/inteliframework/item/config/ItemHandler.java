package com.oop.inteliframework.item.config;

import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.banner.InteliBannerItem;
import com.oop.inteliframework.item.type.firework.InteliFireworkItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.item.type.item.InteliItemMeta;
import com.oop.inteliframework.item.type.skull.InteliSkullItem;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

public class ItemHandler implements PropertyHandler<InteliItem> {
    @Override
    public SerializedProperty toNode(@NonNull InteliItem object) {
        return null;
    }

    @Override
    public InteliItem fromNode(Node node) {
        return null;
    }

    @Override
    public Class<InteliItem> getObjectClass() {
        return InteliItem.class;
    }

    protected static class Serializer {
        protected void serializeBasic(@NonNull BaseParentNode node, @NonNull InteliItem item) {
            // Write material
            node.set("material", item.material().name().toLowerCase(Locale.ROOT));

            final InteliItemMeta meta = item.meta();
            if (meta == null) return;

            // Set the display name
            final String displayName = meta.name();
            if (displayName != null) {
                node.set("display-name", displayName);
            }

            // Set if glowing
            if (meta.glowing()) {
                node.set("glow", true);
            }

            // Set lore
            node.set("lore", meta.lore().raw());
        }

        protected void serializeSkull(@NonNull BaseParentNode node, @NonNull InteliSkullItem skullItem) {
            if (StringUtils.isBlank(skullItem.getTexture()) || skullItem.getTexture() == null) return;
            node.set("texture", skullItem.getTexture());
        }

        protected void serializeBanner(@NonNull BaseParentNode node, @NonNull InteliBannerItem bannerItem) {

        }

        protected void serializeLeatherArmor(@NonNull BaseParentNode node, @NonNull InteliBannerItem bannerItem) {

        }

        protected void serializeFirework(@NonNull BaseParentNode node, @NonNull InteliFireworkItem fireworkItem) {

        }

    }

}
