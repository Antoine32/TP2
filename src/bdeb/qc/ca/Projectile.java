package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;

public class Projectile extends ComplexeEntitie implements Cloneable {
    protected Vector2f positionInitiale;
    protected float distMaximal;

    protected ComplexeEntitie lastTarget = null;

    protected Receiver receiver = null;
    protected int valeurTransmise = 0;

    protected Sound sonDebut;

    protected boolean moi = true;

    public Projectile(float x, float y, int width, int height, String imgPath, int amountImg, float vitX, float vitY, float distMaximal, Explosion explosionBlueprint, ArrayList<Explosion> explosionsList, Receiver receiver, Sound sonDebut) {
        super(x, y, width, height, imgPath, amountImg, explosionBlueprint, explosionsList);
        this.setDeleteOnOutOfFrame(true);
        this.setVitesse(vitX, vitY);
        this.positionInitiale = new Vector2f(0, 0);
        this.distMaximal = distMaximal;

        this.sonDebut = sonDebut;

        this.setReceiver(receiver);
    }

    @Override
    public Projectile clone() {
        Projectile projectile = (Projectile) super.clone();
        projectile.positionInitiale = this.positionInitiale.copy();
        projectile.lastTarget = null;
        return projectile;
    }

    public void joueSonDebut() {
        sonDebut.play(1f, 0.2f);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.setPositionInitiale(x, y);
    }

    @Override
    public void setPosition(Vector2f position) {
        super.setPosition(position);
        this.setPositionInitiale(position.x, position.y);
    }

    public void setMoi(boolean moi) {
        this.moi = moi;
    }

    public void setValeurTransmise(int valeurTransmise) {
        this.valeurTransmise = valeurTransmise;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setPositionInitiale(float x, float y) {
        this.positionInitiale.set(x, y);
    }

    public void setPositionInitiale(Vector2f positionInitiale) {
        this.positionInitiale.set(positionInitiale);
    }

    public void setDistMaximal(float distMaximal) {
        this.distMaximal = distMaximal;
    }

    public Vector2f getPositionInitiale() {
        return positionInitiale;
    }

    public float getDistMaximal() {
        return this.distMaximal;
    }

    public int getValeurTransmise() {
        return valeurTransmise;
    }

    public Entite getLastTarget() {
        return lastTarget;
    }

    public boolean isMoi() {
        return this.moi;
    }

    @Override
    public boolean detectCollision(ComplexeEntitie complexeEntitie) {
        boolean collision = super.detectCollision(complexeEntitie);

        if (collision) {
            this.lastTarget = complexeEntitie.clone();
        }

        return collision;
    }

    @Override
    public void updateLast(GameContainer container, float delta) {
        super.updateLast(container, delta);

        float dist = Math.abs(this.getPositionX() - this.positionInitiale.x) + Math.abs(this.getPositionY() - this.positionInitiale.y);

        if (dist != 0.0f) {
            int num = (int) (dist / (this.distMaximal / (float) this.images.length));

            if (num < this.images.length) {
                this.frame = num;
            } else {
                this.setDetruire(true);
            }
        }
    }

    @Override
    public void lastAction() {
        if (this.lastTarget == null) {
            this.lastTarget = this;
        }

        if (this.receiver != null) {
            this.receiver.send(this.valeurTransmise);
        }

        this.lastTarget.triggerExplosion();
    }
}
