package bdeb.qc.ca;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.ArrayList;

import static bdeb.qc.ca.Window.constrain;

public class Vaisseau extends ComplexeEntitie implements Cloneable {
    protected Projectile projectileBLueprint;
    protected Projectile cargaisonBLueprint;

    protected ArrayList<Projectile> projectilesList;
    protected ArrayList<Entite> cargaisonsList;
    protected ArrayList<Asteroide> asteroidesList;
    protected ArrayList<Entite> hudList;

    protected Cooldown cooldownProjectile;
    protected Cooldown cooldownCargaison;
    protected Cooldown cooldownInactif;
    protected Cooldown cooldownImmuniter;

    protected int quantiterDansVaisseau = 0;
    protected int capaciter = 128 * 128;

    protected Exhaust exhaust;

    protected int vie = 3;

    protected boolean enAnimation = false;

    public Vaisseau(String imgPath, Exhaust exhaust, Projectile projectileBLueprint, Projectile cargaisonBLueprint, Explosion explosionBlueprint, ArrayList<Projectile> projectilesList, ArrayList<Entite> cargaisonsList, ArrayList<Explosion> explosionsList, ArrayList<Asteroide> asteroidesList, ArrayList<Entite> hudList) {
        super(0, 0, 128, 128, imgPath, 5, explosionBlueprint, explosionsList);
        this.setVitesseMax(0.5f, 0.5f);
        this.setAcceleration(0.002f, 0.002f);
        this.setDecceleration(this.getAccelerationX() * 0.35f, this.getAccelerationY() * 0.25f);

        this.projectilesList = projectilesList;
        this.cargaisonsList = cargaisonsList;
        this.asteroidesList = asteroidesList;
        this.hudList = hudList;

        this.cooldownProjectile = new Cooldown(250);
        this.cooldownCargaison = new Cooldown(10000);
        this.cooldownInactif = new Cooldown(1000);
        this.cooldownImmuniter = new Cooldown(2000);

        this.exhaust = exhaust.clone();
        this.exhaust.linkPosition(this.position);

        this.projectileBLueprint = projectileBLueprint;
        this.cargaisonBLueprint = cargaisonBLueprint;

        this.hide = true;
    }

    public void reset(GameContainer container) {
        if (this.isHide()) {
            for (this.vie = 0; this.vie < 3; this.vie++) {
                this.hudList.get(this.vie).setHide(false);
            }

            this.setPosition(container.getWidth(), container.getHeight() / 2);
            this.cooldownInactif.setDoneTrue();
            this.cooldownImmuniter.setDoneTrue();
            this.cooldownCargaison.setDoneTrue();
            this.cooldownProjectile.setDoneTrue();
            this.quantiterDansVaisseau = 0;
            this.setHide(false);
            this.enAnimation = true;
            this.setVitesse(-this.vitesseMax.getX(), 0);
        }
    }

    @Override
    public Vaisseau clone() {
        Vaisseau vaisseau = (Vaisseau) super.clone();

        vaisseau.cooldownProjectile = this.cooldownProjectile.clone();
        vaisseau.cooldownCargaison = this.cooldownCargaison.clone();
        vaisseau.cooldownInactif = this.cooldownInactif.clone();

        vaisseau.exhaust = this.exhaust.clone();
        vaisseau.exhaust.setPosition(vaisseau.position);

        return vaisseau;
    }

    public void setExhaustDist(float x1, float y1, float x2, float y2) {
        this.exhaust.setDist(x1, y1 + (this.exhaust.getHeight() / 2), x2, y2 + (this.exhaust.getHeight() / 2));
    }

    public void setProjectile(Projectile projectileBLueprint) {
        this.projectileBLueprint = projectileBLueprint;
    }

    public void setCargaison(Projectile cargaisonBLueprint) {
        this.cargaisonBLueprint = cargaisonBLueprint;
    }

    public void perdVie() {
        if (this.isAlive() && !this.isEnAnimation()) {
            if (this.cooldownImmuniter.isDone() && this.cooldownInactif.isDone()) {
                this.vie--;
                this.hudList.get(this.vie).setHide(true);
                this.vitesse.y = 100.0f;

                if (this.vie == 0) {
                    System.out.println("mort");
                }
            }

            if (!this.isActive()) {
                this.cooldownInactif.setLastActionNow();
                this.cooldownImmuniter.setLastActionNow();
            }
        }
    }

    public boolean isAlive() {
        return this.vie > 0;
    }

    public boolean isActive() {
        return this.cooldownInactif.getTimeLeft() == 0;
    }

    public boolean isImmune() {
        return this.cooldownImmuniter.getTimeLeft() > 0;
    }

    public boolean isEnAnimation() {
        return enAnimation;
    }

    public void lauchProjectile() {
        if (this.isActive() && !this.isEnAnimation() && this.cooldownProjectile.isDone()) {
            Projectile newProjectile = this.projectileBLueprint.clone();
            newProjectile.setPosition(this.getPositionX(), this.getPositionY() - (this.getHeight() / 2) * scale);
            newProjectile.setScale(this.scale);
            this.projectilesList.add(newProjectile);
        }
    }

    public void lauchCargaison() {
        if (this.isActive() && !this.isEnAnimation() && this.quantiterDansVaisseau > 0 && this.cooldownCargaison.isDone()) {
            Projectile newCargaison = this.cargaisonBLueprint.clone();
            newCargaison.setPosition(this.getPositionX() + (this.getWidth() / 4) * this.scale, this.getPositionY());
            newCargaison.setValeurTransmise(this.quantiterDansVaisseau);
            newCargaison.setScale((float) Math.sqrt(this.getQuantiterDansVaisseau()) / (float) Math.sqrt(this.getCapaciter()));
            this.quantiterDansVaisseau = 0;

            this.cargaisonsList.add(newCargaison);
        }
    }

    public void addToCargaison(Asteroide asteroide) {
        int taille = (asteroide.getHeight() * asteroide.getWidth()) / 2;

        if (this.isActive() && this.quantiterDansVaisseau + taille <= this.capaciter) {
            this.quantiterDansVaisseau += taille;
            asteroide.setDetruire(true);
        }
    }

    @Override
    public void display(Graphics g) {
        if (this.isActive()) {
            super.display(g);
            this.exhaust.display(g);
        } else {
            this.getCurrentImage().setImageColor(255, 0, 0);
            super.display(g);
            this.getCurrentImage().setImageColor(255, 255, 255);
        }
    }

    @Override
    public void updateFirst(GameContainer container, float delta) {
        super.updateFirst(container, delta);

        if (!this.enAnimation) {
            final boolean UP = Keyboard.isKeyDown(Input.KEY_W) || Keyboard.isKeyDown(Input.KEY_UP);
            final boolean DOWN = Keyboard.isKeyDown(Input.KEY_S) || Keyboard.isKeyDown(Input.KEY_DOWN);
            final boolean RIGHT = Keyboard.isKeyDown(Input.KEY_D) || Keyboard.isKeyDown(Input.KEY_RIGHT);
            final boolean LEFT = Keyboard.isKeyDown(Input.KEY_A) || Keyboard.isKeyDown(Input.KEY_LEFT);

            if (this.isActive()) {
                if (UP ^ DOWN) {
                    if (UP) {
                        this.vitesse.y -= delta * this.getAccelerationY();
                    } else {
                        this.vitesse.y += delta * this.getAccelerationY();
                    }
                } else {
                    if (this.vitesse.y > 0) {
                        this.vitesse.y = Math.max(this.vitesse.y - delta * this.getDeccelerationY(), this.getVitesseMinY());
                    } else {
                        this.vitesse.y = Math.min(this.vitesse.y + delta * this.getDeccelerationY(), -this.getVitesseMinY());
                    }
                }
            }

            if (RIGHT ^ LEFT && this.isAlive()) {
                if (LEFT) {
                    this.vitesse.x -= delta * this.getAccelerationX();
                } else {
                    this.vitesse.x += delta * this.getAccelerationX();
                }
            } else {
                if (this.vitesse.x > 0) {
                    this.vitesse.x = Math.max(this.vitesse.x - delta * this.getDeccelerationX(), this.getVitesseMinX());
                } else {
                    this.vitesse.x = Math.min(this.vitesse.x + delta * this.getDeccelerationX(), -this.getVitesseMinX());
                }
            }
        }

        this.exhaust.updateFirst(container, delta);
    }

    @Override
    public void update(GameContainer container, float delta) {
        super.update(container, delta);

        this.position.x = constrain(this.position.x, 0, container.getWidth());
        this.position.y = constrain(this.position.y, 0, container.getHeight());

        if (this.isAlive()) {
            if (Keyboard.isKeyDown(Input.KEY_SPACE)) {
                this.lauchProjectile();
            }

            if (Keyboard.isKeyDown(Input.KEY_E)) {
                this.lauchCargaison();
            }
        }

        this.exhaust.update(container, delta);
    }

    @Override
    public void updateLast(GameContainer container, float delta) {
        super.updateLast(container, delta);

        switch (constrain(Math.round(this.getVitesseX() / 0.25f), -2, 2)) {
            case -2:
                frame = 0;
                this.exhaust.setOffset(1, 0, -2.5f, 0);
                break;
            case -1:
                frame = 1;
                this.exhaust.setOffset(1.5f, 0, -1.5f, 0);
                break;
            case 0:
                frame = 2;
                this.exhaust.setOffset(0, 0, 0, 0);
                break;
            case 1:
                frame = 3;
                this.exhaust.setOffset(5.5f, 0, 2.5f, 0);
                break;
            case 2:
                frame = 4;
                this.exhaust.setOffset(3f, 0, -1, 0);
                break;
        }

        this.exhaust.updateLast(container, delta);

        for (Asteroide asteroide : this.asteroidesList) {
            if (!asteroide.isDetruire() && this.detectCollision(asteroide)) {
                if (asteroide.getWidth() * asteroide.getHeight() < this.getWidth() * this.getHeight()) {
                    if ((asteroide.getPositionY() + (asteroide.getHeight() / 4)) <= (this.getPositionY() - (this.getHeight() / 4))) {
                        this.addToCargaison((Asteroide) asteroide);
                    }
                } else {
                    this.perdVie();
                }
            }
        }

        if (!this.isAlive() && this.cooldownInactif.isDone()) {
            this.setHide(true);
        }

        if (this.enAnimation) {
            float t = (this.getPositionX() - (container.getWidth() / 2)) / (-this.getVitesseX() / 2f);
            float v = this.getVitesseX() + this.getDeccelerationX() * t;

            this.setScale(1f - ((this.getPositionX() - (container.getWidth() / 2)) / (container.getWidth() / 2)));

            if (v <= 0) {
                this.vitesse.x = Math.min(this.vitesse.x + delta * this.getDeccelerationX(), 0.0f);

                if (this.getVitesseX() >= 0) {
                    this.enAnimation = false;
                    this.setScale(1f);
                }
            }
        }
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        this.exhaust.setScale(scale);
    }

    public int getQuantiterDansVaisseau() {
        return quantiterDansVaisseau;
    }

    public int getCapaciter() {
        return capaciter;
    }

    public long getTimeLeftProjectile() {
        return this.cooldownProjectile.getTimeLeft();
    }

    public long getTimeLeftCargaison() {
        return this.cooldownCargaison.getTimeLeft();
    }

    @Override
    public void lastAction() {
        this.triggerExplosion();

        this.quantiterDansVaisseau = 0;
        this.cooldownInactif.setDoneTrue();
        this.cooldownImmuniter.setDoneTrue();
        this.cooldownCargaison.setDoneTrue();
        this.cooldownProjectile.setDoneTrue();
    }
}
