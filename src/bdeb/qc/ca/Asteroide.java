package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import static bdeb.qc.ca.Window.random;
import static bdeb.qc.ca.Window.scl;

public class Asteroide extends ComplexeEntitie implements Cloneable {
    protected ArrayList<Projectile> projectilesList;
    protected ArrayList<Asteroide> asteroideList;
    protected Queue<Object> nouveauAsteroide;

    protected boolean split = false;
    protected float multRotation = 1f;

    protected UUID uuid;

    public Asteroide(float x, float y, int width, int height, String imgPath, int amountImg, Explosion explosionBlueprint, ArrayList<Explosion> explosionsList, ArrayList<Projectile> projectilesList, ArrayList<Asteroide> asteroideList, Queue<Object> nouveauAsteroide) {
        super(x, y, width, height, imgPath, amountImg, explosionBlueprint, explosionsList);

        this.resize((int) (width * scl), (int) (height * scl));

        this.setDeleteOnOutOfFrame(true);
        this.setVitesseMin(0.00f, 0.10f);
        this.setVitesseMax(0.15f, 0.25f);

        this.projectilesList = projectilesList;
        this.asteroideList = asteroideList;
        this.nouveauAsteroide = nouveauAsteroide;
    }

    @Override
    public Asteroide clone() {
        Asteroide asteroide = (Asteroide) super.clone();
        asteroide.multRotation = 1f;
        asteroide.split = false;

        asteroide.uuid = UUID.randomUUID();

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
    }

    @Override
    public void updateLast(GameContainer container, float delta) {
        super.updateLast(container, delta);

        if (!this.isDetruire()) {
            for (Projectile projectile : projectilesList) {
                if (!projectile.isDetruire() && projectile.detectCollision(this)) {
                    this.setSplit(projectile.isMoi());
                    this.setDetruire(true);
                    projectile.setDetruire(true);
                    break;
                }
            }
        }
    }

    @Override
    public void lastAction() {
        //nouveauAsteroide.add(this.uuid);
        //nouveauAsteroide.add(this.uuid);

        if (this.isSplit() && Math.min(this.width, this.height) > 1) {
            Asteroide residue1 = this.clone().setNewRandom();
            residue1.setPosition(residue1.getPositionX() + (residue1.getWidth() / 2), Math.max(residue1.getPositionY(), 0));
            residue1.setScale(this.scale / 2f);
            this.asteroideList.add(residue1);
            residue1.toNouveau();

            Asteroide residue2 = this.clone().setNewRandom();
            residue2.setPosition(residue2.getPositionX() - (residue2.getWidth() / 2), Math.max(residue2.getPositionY(), 0));
            residue2.setScale(this.scale / 2f);
            this.asteroideList.add(residue2);
            residue2.toNouveau();
        }
    }

    public void toNouveau() {
        nouveauAsteroide.add(this.getPositionX() / scl);
        nouveauAsteroide.add(this.getPositionY() / scl);
        nouveauAsteroide.add(this.getVitesseX());
        nouveauAsteroide.add(this.getVitesseY());
        nouveauAsteroide.add(this.getMultRotation());
        nouveauAsteroide.add(this.getScale());
        nouveauAsteroide.add(this.getFrame());
    }

    public boolean isSplit() {
        return split;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public float getMultRotation() {
        return multRotation;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public void setMultRotation(float multRotation) {
        this.multRotation = multRotation;
    }

}
