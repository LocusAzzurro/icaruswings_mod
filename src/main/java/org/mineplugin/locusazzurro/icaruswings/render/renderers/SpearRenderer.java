package org.mineplugin.locusazzurro.icaruswings.render.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.mineplugin.locusazzurro.icaruswings.data.ModData;
import org.mineplugin.locusazzurro.icaruswings.data.ModItemTier;
import org.mineplugin.locusazzurro.icaruswings.entity.SpearEntity;
import org.mineplugin.locusazzurro.icaruswings.items.SpearItem;
import org.mineplugin.locusazzurro.icaruswings.registry.ModelLayerRegistry;
import org.mineplugin.locusazzurro.icaruswings.render.models.SpearModel;
import org.mineplugin.locusazzurro.icaruswings.utils.MapUtils;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SpearRenderer extends EntityRenderer<SpearEntity> {

    private final SpearModel<SpearEntity> model;

    public SpearRenderer(Context context) {
        super(context);
        model = new SpearModel<>(context.bakeLayer(ModelLayerRegistry.SPEAR));
    }

    @Override
    public void render(SpearEntity spearEntity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, spearEntity.yRotO, spearEntity.getYRot()) - 90.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, spearEntity.xRotO, spearEntity.getXRot()) + 90.0F));
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBufferDirect(buffer, this.model.renderType(this.getTextureLocation(spearEntity)), false, spearEntity.isFoil());
        this.model.renderToBuffer(stack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        stack.popPose();
        super.render(spearEntity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpearEntity entity) {
        Tier tier = ((SpearItem)entity.getSpearItemData().getItem()).getTier();
        return getTexture(tier);
    }

    protected static final Map<? extends Tier, ResourceLocation> MATERIALS = MapUtils.Builder
            .<Tier, ResourceLocation>add(Tiers.WOOD, new ResourceLocation(ModData.MOD_ID, "textures/entity/wooden_spear.png"))
            .add(Tiers.STONE, new ResourceLocation(ModData.MOD_ID, "textures/entity/stone_spear.png"))
            .add(Tiers.IRON, new ResourceLocation(ModData.MOD_ID, "textures/entity/iron_spear.png"))
            .add(ModItemTier.STEEL, new ResourceLocation(ModData.MOD_ID, "textures/entity/steel_spear.png"))
            .add(Tiers.GOLD, new ResourceLocation(ModData.MOD_ID, "textures/entity/golden_spear.png"))
            .add(Tiers.DIAMOND, new ResourceLocation(ModData.MOD_ID, "textures/entity/diamond_spear.png"))
            .add(Tiers.NETHERITE, new ResourceLocation(ModData.MOD_ID, "textures/entity/netherite_spear.png"))
            .add(ModItemTier.SYNAPSE_ALLOY, new ResourceLocation(ModData.MOD_ID, "textures/entity/synapse_alloy_spear.png"));

    protected static final ResourceLocation FALLBACK_SPEAR_TEXTURE = new ResourceLocation(ModData.MOD_ID, "textures/entity/wooden_spear.png");

    public static ResourceLocation getTexture(Tier tier) {
        return MATERIALS.getOrDefault(tier, FALLBACK_SPEAR_TEXTURE);
    }
}
