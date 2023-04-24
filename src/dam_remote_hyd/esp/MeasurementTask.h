#ifndef __MEASUREMENTTASK__
#define __MEASUREMENTTASK__

#include "Task.h"
#include "SonarImpl.h"
#include "WaterMeasurement.h"

class MeasurementTask: public Task {

private:
    Sonar* sonar;
    WaterMeasurement* wm;
    enum {INIT, RUNNING} internalState;
    
public:   
    MeasurementTask(WaterMeasurement* wm, int trigPin, int echoPin);
    void init(int period);
    void tick();
};

#endif