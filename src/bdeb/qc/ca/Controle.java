package bdeb.qc.ca;

import org.newdawn.slick.GameContainer;

public abstract class Controle {
    private boolean up = false;
    private boolean down = false;
    private boolean right = false;
    private boolean left = false;

    private boolean trigger = false;
    private boolean send = false;

    Entite entite = null;
    GameContainer container = null;

    public Controle() {
    }

    public Controle(Entite entite) {
        this.entite = entite;
    }

    public Controle(GameContainer container) {
        this.container = container;
    }

    public Controle(GameContainer container, Entite entite) {
        this.container = container;
        this.entite = entite;
    }

    public void update(float delta) {
        up = Up(delta);
        down = Down(delta);
        right = Right(delta);
        left = Left(delta);

        trigger = Trigger();
        send = Send();
    }

    abstract boolean Up(float delta);

    abstract boolean Down(float delta);

    abstract boolean Right(float delta);

    abstract boolean Left(float delta);

    abstract boolean Trigger();

    abstract boolean Send();

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public boolean isSend() {
        return send;
    }
}
