package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;

import java.util.ArrayList;


public class Indicator extends ComplexeEntitie implements Cloneable {

    public Indicator(float x, float y, int width, int height, String imgPath, int amountImg, Explosion explosionBlueprint, ArrayList<Explosion> explosionsList) {
        super(x, y, width, height, imgPath, amountImg, explosionBlueprint, explosionsList);
        this.explosionBlueprint = explosionBlueprint;
        this.explosionsList = explosionsList;
        this.hide = true;
    }

    @Override
    public Indicator clone() {
        Indicator indicator = (Indicator) super.clone();
        return indicator;
    }

    public void adaptSizeToWindow(GameContainer container) {
        int w = (container.getHeight() / 256) * width;
        int h = (container.getHeight() / 256) * height;
        this.resize(w, h);
    }

    @Override
    public void lastAction() {
        this.triggerExplosion();
    }
}
