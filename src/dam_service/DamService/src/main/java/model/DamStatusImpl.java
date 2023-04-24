package model;


public class DamStatusImpl implements DamStatus {
    private static final int LEVEL_1 = 400;
    private static final int LEVEL_2 = 460;
    private static final int DELTA_LEVEL = 4;
    private int waterLevel;
    private State state;
    private int damOpening;
    
    public DamStatusImpl() {
        
    }
    
    @Override
    public void computeDamStatus(int waterLevel) {
        this.waterLevel = waterLevel;
        this.state = this.computeDamState();
        this.damOpening = this.computeDamOpening();
    }

    @Override
    public int getWaterLevel() {
        return this.waterLevel;
    }

    @Override
    public State getCurrentState() {
        return this.state;
    }

    @Override
    public int getDamOpening() {
        return this.damOpening;
    }
    
    private int computeDamOpening() {
        if(this.waterLevel < LEVEL_2) {
            return 0;
        }
        else if(this.waterLevel >= LEVEL_2 && this.waterLevel < (LEVEL_2 + DELTA_LEVEL)) {
            return 20;
        }
        else if(this.waterLevel >= (LEVEL_2 + DELTA_LEVEL) && this.waterLevel < (LEVEL_2 + 2*DELTA_LEVEL)) {
            return 40;
        }
        else if(this.waterLevel >= (LEVEL_2 + 2*DELTA_LEVEL) && this.waterLevel < (LEVEL_2 + 3*DELTA_LEVEL)) {
            return 60;
        }
        else if(this.waterLevel >= (LEVEL_2 + 3*DELTA_LEVEL) && this.waterLevel < (LEVEL_2 + 4*DELTA_LEVEL)) {
            return 80;
        }
        else {
            return 100;
        }       
    }
    
    private State computeDamState() {
        if(this.waterLevel < LEVEL_1) {
            return State.NORMAL;
        }
        else if(this.waterLevel >= LEVEL_1 && this.waterLevel < LEVEL_2) {
            return State.PREALARM;
        }
        else {
            return State.ALARM;
        }
    }
    
    enum State {
        NORMAL, PREALARM, ALARM, MANUAL
    }

}
