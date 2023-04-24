package controller.http;

import controller.AbstractDamVerticle;
import controller.MainController;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import model.DamModelImpl.State;

public class HTTPVerticle extends AbstractDamVerticle {
    
    public HTTPVerticle(MainController controller) {
        super(controller);
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        
        router.route("/dashboard").handler(StaticHandler.create("dashboard"));
        router.route("/dashboard/style.css").handler(StaticHandler.create("dashboard/style.css"));
        router.route("/dashboard/app.js").handler(StaticHandler.create("dashboard/app.js"));

          
        router.get("/data").handler(this::computeData);
          
          
        router.errorHandler(500, rc -> {
            System.err.println("Router Failure");
            Throwable failure = rc.failure();
            if (failure != null) {
              failure.printStackTrace();
            }
          });
          

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(server ->
                System.out.println("HTTP server started on port " + server.actualPort())
            );
    }
    
    
    public void computeData(RoutingContext routingContext) {
        State currentState = this.getController().getDamModel().getCurrentState();
        JsonObject responseJSON = new JsonObject().put("state", currentState.toString());
        
        if(currentState == State.NORMAL) {
            this.sendJsonResponse(routingContext, responseJSON);
        }
        else if(currentState == State.PREALARM) {
            this.parseWaterLevels(responseJSON, routingContext);
        }
        else {
            Integer damOpening = this.getController().getDamModel().getDamOpening();
            responseJSON.put("dam_opening", damOpening);
            
            this.parseWaterLevels(responseJSON, routingContext);
        }
    }
    
    
    
    public void parseWaterLevels(JsonObject responseJSON, RoutingContext routingContext) {
        Future<RowSet<Row>> future = this.getController().getMySQLConnection().getWaterLevels();
        JsonObject levelsJSON = new JsonObject();
        
        future.onComplete(x -> {
            if(x.succeeded()) {
                RowSet<Row> results = x.result();
                
                results.forEach(val -> {
                    levelsJSON.put(val.getLocalDateTime(0).toString(), val.getInteger(1));
                });
                
                responseJSON.put("levels", levelsJSON);
                
                this.sendJsonResponse(routingContext, responseJSON);
            }
         });

    }
    
    private void sendJsonResponse(RoutingContext routingContext, JsonObject responseJson) {
        routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(responseJson.toString());
    }
}
