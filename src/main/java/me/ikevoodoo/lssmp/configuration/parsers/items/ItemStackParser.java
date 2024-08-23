package me.ikevoodoo.lssmp.configuration.parsers.items;

import me.ikevoodoo.helix.api.Helix;
import me.ikevoodoo.helix.api.config.parsing.TypeParser;
import me.ikevoodoo.helix.api.items.HelixItem;
import me.ikevoodoo.helix.api.items.display.ItemDisplayData;
import me.ikevoodoo.helix.api.namespaced.UniqueIdentifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class ItemStackParser implements TypeParser<String, ItemStack> {

    private final Function<HelixItem, ItemDisplayData> displayDataFunction;

    public ItemStackParser(Function<HelixItem, ItemDisplayData> displayDataFunction) {
        this.displayDataFunction = displayDataFunction;
    }

    @Override
    public @NotNull Class<ItemStack> complexType() {
        return ItemStack.class;
    }

    @Override
    public @NotNull Class<String> simpleType() {
        return String.class;
    }

    @Override
    public @NotNull ItemStack deserialize(String s) {
        var key = UniqueIdentifier.parse(s);
        var itemRegistry = Helix.items();

        var custom = itemRegistry.getItem(key);
        if (custom != null) {
            return itemRegistry.createItem(key, this.displayDataFunction.apply(custom));
        }

        var material = Material.matchMaterial(s);
        if (material == null) {
            throw new IllegalStateException("Unknown material " + s);
        }

        return new ItemStack(material);
    }

    @Override
    public @NotNull String serialize(@NotNull ItemStack stack) {
        var itemRegistry = Helix.items();
        var custom = itemRegistry.getItemFromStack(stack);
        if (custom != null) {
            var key = itemRegistry.getKey(custom.getItem());

            return key.toString();
        }

        return stack.getType().getKey().toString();
    }

    @Override
    public List<String> getSimpleExamples() {
        return List.of("minecraft:stone", "stick", "diamond");
    }
}
