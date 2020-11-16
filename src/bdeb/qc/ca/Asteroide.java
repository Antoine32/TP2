package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;

import java.util.ArrayList;

import static bdeb.qc.ca.Window.random;

public class Asteroide extends ComplexeEntitie implements Cloneable {
    protected ArrayList<Projectile> projectilesList;
    protected ArrayList<Asteroide> asteroideList;

    protected boolean split = false;
    protected float multRotation = 1f;

    public Asteroide(float x, float y, int width, int height, String imgPath, int amountImg, Explosion explosionBlueprint, ArrayList<Explosion> explosionsList, ArrayList<Projectile> projectilesList, ArrayList<Asteroide> asteroideList) {
        super(x, y, width, height, imgPath, amountImg, explosionBlueprint, explosionsList);
        this.setDeleteOnOutOfFrame(true);
        this.setVitesseMin(0.00f, 0.10f);
        this.setVitesseMax(0.15f, 0.25f);

        this.projectilesList = projectilesList;
        this.asteroideList = asteroideList;
    }

    @Override
    public Asteroide clone() {
        Asteroide asteroide = (Asteroide) super.clone();
        asteroide.multRotation = 1f;
        asteroide.split = false;

        return asteroide;
    }

    public Asteroide setNewRandom() {
        float signe = (Math.round(random.nextFloat()) * 2) - 1; // donne -1f ou 1f

        this.setVitesse((random.nextFloat() * (this.getVitesseMaxX() - this.getVitesseMinX()) + this.getVitesseMinX()) * signe, random.nextFloat() * (this.getVitesseMaxY() - this.getVitesseMinY()) + this.getVitesseMinY());
        this.setFrame(random.nextInt(this.images.length));

        this.multRotation = random.nextFloat() * 0.05f - 0.025f;

        return this;
    }

    @Override
    public void update(GameContainer container, float delta) {
        super.update(container, delta);

        this.rotation += this.multRotation * delta;
        //this.getCurrentImage().setRotation(this.rotation);
    }

    @Override
    public void updateLast(GameContainer container, float delta) {
        super.updateLast(container, delta);

        if (!this.isDetruire()) {
            for (Projectile projectile : projectilesList) {
                if (!projectile.isDetruire() && projectile.detectCollision(this)) {
                    this.setSplit(true);
                    this.setDetruire(true);
                    projectile.setDetruire(true);
                    break;
                }
            }
        }
    }

    @Override
    public void lastAction() {
        if (this.isSplit() && Math.min(this.width, this.height) > 1) {
            Asteroide residue1 = this.clone().setNewRandom();
            residue1.setPosition(residue1.getPositionX() + (residue1.getWidth() / 2), residue1.getPositionY());
            residue1.setScale(this.scale / 2f);
            this.asteroideList.add(residue1);

            Asteroide residue2 = this.clone().setNewRandom();
            residue2.setPosition(residue2.getPositionX() - (residue2.getWidth() / 2), residue2.getPositionY());
            residue2.setScale(this.scale / 2f);
            this.asteroideList.add(residue2);
        }
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

}
