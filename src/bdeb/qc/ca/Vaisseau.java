package bdeb.qc.ca;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static bdeb.qc.ca.Window.*;

public class Vaisseau extends ComplexeEntitie implements Cloneable {
    protected Projectile projectileBLueprint;
    protected Projectile cargaisonBLueprint;
    protected Asteroide asteroideBlueprint;

    protected ArrayList<Projectile> projectilesList;
    protected ArrayList<Entite> backgroundList;
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

    protected Sound sonExplose;

    protected Controle controle;

    protected boolean asShot = false;
    protected boolean asSent = false;

    protected boolean moi = true;

    public Vaisseau(String imgPath, GameContainer container, Exhaust exhaust, Projectile projectileBLueprint, Projectile cargaisonBLueprint, Explosion explosionBlueprint, Asteroide asteroideBlueprint, ArrayList<Projectile> projectilesList, ArrayList<Entite> backgroundList, ArrayList<Explosion> explosionsList, ArrayList<Asteroide> asteroidesList, ArrayList<Entite> hudList, Sound sonExplose) {
        super(0, 0, 128, 128, imgPath, 5, explosionBlueprint, explosionsList);

        this.hide = true;

        this.setVitesseMax(1f, 1f);
        this.setAcceleration(0.004f, 0.004f);
        this.setDecceleration(this.getAccelerationX() * 0.35f, this.getAccelerationY() * 0.25f);

        this.projectilesList = projectilesList;
        this.backgroundList = backgroundList;
        this.asteroidesList = asteroidesList;
        this.hudList = hudList;

        this.cooldownProjectile = new Cooldown(333);
        this.cooldownCargaison = new Cooldown(10000);
        this.cooldownInactif = new Cooldown(250);
        this.cooldownImmuniter = new Cooldown(1000);

        this.exhaust = exhaust.clone();
        this.exhaust.linkPosition(this.position);

        this.projectileBLueprint = projectileBLueprint;
        this.cargaisonBLueprint = cargaisonBLueprint;
        this.asteroideBlueprint = asteroideBlueprint;

        this.sonExplose = sonExplose;

        this.controle = new Controle(container, this) {
            @Override
            boolean Up(float delta) {
                boolean clavier = !controleSouris && (Keyboard.isKeyDown(Input.KEY_W) || Keyboard.isKeyDown(Input.KEY_UP));

                boolean faireY = controleSouris && !stopPourEtreY(container.getHeight() - Mouse.getY(), delta);
                boolean sourie = faireY && ((container.getHeight() - Mouse.getY()) - entite.getPositionY()) < -1;

                return clavier || sourie;
            }

            @Override
            boolean Down(float delta) {
                boolean clavier = !controleSouris && (Keyboard.isKeyDown(Input.KEY_S) || Keyboard.isKeyDown(Input.KEY_DOWN));

                boolean faireY = controleSouris && !stopPourEtreY(container.getHeight() - Mouse.getY(), delta);
                boolean sourie = faireY && ((container.getHeight() - Mouse.getY()) - entite.getPositionY()) > 1;

                return clavier || sourie;
            }

            @Override
            boolean Right(float delta) {
                boolean clavier = !controleSouris && (Keyboard.isKeyDown(Input.KEY_D) || Keyboard.isKeyDown(Input.KEY_RIGHT));

                boolean faireX = controleSouris && !stopPourEtreX(Mouse.getX(), delta);
                boolean sourie = faireX && (Mouse.getX() - entite.getPositionX()) > 1;

                return clavier || sourie;
            }

            @Override
            boolean Left(float delta) {
                boolean clavier = !controleSouris && (Keyboard.isKeyDown(Input.KEY_A) || Keyboard.isKeyDown(Input.KEY_LEFT));

                boolean faireX = controleSouris && !stopPourEtreX(Mouse.getX(), delta);
                boolean sourie = faireX && (Mouse.getX() - entite.getPositionX()) < -1;

                return clavier || sourie;
            }

            @Override
            boolean Trigger() {
                return Keyboard.isKeyDown(Input.KEY_SPACE) || Mouse.isButtonDown(Input.MOUSE_LEFT_BUTTON);
            }

            @Override
            boolean Send() {
                return Keyboard.isKeyDown(Input.KEY_E) || Mouse.isButtonDown(Input.MOUSE_RIGHT_BUTTON);
            }
        };
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

        vaisseau.moi = false;
        vaisseau.hide = true;

        vaisseau.controle = new Controle(null, vaisseau) {
            @Override
            boolean Up(float delta) {
                return false;
            }

            @Override
            boolean Down(float delta) {
                return false;
            }

            @Override
            boolean Right(float delta) {
                return false;
            }

            @Override
            boolean Left(float delta) {
                return false;
            }

            @Override
            boolean Trigger() {
                return false;
            }

            @Override
            boolean Send() {
                return false;
            }
        };

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
            newProjectile.joueSonDebut();
            newProjectile.setMoi(this.moi);
            this.projectilesList.add(newProjectile);
            asShot = true;
        }
    }

    public void lauchCargaison() {
        if (this.isActive() && !this.isEnAnimation() && this.quantiterDansVaisseau > 0 && this.cooldownCargaison.isDone()) {
            Projectile newCargaison = this.cargaisonBLueprint.clone();
            newCargaison.setPosition(this.getPositionX() + (this.getWidth() / 4) * this.scale, this.getPositionY());
            newCargaison.setValeurTransmise(this.quantiterDansVaisseau);
            newCargaison.setScale((float) Math.sqrt(this.getQuantiterDansVaisseau()) / (float) Math.sqrt(this.getCapaciter()));
            this.quantiterDansVaisseau = 0;

            newCargaison.joueSonDebut();
            this.backgroundList.add(newCargaison);
            asSent = true;
        }
    }

    public void addToCargaison(Asteroide asteroide) {
        int taille = (asteroide.getHeight() * asteroide.getWidth()) / 2;

        if (this.isActive() && this.quantiterDansVaisseau + taille <= this.capaciter) {
            this.quantiterDansVaisseau += taille;

            Asteroide asteroideResidue = asteroide.clone();
            asteroideResidue.setScale(asteroideResidue.getScale() / 2);
            asteroideResidue.setPosition(this.getPositionX(), this.getPositionY() + this.getHeight() / 2);
            asteroideResidue.setCouleur(new Color(55, 55, 55));
            asteroideResidue.setVitesse(0, asteroideResidue.getVitesseMaxY());
            asteroideResidue.setMultRotation(1);
            backgroundList.add(asteroideResidue);

            asteroide.setDetruire(true);
        }
    }

    @Override
    public void display(Graphics g) {
        super.display(g);

        if (this.isActive()) {
            this.exhaust.display(g);
        }
    }

    @Override
    public void updateFirst(GameContainer container, float delta) {
        super.updateFirst(container, delta);

        this.controle.update(delta);

        if (!this.enAnimation) {

            if (this.isActive()) {
                if (this.controle.isUp() ^ this.controle.isDown()) {
                    if (this.controle.isUp()) {
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

            if (this.controle.isRight() ^ this.controle.isLeft() && this.isAlive()) {
                if (this.controle.isLeft()) {
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
            if (controle.isTrigger()) {
                this.lauchProjectile();
            }

            if (controle.isSend()) {
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
                } else if (this.moi) {
                    this.perdVie();
                }
            }
        }

        if (!this.isAlive() && this.cooldownInactif.isDone()) {
            this.setHide(true);
        }

        if (this.enAnimation) {
            this.setScale(1f - ((this.getPositionX() - (container.getWidth() / 2)) / (container.getWidth() / 2)));

            if (stopPourEtreX(container.getWidth() / 2, delta) || this.getVitesseX() > -this.getVitesseMaxX()) {
                this.vitesse.x = Math.min(this.vitesse.x + delta * this.getDeccelerationX(), 0.0f);

                if (this.getVitesseX() >= 0) {
                    System.out.println(getPositionX());
                    this.enAnimation = false;
                    this.setScale(1f);
                }
            }
        }

        if (isActive() && this.couleur.g != this.couleur.r) {
            this.couleur = new Color(255, 255, 255);
        } else if (!isActive() && this.couleur.g == this.couleur.r) {
            this.couleur = new Color(255, 0, 0);
        }
    }

    public boolean stopPourEtre(float destination, float position, float vitesse, float decceleration, float delta) {
        float t = Math.abs(position - destination) / Math.abs(vitesse * delta / 2f);
        float v = Math.abs(vitesse) - Math.abs(decceleration * t * delta);
        float p = Math.abs(position - destination) - Math.abs(vitesse * t * delta) + Math.abs((decceleration / 2f) * t * t * delta * delta);

        return p <= 0 || v >= 0;
    }

    public boolean stopPourEtreX(float x, float delta) {
        return this.getVitesseX() != 0 && stopPourEtre(x, this.getPositionX(), this.getVitesseX(), this.getDeccelerationX(), delta);
    }

    public boolean stopPourEtreY(float y, float delta) {
        return this.getVitesseY() != 0 && stopPourEtre(y, this.getPositionY(), this.getVitesseY(), this.getDeccelerationY(), delta);
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

    public void getComunicationInfo(ConcurrentLinkedQueue<Queue<Float>> concurrentLinkedQueueServer, Queue<Float> nouveauAsteroide) {
        Queue<Float> tab = new LinkedBlockingQueue<>();
        tab.add(this.getPositionX());
        tab.add(this.getPositionY());

        tab.add(this.getVitesseX());
        tab.add(this.getVitesseY());

        tab.add(this.getScale());

        tab.add(this.isHide() ? 1f : 0f);

        tab.add(this.asShot ? 1f : 0f);
        tab.add(this.asSent ? 1f : 0f);

        this.asShot = false;
        this.asSent = false;

        while (!nouveauAsteroide.isEmpty()) {
            tab.add(nouveauAsteroide.poll());
        }

        concurrentLinkedQueueServer.add(tab);
    }

    public void setComunicationInfo(ConcurrentLinkedQueue<Queue<Float>> concurrentLinkedQueueClient) {
        Queue<Float> tab = null;

        while (!concurrentLinkedQueueClient.isEmpty()) {
            tab = concurrentLinkedQueueClient.poll();
        }

        if (tab != null) {
            this.setPosition(tab.poll(), tab.poll());

            this.setVitesse(tab.poll(), tab.poll());

            this.setScale(tab.poll());

            boolean hide = tab.poll() == 1f;

            if (hide && !this.isHide()) {
                this.setHide(true);
            } else if (!hide && this.isHide()) {
                this.setHide(false);
            }

            if (tab.poll() == 1f) {
                this.cooldownProjectile.setDoneTrue();
                this.lauchProjectile();
            }

            if (tab.poll() == 1f) {
                this.cooldownCargaison.setDoneTrue();
                this.lauchCargaison();
            }

            while (!tab.isEmpty()) {
                Asteroide asteroide = asteroideBlueprint.clone();
                asteroide.setPosition(tab.poll(), tab.poll());
                asteroide.setVitesse(tab.poll(), tab.poll());
                asteroide.setMultRotation(tab.poll());
                asteroide.setScale(tab.poll());
                asteroide.setFrame((int) (tab.poll() + 0));
                this.asteroidesList.add(asteroide);
            }
        }

    }

    @Override
    public void lastAction() {
        this.triggerExplosion();
        this.sonExplose.play(1f, 0.2f);

        int quantiterEnlever = 16 * 16;
        float decal = 0;

        while (this.quantiterDansVaisseau > 0) {
            quantiterEnlever = Math.min(quantiterEnlever, this.quantiterDansVaisseau);

            Asteroide asteroideResidue = asteroideBlueprint.clone();
            asteroideResidue.setScale((float) Math.sqrt(quantiterEnlever) / (float) asteroideResidue.getWidth());
            asteroideResidue.setPosition(this.getPositionX() + random.nextFloat() * (this.getWidth() - decal) - ((this.getWidth() - decal) / 2f), this.getPositionY() + decal);
            asteroideResidue.setCouleur(new Color(55, 55, 55));
            asteroideResidue.setVitesse(0, asteroideResidue.getVitesseMaxY() + decal);
            asteroideResidue.setMultRotation(1);
            backgroundList.add(asteroideResidue);

            System.out.println(quantiterDansVaisseau);
            decal = Math.min(decal + this.getVitesseMaxY(), this.getWidth());

            this.quantiterDansVaisseau -= quantiterEnlever;
        }

        this.quantiterDansVaisseau = 0;
        this.cooldownInactif.setDoneTrue();
        this.cooldownImmuniter.setDoneTrue();
        this.cooldownCargaison.setDoneTrue();
        this.cooldownProjectile.setDoneTrue();
    }
}
