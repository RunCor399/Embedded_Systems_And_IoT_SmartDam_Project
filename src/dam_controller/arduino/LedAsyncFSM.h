#ifndef __LEDASYNC__
#define __LEDASYNC__
#include "async_fsm.h"
#include "Led.h"

class LedAsyncFSM : public AsyncFSM {
  public:
    LedAsyncFSM(Led* led);  
    void handleEvent(Event* ev);

  private:
    Led* led;
    enum  {ON,OFF} currentState;
};

#endif