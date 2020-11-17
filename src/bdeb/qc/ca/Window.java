package bdeb.qc.ca;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Window extends BasicGame {
    public static Random random = new Random();

    private GameContainer container;

    private Sound music;

    private TrueTypeFont trueTypeFont;
    private DecimalFormat decimalFormat;

    private boolean clearMemory;
    private Cooldown cooldownMemoryClear;

    private Cooldown cooldownAsteroide;

    private ArrayList[] entitesLayers;

    protected ArrayList<Entite> backgroundLayer = new ArrayList<>();
    protected ArrayList<Asteroide> asteroidsLayer = new ArrayList<>();
    protected ArrayList<Vaisseau> vaisseauxLayer = new ArrayList<>();
    protected ArrayList<Projectile> projectilesLayer = new ArrayList<>();
    protected ArrayList<Explosion> explosionsLayer = new ArrayList<>();
    protected ArrayList<Entite> hudLayer = new ArrayList<>();

    private static final int BACKGROUND_LAYER = 0;
    private static final int ASTEROIDS_LAYER = 1;
    private static final int VAISSEAUX_LAYER = 2;
    private static final int PROJECTILES_LAYER = 3;
    private static final int EXPLOSIONS_LAYER = 4;
    private static final int HUD_LAYER = 5;

    private static final String vaisseauImgPath = "sprite/vaisseau.png";
    private static final String vaisseau2ImgPath = "sprite/vaisseau2.png";
    private static final String projectileImgPath = "sprite/projectile.png";
    private static final String cielImgPath = "sprite/ciel.png";
    private static final String asteroidImgPath = "sprite/asteroid.png";
    private static final String marsImgPath = "sprite/mars.png";
    private static final String planeteImgPath = "sprite/planete2.png";
    private static final String particuleImgPath = "sprite/particule.png";
    private static final String explosionImgPath = "sprite/explosion.png";
    private static final String explosion2ImgPath = "sprite/explosion2.png";
    private static final String exhaustImgPath = "sprite/exhaust.png";
    private static final String ammoIndicatorImgPath = "sprite/ammoIndicator.png";
    private static final String healthIndicatorImgPath = "sprite/healthIndicator.png";
    private static final String cargaisonIndicatorImgPath = "sprite/cargaisonIndicator.png";

    private static final String musicSoundPath = "sound/02 Space Riddle.ogg";

    private Explosion explosionBlueprint;
    private Explosion explosion2Blueprint;

    private Exhaust exhaustBlueprint;

    private Indicator healthIndicatorBlueprint;

    private Projectile projectileBlueprint;
    private Projectile cargaisonBlueprint;

    private Asteroide asteroideBlueprint;

    private Vaisseau vaisseau;

    private Planet planet;

    private Ciel ciel;

    public Window() {
        super("TP2");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;

        this.music = new Sound(musicSoundPath); // Je sais pas pourquoi il retourne une erreur, mais il fonctionne
        this.music.loop(1f, 0.5f);

        Sound sound = new Sound("sound/Laser_Shoot12.wav");
        sound.play();

        this.entitesLayers = new ArrayList[6];
        this.entitesLayers[BACKGROUND_LAYER] = backgroundLayer;
        this.entitesLayers[ASTEROIDS_LAYER] = asteroidsLayer;
        this.entitesLayers[VAISSEAUX_LAYER] = vaisseauxLayer;
        this.entitesLayers[PROJECTILES_LAYER] = projectilesLayer;
        this.entitesLayers[EXPLOSIONS_LAYER] = explosionsLayer;
        this.entitesLayers[HUD_LAYER] = hudLayer;

        this.ciel = new Ciel(350, 400, cielImgPath, 1, container);

        this.planet = new Planet(96, 96, planeteImgPath, 4, container);

        this.explosionBlueprint = new Explosion(0, 0, 64, 64, explosionImgPath, 9, 1f);
        this.explosion2Blueprint = new Explosion(0, 0, 64, 64, explosion2ImgPath, 9, 25f);

        this.exhaustBlueprint = new Exhaust(0, 0, 23, 23, exhaustImgPath, 6);

        this.healthIndicatorBlueprint = new Indicator(0, 0, 48, 30, healthIndicatorImgPath, 1, explosionBlueprint, this.explosionsLayer);
        this.healthIndicatorBlueprint.adaptSizeToWindow(container);

        this.projectileBlueprint = new Projectile(0, 0, 10, 26, projectileImgPath, 3, 0, -1f, (float) container.getHeight() / 2.0f, this.explosionBlueprint, this.explosionsLayer, null);
        this.cargaisonBlueprint = new Projectile(0, 0, 13, 13, particuleImgPath, 3, 1f, 0, (float) container.getWidth(), this.explosion2Blueprint, this.explosionsLayer, this.planet);
        this.cargaisonBlueprint.resize(52, 52);

        this.asteroideBlueprint = new Asteroide(0, 0, 256, 256, asteroidImgPath, 4, this.explosionBlueprint, this.explosionsLayer, this.projectilesLayer, this.asteroidsLayer);

        this.vaisseau = new Vaisseau(vaisseauImgPath, this.exhaustBlueprint, this.projectileBlueprint, this.cargaisonBlueprint, this.explosionBlueprint, this.projectilesLayer, this.backgroundLayer, this.explosionsLayer, this.asteroidsLayer, this.hudLayer);
        this.vaisseau.setExhaustDist(-7, 45, 6, 45);
        this.vaisseau.setCollisionScale(0.75f);

        this.backgroundLayer.add(this.ciel);
        this.backgroundLayer.add(this.planet);

        this.vaisseauxLayer.add(this.vaisseau);

        {
            Vector2f pos = new Vector2f(container.getWidth() - this.healthIndicatorBlueprint.getWidth() * 0.5f, container.getHeight() - this.healthIndicatorBlueprint.getHeight() * 0.5f);

            for (int i = 0; i < this.vaisseau.vie; i++) {
                this.hudLayer.add(this.healthIndicatorBlueprint.clone());
                this.hudLayer.get(i).setPosition(pos.x, pos.y - (this.healthIndicatorBlueprint.getHeight() * i));
            }
        }

        // initialise the font
        Font font = new Font("Verdana", Font.BOLD, 20);
        this.trueTypeFont = new TrueTypeFont(font, true);

        this.decimalFormat = new DecimalFormat("#0.000");

        this.cooldownAsteroide = new Cooldown(adapteCooldownToSize(1500));
        this.cooldownMemoryClear = new Cooldown(10000);

        this.clearMemory = false;

        System.gc();
    }

    private int adapteCooldownToSize(int temps) {
        return (int) ((float) temps / ((float) container.getWidth() / (float) container.getHeight()));
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        for (List<Entite> entites : entitesLayers) {
            for (Entite entite : entites) {
                if (!entite.isHide()) {
                    entite.display(g);
                }
            }
        }

        String strSurMars = "Quantiter sur mars: " + this.planet.getQuantiterSurPlanete();
        this.trueTypeFont.drawString(container.getWidth() * 0.99f - this.trueTypeFont.getWidth(strSurMars), 0, strSurMars);

        String strDansVaisseau = "Quantiter dans le vaisseau: " + this.vaisseau.getQuantiterDansVaisseau() + " / " + this.vaisseau.getCapaciter();
        this.trueTypeFont.drawString(container.getWidth() * 0.99f - this.trueTypeFont.getWidth(strDansVaisseau), this.trueTypeFont.getLineHeight(), strDansVaisseau);

        String strTempsRestant = "Cooldown: " + this.decimalFormat.format((float) this.vaisseau.getTimeLeftCargaison() / 1000.0f) + "s";
        this.trueTypeFont.drawString(0, 0, strTempsRestant);

        String strProjectile = "Cooldown: " + this.decimalFormat.format((float) this.vaisseau.getTimeLeftProjectile() / 1000.0f) + "s";
        this.trueTypeFont.drawString(0, this.trueTypeFont.getLineHeight(), strProjectile);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (clearMemory && cooldownMemoryClear.isDone()) {
            System.gc();
            clearMemory = false;
            System.out.println("clear");
        }

        if (!this.vaisseau.isEnAnimation() && this.cooldownAsteroide.isDone()) {
            if (!this.vaisseau.isHide()) {
                this.cooldownAsteroide.setDelaie(adapteCooldownToSize(1500));
                Asteroide asteroide = asteroideBlueprint.clone().setNewRandom();
                asteroide.setPosition(random.nextFloat() * (container.getWidth() - asteroide.getWidth()) + (asteroide.getWidth() / 2), -(asteroide.getHeight() / 2) + 1);
                this.asteroidsLayer.add(asteroide);
            } else if (this.asteroidsLayer.size() > 0) {
                this.cooldownAsteroide.setDelaie(adapteCooldownToSize(250));
                this.asteroidsLayer.get(0).setDetruire(true);
                this.asteroidsLayer.get(0).triggerExplosion();
            }
        }

        for (List<Entite> entites : entitesLayers) {
            for (Entite entite : entites) {
                if (!entite.isHide()) {
                    entite.updateFirst(container, (float) delta);
                }
            }
        }

        for (List<Entite> entites : entitesLayers) {
            for (Entite entite : entites) {
                if (!entite.isHide()) {
                    entite.update(container, (float) delta);
                }
            }
        }

        for (List<Entite> entites : entitesLayers) {
            for (int i = 0; i < entites.size(); i++) {
                Entite entite = entites.get(i);

                if (!entite.isHide()) {
                    entite.updateLast(container, (float) delta);

                    if (entite.isDetruire()) {
                        entite.lastAction();
                        entites.remove(i);
                        i--;

                        if (!clearMemory) {
                            cooldownMemoryClear.setLastActionNow();
                        }

                        clearMemory = true;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_ESCAPE:
                this.container.exit();
                break;
            case Input.KEY_SPACE:
                if (this.vaisseau.isHide() && this.asteroidsLayer.size() == 0) {
                    this.vaisseau.reset(this.container);
                }
                break;
        }
    }

    public static int constrain(int num, int min, int max) {
        return Math.max(Math.min(num, max), min);
    }

    public static float constrain(float num, float min, float max) {
        return Math.max(Math.min(num, max), min);
    }

    public static void main(String[] args) throws SlickException {
        new AppGameContainer(new Window(), 1600, 900, false).start();
    }
}
