#ifndef __SERVOIMPL__
#define __SERVOIMPL__

#include "Servo.h"
#include "Arduino.h"
#include "ServoTimer2.h"

class ServoImpl: public Servo {

public:
    ServoImpl(int pin);

    void on();
    void setPosition(int angle);
    void off();

private:
    int pin;
    ServoTimer2 motor;
};

#endif
