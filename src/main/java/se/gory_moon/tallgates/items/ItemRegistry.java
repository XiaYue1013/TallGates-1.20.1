package se.gory_moon.tallgates.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import se.gory_moon.tallgates.blocks.BlockRegistry;
import se.gory_moon.tallgates.blocks.BlockTallGate;
import se.gory_moon.tallgates.lib.ModInfo;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MODID);

    // Vanilla 1.12.2 woods
    public static final RegistryObject<Item> OAK_TALL_GATE = register("oak_tall_gate", BlockRegistry.OAK_TALL_GATE);
    public static final RegistryObject<Item> SPRUCE_TALL_GATE = register("spruce_tall_gate", BlockRegistry.SPRUCE_TALL_GATE);
    public static final RegistryObject<Item> BIRCH_TALL_GATE = register("birch_tall_gate", BlockRegistry.BIRCH_TALL_GATE);
    public static final RegistryObject<Item> JUNGLE_TALL_GATE = register("jungle_tall_gate", BlockRegistry.JUNGLE_TALL_GATE);
    public static final RegistryObject<Item> ACACIA_TALL_GATE = register("acacia_tall_gate", BlockRegistry.ACACIA_TALL_GATE);
    public static final RegistryObject<Item> DARK_OAK_TALL_GATE = register("dark_oak_tall_gate", BlockRegistry.DARK_OAK_TALL_GATE);

    // Modern 1.20.1 additions
    public static final RegistryObject<Item> MANGROVE_TALL_GATE = register("mangrove_tall_gate", BlockRegistry.MANGROVE_TALL_GATE);
    public static final RegistryObject<Item> CHERRY_TALL_GATE = register("cherry_tall_gate", BlockRegistry.CHERRY_TALL_GATE);
    public static final RegistryObject<Item> BAMBOO_TALL_GATE = register("bamboo_tall_gate", BlockRegistry.BAMBOO_TALL_GATE);
    public static final RegistryObject<Item> CRIMSON_TALL_GATE = register("crimson_tall_gate", BlockRegistry.CRIMSON_TALL_GATE);
    public static final RegistryObject<Item> WARPED_TALL_GATE = register("warped_tall_gate", BlockRegistry.WARPED_TALL_GATE);

    private static RegistryObject<Item> register(String name, Supplier<BlockTallGate> blockSupplier) {
        return ITEMS.register(name, () -> new ItemTallGate(blockSupplier.get(), new Item.Properties()));
    }
}
