package model;

import model.DamStatusImpl.State;

public interface DamStatus {
    
    void computeDamStatus(int waterLevel);
    
    int getWaterLevel();
    
    State getCurrentState();
    
    int getDamOpening();
}
