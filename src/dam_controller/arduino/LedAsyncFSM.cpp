
#include "LedAsyncFSM.h"
#include "TimerSource.h"
#include "Arduino.h"

LedAsyncFSM::LedAsyncFSM(Led* led){  
    this->led = led;
    this->currentState = OFF;
    timerSource.registerObserver(this);
    led->switchOff();
}

void LedAsyncFSM::handleEvent(Event* ev) {
    switch(this->currentState) {
        case(ON): {
          if (ev->getType() == LED_EVENT){
            this->led->switchOff();
            this->currentState = OFF;
          }
         
          break;
        }

        case(OFF): {
          if (ev->getType() == LED_EVENT){
            this->led->switchOn();
            this->currentState = ON;
          }
          
          break;
        }
    }
}
