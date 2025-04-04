package co.edu.uniquindio.banco.controlador;

import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.banco.modelo.Sesion;
import co.edu.uniquindio.banco.modelo.entidades.Banco;
import co.edu.uniquindio.banco.modelo.entidades.Transaccion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * @author juand
 */
public class PanelClienteControlador implements Initializable {

    private final Banco banco;
    private final Sesion sesion;

    private ObservableList<Transaccion> transacciones;

    @FXML // fx:id="columncategoria"
    private TableColumn<Transaccion, String> columncategoria; // Value injected by FXMLLoader

    @FXML // fx:id="columnfecha"
    private TableColumn<Transaccion, String> columnfecha; // Value injected by FXMLLoader

    @FXML // fx:id="columntipo"
    private TableColumn<Transaccion, String> columntipo; // Value injected by FXMLLoader

    @FXML // fx:id="columnusuario"
    private TableColumn<Transaccion, String> columnusuario; // Value injected by FXMLLoader

    @FXML // fx:id="columnvalor"
    private TableColumn<Transaccion, String> columnvalor; // Value injected by FXMLLoader

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tabletransacciones"
    private TableView<Transaccion> tabletransacciones; // Value injected by FXMLLoader

    @FXML // fx:id="btnsaldo"
    private Button btnsaldo; // Value injected by FXMLLoader

    @FXML // fx:id="lblcuenta"
    private Label lblcuenta; // Value injected by FXMLLoader

    @FXML // fx:id="lblnombre"
    private Label lblnombre; // Value injected by FXMLLoader

    @FXML // fx:id="menubuttonperfil"
    private MenuButton menubuttonperfil; // Value injected by FXMLLoader

    @FXML // fx:id="menubuttontransacciones"
    private MenuButton menubuttontransacciones; // Value injected by FXMLLoader


    public PanelClienteControlador() {
        banco = Banco.getInstancia();
        sesion = Sesion.getInstance();
    }

    @FXML
    void ConsultarSaldoAction(ActionEvent event) {
        crearAlerta("Su saldo actual es de:"+ banco.buscarBilleteraUsuario(sesion.getUsuario().getId()).consultarSaldo(), Alert.AlertType.INFORMATION);
    }

    @FXML
    void CerrarSesionAction(ActionEvent event) {
        sesion.cerrarSesion();
        cerrarVentana();
        navegarVentana("/inicio.fxml", "Banco");
    }

    @FXML
    void ActualizarDatosAction(ActionEvent event) {
        navegarVentana("/actualizar.fxml", "Banco - Actualizar Informacion");
        cerrarVentana();
    }

    @FXML
    void RecargarSaldo(ActionEvent event) {
        try {
            // Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recarga.fxml"));
            Parent root = loader.load();
            RecargaControlador controlador =  loader.getController();
            // Crear la escena
            Scene scene = new Scene(root);

            // Crear un nuevo escenario (ventana)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Banco - Hacer recarga");
            controlador.setPanelClienteControlador(this);

            // Mostrar la nueva ventana
            stage.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void HacerTransferencia(ActionEvent event) {
        try {
            // Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transferencia.fxml"));
            Parent root = loader.load();
            TransferenciaControlador controlador = (TransferenciaControlador) loader.getController();
            // Crear la escena
            Scene scene = new Scene(root);

            // Crear un nuevo escenario (ventana)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Banco - Hacer transferencia");
            controlador.setPanelClienteControlador(this);

            // Mostrar la nueva ventana
            stage.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Asignar las propiedades de la nota a las columnas de la tabla
        columncategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo().toString()));
        columntipo.setCellValueFactory(cellData-> {
            Transaccion transaccion = cellData.getValue();
            if (transaccion.getBilleteraOrigen().equals(banco.buscarBilleteraUsuario(sesion.getUsuario().getId())) && transaccion.getBilleteraDestino().equals(banco.buscarBilleteraUsuario(sesion.getUsuario().getId()))) {
                return new SimpleStringProperty("RECARGA");
            }
            if (transaccion.getBilleteraOrigen().equals(banco.buscarBilleteraUsuario(sesion.getUsuario().getId()))) {
                return new SimpleStringProperty("DEPOSITO");
            }
            return new SimpleStringProperty("INGRESO");
        });

        //Cargar categorias en el ComboBox
        columnfecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFecha().toString()));
        columnusuario.setCellValueFactory(cellData -> {
          Transaccion transaccion = cellData.getValue();
          if (transaccion.getBilleteraOrigen().equals(banco.buscarBilleteraUsuario(sesion.getUsuario().getId()))) {
              return new SimpleStringProperty(transaccion.getBilleteraDestino().getUsuario().getNombre());
          }
          return new SimpleStringProperty(transaccion.getBilleteraOrigen().getUsuario().getNombre());
        });
        columnvalor.setCellValueFactory(cellData -> new SimpleStringProperty("" +cellData.getValue().getMonto()));

        lblnombre.setText(sesion.getUsuario().getNombre()+" "+lblnombre.getText());
        lblcuenta.setText(lblcuenta.getText() + " " + banco.buscarBilleteraUsuario(sesion.getUsuario().getId()).getNumero());

        transacciones = FXCollections.observableArrayList();
        cargarDatosTabla();
        menubuttonperfil.setText(sesion.getUsuario().getNombre());
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
        Stage stage = (Stage) lblcuenta.getScene().getWindow();
        stage.close();
    }

    public void cargarDatosTabla(){
        transacciones.setAll(banco.buscarBilleteraUsuario(sesion.getUsuario().getId()).obtenerTransacciones());
        tabletransacciones.setItems(transacciones);
    }

    public void vaciarDatos() {
        menubuttonperfil.getItems().clear();
        menubuttontransacciones.getItems().clear();
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
