#ifndef __MESSAGESENDER__
#define __MESSAGESENDER__

#include <WiFi.h>
#include <PubSubClient.h>
#include "Arduino.h"

#define BUILTIN_LED 12

class MessageSender {
    
    private:
        const char* SSID = "Eros1969";
        const char* PASSWORD = "NMJygjcimBEcjbMF";
        const char* MQTT_SERVER = "79.40.140.154";
        const char* TOPIC = "water_level";
        PubSubClient* mqttClient;

        void setupWifi();
        void reconnect();

    public:
        MessageSender();
        void sendMessage(char* waterLevel);
};



#endif
