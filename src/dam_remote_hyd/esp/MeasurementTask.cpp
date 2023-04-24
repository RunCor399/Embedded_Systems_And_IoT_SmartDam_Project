#include "MeasurementTask.h"


MeasurementTask::MeasurementTask(WaterMeasurement* wm, int trigPin, int echoPin){
    this->sonar = new SonarImpl(echoPin, trigPin);
    this->wm = wm;
}

void MeasurementTask::init(int period){
    Task::init(period);
    this->internalState = INIT;
}

void MeasurementTask::tick() {
    switch(this->internalState) {
        case(INIT): {

            this->internalState = RUNNING;
            break;
        }

        case(RUNNING): {
            this->wm->insertWaterData(this->sonar->readDistance());

            break;
        }
    }
}