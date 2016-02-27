package fendirain.fendirain.client.models.mobs;

import fendirain.fendirain.client.models.blocks.ModelFendirain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelFenderiumMob extends ModelFendirain {

    public ModelRenderer mainBody;
    public ModelRenderer leftLeg1;
    public ModelRenderer rightLeg1;
    public ModelRenderer leftArm1;
    public ModelRenderer rightArm1;
    public ModelRenderer neck;
    public ModelRenderer leftLeg2;
    public ModelRenderer rightLeg2;
    public ModelRenderer leftArm2;
    public ModelRenderer rightArm2;
    public ModelRenderer head;
    public ModelRenderer leftEye;
    public ModelRenderer rightEye;
    public ModelRenderer mouth;
    boolean lowerArm = false;

    public ModelFenderiumMob() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.leftArm2 = new ModelRenderer(this, 23, 5);
        this.leftArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leftArm2.addBox(-2.0F, -1.0F, -1.0F, 2, 7, 2, 0.0F);
        this.rightEye = new ModelRenderer(this, 0, 13);
        this.rightEye.setRotationPoint(1.3F, -1.4F, -2.4F);
        this.rightEye.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.head = new ModelRenderer(this, 0, 13);
        this.head.setRotationPoint(0.5F, -3.1F, 0.5F);
        this.head.addBox(-3.0F, -3.0F, -2.5F, 6, 6, 5, 0.0F);
        this.leftLeg1 = new ModelRenderer(this, 32, 0);
        this.leftLeg1.setRotationPoint(1.4F, 6.9F, 2.7F);
        this.leftLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
        this.rightArm1 = new ModelRenderer(this, 25, 0);
        this.rightArm1.setRotationPoint(6.4F, 1.8F, 3.0F);
        this.rightArm1.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.leftArm1 = new ModelRenderer(this, 18, 0);
        this.leftArm1.setRotationPoint(-0.4F, 1.8F, 3.0F);
        this.leftArm1.addBox(-0.5F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.leftLeg2 = new ModelRenderer(this, 32, 8);
        this.leftLeg2.setRotationPoint(0.0F, 5.5F, 0.0F);
        this.leftLeg2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F);
        this.rightLeg1 = new ModelRenderer(this, 41, 0);
        this.rightLeg1.setRotationPoint(4.6F, 6.9F, 2.7F);
        this.rightLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 5, 2, 0.0F);
        this.mouth = new ModelRenderer(this, 0, 25);
        this.mouth.setRotationPoint(0.0F, 1.4F, -2.4F);
        this.mouth.addBox(-2.0F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        this.mainBody = new ModelRenderer(this, 0, 0);
        this.mainBody.setRotationPoint(-3.0F, 11.1F, -2.7F);
        this.mainBody.addBox(0.0F, 0.0F, 0.0F, 6, 7, 5, 0.0F);
        this.neck = new ModelRenderer(this, 11, 25);
        this.neck.setRotationPoint(2.5F, -0.4F, 2.0F);
        this.neck.addBox(-0.4F, -0.5F, -0.5F, 2, 1, 2, 0.0F);
        this.rightLeg2 = new ModelRenderer(this, 32, 14);
        this.rightLeg2.setRotationPoint(0.0F, 5.5F, 0.0F);
        this.rightLeg2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F);
        this.leftEye = new ModelRenderer(this, 0, 13);
        this.leftEye.setRotationPoint(-1.3F, -1.4F, -2.4F);
        this.leftEye.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.rightArm2 = new ModelRenderer(this, 23, 15);
        this.rightArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rightArm2.addBox(0.0F, -1.0F, -1.0F, 2, 7, 2, 0.0F);
        this.leftArm1.addChild(this.leftArm2);
        this.head.addChild(this.rightEye);
        this.neck.addChild(this.head);
        this.mainBody.addChild(this.leftLeg1);
        this.mainBody.addChild(this.rightArm1);
        this.mainBody.addChild(this.leftArm1);
        this.leftLeg1.addChild(this.leftLeg2);
        this.mainBody.addChild(this.rightLeg1);
        this.head.addChild(this.mouth);
        this.mainBody.addChild(this.neck);
        this.rightLeg1.addChild(this.rightLeg2);
        this.head.addChild(this.leftEye);
        this.rightArm1.addChild(this.rightArm2);
    }

    @Override
    public void render(Entity entity, float time, float walkSpeed, float otherAngle, float rotationYaw, float rotationPitch, float scale) {
        this.renderFenderium((EntityFenderiumMob) entity, time, walkSpeed, otherAngle, rotationYaw, rotationPitch, scale);
    }

    public void renderFenderium(EntityFenderiumMob entityFenderiumMob, float time, float walkSpeed, float otherAngle, float rotationYaw, float rotationPitch, float scale) {
        this.mainBody.render(scale);
        setRotationAngles(time, walkSpeed, otherAngle, rotationYaw, rotationPitch, scale, entityFenderiumMob);
        if (entityFenderiumMob.getHeldItem() != null && entityFenderiumMob.isCurrentlyChopping()) {
            if (this.rightArm1.rotateAngleX == 0) {
                this.rightArm1.rotateAngleX = -2.5F;
                lowerArm = false;
            }
            this.rightArm1.rotateAngleX = lowerArm ? this.rightArm1.rotateAngleX + 0.18F : this.rightArm1.rotateAngleX - 0.18F;
            if (this.rightArm1.rotateAngleX <= -3.5) lowerArm = true;
            else if (this.rightArm1.rotateAngleX >= -2.5) lowerArm = false;
        } else this.rightArm1.rotateAngleX = 0.0F;
    }

    @Override
    public void setRotationAngles(float time, float walkSpeed, float otherAngle, float rotationYaw, float rotationPitch, float scale, Entity entity) {
        this.head.rotateAngleY = rotationYaw / (180F / (float) Math.PI);
        this.head.rotateAngleX = rotationPitch / (180F / (float) Math.PI);
        EntityFenderiumMob entityFenderiumMob = (EntityFenderiumMob) entity;
        if (entityFenderiumMob.getHeldItem() == null || !entityFenderiumMob.isCurrentlyChopping()) {
            this.rightArm1.rotateAngleX = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 2.0F * walkSpeed * 0.5F;
            this.rightArm1.rotateAngleZ = 0.0F;
            this.leftLeg1.rotateAngleX = MathHelper.cos(time * 0.6662F) * 1.4F * walkSpeed;
            this.leftLeg1.rotateAngleY = 0.0F;
            this.rightLeg1.rotateAngleX = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 1.4F * walkSpeed;
            this.rightLeg1.rotateAngleY = 0.0F;
        } else if (this.leftLeg1.rotateAngleX != 0 || this.rightLeg1.rotateAngleX != 0) {
            this.leftLeg1.rotateAngleX = 0.0F;
            this.rightLeg1.rotateAngleX = 0.0F;
        }
    }
}
