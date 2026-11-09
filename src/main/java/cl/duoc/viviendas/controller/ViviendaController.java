package cl.duoc.viviendas.controller;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ViviendaController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;

    @FXML private TextField txtPiso;
    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;

    @FXML private CheckBox chkPatio;

    @FXML private TableView<Vivienda> tablaViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, Double> colCosto;

    @FXML private Label lblMensaje;

    private ViviendaRepository repositorio;
    private ObservableList<Vivienda> listaViviendas;

    @FXML
    public void initialize() {
        repositorio = new ViviendaRepository();
        listaViviendas = FXCollections.observableArrayList();

        cmbTipo.getItems().addAll("Departamento", "Casa");

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colCosto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().calcularCostoArriendo()).asObject());
        listaViviendas.addAll(repositorio.cargarDepartamentos());
        listaViviendas.addAll(repositorio.cargarCasas());
        tablaViviendas.setItems(listaViviendas);
    }

    @FXML
    public void alCambiarTipo() {
        String tipoSeleccionado = cmbTipo.getValue();

        if (tipoSeleccionado == null) return;

        if (tipoSeleccionado.equals("Departamento")) {
            txtPiso.setDisable(false);
            chkGastoComun.setDisable(false);
            chkEstacionamiento.setDisable(false);
            chkPatio.setDisable(true);
            chkPatio.setSelected(false);
        } else if (tipoSeleccionado.equals("Casa")) {
            chkPatio.setDisable(false);
            txtPiso.setDisable(true);
            txtPiso.clear();
            chkGastoComun.setDisable(true);
            chkGastoComun.setSelected(false);
            chkEstacionamiento.setDisable(true);
            chkEstacionamiento.setSelected(false);
        }
    }
    @FXML
    public void registrarVivienda() {
        try {
            String tipo = cmbTipo.getValue();
            if (tipo == null) {
                mostrarError("Debe seleccionar un tipo de vivienda.");
                return;
            }

            String codigo = txtCodigo.getText();

            double superficie;
            try {
                superficie = Double.parseDouble(txtSuperficie.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("Superficie: debe ser un número válido.");
                return;
            }

            int habitaciones;
            try {
                habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("Habitaciones: debe ser un número entero.");
                return;
            }

            if (tipo.equals("Departamento")) {
                int piso;
                try {
                    piso = Integer.parseInt(txtPiso.getText().trim());
                    if(piso <= 0) {
                        mostrarError("Piso: debe ser un número entero mayor que cero.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("Piso: debe ser un número entero.");
                    return;
                }

                Departamento depto = new Departamento(codigo, superficie, habitaciones, piso, chkGastoComun.isSelected());

                if (chkEstacionamiento.isSelected()) {
                    depto.asignarEstacionamiento();
                }

                List<Departamento> deptos = repositorio.cargarDepartamentos();
                deptos.add(depto);
                repositorio.guardarDepartamentos(deptos);

                listaViviendas.add(depto);
                mostrarExito(codigo + " (Departamento) registrada correctamente.");

            } else if (tipo.equals("Casa")) {
                Casa casa = new Casa(codigo, superficie, habitaciones, chkPatio.isSelected());

                List<Casa> casas = repositorio.cargarCasas();
                casas.add(casa);
                repositorio.guardarCasas(casas);

                listaViviendas.add(casa);
                mostrarExito(codigo + " (Casa) registrada correctamente.");
            }

            limpiarFormulario();

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void eliminarVivienda() {
        Vivienda seleccionada = tablaViviendas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Debe seleccionar una vivienda de la tabla para eliminarla.");
            return;
        }

        if (seleccionada instanceof Departamento) {
            List<Departamento> deptos = repositorio.cargarDepartamentos();
            deptos.removeIf(d -> d.getCodigo().equals(seleccionada.getCodigo()));
            repositorio.guardarDepartamentos(deptos);
        } else if (seleccionada instanceof Casa) {
            List<Casa> casas = repositorio.cargarCasas();
            casas.removeIf(c -> c.getCodigo().equals(seleccionada.getCodigo()));
            repositorio.guardarCasas(casas);
        }

        listaViviendas.remove(seleccionada);


        mostrarExito("Vivienda eliminada correctamente.");
    }


    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();
        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
        chkPatio.setSelected(false);
    }
}