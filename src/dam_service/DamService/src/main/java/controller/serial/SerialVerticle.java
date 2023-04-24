package controller.serial;

import controller.AbstractDamVerticle;
import controller.MainController;
import model.DamModelImpl.State;

public class SerialVerticle extends AbstractDamVerticle {
    private CommChannel serialChannel;

    public SerialVerticle(MainController controller) {
        super(controller);
    }

    public SerialVerticle(MainController controller, CommChannel serialChannel) {
        super(controller);
        this.serialChannel = serialChannel;
    }

    @Override
    public void start() {
        this.serialChannel.setMessageHandler(inputLine -> {
            if (inputLine != null) {
                this.manageMessage(inputLine);
            }
        });
    }
    
    public void sendStateMessage(State state) {
        System.out.println("Changing state: " + state.toString());
        this.serialChannel.sendMsg("STATE;" + state.toString());
    }
    
    public void sendDamOpeningMessage(int damOpening) {
        System.out.println("Changing dam opening: " + damOpening);
        this.serialChannel.sendMsg("DAM;" + String.valueOf(damOpening));
    }
    
    private void manageMessage(final String message) {
        String[] parts = message.split(";");
        switch (parts[0]) {
        //The message contains a State
        case "STATE":
            if (parts[1] != null) {
                boolean inManual = parts[1].equals(State.MANUAL.toString()) ? true : false;
                this.getController().getDamModel().setManualMode(inManual);
            }
            break;
            
        //The message contains a dam opening value
        case "DAM":
            if (parts[1] != null) {
                this.getController().getDamModel().setDamOpening(Integer.parseInt(parts[1]));
            }
            break;
        } 
    }

    @Override
    public void stop() {
        this.serialChannel.close();
    }
}
