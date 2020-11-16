package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Exhaust extends Entite implements Cloneable {
    protected Cooldown cooldownChangeFrame;
    protected Vector2f dist1;
    protected Vector2f dist2;
    protected Vector2f offset1;
    protected Vector2f offset2;

    public Exhaust(float x, float y, int width, int height, String imgPath, int amountImg) {
        super(x, y, width, height, imgPath, amountImg);
        this.cooldownChangeFrame = new Cooldown(50);
        this.dist1 = new Vector2f(0, 0);
        this.dist2 = new Vector2f(0, 0);
        this.offset1 = new Vector2f(0, 0);
        this.offset2 = new Vector2f(0, 0);
    }

    @Override
    public Exhaust clone() {
        Exhaust exhaust = (Exhaust) super.clone();
        exhaust.cooldownChangeFrame = this.cooldownChangeFrame.clone();
        exhaust.dist1 = this.dist1.copy();
        exhaust.dist2 = this.dist2.copy();
        exhaust.offset1 = this.offset1.copy();
        exhaust.offset2 = this.offset2.copy();

        return exhaust;
    }

    @Override
    public void display(Graphics g) {
        g.pushTransform();
        g.translate((dist1.x + offset1.x) * scale, (dist1.y + offset1.y) * scale);
        super.display(g);
        g.popTransform();

        g.pushTransform();
        g.translate((dist2.x + offset2.x) * scale, (dist2.y + offset2.y) * scale);
        super.display(g);
        g.popTransform();
    }

    @Override
    public void updateLast(GameContainer container, float delta) {
        super.updateLast(container, delta);

        if (this.cooldownChangeFrame.isDone()) {
            this.frame += 1;
            this.frame %= this.images.length;
        }
    }

    public void setDist(float x1, float y1, float x2, float y2) {
        this.dist1.set(x1, y1);
        this.dist2.set(x2, y2);
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setOffset(float x1, float y1, float x2, float y2) {
        this.offset1.set(x1, y1);
        this.offset2.set(x2, y2);
    }
}
