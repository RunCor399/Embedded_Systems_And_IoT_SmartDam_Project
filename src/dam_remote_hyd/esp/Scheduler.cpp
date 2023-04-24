#include "Scheduler.h"
#include <Ticker.h>
#include "Arduino.h"

volatile bool timerFlag;
Ticker timer;

void timerHandler(void){
    timerFlag = true;
}

void Scheduler::init(int basePeriod){
  this->basePeriod = basePeriod;
  timerFlag = false;
  long period = 1000l*basePeriod;

  timer.attach_ms(500, timerHandler); 
  this->tasksNumber = 0;
}

bool Scheduler::addTask(Task* task){
  if (this->tasksNumber < MAX_TASKS-1){
    this->taskList[this->tasksNumber] = task;
    this->tasksNumber++;
    return true;
  } else {
    return false; 
  }
}

/* This methods schedule the Tasks if activated */ 
void Scheduler::schedule(){   
  while (!timerFlag){}
  timerFlag = false;
  
  for (int i = 0; i < this->tasksNumber; i++){
    if (this->taskList[i]->isActive() && this->taskList[i]->updateAndCheckTime(this->basePeriod)){
      this->taskList[i]->tick();
    }
  }
}
