package local.anglingqa;

import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Local test instrumentation only. This companion is never included in Angling's release. */
@Mod("angling_client_qa")
public final class AnglingClientQa {
    @Mod.EventBusSubscriber(modid = "angling_client_qa", value = Dist.CLIENT)
    public static final class Client {
        private static int ticks;
        private static String lastRequest = "";
        private static String captureName;
        private static int captureDelay;

        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || ++ticks % 10 != 0) return;
            MinecraftClient client = MinecraftClient.getInstance();
            Path request = client.runDirectory.toPath().resolve("qa-request.txt");
            try {
                if (!Files.exists(request)) return;
                String text = Files.readString(request).trim();
                if (text.equals(lastRequest) || text.isEmpty()) return;
                lastRequest = text;
                String[] parts = text.split("\\s+", 2);
                String action = parts[0];
                String name = parts.length > 1 ? parts[1].replaceAll("[^A-Za-z0-9_-]", "_") : action;
                System.out.println("ANGLING_CLIENT_QA request " + text);
                if (action.equals("snapshot") && client.world != null && client.player != null) {
                    JsonObject result = new JsonObject();
                    JsonArray entities = new JsonArray();
                    for (Entity entity : client.world.getEntities()) {
                        JsonObject entry = new JsonObject();
                        entry.addProperty("type", Registries.ENTITY_TYPE.getId(entity.getType()).toString());
                        entry.addProperty("nbt", entity.writeNbt(new NbtCompound()).toString());
                        entities.add(entry);
                    }
                    result.add("entities", entities);
                    JsonArray blockEntities = new JsonArray();
                    BlockPos center = client.player.getBlockPos();
                    for (BlockPos pos : BlockPos.iterate(center.add(-24, -12, -24), center.add(24, 12, 24))) {
                        BlockEntity entity = client.world.getBlockEntity(pos);
                        if (entity == null) continue;
                        JsonObject entry = new JsonObject();
                        entry.addProperty("state", entity.getCachedState().toString());
                        entry.addProperty("nbt", entity.createNbtWithId().toString());
                        blockEntities.add(entry);
                    }
                    result.add("blockEntities", blockEntities);
                    JsonArray inventory = new JsonArray();
                    for (int slot = 0; slot < client.player.getInventory().size(); slot++) {
                        JsonObject entry = new JsonObject();
                        entry.addProperty("slot", slot);
                        entry.addProperty("nbt", client.player.getInventory().getStack(slot).writeNbt(new NbtCompound()).toString());
                        inventory.add(entry);
                    }
                    result.add("inventory", inventory);
                    Files.writeString(client.runDirectory.toPath().resolve("qa-state-" + name + ".json"),
                            new GsonBuilder().setPrettyPrinting().create().toJson(result));
                    System.out.println("ANGLING_CLIENT_QA snapshot completed " + name);
                } else if (action.equals("reload")) {
                    client.reloadResources().thenRun(() -> client.execute(() -> {
                        queueCapture(name);
                        System.out.println("ANGLING_CLIENT_QA resource reload completed");
                    }));
                } else if (action.equals("inventory") && client.player != null) {
                    client.setScreen(new InventoryScreen(client.player));
                    if (client.currentScreen instanceof CreativeInventoryScreen screen) {
                        ItemGroup group = Registries.ITEM_GROUP.get(new Identifier("angling", "main"));
                        // Match signatures rather than mapped method/field names: this runs
                        // against the actual obfuscated production client, outside the mod.
                        for (var field : CreativeInventoryScreen.class.getDeclaredFields()) {
                            if (field.getType() != java.util.List.class) continue;
                            field.setAccessible(true);
                            Object value = field.get(screen);
                            if (!(value instanceof java.util.List<?> list)) continue;
                            for (Object entry : list) {
                                if (entry instanceof CreativeTabsScreenPage page && page.getVisibleTabs().contains(group)) {
                                    screen.setCurrentPage(page);
                                }
                            }
                        }
                        for (var method : CreativeInventoryScreen.class.getDeclaredMethods()) {
                            if (method.getReturnType() == void.class && method.getParameterCount() == 1
                                    && method.getParameterTypes()[0] == ItemGroup.class) {
                                method.setAccessible(true);
                                method.invoke(screen, group);
                                System.out.println("ANGLING_CLIENT_QA selected creative tab angling:main");
                                break;
                            }
                        }
                    }
                    queueCapture(name);
                } else if (action.equals("close")) {
                    client.setScreen(null);
                    queueCapture(name);
                } else if (action.equals("capture")) {
                    queueCapture(name);
                }
            } catch (Exception exception) {
                System.out.println("ANGLING_CLIENT_QA request failed: " + exception);
            }
        }

        private static void queueCapture(String name) {
            captureName = "angling-qa-" + name + ".png";
            captureDelay = 10;
        }

        @SubscribeEvent
        public static void render(TickEvent.RenderTickEvent event) {
            if (event.phase != TickEvent.Phase.END || captureName == null || --captureDelay > 0) return;
            MinecraftClient client = MinecraftClient.getInstance();
            String name = captureName;
            captureName = null;
            ScreenshotRecorder.saveScreenshot(client.runDirectory, name, client.getFramebuffer(),
                    message -> System.out.println("ANGLING_CLIENT_QA " + message.getString()));
        }
    }
}
