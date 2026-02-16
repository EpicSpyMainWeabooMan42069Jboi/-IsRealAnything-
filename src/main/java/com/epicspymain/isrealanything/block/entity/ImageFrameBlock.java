package com.epicspymain.isrealanything.block.entity;

public class ImageFrameBlock {
    import com.epicspymain.isrealanything.block.entity.ImageFrameBlockEntity;import net.minecraft.class_1750;
import net.minecraft.class_1922;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2350;
import net.minecraft.class_2464;
import net.minecraft.class_2586;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2741;
import net.minecraft.class_2753;
import net.minecraft.class_2769;
import net.minecraft.class_3726;
import net.minecraft.class_4970;
import org.jetbrains.annotations.Nullable;

    public class ImageFrameBlock extends class_2248 implements class_2343 {
        public static final class_2753 FACING = class_2741.field_12481;

        private static final class_265 NORTH_SHAPE = class_2248.method_9541(1.0D, 4.0D, 14.0D, 15.0D, 12.0D, 16.0D);

        private static final class_265 SOUTH_SHAPE = class_2248.method_9541(1.0D, 4.0D, 0.0D, 15.0D, 12.0D, 2.0D);

        private static final class_265 EAST_SHAPE = class_2248.method_9541(0.0D, 4.0D, 1.0D, 2.0D, 12.0D, 15.0D);

        private static final class_265 WEST_SHAPE = class_2248.method_9541(14.0D, 4.0D, 1.0D, 16.0D, 12.0D, 15.0D);

        public ImageFrameBlock(class_4970.class_2251 settings) {
            super(settings);
            method_9590((class_2680)((class_2680)this.field_10647.method_11664()).method_11657((class_2769)FACING, (Comparable)class_2350.field_11043));
        }

        protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) {
            builder.method_11667(new class_2769[] { (class_2769)FACING });
        }

        public class_2680 method_9605(class_1750 ctx) {
            class_2350 side = ctx.method_8038();
            if (side == class_2350.field_11036 || side == class_2350.field_11033)
                return (class_2680)method_9564().method_11657((class_2769)FACING, (Comparable)ctx.method_8042().method_10153());
            return (class_2680)method_9564().method_11657((class_2769)FACING, (Comparable)side);
        }

        public class_265 method_9530(class_2680 state, class_1922 world, class_2338 pos, class_3726 context) {
            return getShapeForDirection((class_2350)state.method_11654((class_2769)FACING));
        }

        public class_265 method_9549(class_2680 state, class_1922 world, class_2338 pos, class_3726 context) {
            return getShapeForDirection((class_2350)state.method_11654((class_2769)FACING));
        }

        private class_265 getShapeForDirection(class_2350 direction) {
            switch (direction) {
                case field_11043:

                case field_11035:

                case field_11034:

                case field_11039:

            }
            return

                    NORTH_SHAPE;
        }

        @Nullable
        public class_2586 method_10123(class_2338 pos, class_2680 state) {
            return (class_2586)new ImageFrameBlockEntity(pos, state);
        }

        public class_2464 method_9604(class_2680 state) {
            return class_2464.field_11458;
        }
    }
}
