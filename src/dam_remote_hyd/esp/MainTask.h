#ifndef __MAINTASK__
#define __MAINTASK__

#include "Task.h"
#include "WaterMeasurement.h"
#include "CommTask.h"
#include "MeasurementTask.h"
#include "AlarmTask.h"
#include "Led.h"


class MainTask: public Task {

    private:
        const int PERIOD_1 = 5000;
        const int PERIOD_2 = 10000;
        WaterMeasurement* wm;
        MessageSender* ms;
        CommTask* ct;
        MeasurementTask* mt;
        Light* led;
        AlarmTask* at;
        void sendStateChangingLevel(int);
        enum {INIT, NORMAL, PREALARM, ALARM} externalState;

    public:
        MainTask(WaterMeasurement* wm, MessageSender* ms, CommTask* ct, MeasurementTask* mt, AlarmTask* at, Led* led);   
        void init(int period);
        void tick();

}; 

#endif
