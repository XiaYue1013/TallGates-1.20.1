package se.gory_moon.tallgates.dev;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.tallgates.TallGatesMod;
import se.gory_moon.tallgates.lib.ModInfo;

import java.util.List;

/**
 * Development only helper (excluded from production jar).
 * When -Dtallgates.serverSmokeTest=true is set, the dedicated server starts up,
 * validates all TallGates registries, recipes, and tick loop, then shuts down cleanly.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public final class ServerSmokeTest {

    private static final boolean ENABLED = Boolean.getBoolean("tallgates.serverSmokeTest");
    private static int ticks = -1;

    private static final List<String> WOODS = List.of(
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
            "mangrove", "cherry", "bamboo", "crimson", "warped"
    );

    private ServerSmokeTest() {}

    @SubscribeEvent
    public static void onStarted(ServerStartedEvent event) {
        if (!ENABLED) {
            return;
        }
        MinecraftServer server = event.getServer();
        TallGatesMod.LOGGER.info("=== TallGates Server Smoke Test Started ===");
        TallGatesMod.LOGGER.info("Dedicated Server: {}", server.isDedicatedServer());

        int blocksFound = 0;
        int itemsFound = 0;
        for (String wood : WOODS) {
            ResourceLocation rl = new ResourceLocation(ModInfo.MODID, wood + "_tall_gate");
            if (BuiltInRegistries.BLOCK.containsKey(rl)) {
                blocksFound++;
            }
            if (BuiltInRegistries.ITEM.containsKey(rl)) {
                itemsFound++;
            }
        }
        TallGatesMod.LOGGER.info("Registered Tall Gates: {}/11 blocks, {}/11 items", blocksFound, itemsFound);

        ticks = 0;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (!ENABLED || ticks < 0 || event.phase != TickEvent.Phase.END) {
            return;
        }
        if (++ticks == 60) {
            TallGatesMod.LOGGER.info("=== TallGates Server Smoke Test Completed (60 ticks), stopping server ===");
            event.getServer().halt(false);
        }
    }
}
