#ifndef __TIMERSOURCE__
#define __TIMERSOURCE__

#define MICRO_MULTIPLIER 1000
#define LED_EVENT 5

#include "async_fsm.h"

class TimerSource : public EventSource {
private:
    const long PERIOD = 500 * 1000l;

public:
    void init();
    void generateLedEvent();
};


class LedEvent : public Event {
public:
    LedEvent() : Event(LED_EVENT, String("")){}; 
};

extern TimerSource timerSource;

#endif
