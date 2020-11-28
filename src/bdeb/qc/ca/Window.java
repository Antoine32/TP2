package bdeb.qc.ca;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.awt.Font;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Window extends BasicGame {
    public static Random random = new Random();

    public static boolean controleSouris = true;

    private GameContainer container;

    private Sound music;

    private TrueTypeFont trueTypeFont;
    private TrueTypeFont trueTypeFontTitle;
    private DecimalFormat decimalFormat;

    private Cooldown cooldownAsteroide;

    private ArrayList[] entitesLayers;

    private ArrayList<Entite> backgroundLayer = new ArrayList<>();
    private ArrayList<Asteroide> asteroidsLayer = new ArrayList<>();
    private ArrayList<Vaisseau> vaisseauxLayer = new ArrayList<>();
    private ArrayList<Projectile> projectilesLayer = new ArrayList<>();
    private ArrayList<Explosion> explosionsLayer = new ArrayList<>();
    private ArrayList<Entite> hudLayer = new ArrayList<>();

    private static final int BACKGROUND_LAYER = 0;
    private static final int ASTEROIDS_LAYER = 1;
    private static final int VAISSEAUX_LAYER = 2;
    private static final int PROJECTILES_LAYER = 3;
    private static final int EXPLOSIONS_LAYER = 4;
    private static final int HUD_LAYER = 5;

    private static final String vaisseauImgPath = "sprite/vaisseau.png";
    private static final String projectileImgPath = "sprite/projectile.png";
    private static final String cielImgPath = "sprite/ciel.png";
    private static final String asteroidImgPath = "sprite/asteroid.png";
    private static final String planeteImgPath = "sprite/planete2.png";
    private static final String particuleImgPath = "sprite/particule.png";
    private static final String explosionImgPath = "sprite/explosion.png";
    private static final String explosion2ImgPath = "sprite/explosion2.png";
    private static final String exhaustImgPath = "sprite/exhaust.png";
    private static final String ammoIndicatorImgPath = "sprite/ammoIndicator.png";
    private static final String healthIndicatorImgPath = "sprite/healthIndicator.png";
    private static final String cargaisonIndicatorImgPath = "sprite/cargaisonIndicator.png";

    private static final String musicSoundPath = "sound/02 Space Riddle.ogg";
    private static final String laserSoundPath = "sound/Laser_Shoot9.wav";
    private static final String explosionSoundPath = "sound/Explosion6.wav";
    private static final String explosionVaisseauSoundPath = "sound/Explosion7.wav";
    private static final String cargaisonExplosionSoundPath = "sound/Hit_Hurt12.wav";
    private static final String cargaisonSoundPath = "sound/Hit_Hurt2.wav";

    private Explosion explosionBlueprint;
    private Explosion explosion2Blueprint;

    private Exhaust exhaustBlueprint;

    private Indicator healthIndicatorBlueprint;

    private Projectile projectileBlueprint;
    private Projectile cargaisonBlueprint;

    private Asteroide asteroideBlueprint;

    private Vaisseau vaisseau;

    private Vaisseau vaisseauB;

    private Planet planet;

    private Ciel ciel;

    private Server server;
    private Client client;

    private ConcurrentLinkedQueue<Queue<Object>> queueVaisseauServer;
    private ConcurrentLinkedQueue<Queue<String>> queueVaisseauClient;

    private Queue<UUID> ancienEntite;
    private Queue<Object> nouveauAsteroide;

    public static boolean communication = false;
    private boolean communicationSlave = false;

    private String address;

    private boolean playing = false;

    public static float scl;

    public Window(String address) {
        super("TP2");
        this.address = address;
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        this.container = container;
        container.setShowFPS(false);
        container.setVSync(true);
        container.setAlwaysRender(true);

        scl = container.getHeight() / 900f;

        this.queueVaisseauServer = new ConcurrentLinkedQueue<Queue<Object>>();
        this.queueVaisseauClient = new ConcurrentLinkedQueue<Queue<String>>();

        this.ancienEntite = new LinkedBlockingQueue<UUID>();
        this.nouveauAsteroide = new LinkedBlockingQueue<Object>();

        this.music = new Sound(musicSoundPath); // Je sais pas pourquoi il retourne une erreur, mais il fonctionne
        this.music.loop(1f, 0.5f);

        Sound sonLaser = new Sound(laserSoundPath);
        Sound sonExplosion = new Sound(explosionSoundPath);

        Sound sonExplosionVaisseau = new Sound(explosionVaisseauSoundPath);
        Sound sonCargaisonExplosion = new Sound(cargaisonExplosionSoundPath);

        Sound sonCargaison = new Sound(cargaisonSoundPath);

        this.entitesLayers = new ArrayList[6];
        this.entitesLayers[BACKGROUND_LAYER] = backgroundLayer;
        this.entitesLayers[ASTEROIDS_LAYER] = asteroidsLayer;
        this.entitesLayers[VAISSEAUX_LAYER] = vaisseauxLayer;
        this.entitesLayers[PROJECTILES_LAYER] = projectilesLayer;
        this.entitesLayers[EXPLOSIONS_LAYER] = explosionsLayer;
        this.entitesLayers[HUD_LAYER] = hudLayer;

        this.ciel = new Ciel(350, 400, cielImgPath, 1, container);

        this.planet = new Planet(96, 96, planeteImgPath, 4, container);

        this.explosionBlueprint = new Explosion(0, 0, 64, 64, explosionImgPath, 9, 1f, sonExplosion);
        this.explosion2Blueprint = new Explosion(0, 0, 64, 64, explosion2ImgPath, 9, 25f, sonCargaisonExplosion);

        this.exhaustBlueprint = new Exhaust(0, 0, 23, 23, exhaustImgPath, 6);

        this.healthIndicatorBlueprint = new Indicator(0, 0, 48, 30, healthIndicatorImgPath, 1, explosionBlueprint,
                this.explosionsLayer);
        this.healthIndicatorBlueprint.adaptSizeToWindow(container);

        this.projectileBlueprint = new Projectile(0, 0, 10, 26, projectileImgPath, 3, 0, -1f,
                (float) container.getHeight() / 2.0f, this.explosionBlueprint, this.explosionsLayer, null, sonLaser);
        this.cargaisonBlueprint = new Projectile(0, 0, 13, 13, particuleImgPath, 3, 1f, 0, (float) container.getWidth(),
                this.explosion2Blueprint, this.explosionsLayer, this.planet, sonCargaison);
        this.cargaisonBlueprint.resize(52, 52);

        this.asteroideBlueprint = new Asteroide(0, 0, 256, 256, asteroidImgPath, 4, this.explosionBlueprint,
                this.explosionsLayer, this.projectilesLayer, this.asteroidsLayer, this.ancienEntite, this.nouveauAsteroide);

        this.vaisseau = new Vaisseau(vaisseauImgPath, container, this.exhaustBlueprint, this.projectileBlueprint,
                this.cargaisonBlueprint, this.explosionBlueprint, this.asteroideBlueprint, this.projectilesLayer,
                this.backgroundLayer, this.explosionsLayer, this.asteroidsLayer, sonExplosionVaisseau);
        this.vaisseau.setExhaustDist(-7, 45, 6, 45);
        this.vaisseau.setCollisionScale(0.75f);

        this.vaisseauB = this.vaisseau.clone();
        this.vaisseauB.setCouleur(new Color(0, 0, 255));

        this.backgroundLayer.add(this.ciel);
        this.backgroundLayer.add(this.planet);

        this.vaisseauxLayer.add(this.vaisseau);

        {
            Vector2f pos = new Vector2f(container.getWidth() - this.healthIndicatorBlueprint.getWidth() * 0.5f,
                    container.getHeight() - this.healthIndicatorBlueprint.getHeight() * 0.5f);

            for (int i = 0; i < this.vaisseau.vie; i++) {
                Indicator ind = this.healthIndicatorBlueprint.clone();
                ind.setPosition(pos.x, pos.y - (this.healthIndicatorBlueprint.getHeight() * i));
                this.hudLayer.add(ind);
                this.vaisseau.vieList.add(ind);
            }
        }

        {
            Vector2f pos = new Vector2f(this.healthIndicatorBlueprint.getWidth() * 0.5f,
                    container.getHeight() - this.healthIndicatorBlueprint.getHeight() * 0.5f);

            for (int i = 0; i < this.vaisseau.vie; i++) {
                Indicator ind = this.healthIndicatorBlueprint.clone();
                ind.setPosition(pos.x, pos.y - (this.healthIndicatorBlueprint.getHeight() * i));
                ind.setCouleur(new Color(0, 0, 255));
                this.hudLayer.add(ind);
                this.vaisseauB.vieList.add(ind);
            }
        }

        // initialise the font
        Font font = new Font("Verdana", Font.BOLD, 20);
        this.trueTypeFont = new TrueTypeFont(font, true);

        Font fontTitle = new Font("Verdana", Font.BOLD, 40);
        this.trueTypeFontTitle = new TrueTypeFont(fontTitle, true);

        this.decimalFormat = new DecimalFormat("#0.000");

        this.cooldownAsteroide = new Cooldown(adapteCooldownToSize(1500));

        System.gc();
    }

    private int adapteCooldownToSize(int temps) {
        return (int) ((float) temps / ((float) container.getWidth() / (float) container.getHeight()));
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        for (ArrayList<Entite> entites : entitesLayers) {
            for (Entite entite : entites) {
                if (!entite.isHide()) {
                    entite.display(g);
                }
            }
        }

        if (this.vaisseau.isHide()) {
            String strDebutJeu = "Espace pour commencer";
            this.trueTypeFontTitle.drawString(
                    (container.getWidth() - this.trueTypeFontTitle.getWidth(strDebutJeu)) * 0.5f,
                    (container.getHeight() - this.trueTypeFontTitle.getHeight(strDebutJeu)) * 0.5f, strDebutJeu);
        }

        String strSurMars = "Quantité sur mars: " + this.planet.getQuantiterSurPlanete();
        this.trueTypeFont.drawString(container.getWidth() * 0.99f - this.trueTypeFont.getWidth(strSurMars), 0,
                strSurMars);

        String strDansVaisseau = "Quantité dans le vaisseau: " + this.vaisseau.getQuantiterDansVaisseau() + " / "
                + this.vaisseau.getCapaciter();
        this.trueTypeFont.drawString(container.getWidth() * 0.99f - this.trueTypeFont.getWidth(strDansVaisseau),
                this.trueTypeFont.getLineHeight(), strDansVaisseau);

        String strTempsRestant = "Cooldown: "
                + this.decimalFormat.format((float) this.vaisseau.getTimeLeftCargaison() / 1000.0f) + "s";
        this.trueTypeFont.drawString(0, 0, strTempsRestant);

        String strProjectile = "Cooldown: "
                + this.decimalFormat.format((float) this.vaisseau.getTimeLeftProjectile() / 1000.0f) + "s";
        this.trueTypeFont.drawString(0, this.trueTypeFont.getLineHeight(), strProjectile);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        float deltaf = (float) delta;

        if (Mouse.isButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            this.restart();
        }

        if (this.cooldownAsteroide.isDone()) {
            if (playing) {
                if (!communicationSlave && !this.vaisseau.isEnAnimation()) {
                    this.cooldownAsteroide.setDelaie(adapteCooldownToSize(1500));
                    Asteroide asteroide = asteroideBlueprint.clone().setNewRandom();
                    asteroide.setPosition(random.nextFloat() * (container.getWidth() - asteroide.getWidth())
                            + (asteroide.getWidth() / 2), -(asteroide.getHeight() / 2) + 1);
                    this.asteroidsLayer.add(asteroide);

                    if (communication) {
                        asteroide.toNouveau();
                    }
                }
            } else if (this.asteroidsLayer.size() > 0) {
                this.cooldownAsteroide.setDelaie(adapteCooldownToSize(250));
                this.asteroidsLayer.get(0).setDetruire(true);
                this.asteroidsLayer.get(0).triggerExplosion();
            }
        }

        if (communication) {
            this.vaisseauB.setCouleur(new Color(0, 0, 255));
            this.vaisseauB.setComunicationInfo(queueVaisseauClient);

            if (queueVaisseauServer.isEmpty()) {
                this.vaisseau.getComunicationInfo(queueVaisseauServer, ancienEntite, nouveauAsteroide);
            }
        }

        for (ArrayList<Entite> entites : entitesLayers) {
            for (Entite entite : entites) {
                if (!entite.isHide()) {
                    entite.updateFirst(container, deltaf);
                }
            }
        }

        for (ArrayList<Entite> entites : entitesLayers) {
            for (Entite entite : entites) {
                if (!entite.isHide()) {
                    entite.update(container, deltaf);
                }
            }
        }

        for (ArrayList<Entite> entites : entitesLayers) {
            for (int i = 0; i < entites.size(); i++) {
                Entite entite = entites.get(i);

                if (!entite.isHide()) {
                    entite.updateLast(container, deltaf);

                    if (entite.isDetruire()) {
                        entite.lastAction();
                        entites.remove(i);
                        i--;
                    }
                }
            }
        }

        if (this.playing) {
            boolean notHiden = false;

            for (Vaisseau vaisseau : vaisseauxLayer) {
                notHiden = notHiden || !vaisseau.isHide();
            }

            if (!notHiden) {
                this.playing = false;
            }
        } else {
            boolean hiden = false;

            for (Vaisseau vaisseau : vaisseauxLayer) {
                hiden = hiden || vaisseau.isHide();
            }

            if (!hiden) {
                this.playing = true;
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
                this.restart();
                break;
            case Input.KEY_MINUS:
                if (!communication) {
                    try {
                        this.vaisseauxLayer.add(this.vaisseauB);

                        server = new Server(60606, queueVaisseauServer);
                        server.start();

                        client = new Client(50505, address, queueVaisseauClient);
                        client.start();

                        communication = true;
                        communicationSlave = false;
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Input.KEY_EQUALS:
                if (!communication) {
                    try {
                        this.vaisseauxLayer.add(this.vaisseauB);

                        server = new Server(50505, queueVaisseauServer);
                        server.start();

                        client = new Client(60606, address, queueVaisseauClient);
                        client.start();

                        communication = true;
                        communicationSlave = true;
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void restart() {
        if (!this.playing && this.asteroidsLayer.size() == 0) {
            this.vaisseauxLayer.forEach((vaisseau) -> vaisseau.reset(this.container));
        }
    }

    public static int constrain(int num, int min, int max) {
        return Math.max(Math.min(num, max), min);
    }

    public static float constrain(float num, float min, float max) {
        return Math.max(Math.min(num, max), min);
    }

    public static void main(String[] args) throws SlickException {
        String address = "127.0.0.1";

        int width = 1600;
        int height = 900;
        boolean fullscreen = false;

        if (args.length > 0) {
            address = args[0];
        }

        if (args.length > 1) {
            width = Integer.parseInt(args[1]);
        }

        if (args.length > 2) {
            height = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            fullscreen = args[3].contentEquals("true");
        }

        if (args.length > 4) {
            controleSouris = args[4].contentEquals("true");
        }

        new AppGameContainer(new Window(address), width, height, fullscreen).start();
    }
}
