package controller;

import io.vertx.mqtt.MqttClient;

public class MQTTVerticle extends AbstractDamVerticle {

    public MQTTVerticle(MainController controller) {
        super(controller);
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);
      
        
        client.publishHandler(message -> {
            String payload = message.payload().toString();
            System.out.println(payload);
            
            this.getController().getDamModel().computeDamStatus(Integer.parseInt(payload));
          });
        client.subscribeCompletionHandler(mqttSubAckMessage -> {
            System.out.println("Id of just received SUBACK packet is " + mqttSubAckMessage.messageId());
            for (int sp : mqttSubAckMessage.grantedQoSLevels()) {
                if (sp == 0x80) {
                    System.out.println("Failure");
                } else {
                    System.out.println("Success. Maximum QoS is " + sp);
                }
            }
        });
        
        client.connect(1883, "79.40.140.154", s -> {
            client.subscribe("water_level", 2);
        });
    }
}
