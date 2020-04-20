package sample;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerUtils {
    private static ServerUtils single_instace = null;
    private ResultSet resultSet;

    private ServerUtils() {
    }

    public static ServerUtils getInstanceServerUtils() {
        if (single_instace == null) {
            single_instace = new ServerUtils();
        }
        return single_instace;
    }

    private Socket clientSocket;
    private DataInputStream consoleInput;
    private Connection conn;

    public void databaseConnect() throws SQLException {
        String host = "jdbc:mysql://localhost:3306/labs?serverTimezone=UTC#";
        String user = "root";
        String pass = "1213";
        conn = DriverManager.getConnection(host, user, pass);
    }

    public void select(String id) throws SQLException {
        PreparedStatement selectPrep = null;
        String selectSt = "SELECT id_car,name_car " + "FROM warmachine " + "WHERE id_car = ?";
        selectPrep = conn.prepareStatement(selectSt);
        selectPrep.setInt(1, Integer.valueOf(id));
        ResultSet selectSet = selectPrep.executeQuery();
        resultToMap(selectSet);
    }

    public void select() throws SQLException {
        PreparedStatement selectPrep;
        String selectSt = "SELECT id_car,name_car " + "FROM warmachine";
        selectPrep = conn.prepareStatement(selectSt);
        ResultSet selectSet = selectPrep.executeQuery();
        resultToMap(selectSet);
    }

    public void insert(String data) throws SQLException {
        String insertSt = "INSERT into warmachine(name_car)values(?)";
        PreparedStatement insertPrep = conn.prepareStatement(insertSt);
        insertPrep.setString(1,data);
        insertPrep.executeUpdate();
        select();
    }
    public void delete(String id) throws SQLException {
        PreparedStatement selectPrep = null;
        String deleteSt = "delete from warmachine where  id_car= ?";
        selectPrep = conn.prepareStatement(deleteSt);
        selectPrep.setInt(1, Integer.valueOf(id));
        selectPrep.executeUpdate();
        select();
    }


    private void resultToMap(ResultSet resultSet) throws SQLException {
        Map<String, List<String>> valueMap = new HashMap<>();
        while (resultSet.next()) {
            String columnAstring = String.valueOf(resultSet.getInt(1));
            String columnBstring = resultSet.getString(2);
            valueMap.putIfAbsent(columnAstring, new ArrayList<>());
            valueMap.get(columnAstring).add(columnBstring);
        }
        sendSizeOfResult(valueMap.size());
        getStringFromMap(valueMap);
    }

    private void getStringFromMap(Map<String, List<String>> valueMap) {
        String resultSelect = "";
        String resultSelect2 = "";
        for (Map.Entry<String, List<String>> string : valueMap.entrySet()) {
            List<String> valueList = string.getValue();
            resultSelect = string.getKey();
            for (String s : valueList) {
                resultSelect2 = String.join(" ", resultSelect, s);
                sendRes(resultSelect2);
            }
        }
    }

    public void sendRes(String res) {
        DataOutputStream toClient = null;
        try {
            toClient = new DataOutputStream(clientSocket.getOutputStream());
            toClient.writeUTF(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendSizeOfResult(int size) {
        DataOutputStream toClient = null;
        try {
            toClient = new DataOutputStream(clientSocket.getOutputStream());
            toClient.writeInt(size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket start(int port) throws IOException {
        try {
            ServerSocket socket = new ServerSocket(port);
            clientSocket = socket.accept();
            consoleInput = new DataInputStream(new BufferedInputStream(System.in));
            if (clientSocket != null) {
                System.out.println("connected");
            }
        } catch (UnknownHostException i) {
            System.out.println(i);
        }
        return clientSocket;
    }

    public void stop(boolean isKill) throws IOException {
        if (isKill) {
            clientSocket.close();
        }
    }

    public String getString() {
        String tempString = "";
        try {
            while (tempString.isEmpty()) {
                DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream());
                tempString = fromClient.readUTF();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempString;
    }
}
