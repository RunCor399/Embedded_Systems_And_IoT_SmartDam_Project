#include "MainTask.h"
#include "Arduino.h"

MainTask::MainTask(WaterMeasurement* wm, MessageSender* ms, CommTask* ct, MeasurementTask* mt, AlarmTask* at, Led* led) {
    this->wm = wm;
    this->ms = ms;
    this->ct = ct;
    this->mt = mt;
    this->at = at;
    this->led = led;
}

void MainTask::init(int period) {
    Task::init(period);
    this->externalState = INIT;
}

void MainTask::tick() {
    switch(this->externalState) {
        case(INIT): {
            Serial.println("INIT");
            this->mt->setActive(true);
            this->externalState = NORMAL;
            break;
        }

        case(NORMAL): {
            Serial.println("NORMAL");
            int waterLevel = this->wm->getWaterLevel();

            if (waterLevel >= 400 && waterLevel < 460) {
                this->at->setActive(true);
                this->ct->init(PERIOD_2);
                this->ct->setActive(true);
                this->externalState = PREALARM;
                this->sendStateChangingLevel(waterLevel);
            }
            else if (waterLevel >= 460) {
                this->led->switchOn();
                this->ct->init(PERIOD_1);
                this->ct->setActive(true);
                this->externalState = ALARM;
                this->sendStateChangingLevel(waterLevel);
            }
            break;
        }

        case(PREALARM): {
            Serial.println("PREALARM");
            int waterLevel = this->wm->getWaterLevel();

            if (waterLevel < 400) {
                this->at->setActive(false);
                this->ct->setActive(false);
                this->externalState = NORMAL;
                this->sendStateChangingLevel(waterLevel);
            }
            else if (waterLevel >= 460) {
                this->at->setActive(false);
                this->ct->init(PERIOD_1);
                this->ct->setActive(true);
                this->led->switchOn();
                this->externalState = ALARM;
                this->sendStateChangingLevel(waterLevel);
            }

            break;
        }

        case(ALARM): {
            Serial.println("ALARM");
            int waterLevel = this->wm->getWaterLevel();
            
            if (waterLevel < 400) {
                this->led->switchOff();
                this->ct->setActive(false);
                this->externalState = NORMAL;
                this->sendStateChangingLevel(waterLevel);
            }
            else if (waterLevel >= 400 && waterLevel < 460) {
                this->at->setActive(true);
                this->ct->init(PERIOD_2);
                this->ct->setActive(true);
                this->led->switchOff();
                this->externalState = PREALARM;
                this->sendStateChangingLevel(waterLevel);
            }
            break;
        }
    }
}

void MainTask::sendStateChangingLevel(int level){
  char cstr[16];
  itoa(level, cstr, 10);
  this->ms->sendMessage(cstr);
}
