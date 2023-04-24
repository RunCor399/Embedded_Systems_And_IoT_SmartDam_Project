#ifndef __MSGSERVICE__
#define __MSGSERVICE__

#define DAM_CHANGE_EVENT_BT 1
#define DAM_CHANGE_EVENT_SR 2
#define MANUAL_STATE_EVENT 3
#define STATE_CHANGE_EVENT 4

#include "async_fsm.h"
#include "Arduino.h"

class MsgService : public EventSource {
public:
    virtual void init() = 0;
    virtual void sendMessage(String message) = 0;
    virtual void parseMessage(String message) = 0;

    void sendStateMessage(String message){
        String packed_message = String("STATE;");
        packed_message.concat(message);

        this->sendMessage(packed_message);
    }

    void sendDamMessage(String message){
        String packed_message = String("DAM;");
        packed_message.concat(message);

        this->sendMessage(packed_message);
    }
};

#endif
