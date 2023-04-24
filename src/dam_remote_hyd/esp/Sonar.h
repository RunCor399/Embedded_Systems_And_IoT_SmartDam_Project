#ifndef __SONAR__
#define __SONAR__

class Sonar {

public:
    virtual int readDistance() = 0;
    virtual int durationToDistance(float duration) = 0;
};

#endif
