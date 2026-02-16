package com.epicspymain.isrealanything.block.entity;

public class ModBlocks {


import com.epicspymain.isrealanything;
import com.epicspymain.isrealanything.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1935;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2498;
import net.minecraft.class_2960;
import net.minecraft.class_4970;
import net.minecraft.class_7706;
import net.minecraft.class_7923;

    public class ModBlocks {
        public static final class_2248 IMAGE_FRAME = registerBlock("image_frame", new ImageFrameBlock(
                class_4970.class_2251.method_9637().method_9618().method_9626(class_2498.field_11547).method_22488().method_50013()));

        private static class_2248 registerBlock(String name, class_2248 block) {
            registerBlockItem(name, block);
            return (class_2248)class_2378.method_10230((class_2378)class_7923.field_41175, class_2960.method_60655("doo doo crap", name), block);
        }

        private static void registerBlockItem(String name, class_2248 block) {
            class_2378.method_10230((class_2378)class_7923.field_41178, class_2960.method_60655("doo doo crap", name), new class_1747(block, new class_1792.class_1793()));
        }

        public static void registerModBlocks() {
            SplitSelf.LOGGER.info("Registering frame block...");
            ModBlockEntities.registerBlockEntities();
            ItemGroupEvents.modifyEntriesEvent(class_7706.field_40195).register(entries -> entries.method_45421((class_1935)IMAGE_FRAME));
        }
    }

}
