package model;

import controller.MainController;


public class DamModelImpl implements DamModel{
    private static final int LEVEL_1 = 400;
    private static final int LEVEL_2 = 460;
    private static final int DELTA_LEVEL = 4;
    private final MainController controller;
    private int waterLevel;
    private State state;
    private int damOpening;
    
    public DamModelImpl(MainController controller) {
        this.controller = controller;
        this.state = State.ALARM;
    }
    
    @Override
    public void computeDamStatus(int waterLevel) {
        this.waterLevel = waterLevel;      
        
        State nextState = this.computeDamState();
        
        if(nextState != State.NORMAL) {
            this.controller.getMySQLConnection().insertWaterLevel(this.waterLevel);
        }
        
        if((this.state == State.MANUAL) && (nextState == State.ALARM)) {
            return;       
        }
        
        this.damOpening = this.computeDamOpening();
        this.state = nextState;
        
        this.controller.getSerialVerticle().sendStateMessage(this.state);
        this.controller.getSerialVerticle().sendDamOpeningMessage(this.damOpening);
    }

    @Override
    public int getWaterLevel() {
        return this.waterLevel;
    }
    
    @Override
    public void setManualMode(final boolean isInMaunual) {
        if(this.state == State.ALARM || this.state == State.MANUAL) {
            this.state = (isInMaunual) ?  State.MANUAL : State.ALARM;
        }
    }

    @Override
    public State getCurrentState() {
        return this.state;
    }

    @Override
    public int getDamOpening() {
        return this.damOpening;
    }
    
    @Override
    public void setDamOpening(int damOpening) {
        if(this.state == State.MANUAL) {
            this.damOpening = damOpening;
        }
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
    
    public enum State {
        NORMAL, PREALARM, ALARM, MANUAL
    }

}
