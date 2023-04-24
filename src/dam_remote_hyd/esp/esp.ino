#include "Scheduler.h"
#include "AlarmTask.h"
#include "MeasurementTask.h"
#include "MainTask.h"
#include "CommTask.h"
#include "MessageSender.h"
#include "WaterMeasurement.h"
#include "Led.h"
#include "Task.h"
#include "config.h"

Scheduler scheduler;

void setup(){
  scheduler.init(SCHEDULER_PERIOD);

  WaterMeasurement* wm = new WaterMeasurement();
  MessageSender* ms = new MessageSender();
  Led* led = new Led(LED_PIN);

  AlarmTask* alarmTask = new AlarmTask(led);
  alarmTask->init(500);
  alarmTask->setActive(false);  
   
  CommTask* commTask = new CommTask(wm, ms);
  commTask->init(5000);
  commTask->setActive(false);

  MeasurementTask* measurementTask = new MeasurementTask(wm, TRIG_PIN, ECHO_PIN);
  measurementTask->init(5000);
  measurementTask->setActive(true);
  
  MainTask* mainTask = new MainTask(wm, ms, commTask, measurementTask, alarmTask, led);
  mainTask->init(5000);
  mainTask->setActive(true);
  
  
  scheduler.addTask(alarmTask);
  scheduler.addTask(measurementTask);
  scheduler.addTask(mainTask);
  scheduler.addTask(commTask);
  
}

void loop(){
  scheduler.schedule();
}
