#ifndef __WATERMEASUREMENT__
#define __WATERMEASUREMENT__

class WaterMeasurement {

private: 
    const int BRIDGE_HEIGHT = 500;
    int waterLevel;

public:
    WaterMeasurement();
    int getWaterLevel();
    void insertWaterData(int distance);
};

#endif
