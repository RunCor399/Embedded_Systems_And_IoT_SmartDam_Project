#include <TimerOne.h>
#include "TimerSource.h"

volatile bool timerFlag;
TimerSource timerSource;

void timerTick(void){
    timerSource.generateLedEvent();
}

void TimerSource::init(){
    Timer1.initialize(500000);
    Timer1.attachInterrupt(timerTick);
}

void TimerSource::generateLedEvent() {
    this->generateEvent(new LedEvent());
}
