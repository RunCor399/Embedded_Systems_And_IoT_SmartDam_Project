package model;

import model.DamModelImpl.State;

public interface DamModel {
    
    void computeDamStatus(int waterLevel);
    
    int getWaterLevel();
    
    State getCurrentState();
    
    int getDamOpening();

    void setDamOpening(int damOpening);

    void setManualMode(boolean isInMaunual);
}
