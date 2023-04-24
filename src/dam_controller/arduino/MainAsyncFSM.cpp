#include "MainAsyncFSM.h"
#include "MsgServiceSerial.h"
#include "MsgServiceBluetooth.h"
#include "Arduino.h"

MainAsyncFSM::MainAsyncFSM(LedAsyncFSM* ledFSM, Led* led) { 
    this->ledFSM = ledFSM;
    this->servo = new ServoImpl(11);
    this->servo->on();
    this-> led = led;
    this->currentState = NORMAL;
    msgServiceSerial.registerObserver(this);
    msgServiceBluetooth.registerObserver(this);
}

void MainAsyncFSM::handleEvent(Event* ev) {
    switch(this->currentState) {
        
        case(NORMAL): 
        case(PREALARM): {
            Serial.println("Normal");
            if (ev->getType() == STATE_CHANGE_EVENT){               
                this->changeState(ev->getMessage()); 
                msgServiceBluetooth.sendStateMessage(String(ev->getMessage()));              
            } 
            break;
        }

        case(ALARM): {
            Serial.println("Alarm");
            if (ev->getType() == DAM_CHANGE_EVENT_SR){
                this->servo->setPosition(ev->getMessage().toInt());
                msgServiceBluetooth.sendDamMessage(ev->getMessage());
            }
            else if (ev->getType() == STATE_CHANGE_EVENT){
                this->changeState(ev->getMessage()); 
                
                msgServiceBluetooth.sendStateMessage(ev->getMessage()); 
            }
            else if (ev->getType() == MANUAL_STATE_EVENT){
                this->currentState = MANUAL;
                this->ledFSM->setActive(false);
                this->led->switchOn();
                
                msgServiceSerial.sendStateMessage(ev->getMessage());
            } 
            break;
        }

        case(MANUAL): {
             Serial.println("Manual");
             if (ev->getType() == DAM_CHANGE_EVENT_BT){
                this->servo->setPosition(ev->getMessage().toInt());
                msgServiceSerial.sendDamMessage(ev->getMessage());
            }
            else if (ev->getType() == STATE_CHANGE_EVENT){
                String new_state(ev->getMessage());
                this->changeState(ev->getMessage());

                msgServiceBluetooth.sendStateMessage(ev->getMessage());
            }
            else if (ev->getType() == MANUAL_STATE_EVENT){
                this->currentState = ALARM;
                this->ledFSM->setActive(true);
                this->led->switchOff();
                
                msgServiceSerial.sendStateMessage(ev->getMessage());
            } 
            break;
        }
    }
}

void MainAsyncFSM::changeState(String new_state) {
    if (new_state.equals(String("NORMAL"))) {
        this->currentState = NORMAL;
        this->ledFSM->setActive(false);
        this->led->switchOff();
    }
    else if (new_state.equals(String("PREALARM"))) {
        this->currentState = PREALARM;
        this->ledFSM->setActive(false);
        this->led->switchOff();
    }
    else if (new_state.equals(String("ALARM"))) {
        this->currentState = ALARM;
        this->ledFSM->setActive(true);
    }
}
