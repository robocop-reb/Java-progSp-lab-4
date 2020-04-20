package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button select;
    @FXML
    private TextField idField;
    @FXML
    private TextField typeField;
    @FXML
    private Button connect;
    private static Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;


    public TableView<WarMachine> sqlTable = new TableView<WarMachine>();

    public TableColumn<WarMachine, String> id;

    public TableColumn<WarMachine, String> type;
    ObservableList<WarMachine> data = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        id.setCellValueFactory(new PropertyValueFactory<WarMachine, String>("id"));
        type.setCellValueFactory(new PropertyValueFactory<WarMachine, String>("type"));
    }


    public void handleConnectButtonClick() throws IOException {
        clientSocket = new Socket("127.0.0.1", 5000);
    }

    public void handleSelectButtonClick() throws IOException {
        output = new DataOutputStream(Controller.clientSocket.getOutputStream());
        output.writeUTF("Select");
        String id = idField.getText();
        output.writeUTF(id);
        getResultSet();
    }

    public void handleInsertButtonClick() throws IOException {
        output = new DataOutputStream(Controller.clientSocket.getOutputStream());
        output.writeUTF("Insert");
        String type = typeField.getText();
        output.writeUTF(type);
        getResultSet();
    }
    public void handleDeleteButtonClick() throws IOException {
        output = new DataOutputStream(Controller.clientSocket.getOutputStream());
        output.writeUTF("Delete");
        String id = idField.getText();
        output.writeUTF(id);
        getResultSet();
    }
    public void handleDisconnectButtonClick() throws IOException {
        output = new DataOutputStream(Controller.clientSocket.getOutputStream());
        output.writeUTF("Kill");
       clientSocket.close();
    }

    public void getResultSet() throws IOException {
        DataInputStream fromServer = new DataInputStream(clientSocket.getInputStream());
        int size = fromServer.readInt();
        for (int i = 0; i < size; i++) {
            String tempString = fromServer.readUTF();
            List<String> items = Arrays.asList(tempString.split(" "));
            System.out.println(items);
            data.add(new WarMachine(items.get(0), items.get(1)));
            sqlTable.setItems(data);
        }

    }

}
