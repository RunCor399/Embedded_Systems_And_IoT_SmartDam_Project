#ifndef __COMMTASK__
#define __COMMTASK__

#include "Task.h"
#include "WaterMeasurement.h"
#include "MessageSender.h"

class CommTask: public Task {

    private:
        WaterMeasurement* wm;
        MessageSender* ms;

    public:
        CommTask(WaterMeasurement* wm, MessageSender* ms);
        void init(int period);
        void tick();

};

#endif
