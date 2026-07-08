import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

// Ventana principal del programa.
// Aqui esta el formulario con los 4 botones: Crear, Leer, Actualizar y Eliminar
public class VentanaContactos extends JFrame {

    // Colores del tema morado oscuro
    private Color colorFondo = new Color(30, 27, 46);
    private Color colorPanel = new Color(43, 38, 64);
    private Color colorMorado = new Color(142, 68, 173);
    private Color colorMoradoOscuro = new Color(94, 43, 122);
    private Color colorTexto = new Color(230, 224, 245);

    private GestorContactos gestor;

    private JTextField campoNombre;
    private JTextField campoTelefono;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel etiquetaEstado;

    public VentanaContactos() {
        gestor = new GestorContactos();

        setTitle("Gestor de Contactos - CRUD con Archivo");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(colorFondo);
        setLayout(new BorderLayout(10, 10));

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.WEST);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelEstado(), BorderLayout.SOUTH);

        // al abrir la ventana, se muestran los contactos que ya existan en el archivo
        leerContactos();
    }

    // Panel de arriba con el titulo del programa
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setBackground(colorFondo);
        JLabel titulo = new JLabel("Gestor de Contactos (archivo .txt)");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(colorTexto);
        panel.add(titulo);
        return panel;
    }

    // Panel de la izquierda con los campos de texto y los botones CRUD
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 5, 8));
        panel.setBackground(colorPanel);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel etiquetaNombre = new JLabel("Nombre:");
        etiquetaNombre.setForeground(colorTexto);
        campoNombre = new JTextField();

        JLabel etiquetaTelefono = new JLabel("Telefono:");
        etiquetaTelefono.setForeground(colorTexto);
        campoTelefono = new JTextField();

        JButton botonCrear = crearBoton("Crear");
        JButton botonLeer = crearBoton("Leer");
        JButton botonActualizar = crearBoton("Actualizar");
        JButton botonEliminar = crearBoton("Eliminar");

        botonCrear.addActionListener(e -> crearContacto());
        botonLeer.addActionListener(e -> leerContactos());
        botonActualizar.addActionListener(e -> actualizarContacto());
        botonEliminar.addActionListener(e -> eliminarContacto());

        panel.add(etiquetaNombre);
        panel.add(campoNombre);
        panel.add(etiquetaTelefono);
        panel.add(campoTelefono);
        panel.add(botonCrear);
        panel.add(botonLeer);
        panel.add(botonActualizar);
        panel.add(botonEliminar);

        return panel;
    }

    // Metodo de apoyo para que los 4 botones se vean iguales (morado sobre fondo oscuro)
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(colorMorado);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBorder(BorderFactory.createLineBorder(colorMoradoOscuro, 2));
        return boton;
    }

    // Panel del centro con la tabla que muestra los contactos leidos del archivo
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 15));

        String[] columnas = {"Nombre", "Telefono"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        tabla.setBackground(colorPanel);
        tabla.setForeground(colorTexto);
        tabla.setGridColor(colorMoradoOscuro);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setBackground(colorMoradoOscuro);
        tabla.getTableHeader().setForeground(Color.WHITE);

        // al hacer clic en una fila, se llenan los campos de texto con ese contacto
        // esto ayuda para poder actualizar o eliminar ese contacto facilmente
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                campoNombre.setText(modeloTabla.getValueAt(fila, 0).toString());
                campoTelefono.setText(modeloTabla.getValueAt(fila, 1).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // Panel de abajo con un mensaje de estado para el usuario
    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel();
        panel.setBackground(colorFondo);
        etiquetaEstado = new JLabel("Listo.");
        etiquetaEstado.setForeground(colorTexto);
        panel.add(etiquetaEstado);
        return panel;
    }

    // ---------- Acciones de los 4 botones (los casos de uso) ----------

    // CREATE
    private void crearContacto() {
        String nombre = campoNombre.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.equals("") || telefono.equals("")) {
            JOptionPane.showMessageDialog(this, "Debes llenar el nombre y el telefono.");
            return;
        }

        Contacto nuevo = new Contacto(nombre, telefono);
        gestor.crear(nuevo);
        etiquetaEstado.setText("Contacto creado: " + nombre);
        limpiarCampos();
        leerContactos();
    }

    // READ
    private void leerContactos() {
        ArrayList<Contacto> lista = gestor.leerTodos();
        modeloTabla.setRowCount(0); // borra las filas viejas de la tabla

        for (int i = 0; i < lista.size(); i++) {
            Contacto c = lista.get(i);
            Object[] fila = {c.getNombre(), c.getTelefono()};
            modeloTabla.addRow(fila);
        }

        etiquetaEstado.setText("Se leyeron " + lista.size() + " contactos del archivo.");
    }

    // UPDATE
    private void actualizarContacto() {
        String nombre = campoNombre.getText().trim();
        String telefonoNuevo = campoTelefono.getText().trim();

        if (nombre.equals("") || telefonoNuevo.equals("")) {
            JOptionPane.showMessageDialog(this, "Debes escribir el nombre y el nuevo telefono.");
            return;
        }

        boolean exito = gestor.actualizar(nombre, telefonoNuevo);

        if (exito) {
            etiquetaEstado.setText("Contacto actualizado: " + nombre);
        } else {
            etiquetaEstado.setText("No se encontro un contacto con ese nombre.");
        }

        limpiarCampos();
        leerContactos();
    }

    // DELETE
    private void eliminarContacto() {
        String nombre = campoNombre.getText().trim();

        if (nombre.equals("")) {
            JOptionPane.showMessageDialog(this, "Escribe el nombre del contacto a eliminar.");
            return;
        }

        boolean exito = gestor.eliminar(nombre);

        if (exito) {
            etiquetaEstado.setText("Contacto eliminado: " + nombre);
        } else {
            etiquetaEstado.setText("No se encontro un contacto con ese nombre.");
        }

        limpiarCampos();
        leerContactos();
    }

    private void limpiarCampos() {
        campoNombre.setText("");
        campoTelefono.setText("");
    }

    public static void main(String[] args) {
        VentanaContactos ventana = new VentanaContactos();
        ventana.setVisible(true);
    }
}
