package se.gory_moon.tallgates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.gory_moon.tallgates.lib.ModInfo;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TallGatesTest {

    private static final List<String> WOODS = List.of(
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
            "mangrove", "cherry", "bamboo", "crimson", "warped"
    );

    @Test
    public void testModInfo() {
        Assertions.assertEquals("tallgates", ModInfo.MODID);
        Assertions.assertEquals("TallGates", ModInfo.MOD_NAME);
    }

    @Test
    public void testBlockstatesExistAndValid() throws Exception {
        File blockstateDir = new File("src/main/resources/assets/tallgates/blockstates");
        Assertions.assertTrue(blockstateDir.exists(), "Blockstate dir must exist");

        for (String wood : WOODS) {
            File f = new File(blockstateDir, wood + "_tall_gate.json");
            Assertions.assertTrue(f.exists(), "Blockstate file missing: " + f.getName());

            try (FileReader reader = new FileReader(f, StandardCharsets.UTF_8)) {
                JsonElement el = JsonParser.parseReader(reader);
                Assertions.assertTrue(el.isJsonObject());
                JsonObject obj = el.getAsJsonObject();
                Assertions.assertTrue(obj.has("variants"), "Missing variants in " + f.getName());
                JsonObject variants = obj.getAsJsonObject("variants");
                // 4 directions * 2 halves * 2 in_wall * 2 open = 32 variants
                Assertions.assertEquals(32, variants.size(), "Variant count mismatch in " + f.getName());
            }
        }
    }

    @Test
    public void testBlockModelsExistAndValid() throws Exception {
        File modelDir = new File("src/main/resources/assets/tallgates/models/block");
        Assertions.assertTrue(modelDir.exists(), "Block model dir must exist");

        String[] templates = {
                "template_tall_gate_lower_closed.json",
                "template_tall_gate_lower_open.json",
                "template_tall_gate_upper_closed.json",
                "template_tall_gate_upper_open.json"
        };
        for (String tmpl : templates) {
            File f = new File(modelDir, tmpl);
            Assertions.assertTrue(f.exists(), "Template model missing: " + tmpl);
        }

        String[] types = {"lower_closed", "lower_open", "upper_closed", "upper_open"};
        for (String wood : WOODS) {
            for (String type : types) {
                File f = new File(modelDir, wood + "_tall_gate_" + type + ".json");
                Assertions.assertTrue(f.exists(), "Wood block model missing: " + f.getName());
                try (FileReader reader = new FileReader(f, StandardCharsets.UTF_8)) {
                    JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                    Assertions.assertTrue(obj.has("parent"));
                    Assertions.assertTrue(obj.has("textures"));
                    JsonObject tex = obj.getAsJsonObject("textures");
                    Assertions.assertEquals("minecraft:block/" + wood + "_planks", tex.get("texture").getAsString());
                }
            }
        }
    }

    @Test
    public void testItemModelsAndTexturesExist() {
        File itemModelDir = new File("src/main/resources/assets/tallgates/models/item");
        File itemTexDir = new File("src/main/resources/assets/tallgates/textures/item");

        Assertions.assertTrue(itemModelDir.exists());
        Assertions.assertTrue(itemTexDir.exists());

        for (String wood : WOODS) {
            File modelFile = new File(itemModelDir, wood + "_tall_gate.json");
            File texFile = new File(itemTexDir, wood + "_tall_gate.png");

            Assertions.assertTrue(modelFile.exists(), "Missing item model: " + modelFile.getName());
            Assertions.assertTrue(texFile.exists(), "Missing item texture: " + texFile.getName());
        }
    }

    @Test
    public void testRecipesExistAndValid() throws Exception {
        File recipeDir = new File("src/main/resources/data/tallgates/recipes");
        Assertions.assertTrue(recipeDir.exists(), "Recipe dir must exist");

        for (String wood : WOODS) {
            File craftFile = new File(recipeDir, wood + "_tall_gate.json");
            Assertions.assertTrue(craftFile.exists(), "Craft recipe missing: " + craftFile.getName());
            try (FileReader reader = new FileReader(craftFile, StandardCharsets.UTF_8)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                Assertions.assertEquals("minecraft:crafting_shaped", obj.get("type").getAsString());
                Assertions.assertEquals("tallgates:" + wood + "_tall_gate", obj.getAsJsonObject("result").get("item").getAsString());
            }

            File uncraftFile = new File(recipeDir, wood + "_tall_single.json");
            Assertions.assertTrue(uncraftFile.exists(), "Uncraft recipe missing: " + uncraftFile.getName());
            try (FileReader reader = new FileReader(uncraftFile, StandardCharsets.UTF_8)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                Assertions.assertEquals("minecraft:crafting_shapeless", obj.get("type").getAsString());
                Assertions.assertEquals("minecraft:" + wood + "_fence_gate", obj.getAsJsonObject("result").get("item").getAsString());
                Assertions.assertEquals(2, obj.getAsJsonObject("result").get("count").getAsInt());
            }
        }
    }

    @Test
    public void testAdvancementsExistAndValid() throws Exception {
        File advDir = new File("src/main/resources/data/tallgates/advancements/recipes");
        Assertions.assertTrue(advDir.exists(), "Advancement dir must exist");

        for (String wood : WOODS) {
            File craftAdv = new File(advDir, wood + "_tall_fence_gate.json");
            File uncraftAdv = new File(advDir, "reverse_" + wood + "_fence_gate.json");

            Assertions.assertTrue(craftAdv.exists(), "Craft advancement missing: " + craftAdv.getName());
            Assertions.assertTrue(uncraftAdv.exists(), "Uncraft advancement missing: " + uncraftAdv.getName());

            try (FileReader reader = new FileReader(craftAdv, StandardCharsets.UTF_8)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                Assertions.assertEquals("minecraft:recipes/root", obj.get("parent").getAsString());
            }
        }
    }

    @Test
    public void testLootTablesExistAndValid() throws Exception {
        File lootDir = new File("src/main/resources/data/tallgates/loot_tables/blocks");
        Assertions.assertTrue(lootDir.exists(), "Loot tables dir must exist");

        for (String wood : WOODS) {
            File f = new File(lootDir, wood + "_tall_gate.json");
            Assertions.assertTrue(f.exists(), "Loot table missing: " + f.getName());
            try (FileReader reader = new FileReader(f, StandardCharsets.UTF_8)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                Assertions.assertEquals("minecraft:block", obj.get("type").getAsString());
                Assertions.assertTrue(obj.has("pools"));
            }
        }
    }

    @Test
    public void testTagsExistAndValid() throws Exception {
        File blockFenceGates = new File("src/main/resources/data/minecraft/tags/blocks/fence_gates.json");
        File blockWoodenFenceGates = new File("src/main/resources/data/minecraft/tags/blocks/wooden_fence_gates.json");
        File blockAxe = new File("src/main/resources/data/minecraft/tags/blocks/mineable/axe.json");
        File itemFenceGates = new File("src/main/resources/data/minecraft/tags/items/fence_gates.json");
        File itemWoodenFenceGates = new File("src/main/resources/data/minecraft/tags/items/wooden_fence_gates.json");

        Assertions.assertTrue(blockFenceGates.exists());
        Assertions.assertTrue(blockWoodenFenceGates.exists());
        Assertions.assertTrue(blockAxe.exists());
        Assertions.assertTrue(itemFenceGates.exists());
        Assertions.assertTrue(itemWoodenFenceGates.exists());

        try (FileReader reader = new FileReader(blockFenceGates, StandardCharsets.UTF_8)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (String wood : WOODS) {
                Assertions.assertTrue(obj.getAsJsonArray("values").toString().contains("tallgates:" + wood + "_tall_gate"));
            }
        }
    }

    @Test
    public void testLanguageFiles() throws Exception {
        File enFile = new File("src/main/resources/assets/tallgates/lang/en_us.json");
        File zhFile = new File("src/main/resources/assets/tallgates/lang/zh_tw.json");

        Assertions.assertTrue(enFile.exists(), "en_us.json missing");
        Assertions.assertTrue(zhFile.exists(), "zh_tw.json missing");

        try (FileReader reader = new FileReader(enFile, StandardCharsets.UTF_8)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (String wood : WOODS) {
                Assertions.assertTrue(obj.has("block.tallgates." + wood + "_tall_gate"));
                Assertions.assertTrue(obj.has("item.tallgates." + wood + "_tall_gate"));
            }
        }

        try (FileReader reader = new FileReader(zhFile, StandardCharsets.UTF_8)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            for (String wood : WOODS) {
                Assertions.assertTrue(obj.has("block.tallgates." + wood + "_tall_gate"));
                Assertions.assertTrue(obj.has("item.tallgates." + wood + "_tall_gate"));
            }
        }
    }
}
