package com.epicspymain.isrealanything.events;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SignEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger("IsRealAnything");
    private static final Random RANDOM = new Random();


    public static void spawnSign(ServerWorld world, ServerPlayerEntity player, String message) {
        BlockPos playerPos = player.getBlockPos();

        // Try to find a suitable location within 50-60 block radius
        for (int attempts = 0; attempts < 20; attempts++) {
            int offsetX = RANDOM.nextInt(120) - 60 + (RANDOM.nextBoolean() ? 50 : -50);
            int offsetZ = RANDOM.nextInt(120) - 60 + (RANDOM.nextBoolean() ? 50 : -50);

            BlockPos signPos = playerPos.add(offsetX, 0, offsetZ);

            // Find ground level
            signPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, signPos);

            // Check if location is safe (not in liquid, not replacing important blocks)
            if (world.getBlockState(signPos).isAir() &&
                    !world.getBlockState(signPos.down()).isLiquid() &&
                    world.getBlockState(signPos.down()).isSolidBlock(world, signPos.down())) {

                // Place sign
                world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());

                // Get sign block entity and set text
                if (world.getBlockEntity(signPos) instanceof SignBlockEntity signEntity) {
                    // Split message into lines (max 4 lines, 15 chars each)
                    String[] lines = splitMessageForSign(message);

                    SignText frontText = new SignText();
                    for (int i = 0; i < Math.min(lines.length, 4); i++) {
                        frontText = frontText.withMessage(i, Text.literal(lines[i]));
                    }

                    signEntity.setText(frontText, true);
                    signEntity.markDirty();

                    LOGGER.info("Spawned sign at {} with message: {}", signPos, message);
                    return;
                }
            }
        }

        LOGGER.warn("Failed to find suitable location for sign after 20 attempts");
    }


    public static void spawnMultipleSigns(ServerWorld world, ServerPlayerEntity player, String... messages) {
        for (String message : messages) {
            spawnSign(world, player, message);
        }
    }


    private static String[] splitMessageForSign(String message) {
        if (message.length() <= 15) {
            return new String[]{message, "", "", ""};
        }

        // Check if message contains newlines
        if (message.contains("\n")) {
            String[] parts = message.split("\n");
            String[] result = new String[4];
            for (int i = 0; i < 4; i++) {
                result[i] = i < parts.length ? parts[i] : "";
            }
            return result;
        }

        // Auto-split long messages
        String[] words = message.split(" ");
        String[] lines = new String[]{"", "", "", ""};
        int currentLine = 0;

        for (String word : words) {
            if (currentLine >= 4) break;

            if ((lines[currentLine] + word).length() <= 15) {
                lines[currentLine] += (lines[currentLine].isEmpty() ? "" : " ") + word;
            } else {
                currentLine++;
                if (currentLine < 4) {
                    lines[currentLine] = word;
                }
            }
        }

        return lines;
    }
}