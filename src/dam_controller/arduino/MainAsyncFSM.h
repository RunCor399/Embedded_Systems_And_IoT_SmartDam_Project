#ifndef __MAINASYNCFSM__
#define __MAINASYNCFSM__
#include "async_fsm.h"
#include "LedAsyncFSM.h"
#include "ServoImpl.h"
#include "Led.h"

class MainAsyncFSM : public AsyncFSM {
  public:
    MainAsyncFSM(LedAsyncFSM* ledAsyncFSM, Led* led);  
    void handleEvent(Event* ev);

  private:
    LedAsyncFSM* ledFSM;
    ServoImpl* servo;
    Led* led;
    enum  {NORMAL, PREALARM, ALARM, MANUAL} currentState;

    void changeState(String new_state);
};
    
#endif
