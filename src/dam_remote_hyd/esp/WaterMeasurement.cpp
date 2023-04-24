#include "WaterMeasurement.h"

WaterMeasurement::WaterMeasurement(){
    this->waterLevel = 0;
}

int WaterMeasurement::getWaterLevel(){
    return this->waterLevel;
}

void WaterMeasurement::insertWaterData(int distance){
    this->waterLevel = BRIDGE_HEIGHT - distance;
}