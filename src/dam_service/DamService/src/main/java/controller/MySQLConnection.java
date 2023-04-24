package controller;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MySQLConnection {
    private final MySQLPool mySQLPool;
    
    public MySQLConnection() {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3305)
                .setHost("localhost")
                .setDatabase("dam")
                .setUser("damAdmin")
                .setPassword("admin");

              PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);
              
        this.mySQLPool = MySQLPool.pool(connectOptions, poolOptions);
    }
    
    
    public void insertWaterLevel(int waterLevel) {
        this.mySQLPool.preparedQuery("INSERT INTO water_level (level) VALUES  (?)")
        .execute(Tuple.of(waterLevel), ar -> {
            
            if (ar.succeeded()) {
                System.out.println("New water level value: " + waterLevel);
              } 
            else {
                System.out.println("Failure: " + ar.cause().getMessage());
              } 
            
            
        });
    }
    
    public Future<RowSet<Row>> getWaterLevels(){
        Promise<RowSet<Row>> promise = Promise.promise();
       
        this.mySQLPool.preparedQuery("SELECT timestamp, level FROM water_level ORDER BY timestamp DESC LIMIT 20")
                      .execute(ar -> {
                          if(ar.succeeded()) {
                              promise.complete(ar.result());
                          }
                          else {
                              promise.complete(null);
                          }
                      });
        
        return promise.future();
    }
}
