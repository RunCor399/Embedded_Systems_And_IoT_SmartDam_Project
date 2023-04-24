#ifndef __ALARMTASK__
#define __ALARMTASK__

#include "Task.h"
#include "Led.h"

class AlarmTask: public Task {

private:
    Light* led;
    enum {ON, OFF} blinkState;

public:
    AlarmTask(Light* led);
    void setActive(int active);
    void init(int period);
    void tick();
};

#endif
