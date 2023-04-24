#include "Led.h"
#include "MsgServiceBluetooth.h"
#include "MsgServiceSerial.h"
#include "LedAsyncFSM.h"
#include "MainAsyncFSM.h"
#include "TimerSource.h"

LedAsyncFSM* ledAsyncFSM;
AsyncFSM* mainAsyncFSM;

void setup() {
  Led* led = new Led(13); 
  ledAsyncFSM = new LedAsyncFSM(led);
  ledAsyncFSM->setActive(false);
  mainAsyncFSM = new MainAsyncFSM(ledAsyncFSM, led);
  mainAsyncFSM->setActive(true);
  msgServiceBluetooth.init();
  timerSource.init();
  msgServiceSerial.init();

  Serial.println("Start");
}

void loop() {
  mainAsyncFSM->checkEvents();
  ledAsyncFSM->checkEvents();

  msgServiceBluetooth.bluetoothSerialEvent();
}
