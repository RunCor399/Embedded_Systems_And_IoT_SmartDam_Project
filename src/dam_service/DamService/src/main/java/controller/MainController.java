
package controller;

import controller.http.HTTPVerticle;
import controller.serial.SerialCommChannel;
import controller.serial.SerialVerticle;

import io.vertx.core.Vertx;
import model.DamModel;
import model.DamModelImpl;
import model.DamStatusImpl;

public class MainController {
    private final DamModel damModel;
    private final SerialVerticle serialVerticle;
    private final MQTTVerticle mqttVerticle;
    private final MySQLConnection mySQLConnection;
    private final HTTPVerticle httpVerticle;
    
    public MainController() {
        this.damModel = new DamModelImpl(this);
        this.mqttVerticle = new MQTTVerticle(this);
        this.serialVerticle = new SerialVerticle(this, new SerialCommChannel("COM8", 9600));
        this.mySQLConnection = new MySQLConnection();
        this.httpVerticle = new HTTPVerticle(this);
        
    }
    
    public void startServices() {
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(this.serialVerticle);
        vertx.deployVerticle(this.mqttVerticle);
        vertx.deployVerticle(this.httpVerticle);
    }
    
    public DamModel getDamModel() {
        return this.damModel;
    }

    public SerialVerticle getSerialVerticle() {
        return this.serialVerticle;
    }

    public MQTTVerticle getMqttVerticle() {
        return this.mqttVerticle;
    }
    
    public MySQLConnection getMySQLConnection() {
        return this.mySQLConnection;
    }

    public static void main(String... strings) {
       MainController controller = new MainController();
       controller.startServices();
    }
    
    
}
