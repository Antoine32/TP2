package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.ArrayList;

import static bdeb.qc.ca.Window.random;

public class Planet extends Entite implements Cloneable, Receiver {
    protected Image[][] mainImage;

    protected int quantiterSurPlanete = 0;

    protected ArrayList<Integer> possibiliter = new ArrayList<>();

    protected int scaleX;
    protected int scaleY;

    public Planet(int width, int height, String imgPath, int amountImg, GameContainer container) {
        super(0, 0, width, height, imgPath, amountImg);

        this.mainImage = new Image[48][48];

        //this.resize(container.getHeight() * 2 - ((container.getHeight() * 2) % this.mainImage.length), container.getHeight() * 2 - ((container.getHeight() * 2) % this.mainImage.length));
        this.setScale((container.getHeight() * 2 - ((container.getHeight() * 2) % this.mainImage.length)) / this.width);

        this.setPosition(container.getWidth() + (this.getWidth() / 3), container.getHeight() / 2);

        this.scaleX = this.width / this.mainImage.length;
        this.scaleY = this.height / this.mainImage[0].length;

        for (int i = 0; i < this.mainImage.length; i++) {
            for (int j = 0; j < this.mainImage[i].length; j++) {
                this.mainImage[i][j] = this.images[0].getSubImage(i * scaleX, j * scaleY, scaleX, scaleY);
                //this.mainImage[i][j].setCenterOfRotation(this.width / 2 - (i * scaleX), this.height / 2 - (j * scaleY));
                this.possibiliter.add(i + (j * this.mainImage.length));
            }
        }
    }

    @Override
    public Planet clone() {
        return (Planet) super.clone();
    }

    @Override
    public void display(Graphics g) {
        g.pushTransform();
        //g.translate(this.position.x - (this.width / 2), this.position.y - (this.height / 2));
        g.scale(scale, scale);
        g.translate(this.position.x * adapt - (this.width / 2), this.position.y * adapt - (this.height / 2));
        g.rotate(this.width / 2, this.height / 2, this.rotation);

        for (int i = 0; i < this.mainImage.length; i++) {
            for (int j = 0; j < this.mainImage[i].length; j++) {
                this.mainImage[i][j].draw((i * this.scaleX), (j * this.scaleY));
            }
        }

        g.popTransform();
    }

    @Override
    public void update(GameContainer container, float delta) {
        super.update(container, delta);

        this.rotation -= delta / 500f;

        for (int i = 0; i < this.mainImage.length; i++) {
            for (int j = 0; j < this.mainImage[i].length; j++) {
                //this.mainImage[i][j].setRotation(this.rotation);
            }
        }
    }

    public int getQuantiterSurPlanete() {
        return quantiterSurPlanete;
    }

    @Override
    public void send(int packet) {
        this.quantiterSurPlanete += packet;

        if (this.frame < this.images.length - 1) {
            int indexX, indexY;
            int indexPossibiliter;
            int size = this.mainImage.length * this.mainImage[0].length;
            int importance = (this.quantiterSurPlanete / 256) - (this.frame * size) - (size - this.possibiliter.size());

            for (int a = 0; a < importance; a++) {
                indexPossibiliter = random.nextInt(this.possibiliter.size());
                indexX = possibiliter.get(indexPossibiliter) % this.mainImage.length;
                indexY = possibiliter.get(indexPossibiliter) / this.mainImage.length;
                this.possibiliter.remove(indexPossibiliter);

                this.mainImage[indexX][indexY] = this.images[this.frame + 1].getSubImage((indexX * scaleX), (indexY * scaleY), scaleX, scaleY);

                if (this.possibiliter.size() == 0) {
                    this.frame++;

                    for (int i = 0; i < this.mainImage.length; i++) {
                        for (int j = 0; j < this.mainImage[i].length; j++) {
                            this.possibiliter.add(i + (j * this.mainImage.length));
                        }
                    }
                    break;
                }
            }

            System.gc();
        }
    }
}
