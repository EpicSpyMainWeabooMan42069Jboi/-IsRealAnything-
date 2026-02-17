package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

    public class NotepadManager {
        private static final Logger LOGGER = LoggerFactory.getLogger("IsRealAnything");


        public static void execute(ServerPlayerEntity player, String... lines) {
            try {

                File tempFile = File.createTempFile("isrealanything_", ".txt");


                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }


                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb;

                if (os.contains("win")) {
                    // Windows - use notepad
                    pb = new ProcessBuilder("notepad.exe", tempFile.getAbsolutePath());
                } else if (os.contains("mac")) {
                    // macOS - use TextEdit
                    pb = new ProcessBuilder("open", "-a", "TextEdit", tempFile.getAbsolutePath());
                } else {
                    // Linux - try gedit
                    pb = new ProcessBuilder("gedit", tempFile.getAbsolutePath());
                }

                pb.start();
                LOGGER.info("Opened notepad with {} lines", lines.length);

            } catch (IOException e) {
                LOGGER.error("Failed to open notepad", e);
                // Fallback: send messages in chat
                for (String line : lines) {
                    player.sendMessage(Text.literal(line), false);
                }
            }
        }


        public static void executeWithMessage(ServerPlayerEntity player, Text[] messages) {
            String[] lines = new String[messages.length];
            for (int i = 0; i < messages.length; i++) {
                lines[i] = messages[i].getString();
            }
            execute(player, lines);
        }
    }
}
