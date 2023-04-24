#ifndef __MSGSERVICESERIAL__
#define __MSGSERVICESERIAL__
#include "MsgService.h"

class MsgServiceSerial : public MsgService {
public:
    void init();
    void sendMessage(String message);
    void parseMessage(String message);
    
};


class DamChangeEventSR : public Event {
public:
    DamChangeEventSR(String message) : Event(DAM_CHANGE_EVENT_SR, message){};
};

class StateChangeEvent : public Event {
public:
    StateChangeEvent(String message) : Event(STATE_CHANGE_EVENT, message){};
};


extern MsgServiceSerial msgServiceSerial;

#endif
