#include "SonarImpl.h"
#include "Arduino.h"

SonarImpl::SonarImpl(int echoPin, int triggerPin){
    this->echoPin = echoPin;
    this->triggerPin = triggerPin;

    pinMode(echoPin, INPUT);
    pinMode(triggerPin, OUTPUT);
}

int SonarImpl::readDistance(){
    digitalWrite(this->triggerPin, LOW);
    delayMicroseconds(5);
    digitalWrite(this->triggerPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(this->triggerPin, LOW);

    float duration = pulseIn(echoPin, HIGH ,60000);

    int distance = this->durationToDistance(duration);

    return distance;
}

int SonarImpl::durationToDistance(float duration){
   const float sound_velocity = (331.45 + 0.62 * TEMPERATURE);
   float timeElapsed = duration / 1000.0 / 1000.0 / 2;

   return (int)(timeElapsed * sound_velocity*100.0);
}
