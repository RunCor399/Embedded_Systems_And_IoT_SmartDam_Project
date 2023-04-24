#include "MsgServiceBluetooth.h"
#include "Arduino.h"

String contentBT;
MsgServiceBluetooth msgServiceBluetooth;
SoftwareSerial channel(2, 3);

void MsgServiceBluetooth::init(){
    channel.begin(9600);
    contentBT.reserve(256);
    contentBT = "";
}

void MsgServiceBluetooth::sendMessage(String message){
    channel.println(message);
}

void MsgServiceBluetooth::parseMessage(String message){
    const int size = message.length() + 1;
    char message_c[size];
    
    message.toCharArray(message_c, size);

    char* id = strtok(message_c, ";");
    char* value = strtok(NULL, ";");
    
    if(strcmp(id,"STATE") == 0){
        this->generateEvent(new ManualStateEvent(String(value)));
    } 
    else if(strcmp(id,"DAM") == 0){
        this->generateEvent(new DamChangeEventBT(String(value)));        
    }
}

void MsgServiceBluetooth::bluetoothSerialEvent() {
  while (channel.available()) {
    noInterrupts();
    char ch = (char) channel.read(); 
    interrupts();
    if(ch == '\n'){
        msgServiceBluetooth.parseMessage(contentBT);
        contentBT = "";
    } else {
        contentBT += ch;
    }
  }
}
