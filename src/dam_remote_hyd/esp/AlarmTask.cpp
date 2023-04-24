#include "AlarmTask.h"
#include "Arduino.h"

/* Task for control the blinking of a led */
AlarmTask::AlarmTask(Light* led) {
    this->led = led;
}

void AlarmTask::init(int period) {
    Task::init(period);
    this->blinkState = OFF;
}

void AlarmTask::tick() {
    switch (blinkState) {
        case OFF: {
            led->switchOn();
            this->blinkState = ON;
            break;
        }
    
        case ON: {
            this->led->switchOff();
            this->blinkState = OFF;
            break;
        }
    }
}

void AlarmTask::setActive(int active) {
    Task::setActive(active);
    if (!active) {
        this->led->switchOff();
    }
}
