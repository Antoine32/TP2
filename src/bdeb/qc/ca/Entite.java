package bdeb.qc.ca;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import static bdeb.qc.ca.Window.constrain;

public abstract class Entite implements Cloneable {
    protected Vector2f position;
    protected Vector2f vitesse;
    protected Vector2f vitesseMax;
    protected Vector2f vitesseMin;
    protected Vector2f acceleration;
    protected Vector2f decceleration;
    protected float rotation = 0;
    protected Color couleur = new Color(255, 255, 255);

    protected float scale = 1f;
    protected float adapt = 1f / scale;

    protected int width;
    protected int height;

    protected int frame;
    protected Image[] images;

    protected boolean deleteOnOutOfFrame;
    protected boolean detruire;

    protected boolean hide;

    public Entite(float x, float y, int width, int height, String imgPath, int amountImg) {
        this.position = new Vector2f(x, y);
        this.vitesse = new Vector2f(0, 0);
        this.vitesseMax = new Vector2f(1, 1);
        this.vitesseMin = new Vector2f(0, 0);
        this.acceleration = new Vector2f(0, 0);
        this.decceleration = new Vector2f(0, 0);

        this.width = width;
        this.height = height;

        this.images = new Image[amountImg];
        this.frame = 0;

        this.deleteOnOutOfFrame = false;
        this.detruire = false;

        this.hide = false;

        try {
            SpriteSheet spriteSheet = new SpriteSheet(imgPath, this.width, this.height);

            for (int i = 0; i < this.images.length; i++) {
                this.images[i] = spriteSheet.getSprite(i, 0);
                this.images[i].setCenterOfRotation(this.images[i].getWidth() / 2, this.images[i].getHeight() / 2);
            }
        } catch (SlickException e) {
            System.out.println("Image non trouvée pour " + getClass());
        }
    }

    @Override
    public Entite clone() {
        Entite entite;

        try {
            entite = (Entite) super.clone();
            entite.position = this.position.copy();
            entite.vitesse = this.vitesse.copy();
            entite.rotation = 0;
            entite.detruire = false;

            //entite.images = new Image[this.images.length];
            //for (int i = 0; i < entite.images.length; i++) {
            //entite.images[i] = this.images[i].copy();
            //}
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            entite = null;
        }

        return entite;
    }

    public Image getCurrentImage() {
        return this.images[this.frame];
    }

    public void display(Graphics g) {
        g.pushTransform();
        g.setColor(couleur);
        g.scale(scale, scale);
        g.translate(this.position.x * adapt - (this.width / 2), this.position.y * adapt - (this.height / 2));
        //g.drawOval(0, 0, this.width, this.width);
        g.rotate(this.width / 2, this.height / 2, this.rotation);
        g.drawImage(this.getCurrentImage(), 0, 0, this.couleur);
        g.popTransform();
    }

    public void updateFirst(GameContainer container, float delta) {
    }

    public void update(GameContainer container, float delta) {
        this.vitesse.x = constrain(this.vitesse.x, -this.vitesseMax.x, this.vitesseMax.x);
        this.vitesse.y = constrain(this.vitesse.y, -this.vitesseMax.y, this.vitesseMax.y);

        this.position.x += this.vitesse.x * delta;
        this.position.y += this.vitesse.y * delta;
    }

    public void updateLast(GameContainer container, float delta) {
        if (this.deleteOnOutOfFrame && ((this.position.x - (this.getWidth() / 2) >= container.getWidth()) ||
                (this.position.x + (this.getWidth() / 2) < 0) ||
                (this.position.y - (this.getHeight() / 2) >= container.getHeight()) ||
                (this.position.y + (this.getHeight() / 2) < 0))) {
            this.setDetruire(true);
        }
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        for (int i = 0; i < this.images.length; i++) {
            this.images[i] = this.images[i].getScaledCopy(width, height);
            this.images[i].setCenterOfRotation(this.images[i].getWidth() / 2, this.images[i].getHeight() / 2);
        }
    }

    public void lastAction() {
    }

    public float getScale() {
        return scale;
    }

    public float getPositionX() {
        return this.position.getX();
    }

    public float getPositionY() {
        return this.position.getY();
    }

    public float getVitesseX() {
        return this.vitesse.getX();
    }

    public float getVitesseY() {
        return this.vitesse.getY();
    }

    public float getVitesseMaxX() {
        return vitesseMax.getX();
    }

    public float getVitesseMaxY() {
        return vitesseMax.getY();
    }

    public float getVitesseMinX() {
        return vitesseMin.getX();
    }

    public float getVitesseMinY() {
        return vitesseMin.getY();
    }

    public float getAccelerationX() {
        return this.acceleration.getX();
    }

    public float getAccelerationY() {
        return this.acceleration.getY();
    }

    public float getDeccelerationX() {
        return this.decceleration.getX();
    }

    public float getDeccelerationY() {
        return this.decceleration.getY();
    }

    public Color getCouleur() {
        return couleur;
    }

    public int getWidth() { // Largeur de l’entite
        int w = (int) (this.width * this.scale);
        return w;
    }

    public int getHeight() { // Hauteur de l’entite
        int h = (int) (this.height * this.scale);
        return h;
    }

    public int getFrame() {
        int f = this.frame;
        return f;
    }

    public boolean isDeleteOnOutOfFrame() {
        return deleteOnOutOfFrame;
    }

    public boolean isDetruire() {
        return detruire;
    }

    public boolean isHide() {
        return hide;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.adapt = 1f / scale;
    }

    public void setDetruire(boolean detruire) {
        this.detruire = detruire;
    }

    public void setHide(boolean hide) {
        if (hide) {
            this.lastAction();
        }
        this.hide = hide;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public void linkPosition(Vector2f position) {
        this.position = position;
    }

    public void setVitesse(float x, float y) {
        this.vitesse.set(x, y);
    }

    public void setVitesse(Vector2f position) {
        this.vitesse.set(position);
    }

    public void setVitesseMax(Vector2f vitesseMax) {
        this.vitesseMax.set(vitesseMax);
    }

    public void setVitesseMax(float x, float y) {
        this.vitesseMax.set(x, y);
    }

    public void setVitesseMin(Vector2f vitesseMin) {
        this.vitesseMin.set(vitesseMin);
    }

    public void setVitesseMin(float x, float y) {
        this.vitesseMin.set(x, y);
    }

    public void setAcceleration(Vector2f acceleration) {
        this.acceleration.set(acceleration);
    }

    public void setAcceleration(float x, float y) {
        this.acceleration.set(x, y);
    }

    public void setDecceleration(Vector2f acceleration) {
        this.decceleration.set(acceleration);
    }

    public void setDecceleration(float x, float y) {
        this.decceleration.set(x, y);
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public void setDeleteOnOutOfFrame(boolean deleteOnOutOfFrame) {
        this.deleteOnOutOfFrame = deleteOnOutOfFrame;
    }
}
