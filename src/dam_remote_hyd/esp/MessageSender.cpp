#include "MessageSender.h"

WiFiClient espClient;

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  if ((char)payload[0] == '1') {
    digitalWrite(BUILTIN_LED, LOW);  

  } else {
    digitalWrite(BUILTIN_LED, HIGH); 
  }
}

MessageSender::MessageSender() {
  this->mqttClient = new PubSubClient(espClient);
  pinMode(BUILTIN_LED, OUTPUT);
  Serial.begin(115200);
  this->setupWifi();
  this->mqttClient->setServer(MQTT_SERVER, 1883);
  this->reconnect();
}

void MessageSender::setupWifi() {
  delay(10);

  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(SSID);

  WiFi.mode(WIFI_STA);
  WiFi.begin(SSID, PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void MessageSender::reconnect() {
  if (!this->mqttClient->connected()) {
    Serial.print("Attempting MQTT connection...");

    // Create a random client ID
    String clientId = "ESPClient-";
    clientId += String(random(0xffff), HEX);

    // Attempt to connect
    if (this->mqttClient->connect(clientId.c_str())) {
      Serial.println("connected");
    } else {
        Serial.print("failed, rc=");
        Serial.print(this->mqttClient->state());
        Serial.println("try again in 5 seconds");
    }
  }
}

void MessageSender::sendMessage(char* waterLevel) {
    Serial.println(String("STAMPO ") + waterLevel);
    this->reconnect();
    if(this->mqttClient->connected()){
      this->mqttClient->publish(TOPIC, waterLevel);
    } else {
      Serial.println("Non connesso");
    }
     
}
