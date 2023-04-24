#include "Led.h"
#include "Arduino.h"

Led::Led(int pin){
  this->pin = pin;
  this->active = false;
  pinMode(pin,OUTPUT);
}

void Led::switchOn(){
  digitalWrite(pin,HIGH);
  this->active = true;
}

void Led::switchOff(){
  digitalWrite(pin,LOW);
  this->active = false;
};

bool Led::isActive(){
   return this->active;
}