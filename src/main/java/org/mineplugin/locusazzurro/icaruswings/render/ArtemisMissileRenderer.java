package org.mineplugin.locusazzurro.icaruswings.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.mineplugin.locusazzurro.icaruswings.data.ModData;
import org.mineplugin.locusazzurro.icaruswings.entity.ArtemisMissileEntity;
import org.mineplugin.locusazzurro.icaruswings.render.models.ArtemisMissileModel;

@OnlyIn(Dist.CLIENT)
public class ArtemisMissileRenderer extends EntityRenderer<ArtemisMissileEntity> {

    private static final ResourceLocation MISSILE = new ResourceLocation(ModData.MOD_ID, "textures/entity/artemis_missile.png");
    private final ArtemisMissileModel model = new ArtemisMissileModel();

    public ArtemisMissileRenderer(Context p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Override
    public ResourceLocation getTextureLocation(ArtemisMissileEntity p_110775_1_) {
        return MISSILE;
    }

    public void render(ArtemisMissileEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.15f,0.15f, 0.15f);
        matrixStackIn.translate(0f, -0.25f, 0f);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(entityYaw));
        matrixStackIn.mulPose(com.mojang.math.Vector3f.XN.rotationDegrees(entityIn.xRot));
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(MISSILE));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }
}
