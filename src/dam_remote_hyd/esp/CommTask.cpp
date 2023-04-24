#include "CommTask.h"
#include "Arduino.h"

CommTask::CommTask(WaterMeasurement* wm, MessageSender* ms) {
    this->wm = wm;
    this->ms = ms;
}

void CommTask::init(int period) {
    Task::init(period);

}

void CommTask::tick() {
    char cstr[16];
    itoa(this->wm->getWaterLevel(), cstr, 10);
    this->ms->sendMessage(cstr);
}
