package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;

public class Explosion extends Entite implements Cloneable {
    protected Cooldown cooldownChangeFrame;
    protected float multSize = 1f;

    public Explosion(float x, float y, int width, int height, String imgPath, int amountImg, float multSize) {
        super(x, y, width, height, imgPath, amountImg);
        this.cooldownChangeFrame = new Cooldown(50);
        this.setMultSize(multSize);
    }

    @Override
    public Explosion clone() {
        Explosion explosion = (Explosion) super.clone();
        explosion.cooldownChangeFrame = this.cooldownChangeFrame.clone();

        return explosion;
    }

    @Override
    public void resize(int width, int height) {
        width *= multSize;
        height *= multSize;
        super.resize(width, height);
    }

    @Override
    public void updateLast(GameContainer container, float delta) {
        super.updateLast(container, delta);

        this.setDetruire(false);
        if (this.cooldownChangeFrame.isDone()) {
            if (this.frame + 1 < this.images.length) {
                this.frame += 1;
            } else {
                this.setDetruire(true);
            }
        }
    }

    public float getMultSize() {
        return multSize;
    }

    public void setMultSize(float multSize) {
        this.multSize = multSize;
    }
}
