package fendirain.fendirain.client.models.mobs;

import fendirain.fendirain.client.models.blocks.ModelFendirain;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelFenderiumMob extends ModelFendirain {

    public ModelRenderer MainBody;
    public ModelRenderer LeftLeg1;
    public ModelRenderer RightLeg1;
    public ModelRenderer LeftArm1;
    public ModelRenderer RightArm1;
    public ModelRenderer Neck;
    public ModelRenderer LeftLeg2;
    public ModelRenderer RightLeg2;
    public ModelRenderer LeftArm2;
    public ModelRenderer RightArm2;
    public ModelRenderer Head;
    public ModelRenderer LeftEye;
    public ModelRenderer RightEye;
    public ModelRenderer Mouth;

    public ModelFenderiumMob() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.LeftArm2 = new ModelRenderer(this, 23, 5);
        this.LeftArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.LeftArm2.addBox(-2.0F, -1.0F, -1.0F, 2, 7, 2, 0.0F);
        this.RightEye = new ModelRenderer(this, 0, 13);
        this.RightEye.setRotationPoint(1.3F, -1.4F, -2.4F);
        this.RightEye.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.Head = new ModelRenderer(this, 0, 13);
        this.Head.setRotationPoint(0.5F, -3.1F, 0.5F);
        this.Head.addBox(-3.0F, -3.0F, -2.5F, 6, 6, 5, 0.0F);
        this.LeftLeg1 = new ModelRenderer(this, 32, 0);
        this.LeftLeg1.setRotationPoint(1.4F, 6.9F, 2.7F);
        this.LeftLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
        this.RightArm1 = new ModelRenderer(this, 25, 0);
        this.RightArm1.setRotationPoint(6.4F, 1.8F, 3.0F);
        this.RightArm1.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.LeftArm1 = new ModelRenderer(this, 18, 0);
        this.LeftArm1.setRotationPoint(-0.4F, 1.8F, 3.0F);
        this.LeftArm1.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.LeftLeg2 = new ModelRenderer(this, 32, 8);
        this.LeftLeg2.setRotationPoint(0.0F, 5.5F, 0.0F);
        this.LeftLeg2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F);
        this.RightLeg1 = new ModelRenderer(this, 41, 0);
        this.RightLeg1.setRotationPoint(4.6F, 6.9F, 2.7F);
        this.RightLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
        this.Mouth = new ModelRenderer(this, 0, 25);
        this.Mouth.setRotationPoint(0.0F, 1.4F, -2.4F);
        this.Mouth.addBox(-2.0F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        this.MainBody = new ModelRenderer(this, 0, 0);
        this.MainBody.setRotationPoint(-3.0F, 11.1F, -2.7F);
        this.MainBody.addBox(0.0F, 0.0F, 0.0F, 6, 7, 5, 0.0F);
        this.Neck = new ModelRenderer(this, 11, 25);
        this.Neck.setRotationPoint(2.5F, -0.4F, 2.0F);
        this.Neck.addBox(-0.4F, -0.5F, -0.5F, 2, 1, 2, 0.0F);
        this.RightLeg2 = new ModelRenderer(this, 32, 14);
        this.RightLeg2.setRotationPoint(0.0F, 5.5F, 0.0F);
        this.RightLeg2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F);
        this.LeftEye = new ModelRenderer(this, 0, 13);
        this.LeftEye.setRotationPoint(-1.3F, -1.4F, -2.4F);
        this.LeftEye.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.RightArm2 = new ModelRenderer(this, 23, 15);
        this.RightArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.RightArm2.addBox(0.0F, -1.0F, -1.0F, 2, 7, 2, 0.0F);
        this.LeftArm1.addChild(this.LeftArm2);
        this.Head.addChild(this.RightEye);
        this.Neck.addChild(this.Head);
        this.MainBody.addChild(this.LeftLeg1);
        this.MainBody.addChild(this.RightArm1);
        this.MainBody.addChild(this.LeftArm1);
        this.LeftLeg1.addChild(this.LeftLeg2);
        this.MainBody.addChild(this.RightLeg1);
        this.Head.addChild(this.Mouth);
        this.MainBody.addChild(this.Neck);
        this.RightLeg1.addChild(this.RightLeg2);
        this.Head.addChild(this.LeftEye);
        this.RightArm1.addChild(this.RightArm2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.MainBody.render(f5);
    }

    @Override
    public void setRotationAngles(float time, float walkSpeed, float otherAngle, float rotationYaw, float rotationPitch, float scale, Entity entity) {
        this.Head.rotateAngleY = rotationYaw / (180F / (float) Math.PI);
        this.Head.rotateAngleX = rotationPitch / (180F / (float) Math.PI);
        this.RightArm1.rotateAngleX = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 2.0F * walkSpeed * 0.5F;
        this.RightArm2.rotateAngleZ = 0.0F;
        this.LeftLeg1.rotateAngleX = MathHelper.cos(time * 0.6662F) * 1.4F * walkSpeed;
        this.LeftLeg1.rotateAngleY = 0.0F;
        this.RightLeg1.rotateAngleX = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 1.4F * walkSpeed;
        this.RightLeg1.rotateAngleY = 0.0F;
    }
}
