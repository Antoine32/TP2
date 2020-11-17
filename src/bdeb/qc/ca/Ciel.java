package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Ciel extends Entite implements Cloneable {
    public Ciel(int width, int height, String imgPath, int amountImg, GameContainer container) {
        super(container.getWidth() / 2, container.getHeight() / 2, width, height, imgPath, amountImg);
        this.resize(container.getWidth(), container.getHeight());
        this.setVitesse(0, 0.5f);
    }

    @Override
    public Ciel clone() {
        return (Ciel) super.clone();
    }

    @Override
    public void display(Graphics g) {
        super.display(g);
        g.drawImage(images[frame], position.x - (width / 2), position.y - 3 * (height / 2) + 1);
    }

    @Override
    public void update(GameContainer container, float delta) {
        super.update(container, delta);

        if (this.getPositionY() >= container.getHeight() * 1.5f) {
            this.position.y -= container.getHeight();
        }
    }
}
