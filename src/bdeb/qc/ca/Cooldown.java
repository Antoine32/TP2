package bdeb.qc.ca;

public class Cooldown implements Cloneable {
    protected long delaie;
    protected long lastAction;

    Cooldown(long delaie) {
        this.delaie = delaie;
        this.lastAction = System.currentTimeMillis() - this.delaie;
    }

    @Override
    public Cooldown clone() {
        Cooldown cooldown;

        try {
            cooldown = (Cooldown) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cooldown = null;
        }

        return cooldown;
    }

    public boolean isDone() {
        if (this.getTimeLeft() == 0 && this.getDelaie() > -1) {
            this.lastAction = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public long getDelaie() {
        return this.delaie;
    }

    public long getLastAction() {
        return this.lastAction;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - this.lastAction;
    }

    public long getTimeLeft() {
        return Math.max(this.delaie - this.getTimePassed(), 0);
    }

    public void setDelaie(long delaie) {
        this.delaie = delaie;
    }

    public void setLastActionNow() {
        this.lastAction = System.currentTimeMillis();
    }

    public void setDoneTrue() {
        this.lastAction = System.currentTimeMillis() - this.delaie;
    }
}
