#include "MsgServiceSerial.h"
#include "Arduino.h"
#include "string.h"

String contentSR;
MsgServiceSerial msgServiceSerial;

void MsgServiceSerial::init(){
    Serial.begin(9600);
    contentSR.reserve(256);
    contentSR = "";
}

void MsgServiceSerial::sendMessage(String message){
    Serial.println(message);
}

void MsgServiceSerial::parseMessage(String message){
    int size = message.length() + 1;
    char message_c[size];

    message.toCharArray(message_c, size);

    char* id = strtok(message_c, ";");
    char* value = strtok(NULL, ";");
    
    if(strcmp(id,"STATE") == 0){  
        this->generateEvent(new StateChangeEvent(String(value)));
    } 
    else if(strcmp(id,"DAM") == 0){
        this->generateEvent(new DamChangeEventSR(String(value)));        
    }
}

void serialEvent() {
  while (Serial.available()) { 
    noInterrupts();
    char ch = (char) Serial.read();
    interrupts();
    if (ch == '\n'){
      msgServiceSerial.parseMessage(contentSR);
      contentSR = "";
    } else {
      contentSR += ch;      
    }
  }
}
