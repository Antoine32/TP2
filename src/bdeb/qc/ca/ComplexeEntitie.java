package bdeb.qc.ca;

import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.UUID;

public class ComplexeEntitie extends Entite implements Cloneable {
    protected Explosion explosionBlueprint;
    protected ArrayList<Explosion> explosionsList;

    protected float collisionScale = 1f;

    protected UUID uuid;

    public ComplexeEntitie(float x, float y, int width, int height, String imgPath, int amountImg, Explosion explosionBlueprint, ArrayList<Explosion> explosionsList) {
        super(x, y, width, height, imgPath, amountImg);
        this.explosionBlueprint = explosionBlueprint;
        this.explosionsList = explosionsList;

        this.uuid = UUID.randomUUID();
    }

    @Override
    public ComplexeEntitie clone() {
        ComplexeEntitie complexeEntitie = (ComplexeEntitie) super.clone();
        complexeEntitie.uuid = UUID.randomUUID();

        return complexeEntitie;
    }

    public void triggerExplosion() {
        Explosion explosion = this.explosionBlueprint.clone();
        explosion.resize(this.getWidth(), this.getWidth());
        explosion.setScale(this.getScale());
        explosion.setPosition(this.getPositionX(), this.getPositionY());

        this.explosionsList.add(explosion);
    }

    public boolean detectCollision(ComplexeEntitie complexeEntitie) {
        Vector2f distance = new Vector2f(Math.abs(this.getPositionX() - complexeEntitie.getPositionX()), Math.abs(this.getPositionY() - complexeEntitie.getPositionY()));
        float minimum = (this.getWidth() * this.getCollisionScale() + complexeEntitie.getWidth() * complexeEntitie.getCollisionScale()) / 2f;
        float minimumSquared = minimum * minimum;

        return distance.lengthSquared() < minimumSquared;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public float getCollisionScale() {
        return collisionScale;
    }

    public void setCollisionScale(float collisionScale) {
        this.collisionScale = collisionScale;
    }
}
