#ifndef __SONARIMPL__
#define __SONARIMPL__

#include "Sonar.h"

class SonarImpl : public Sonar {

public:
    SonarImpl(int echoPin, int triggerPin);
    int readDistance();
    
private:
    const int TEMPERATURE = 20;
    int durationToDistance(float duration);

    int echoPin;
    int triggerPin;
};

#endif
