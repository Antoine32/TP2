package bdeb.qc.ca;

import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;

public class ComplexeEntitie extends Entite implements Cloneable {
    protected Explosion explosionBlueprint;
    protected ArrayList<Explosion> explosionsList;

    public ComplexeEntitie(float x, float y, int width, int height, String imgPath, int amountImg, Explosion explosionBlueprint, ArrayList<Explosion> explosionsList) {
        super(x, y, width, height, imgPath, amountImg);
        this.explosionBlueprint = explosionBlueprint;
        this.explosionsList = explosionsList;
    }

    @Override
    public ComplexeEntitie clone() {
        ComplexeEntitie complexeEntitie = (ComplexeEntitie) super.clone();
        return complexeEntitie;
    }

    public void triggerExplosion() {
        Explosion explosion = this.explosionBlueprint.clone();
        explosion.resize((int) (this.getWidth() * this.getScale()), (int) (this.getWidth() * this.getScale()));
        explosion.setPosition(this.getPositionX(), this.getPositionY());

        this.explosionsList.add(explosion);
    }

    public boolean detectCollision(ComplexeEntitie complexeEntitie) {
        Vector2f distance = new Vector2f(Math.abs(this.getPositionX() - complexeEntitie.getPositionX()), Math.abs(this.getPositionY() - complexeEntitie.getPositionY()));
        float minimum = (this.getWidth() * this.getCollisionScale() + complexeEntitie.getWidth() * complexeEntitie.getCollisionScale()) / 2f;
        float minimumSquared = minimum * minimum;

        return distance.lengthSquared() < minimumSquared;
    }
}
