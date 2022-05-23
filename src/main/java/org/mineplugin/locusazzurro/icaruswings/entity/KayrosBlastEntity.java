package org.mineplugin.locusazzurro.icaruswings.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkHooks;
import org.mineplugin.locusazzurro.icaruswings.data.ModConfig;
import org.mineplugin.locusazzurro.icaruswings.data.ModDamageSources;
import org.mineplugin.locusazzurro.icaruswings.effect.EffectInevitability;
import org.mineplugin.locusazzurro.icaruswings.registry.EffectRegistry;
import org.mineplugin.locusazzurro.icaruswings.registry.EntityTypeRegistry;
import org.mineplugin.locusazzurro.icaruswings.registry.ItemRegistry;
import org.mineplugin.locusazzurro.icaruswings.registry.SoundRegistry;
import org.mineplugin.locusazzurro.icaruswings.utils.KayrosGenUtils;
import org.mineplugin.locusazzurro.icaruswings.utils.MathUtils;

import java.util.List;

public class KayrosBlastEntity extends ThrowableItemProjectile {


    private static final boolean ALLOW_TERRAIN = ModConfig.ALLOW_DEMETER_CHANGE_TERRAIN.get();
    private static final EntityDataAccessor<CompoundTag> LANDSCAPE = SynchedEntityData.defineId(KayrosBlastEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private final int maxLife = 600;
    private int life;

    public KayrosBlastEntity(EntityType<? extends KayrosBlastEntity> type, Level level) {
        super(type, level);
    }

    public KayrosBlastEntity(LivingEntity entity, Level world, CompoundTag terrain){
        super(EntityTypeRegistry.kayrosBlastEntity.get(), entity, world);
        this.entityData.set(LANDSCAPE, terrain);
    }

    @Override
    public void tick(){
        super.tick();
        if (life >= maxLife) {
            this.discard();
        }
        life++;

        if (level.isClientSide()){
            double xR = level.random.nextDouble() * 0.1d - 0.05d;
            double yR = level.random.nextDouble() * 0.1d - 0.05d;
            double zR = level.random.nextDouble() * 0.1d - 0.05d;
            level.addParticle(ParticleTypes.WITCH, this.getX(), this.getY(), this.getZ(), xR,yR,zR);
        }
    }

    @Override
    public void onHit(HitResult ray){
        super.onHit(ray);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.timeRiftCollapse.get(), SoundSource.PLAYERS, 2.0F, 1.1F);
    }

    @Override
    public void onHitBlock(BlockHitResult ray){
        super.onHitBlock(ray);
        BlockPos pos = ray.getBlockPos();
        if (!level.isClientSide()){
            if (ALLOW_TERRAIN) {
                byte[] terrain = this.entityData.get(LANDSCAPE).getByteArray("Landscape");
                int xo = pos.getX() - 4;
                int yo = pos.getY() - level.random.nextInt(3);
                int zo = pos.getZ() - 4;
                for (int i = 0; i < terrain.length; i++) {
                    int y = i / 64;
                    int z = (i - 64 * y) / 8;
                    int x = i - 64 * y - 8 * z;
                    level.setBlock(new BlockPos(xo + x, yo + y, zo + z), KayrosGenUtils.palette[terrain[i]], 3);
                }
            }
            this.level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(4)).forEach(e -> {
                ((EffectInevitability) EffectRegistry.inevitability.get()).addEffect(e, 3);
                e.hurt(ModDamageSources.timeRift(this.getOwner()), 5.0f);
            });
            ((ServerLevel)level).sendParticles(ParticleTypes.WITCH,
                    pos.getX(), pos.getY(), pos.getZ(),
                    4000, 4, 4, 4, 0.001);
        }
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            ((EffectInevitability) EffectRegistry.inevitability.get()).addEffect(livingEntity, 5);
            livingEntity.hurt(ModDamageSources.timeRift(this.getOwner()), 15.0f);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity instanceof LivingEntity;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Life", this.life);
        nbt.putByteArray("Landscape", entityData.get(LANDSCAPE).getByteArray("Landscape"));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.life = nbt.getInt("Life");
        entityData.set(LANDSCAPE, KayrosGenUtils.convertToTag(nbt.getByteArray("Landscape")));
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.demeterCharge.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LANDSCAPE, KayrosGenUtils.convertToTag(KayrosGenUtils.cubeTerrain));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
