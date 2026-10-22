package se.gory_moon.tallgates;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import se.gory_moon.tallgates.blocks.BlockRegistry;
import se.gory_moon.tallgates.items.ItemRegistry;
import se.gory_moon.tallgates.lib.ModInfo;

@Mod(ModInfo.MODID)
public class TallGatesMod {
    public static final Logger LOGGER = LogUtils.getLogger();

    public TallGatesMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        LOGGER.info("TallGates initialized.");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS || event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            ItemRegistry.ITEMS.getEntries().forEach(entry -> event.accept(entry.get()));
        }
    }
}
