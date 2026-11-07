package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {
    private static final String ARCHIVO_DEPARTAMENTOS = "departamentos.json";
    private static final String ARCHIVO_CASAS = "casas.json";
    private Gson gson;

    public ViviendaRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Departamento> cargarDepartamentos() {
        return cargarDatos(ARCHIVO_DEPARTAMENTOS, new TypeToken<List<Departamento>>(){}.getType());
    }

    public List<Casa> cargarCasas() {
        return cargarDatos(ARCHIVO_CASAS, new TypeToken<List<Casa>>(){}.getType());
    }

    public void guardarDepartamentos(List<Departamento> departamentos) {
        guardarDatos(ARCHIVO_DEPARTAMENTOS, departamentos);
    }

    public void guardarCasas(List<Casa> casas) {
        guardarDatos(ARCHIVO_CASAS, casas);
    }

    private <T> List<T> cargarDatos(String ruta, Type tipo) {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(archivo)) {
            List<T> datos = gson.fromJson(reader, tipo);
            return datos != null ? datos : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error al cargar archivo: " + ruta);
            return new ArrayList<>();
        }
    }

    private void guardarDatos(String ruta, Object datos) {
        try (Writer writer = new FileWriter(ruta)) {
            gson.toJson(datos, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + ruta);
        }
    }
}