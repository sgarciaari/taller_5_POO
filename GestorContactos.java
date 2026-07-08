import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// Esta clase se encarga de todo el manejo del archivo (CRUD)
// Create, Read, Update, Delete de contactos guardados en "contactos.txt"
public class GestorContactos {

    private String rutaArchivo = "contactos.txt";

    // CREATE: agrega un contacto nuevo al final del archivo
    public void crear(Contacto contacto) {
        try {
            FileWriter fw = new FileWriter(rutaArchivo, true); // true = agregar al final
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contacto.aLinea());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al crear el contacto: " + e.getMessage());
        }
    }

    // READ: lee todos los contactos que estan guardados en el archivo
    public ArrayList<Contacto> leerTodos() {
        ArrayList<Contacto> lista = new ArrayList<Contacto>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            return lista; // si el archivo no existe todavia, se devuelve la lista vacia
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().equals("")) {
                    String[] partes = linea.split(";");
                    if (partes.length == 2) {
                        Contacto c = new Contacto(partes[0], partes[1]);
                        lista.add(c);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error al leer los contactos: " + e.getMessage());
        }

        return lista;
    }

    // UPDATE: busca un contacto por nombre y le cambia el telefono
    public boolean actualizar(String nombreBuscado, String telefonoNuevo) {
        ArrayList<Contacto> lista = leerTodos();
        boolean encontrado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNombre().equalsIgnoreCase(nombreBuscado)) {
                lista.get(i).setTelefono(telefonoNuevo);
                encontrado = true;
            }
        }

        if (encontrado) {
            reescribirArchivo(lista);
        }

        return encontrado;
    }

    // DELETE: busca un contacto por nombre y lo elimina del archivo
    public boolean eliminar(String nombreBuscado) {
        ArrayList<Contacto> lista = leerTodos();
        ArrayList<Contacto> listaNueva = new ArrayList<Contacto>();
        boolean eliminado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNombre().equalsIgnoreCase(nombreBuscado)) {
                eliminado = true; // este contacto no se agrega a la lista nueva
            } else {
                listaNueva.add(lista.get(i));
            }
        }

        if (eliminado) {
            reescribirArchivo(listaNueva);
        }

        return eliminado;
    }

    // Metodo de apoyo: vuelve a escribir todo el archivo con la lista que se le pasa
    // Se usa despues de actualizar o eliminar, porque un archivo de texto no se puede
    // "editar" a la mitad, hay que reescribirlo completo
    private void reescribirArchivo(ArrayList<Contacto> lista) {
        try {
            FileWriter fw = new FileWriter(rutaArchivo, false); // false = sobrescribe el archivo
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < lista.size(); i++) {
                bw.write(lista.get(i).aLinea());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al reescribir el archivo: " + e.getMessage());
        }
    }
}
