package se.gory_moon.tallgates.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import se.gory_moon.tallgates.lib.ModInfo;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MODID);

    // Vanilla 1.12.2 woods
    public static final RegistryObject<BlockTallGate> OAK_TALL_GATE = register("oak_tall_gate", WoodType.OAK);
    public static final RegistryObject<BlockTallGate> SPRUCE_TALL_GATE = register("spruce_tall_gate", WoodType.SPRUCE);
    public static final RegistryObject<BlockTallGate> BIRCH_TALL_GATE = register("birch_tall_gate", WoodType.BIRCH);
    public static final RegistryObject<BlockTallGate> JUNGLE_TALL_GATE = register("jungle_tall_gate", WoodType.JUNGLE);
    public static final RegistryObject<BlockTallGate> ACACIA_TALL_GATE = register("acacia_tall_gate", WoodType.ACACIA);
    public static final RegistryObject<BlockTallGate> DARK_OAK_TALL_GATE = register("dark_oak_tall_gate", WoodType.DARK_OAK);

    // Modern 1.20.1 additions
    public static final RegistryObject<BlockTallGate> MANGROVE_TALL_GATE = register("mangrove_tall_gate", WoodType.MANGROVE);
    public static final RegistryObject<BlockTallGate> CHERRY_TALL_GATE = register("cherry_tall_gate", WoodType.CHERRY);
    public static final RegistryObject<BlockTallGate> BAMBOO_TALL_GATE = register("bamboo_tall_gate", WoodType.BAMBOO);
    public static final RegistryObject<BlockTallGate> CRIMSON_TALL_GATE = register("crimson_tall_gate", WoodType.CRIMSON);
    public static final RegistryObject<BlockTallGate> WARPED_TALL_GATE = register("warped_tall_gate", WoodType.WARPED);

    private static RegistryObject<BlockTallGate> register(String name, WoodType woodType) {
        return BLOCKS.register(name, () -> new BlockTallGate(
                BlockBehaviour.Properties.of()
                        .strength(2.0F, 3.0F)
                        .sound(woodType.soundType())
                        .noOcclusion(),
                woodType
        ));
    }
}
