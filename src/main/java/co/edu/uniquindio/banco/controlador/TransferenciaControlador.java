package co.edu.uniquindio.banco.controlador;

import co.edu.uniquindio.banco.modelo.Sesion;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import co.edu.uniquindio.banco.modelo.enums.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author juand
 */
public class TransferenciaControlador implements Initializable {
    private ObservableList<Categoria> categorias;
    private Banco banco;
    private Sesion sesion;
    @Setter
    private PanelClienteControlador panelClienteControlador;

    @FXML // fx:id="btntransferir"
    private Button btntransferir; // Value injected by FXMLLoader

    @FXML // fx:id="combocategoria"
    private ComboBox<Categoria> combocategoria; // Value injected by FXMLLoader

    @FXML // fx:id="txtcuenta"
    private TextField txtcuenta; // Value injected by FXMLLoader

    @FXML // fx:id="txtmonto"
    private TextField txtmonto; // Value injected by FXMLLoader

    @FXML
    void TransferirAction(ActionEvent event) {
        if (validarDatos()){
            try {
                banco.realizarTransferencia(banco.buscarBilleteraUsuario(sesion.getUsuario().getId()).getNumero(),txtcuenta.getText(),Float.parseFloat(txtmonto.getText()),combocategoria.getSelectionModel().getSelectedItem());
                vaciarDatos();
                crearAlerta("Transferencia exitosa", Alert.AlertType.INFORMATION);
                panelClienteControlador.cargarDatosTabla();
                cerrarVentana();
            }catch (Exception e){
                crearAlerta(e.getMessage(),Alert.AlertType.ERROR);
            }
        }
    }

    public TransferenciaControlador() {
        banco = Banco.getInstancia();
        sesion = Sesion.getInstance();
        categorias = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categorias.setAll(Categoria.values());
        combocategoria.setItems(categorias);
    }

    boolean validarDatos(){
        String Alerta= "";
        if (txtcuenta.getText().isEmpty()) Alerta = Alerta +"Ingrese un cuenta - ";
        if (txtmonto.getText().isEmpty()) Alerta = Alerta +"Ingrese un monto - ";
        if(combocategoria.getSelectionModel().getSelectedItem() == null) Alerta = Alerta +"Ingrese una categoria";
        if (!Alerta.isEmpty()) {
            crearAlerta(Alerta, Alert.AlertType.ERROR);
            return false;
        }
        try{
            Float.parseFloat(txtmonto.getText());
        }catch (NumberFormatException e){
            crearAlerta("Ingrese un monto valido", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public void vaciarDatos() {
        combocategoria.getSelectionModel().clearSelection();
        txtcuenta.clear();
        txtmonto.clear();
    }

    /**
     * Método que se encarga de mostrar una alerta en pantalla
     * @param mensaje mensaje a mostrar
     * @param tipo tipo de alerta
     */
    public void crearAlerta(String mensaje, Alert.AlertType tipo){
        Alert alert = new Alert(tipo);
        alert.setTitle("Alerta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void cerrarVentana(){
        Stage stage = (Stage) txtmonto.getScene().getWindow();
        stage.close();
    }
}

