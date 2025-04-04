package co.edu.uniquindio.banco.controlador;

import co.edu.uniquindio.banco.modelo.Sesion;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

/**
 * @author juand
 */
public class RecargaControlador {
    private Banco banco;
    private Sesion sesion;
    @Setter
    private PanelClienteControlador panelClienteControlador;

    @FXML // fx:id="btnrecargar"
    private Button btnrecargar; // Value injected by FXMLLoader

    @FXML // fx:id="txtmonto"
    private TextField txtmonto; // Value injected by FXMLLoader

    public RecargaControlador() {
        banco = Banco.getInstancia();
        sesion = Sesion.getInstance();
    }

    @FXML
    void RecargarAction(ActionEvent event) {
        if (validarDatos()){
            try {
                banco.recargarBilletera(banco.buscarBilleteraUsuario(sesion.getUsuario().getId()).getNumero(),Float.parseFloat(txtmonto.getText()));
                vaciarDatos();
                crearAlerta("Recarga exitosa", Alert.AlertType.INFORMATION);
                panelClienteControlador.cargarDatosTabla();
                cerrarVentana();
            }catch (Exception e){
                crearAlerta(e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    boolean validarDatos(){
        if (txtmonto.getText().isEmpty()){
            crearAlerta("Ingrese monto", Alert.AlertType.ERROR);
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
        txtmonto.clear();
    }

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
