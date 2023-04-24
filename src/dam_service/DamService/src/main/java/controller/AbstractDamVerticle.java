package controller;
import io.vertx.core.AbstractVerticle;
import model.DamModel;

public class AbstractDamVerticle extends AbstractVerticle{
    private MainController controller;
    
    public AbstractDamVerticle(MainController controller) {
        this.controller = controller;
    }
    
    public MainController getController() {
        return this.controller;
    }
    
    public void setController(MainController controller) {
        this.controller = controller;
    }
}
