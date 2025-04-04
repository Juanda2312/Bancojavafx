package co.edu.uniquindio.banco.controlador;

import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.banco.modelo.Sesion;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import co.edu.uniquindio.banco.modelo.entidades.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @author juand
 */
public class LoginControlador {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private Button btnvolver;

    @FXML // fx:id="btniniciarsesion"
    private Button btniniciarsesion; // Value injected by FXMLLoader

    @FXML // fx:id="txtcontraseña"
    private TextField txtcontrasena; // Value injected by FXMLLoader

    @FXML // fx:id="txtid"
    private TextField txtid; // Value injected by FXMLLoader

    private final Banco banco;

    public LoginControlador() {
        banco = Banco.getInstancia();
    }

    @FXML
    void IniciarSesionAction(ActionEvent event) {
        if (!(txtid.getText().isEmpty()|| txtcontrasena.getText().isEmpty())) {
            Usuario usuario = banco.IniciarSesion(txtid.getText(), txtcontrasena.getText());
            if (usuario != null) {
                Sesion sesion = Sesion.getInstance();
                sesion.setUsuario(usuario);
                navegarVentana("/panelCliente.fxml", "Banco - Panel de transferencias");
                cerrarVentana();
            }else {
                crearAlerta("No se ha encontrado un usuario con esas credenciales", Alert.AlertType.ERROR);
            }
        }else{
            crearAlerta("Rellene todos los campos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void volverAction(ActionEvent event) {
        navegarVentana("/inicio.fxml", "Banco");
        cerrarVentana();
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

    /**
     * Método que se encarga de cerrar la ventana actual
     */
    public void cerrarVentana(){
        Stage stage = (Stage) txtid.getScene().getWindow();
        stage.close();
    }

    /**
     * Método que permite ir a la venana indicada por el nombre del archivo FXML
     * @param nombreArchivoFxml Nombre del archivo FXML
     * @param tituloVentana Título de la ventana
     */
    public void navegarVentana(String nombreArchivoFxml, String tituloVentana) {
        try {

            // Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Crear un nuevo escenario (ventana)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(tituloVentana);

            // Mostrar la nueva ventana
            stage.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
