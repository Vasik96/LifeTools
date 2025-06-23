package com.lifetools.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;

import java.util.Optional;
import java.util.function.Function;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.util.Utility.sendFeedback;

public class Launch {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public void handleLaunch() {
        if (client.player != null) {
            PlayerEntity player = client.player;
            World world = player.getWorld();

            // Define launch distance
            double forwardDistance = 0.4;
            double upwardDistance = 0.3;

            // Calculate motion
            double xMotion = player.getRotationVector().x * forwardDistance;
            double zMotion = player.getRotationVector().z * forwardDistance;

            // Apply motion
            player.setVelocity(xMotion, upwardDistance, zMotion);
            player.sendMessage(Text.literal(INFO_PREFIX + "Launched!"), false);

            // Create explosion effect
            createExplosion(world, player.getPos());
        } else {
            sendFeedback(Text.literal(INFO_PREFIX + "Player not found."));
        }
    }

    public void createExplosion(World world, Vec3d pos) {
        AdvancedExplosionBehavior explosionBehavior = new AdvancedExplosionBehavior(
                true, false,
                Optional.of(1.22F),
                Registries.BLOCK.getEntryList(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
        );

        world.createExplosion(
                null, null, explosionBehavior,
                pos.getX(), pos.getY(), pos.getZ(),
                1.2F, false, World.ExplosionSourceType.TRIGGER,
                ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
        );
    }
}
