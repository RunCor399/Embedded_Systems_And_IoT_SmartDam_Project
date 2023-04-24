#ifndef __MSGSERVICEBLUETOOTH__
#define __MSGSERVICEBLUETOOTH__
#include <Wire.h>
#include "MsgService.h"
#include "SoftwareSerial.h"

class MsgServiceBluetooth : public MsgService {
public:
    void init();
    void sendMessage(String message);
    void parseMessage(String message);
    void bluetoothSerialEvent();
};


class DamChangeEventBT : public Event {
public:
    DamChangeEventBT(String message) : Event(DAM_CHANGE_EVENT_BT, message){};  
};

class ManualStateEvent : public Event {
public:
    ManualStateEvent(String message) : Event(MANUAL_STATE_EVENT, message){};
};

extern MsgServiceBluetooth msgServiceBluetooth;

#endif
