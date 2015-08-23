package fendirain.fendirain.client.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelFendinainMob extends ModelFendirain {
    public ModelRenderer head;
    public ModelRenderer eye1;
    public ModelRenderer eye2;
    public ModelRenderer mouthLeft;
    public ModelRenderer mouthMain;
    public ModelRenderer mouthRight;
    public ModelRenderer neck;
    public ModelRenderer body;
    public ModelRenderer leftArm1;
    public ModelRenderer leftArm2;
    public ModelRenderer rightArm1;
    public ModelRenderer rightArm2;
    public ModelRenderer leftLeg;
    public ModelRenderer rightLeg;

    public ModelFendinainMob() {
        this.textureWidth = 30;
        this.textureHeight = 26;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, -5.5F, 0.2F);
        this.head.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
        this.eye1 = new ModelRenderer(this, 1, 0);
        this.eye1.setRotationPoint(-1.2F, -0.6F, -2.4F);
        this.eye1.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.eye2 = new ModelRenderer(this, 1, 2);
        this.eye2.setRotationPoint(1.3F, -0.6F, -2.4F);
        this.eye2.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.mouthLeft = new ModelRenderer(this, 10, 12);
        this.mouthLeft.setRotationPoint(-1.4F, 1.1F, -2.4F);
        this.mouthLeft.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.mouthMain = new ModelRenderer(this, 10, 11);
        this.mouthMain.setRotationPoint(0.0F, 1.5F, -2.6F);
        this.mouthMain.addBox(-1.5F, -0.5F, -0.5F, 3, 1, 2, 0.0F);
        this.mouthRight = new ModelRenderer(this, 10, 12);
        this.mouthRight.setRotationPoint(1.4F, 1.1F, -2.4F);
        this.mouthRight.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.neck = new ModelRenderer(this, 0, 11);
        this.neck.setRotationPoint(0.0F, 2.1F, 0.5F);
        this.neck.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 1, 0.0F);
        this.body = new ModelRenderer(this, 0, 17);
        this.body.setRotationPoint(0.0F, 18.5F, -0.3F);
        this.body.addBox(-2.0F, -2.5F, -2.0F, 4, 5, 4, 0.0F);
        this.leftArm1 = new ModelRenderer(this, 21, 5);
        this.leftArm1.setRotationPoint(-2.8F, -1.0F, 0.0F);
        this.leftArm1.addBox(0.0F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.leftArm2 = new ModelRenderer(this, 21, 0);
        this.leftArm2.setRotationPoint(0.1F, -0.5F, 0.0F);
        this.leftArm2.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.rightArm1 = new ModelRenderer(this, 26, 5);
        this.rightArm1.setRotationPoint(2.0F, -1.0F, 0.0F);
        this.rightArm1.addBox(0.0F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.rightArm2 = new ModelRenderer(this, 26, 0);
        this.rightArm2.setRotationPoint(0.7F, -0.5F, 0.0F);
        this.rightArm2.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.leftLeg = new ModelRenderer(this, 21, 0);
        this.leftLeg.setRotationPoint(-1.1F, 2.4F, 0.1F);
        this.leftLeg.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.rightLeg = new ModelRenderer(this, 26, 0);
        this.rightLeg.setRotationPoint(1.1F, 2.4F, 0.1F);
        this.rightLeg.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.body.addChild(this.head);
        this.head.addChild(this.eye1);
        this.head.addChild(this.eye2);
        this.head.addChild(this.mouthLeft);
        this.head.addChild(this.mouthMain);
        this.head.addChild(this.mouthRight);
        this.head.addChild(this.neck);
        this.body.addChild(this.leftArm1);
        this.leftArm1.addChild(this.leftArm2);
        this.body.addChild(this.rightArm1);
        this.rightArm1.addChild(this.rightArm2);
        this.body.addChild(rightLeg);
        this.body.addChild(leftLeg);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.body.render(f5);
    }

    @Override
    public void setRotationAngles(float time, float walkSpeed, float otherAngle, float rotationYaw, float rotationPitch, float scale, Entity entity) {
        this.head.rotateAngleY = rotationYaw / (180F / (float) Math.PI);
        this.head.rotateAngleX = rotationPitch / (180F / (float) Math.PI);
        this.leftArm1.rotateAngleX = MathHelper.cos(time * 0.6662F) * 2.0F * walkSpeed * 0.5F;
        this.leftArm1.rotateAngleZ = 0.0F;
        this.rightArm1.rotateAngleX = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 2.0F * walkSpeed * 0.5F;
        this.rightArm1.rotateAngleZ = 0.0F;
        this.leftLeg.rotateAngleX = MathHelper.cos(time * 0.6662F) * 1.4F * walkSpeed;
        this.leftLeg.rotateAngleY = 0.0F;
        this.rightLeg.rotateAngleX = MathHelper.cos(time * 0.6662F + (float) Math.PI) * 1.4F * walkSpeed;
        this.rightLeg.rotateAngleY = 0.0F;
    }
}
