package social;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.util.List;
import social.AdminService.UsuarioInfo;
import informe.ReportePDF;

public class AdminPanel extends JDialog {

    private static final long serialVersionUID = 1L;
    private DefaultListModel<UsuarioInfo> modeloLista;
    private JList<UsuarioInfo> listaUsuarios;
    private UsuarioInfo seleccionado;
    private String usuarioAdminActual;

    private JLabel lblNombre;
    private JLabel lblUsuario;
    private JLabel lblCreado;
    private JLabel lblEstado;
    private JComboBox<String> comboRol;
    private JButton btnEstado;
    private JButton btnGuardarRol;
    private JPanel  panelDetalle;
    private JButton btnReporte;

    private static final Color BG_DARK = new Color(0x2A, 0x2A, 0x2A);
    private static final Color BG_PANEL = new Color(0x3A, 0x3A, 0x3A);
    private static final Color BG_LIST = new Color(0x33, 0x33, 0x33);
    private static final Color PURPLE = new Color(0x7B, 0x2F, 0xFF);
    private static final Color TEXT_WHITE = new Color(0xF0, 0xF0, 0xF0);
    private static final Color TEXT_MUTED = new Color(0xAA, 0xAA, 0xAA);
    private static final Color SEPARATOR = new Color(0x55, 0x55, 0x55);

    public AdminPanel(Dialog owner, String usuarioAdminActual) {
        super(owner, "Panel de Administración", true);
        this.usuarioAdminActual = usuarioAdminActual;

        setSize(650, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);
        

        // Header 
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(BG_DARK);
        panelHeader.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, SEPARATOR),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitulo = new JLabel("Gestión de usuarios");
        lblTitulo.setFont(new Font("Arial Black", Font.BOLD, 16));
        lblTitulo.setForeground(TEXT_WHITE);
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        JButton btnRecargar = new JButton("Actualizar");
        btnRecargar.setUI(new BasicButtonUI());
        btnRecargar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRecargar.setBackground(PURPLE);
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.setOpaque(true);
        btnRecargar.setContentAreaFilled(true);
        btnRecargar.setBorderPainted(false);
        btnRecargar.setFocusPainted(false);
        btnRecargar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRecargar.setPreferredSize(new Dimension(100, 30));
        panelHeader.add(btnRecargar, BorderLayout.EAST);

        getContentPane().add(panelHeader, BorderLayout.NORTH);

        // SplitPane: lista + detalle 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(220);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_DARK);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Lista izquierda
        modeloLista = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloLista);
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUsuarios.setFont(new Font("Arial", Font.PLAIN, 14));
        listaUsuarios.setBackground(BG_LIST);
        listaUsuarios.setForeground(TEXT_WHITE);
        listaUsuarios.setBorder(new EmptyBorder(6, 6, 6, 6));
        listaUsuarios.setFixedCellHeight(38);
        listaUsuarios.setSelectionBackground(PURPLE);
        listaUsuarios.setSelectionForeground(Color.WHITE);

        JScrollPane scrollLista = new JScrollPane(listaUsuarios);
        scrollLista.setBorder(new MatteBorder(0, 0, 0, 1, SEPARATOR));
        scrollLista.getViewport().setBackground(BG_LIST);
        splitPane.setLeftComponent(scrollLista);

        //Panel detalle
        panelDetalle = new JPanel(new GridBagLayout());
        panelDetalle.setBackground(BG_PANEL);
        panelDetalle.setBorder(new EmptyBorder(20, 20, 20, 20));
        splitPane.setRightComponent(panelDetalle);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
     // Panel inferior, independiente del resto (reportes / acciones globales)
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panelFooter.setBackground(BG_DARK);
        panelFooter.setBorder(new MatteBorder(1, 0, 0, 0, SEPARATOR));

        btnReporte = new JButton("Generar Reporte PDF");
        styleBoton(btnReporte, PURPLE);
        btnReporte.setPreferredSize(new Dimension(200, 36));
        btnReporte.addActionListener(e -> onGenerarReporte());
        
        JLabel lblInforme = new JLabel("Informe De Usuarios");
        lblInforme.setForeground(new Color(240, 240, 240));
        lblInforme.setFont(new Font("Arial Black", Font.BOLD, 14));
        panelFooter.add(lblInforme);
        
        Component horizontalStrut = Box.createHorizontalStrut(220);
        panelFooter.add(horizontalStrut);
        panelFooter.add(btnReporte);

        getContentPane().add(panelFooter, BorderLayout.SOUTH);

        construirPanelDetalle();
        mostrarDetallePorDefecto();

        // Listeners
        listaUsuarios.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionado = listaUsuarios.getSelectedValue();
                if (seleccionado != null) poblarDetalle(seleccionado);
            }
        });

        btnGuardarRol.addActionListener(e -> onCambiarRol());
        btnEstado.addActionListener(e -> onCambiarEstado());
        btnRecargar.addActionListener(e -> recargarLista());

        recargarLista();
    }

    // Construcción del panel de detalle 
    private void construirPanelDetalle() {
        panelDetalle.removeAll();

        // Nombre
        GridBagConstraints gbcCapNombre = new GridBagConstraints();
        gbcCapNombre.gridx = 0; gbcCapNombre.gridy = 0;
        gbcCapNombre.fill = GridBagConstraints.HORIZONTAL;
        gbcCapNombre.anchor = GridBagConstraints.WEST;
        gbcCapNombre.weightx = 1.0;
        gbcCapNombre.insets = new Insets(0, 0, 2, 0);
        JLabel capNombre = new JLabel("Nombre");
        capNombre.setFont(new Font("Arial", Font.PLAIN, 11));
        capNombre.setForeground(TEXT_MUTED);
        panelDetalle.add(capNombre, gbcCapNombre);

        GridBagConstraints gbcLblNombre = new GridBagConstraints();
        gbcLblNombre.gridx = 0; gbcLblNombre.gridy = 1;
        gbcLblNombre.fill = GridBagConstraints.HORIZONTAL;
        gbcLblNombre.anchor = GridBagConstraints.WEST;
        gbcLblNombre.weightx = 1.0;
        gbcLblNombre.insets = new Insets(0, 0, 14, 0);
        lblNombre = new JLabel("-");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
        lblNombre.setForeground(TEXT_WHITE);
        panelDetalle.add(lblNombre, gbcLblNombre);

        // Usuario
        GridBagConstraints gbcCapUsuario = new GridBagConstraints();
        gbcCapUsuario.gridx = 0; gbcCapUsuario.gridy = 2;
        gbcCapUsuario.fill = GridBagConstraints.HORIZONTAL;
        gbcCapUsuario.anchor = GridBagConstraints.WEST;
        gbcCapUsuario.weightx = 1.0;
        gbcCapUsuario.insets = new Insets(0, 0, 2, 0);
        JLabel capUsuario = new JLabel("Usuario");
        capUsuario.setFont(new Font("Arial", Font.PLAIN, 11));
        capUsuario.setForeground(TEXT_MUTED);
        panelDetalle.add(capUsuario, gbcCapUsuario);

        GridBagConstraints gbcLblUsuario = new GridBagConstraints();
        gbcLblUsuario.gridx = 0; gbcLblUsuario.gridy = 3;
        gbcLblUsuario.fill = GridBagConstraints.HORIZONTAL;
        gbcLblUsuario.anchor = GridBagConstraints.WEST;
        gbcLblUsuario.weightx = 1.0;
        gbcLblUsuario.insets = new Insets(0, 0, 14, 0);
        lblUsuario = new JLabel("-");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsuario.setForeground(TEXT_WHITE);
        panelDetalle.add(lblUsuario, gbcLblUsuario);

        // Miembro desde
        GridBagConstraints gbcCapCreado = new GridBagConstraints();
        gbcCapCreado.gridx = 0; gbcCapCreado.gridy = 4;
        gbcCapCreado.fill = GridBagConstraints.HORIZONTAL;
        gbcCapCreado.anchor = GridBagConstraints.WEST;
        gbcCapCreado.weightx = 1.0;
        gbcCapCreado.insets = new Insets(0, 0, 2, 0);
        JLabel capCreado = new JLabel("Miembro desde");
        capCreado.setFont(new Font("Arial", Font.PLAIN, 11));
        capCreado.setForeground(TEXT_MUTED);
        panelDetalle.add(capCreado, gbcCapCreado);

        GridBagConstraints gbcLblCreado = new GridBagConstraints();
        gbcLblCreado.gridx = 0; gbcLblCreado.gridy = 5;
        gbcLblCreado.fill = GridBagConstraints.HORIZONTAL;
        gbcLblCreado.anchor = GridBagConstraints.WEST;
        gbcLblCreado.weightx = 1.0;
        gbcLblCreado.insets = new Insets(0, 0, 14, 0);
        lblCreado = new JLabel("-");
        lblCreado.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCreado.setForeground(TEXT_WHITE);
        panelDetalle.add(lblCreado, gbcLblCreado);

        // Estado
        GridBagConstraints gbcCapEstado = new GridBagConstraints();
        gbcCapEstado.gridx = 0; gbcCapEstado.gridy = 6;
        gbcCapEstado.fill = GridBagConstraints.HORIZONTAL;
        gbcCapEstado.anchor = GridBagConstraints.WEST;
        gbcCapEstado.weightx = 1.0;
        gbcCapEstado.insets = new Insets(0, 0, 2, 0);
        JLabel capEstado = new JLabel("Estado");
        capEstado.setFont(new Font("Arial", Font.PLAIN, 11));
        capEstado.setForeground(TEXT_MUTED);
        panelDetalle.add(capEstado, gbcCapEstado);

        GridBagConstraints gbcLblEstado = new GridBagConstraints();
        gbcLblEstado.gridx = 0; gbcLblEstado.gridy = 7;
        gbcLblEstado.fill = GridBagConstraints.HORIZONTAL;
        gbcLblEstado.anchor = GridBagConstraints.WEST;
        gbcLblEstado.weightx = 1.0;
        gbcLblEstado.insets = new Insets(0, 0, 18, 0);
        lblEstado = new JLabel("-");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstado.setForeground(TEXT_MUTED);
        panelDetalle.add(lblEstado, gbcLblEstado);

        // Separador
        GridBagConstraints gbcSep = new GridBagConstraints();
        gbcSep.gridx = 0; gbcSep.gridy = 8;
        gbcSep.fill = GridBagConstraints.HORIZONTAL;
        gbcSep.weightx = 1.0;
        gbcSep.insets = new Insets(0, 0, 14, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(SEPARATOR);
        panelDetalle.add(sep, gbcSep);

        // Label Rol
        GridBagConstraints gbcCapRol = new GridBagConstraints();
        gbcCapRol.gridx = 0; gbcCapRol.gridy = 9;
        gbcCapRol.fill = GridBagConstraints.HORIZONTAL;
        gbcCapRol.anchor = GridBagConstraints.WEST;
        gbcCapRol.weightx = 1.0;
        gbcCapRol.insets = new Insets(0, 0, 6, 0);
        JLabel capRol = new JLabel("Rol");
        capRol.setFont(new Font("Arial", Font.PLAIN, 11));
        capRol.setForeground(TEXT_MUTED);
        panelDetalle.add(capRol, gbcCapRol);

        // ComboBox Rol
        GridBagConstraints gbcComboRol = new GridBagConstraints();
        gbcComboRol.gridx = 0; gbcComboRol.gridy = 10;
        gbcComboRol.fill = GridBagConstraints.HORIZONTAL;
        gbcComboRol.weightx = 1.0;
        gbcComboRol.insets = new Insets(0, 0, 8, 0);
        comboRol = new JComboBox<>(new String[]{"usuario", "supervisor", "admin"});
        comboRol.setFont(new Font("Arial", Font.PLAIN, 14));
        comboRol.setBackground(BG_LIST);
        comboRol.setForeground(TEXT_WHITE);
        comboRol.setEnabled(false);
        panelDetalle.add(comboRol, gbcComboRol);

        // Botón Guardar Rol
        GridBagConstraints gbcBtnGuardarRol = new GridBagConstraints();
        gbcBtnGuardarRol.gridx = 0; gbcBtnGuardarRol.gridy = 11;
        gbcBtnGuardarRol.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnGuardarRol.weightx = 1.0;
        gbcBtnGuardarRol.insets = new Insets(0, 0, 8, 0);
        btnGuardarRol = new JButton("Guardar rol");
        styleBoton(btnGuardarRol, PURPLE);
        btnGuardarRol.setEnabled(false);
        panelDetalle.add(btnGuardarRol, gbcBtnGuardarRol);

        // Botón Estado
        GridBagConstraints gbcBtnEstado = new GridBagConstraints();
        gbcBtnEstado.gridx = 0; gbcBtnEstado.gridy = 12;
        gbcBtnEstado.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnEstado.weightx = 1.0;
        gbcBtnEstado.insets = new Insets(0, 0, 0, 0);
        btnEstado = new JButton("—");
        styleBoton(btnEstado, new Color(0xAA, 0x22, 0x22));
        btnEstado.setEnabled(false);
        panelDetalle.add(btnEstado, gbcBtnEstado);

        // Espaciador
        GridBagConstraints gbcGlue = new GridBagConstraints();
        gbcGlue.gridx = 0; gbcGlue.gridy = 13;
        gbcGlue.fill = GridBagConstraints.BOTH;
        gbcGlue.weightx = 1.0; gbcGlue.weighty = 1.0;
        panelDetalle.add(Box.createVerticalGlue(), gbcGlue);
    }

    private void styleBoton(JButton btn, Color bg) {
        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 36));
    }

    // Poblar / limpiar detalle 

    private void mostrarDetallePorDefecto() {
        lblNombre.setText("Seleccioná un usuario");
        lblNombre.setFont(new Font("Arial", Font.ITALIC, 14));
        lblNombre.setForeground(TEXT_MUTED);
        lblUsuario.setText("—");
        lblCreado.setText("—");
        lblEstado.setText("—");
        lblEstado.setForeground(TEXT_MUTED);
        comboRol.setEnabled(false);
        btnGuardarRol.setEnabled(false);
        btnEstado.setText("—");
        btnEstado.setEnabled(false);
    }

    private void poblarDetalle(UsuarioInfo u) {
        lblNombre.setText(u.nombre);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 15));
        lblNombre.setForeground(TEXT_WHITE);
        lblUsuario.setText("@" + u.usuario);
        lblCreado.setText(u.creadoEn);

        if (u.activo) {
            lblEstado.setText("● Activo");
            lblEstado.setForeground(new Color(0x44, 0xDD, 0x77));
            btnEstado.setText("Suspender cuenta");
        } else {
            lblEstado.setText("● Suspendido");
            lblEstado.setForeground(new Color(0xFF, 0x55, 0x55));
            btnEstado.setText("Activar cuenta");
        }

        comboRol.setSelectedItem(u.rol);
        comboRol.setEnabled(true);
        btnGuardarRol.setEnabled(true);
        btnEstado.setEnabled(true);
    }

    // Acciones

    private void onCambiarRol() {
        if (seleccionado == null) return;
        String nuevoRol = (String) comboRol.getSelectedItem();

        if (nuevoRol.equals(seleccionado.rol)) {
            JOptionPane.showMessageDialog(this,
                    "El usuario ya tiene ese rol.", "Sin cambios",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Cambiar rol de @" + seleccionado.usuario + " a \"" + nuevoRol + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        boolean ok = AdminService.cambiarRol(seleccionado.id, nuevoRol);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Rol actualizado.", "Listo",
                    JOptionPane.INFORMATION_MESSAGE);
            recargarLista();
        } else {
            if ("admin".equalsIgnoreCase(nuevoRol)) {
                JOptionPane.showMessageDialog(this, 
                        "No se pudo asignar el rol.\nEl sistema permite un máximo de 3 administradores simultáneos.", 
                        "Límite alcanzado", 
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar el rol en la base de datos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            recargarLista(); 
        }
    }

    private void onCambiarEstado() {
        if (seleccionado == null) return;
        boolean activar = !seleccionado.activo;
        String accion   = activar ? "activar" : "suspender";

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Deseas " + accion + " la cuenta de @" + seleccionado.usuario + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        boolean ok = AdminService.cambiarEstado(seleccionado.id, activar);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Cuenta " + (activar ? "activada" : "suspendida") + ".", "Listo",
                    JOptionPane.INFORMATION_MESSAGE);
            recargarLista();
        } else {
            JOptionPane.showMessageDialog(this, "Error al cambiar estado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onGenerarReporte() {
        btnReporte.setEnabled(false);
        btnReporte.setText("Generando...");

        new SwingWorker<Void, Void>() {
            private String errorMensaje = null;
            private String rutaGenerada = null;

            @Override
            protected Void doInBackground() {
                try {
                    rutaGenerada = ReportePDF.generarReporte();
                } catch (Exception ex) {
                    errorMensaje = ex.getMessage();
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                btnReporte.setEnabled(true);
                btnReporte.setText("Generar Reporte PDF");

                if (errorMensaje != null) {
                    JOptionPane.showMessageDialog(AdminPanel.this,
                            "Error al generar el reporte:\n" + errorMensaje,
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(AdminPanel.this,
                            "Reporte generado en:\n" + rutaGenerada,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }.execute();
    }

    private void recargarLista() {
        int idAnterior = seleccionado != null ? seleccionado.id : -1;
        modeloLista.clear();
        mostrarDetallePorDefecto();
        seleccionado = null;

        List<UsuarioInfo> usuarios = AdminService.cargarUsuarios(this.usuarioAdminActual);
        for (UsuarioInfo u : usuarios) {
            modeloLista.addElement(u);
        }

        if (idAnterior != -1) {
            for (int i = 0; i < modeloLista.size(); i++) {
                if (modeloLista.get(i).id == idAnterior) {
                    listaUsuarios.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public static void mostrar(Dialog owner, String usuarioAdminActual) {
        new AdminPanel(owner, usuarioAdminActual).setVisible(true);
    }
}