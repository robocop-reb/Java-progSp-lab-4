package sample;

import java.io.IOException;
import java.sql.*;

public class ServerSp4 {
    public static void main(String[] args) throws SQLException, IOException {
        ServerUtils runningServer = ServerUtils.getInstanceServerUtils();
        runningServer.start(5000);
        runningServer.databaseConnect();
        String clientCommand = "";
         while (!clientCommand.equals("Kill")){
             clientCommand = runningServer.getString();
            switch (clientCommand) {
                case "Select":
                    runningServer.select(runningServer.getString());
                    break;
                case "Insert":
                    runningServer.insert(runningServer.getString());
                    break;
                case "Delete":
                    runningServer.delete(runningServer.getString());
                    break;
                case "Kill":
                    runningServer.stop(true);
                    break;
                default:
            }
        }
    }
}