package login_test;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import org.mindrot.jbcrypt.BCrypt;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public class Login extends JDialog {

    private static final long serialVersionUID = 1L;
    public static JTextField campoUsuario;
    private final JPasswordField campoContrasena;
    private final JButton botonAcceder = new JButton("ACCEDER");
    private int intentos = 2;
    private boolean loginExitoso = false;
    private String usuarioNombre;
    private String usuarioRol;
    private int usuarioId;

    public boolean isLoginExitoso(){ return loginExitoso; }
    public String getUsuarioNombre(){ return usuarioNombre; }
    public String getUsuarioRol(){ return usuarioRol; }
    public int    getUsuarioId(){ return usuarioId; }

    private static final Color BG= new Color(0x1E, 0x1E, 0x1E);
    private static final Color PURPLE= new Color(0x7B, 0x2F, 0xFF);
    private static final Color GRAY_TEXT= new Color(0xAA, 0xAA, 0xAA);

    public Login(Frame owner) {
        super(owner, "Iniciar Sesión", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(420, 560);
        setLocationRelativeTo(owner);
        setResizable(false);

        // Fondo oscuro 
        JPanel panelDeFondo = new JPanel(new GridBagLayout());
        panelDeFondo.setBackground(BG);
        panelDeFondo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            new LineBorder(PURPLE, 3)
        ));
        setContentPane(panelDeFondo);

        // Panel de login centrado
        JPanel panelDeLogin = new JPanel(new GridBagLayout());
        panelDeLogin.setOpaque(false);
        panelDeLogin.setPreferredSize(new Dimension(320, 460));
        panelDeFondo.add(panelDeLogin, new GridBagConstraints());

        // Título
        JLabel nivelTitulo = new JLabel("Iniciar Sesión");
        nivelTitulo.setFont(new Font("Arial Black", Font.BOLD, 32));
        nivelTitulo.setForeground(Color.WHITE);
        nivelTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbcTitulo = new GridBagConstraints();
        gbcTitulo.gridx = 0; gbcTitulo.gridy = 0;
        gbcTitulo.insets = new Insets(20, 0, 2, 0);
        panelDeLogin.add(nivelTitulo, gbcTitulo);

        // Subtítulo
        JLabel subtitle = new JLabel("Accede a tu cuenta");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(GRAY_TEXT);
        subtitle.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbcSubtitle = new GridBagConstraints();
        gbcSubtitle.gridx = 0; gbcSubtitle.gridy = 1;
        gbcSubtitle.insets = new Insets(0, 0, 24, 0);
        panelDeLogin.add(subtitle, gbcSubtitle);

        // Label Usuario 
        JLabel nivelUsuario = new JLabel("Usuario");
        nivelUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        nivelUsuario.setForeground(GRAY_TEXT);
        GridBagConstraints gbcLabelUsuario = new GridBagConstraints();
        gbcLabelUsuario.gridx = 0; gbcLabelUsuario.gridy = 2;
        gbcLabelUsuario.fill = GridBagConstraints.HORIZONTAL;
        gbcLabelUsuario.insets = new Insets(0, 0, 4, 0);
        panelDeLogin.add(nivelUsuario, gbcLabelUsuario);

        // Campo Usuario
        campoUsuario = new JTextField(20);
        estilarCampo(campoUsuario, "Nombre de usuario");
        GridBagConstraints gbcCampoUsuario = new GridBagConstraints();
        gbcCampoUsuario.gridx = 0; gbcCampoUsuario.gridy = 3;
        gbcCampoUsuario.fill = GridBagConstraints.HORIZONTAL;
        gbcCampoUsuario.insets = new Insets(0, 0, 16, 0);
        panelDeLogin.add(campoUsuario, gbcCampoUsuario);

        //  Label Contraseña 
        JLabel nivelContrasena = new JLabel("Contraseña");
        nivelContrasena.setFont(new Font("Arial", Font.PLAIN, 13));
        nivelContrasena.setForeground(GRAY_TEXT);
        GridBagConstraints gbcLabelContrasena = new GridBagConstraints();
        gbcLabelContrasena.gridx = 0; gbcLabelContrasena.gridy = 4;
        gbcLabelContrasena.fill = GridBagConstraints.HORIZONTAL;
        gbcLabelContrasena.insets = new Insets(0, 0, 4, 0);
        panelDeLogin.add(nivelContrasena, gbcLabelContrasena);

        // Campo Contraseña 
        campoContrasena = new JPasswordField(20);
        estilarCampo(campoContrasena, "••••••••••••");
        GridBagConstraints gbcCampoContrasena = new GridBagConstraints();
        gbcCampoContrasena.gridx = 0; gbcCampoContrasena.gridy = 5;
        gbcCampoContrasena.fill = GridBagConstraints.HORIZONTAL;
        gbcCampoContrasena.insets = new Insets(0, 0, 28, 0);
        panelDeLogin.add(campoContrasena, gbcCampoContrasena);

        //  Botón Acceder 
        botonAcceder.setFont(new Font("Arial Black", Font.BOLD, 14));
        botonAcceder.setBackground(PURPLE);
        botonAcceder.setForeground(Color.WHITE);
        botonAcceder.setOpaque(true);
        botonAcceder.setContentAreaFilled(true);
        botonAcceder.setBorderPainted(false);
        botonAcceder.setFocusPainted(false);
        botonAcceder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonAcceder.setPreferredSize(new Dimension(320, 44));
        botonAcceder.addActionListener(e -> validarLogin());
        GridBagConstraints gbcBotonAcceder = new GridBagConstraints();
        gbcBotonAcceder.gridx = 0; gbcBotonAcceder.gridy = 6;
        gbcBotonAcceder.fill = GridBagConstraints.HORIZONTAL;
        gbcBotonAcceder.insets = new Insets(0, 0, 16, 0);
        panelDeLogin.add(botonAcceder, gbcBotonAcceder);

        //  Fila "¿No tienes cuenta? Regístrate" 
        JPanel registroPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registroPanel.setOpaque(false);
        JLabel txtReg = new JLabel("¿No tienes cuenta?");
        txtReg.setFont(new Font("Arial", Font.PLAIN, 13));
        txtReg.setForeground(GRAY_TEXT);
        JButton botonRegistrarse = new JButton("Regístrate");
        botonRegistrarse.setFont(new Font("Arial", Font.PLAIN, 13));
        botonRegistrarse.setForeground(PURPLE);
        botonRegistrarse.setOpaque(false);
        botonRegistrarse.setContentAreaFilled(false);
        botonRegistrarse.setBorderPainted(false);
        botonRegistrarse.setFocusPainted(false);
        botonRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonRegistrarse.setMargin(new Insets(0, 0, 0, 0));
        botonRegistrarse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Registro.mostrar(Login.this);
            }
        });
        registroPanel.add(txtReg);
        registroPanel.add(botonRegistrarse);
        GridBagConstraints gbcRegistroPanel = new GridBagConstraints();
        gbcRegistroPanel.gridx = 0; gbcRegistroPanel.gridy = 7;
        gbcRegistroPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcRegistroPanel.insets = new Insets(0, 0, 8, 0);
        panelDeLogin.add(registroPanel, gbcRegistroPanel);

        //  Botón Salir 
        JButton botonSalir = new JButton("Salir");
        botonSalir.setFont(new Font("Arial", Font.ITALIC, 13));
        botonSalir.setForeground(GRAY_TEXT);
        botonSalir.setOpaque(false);
        botonSalir.setContentAreaFilled(false);
        botonSalir.setBorderPainted(false);
        botonSalir.setFocusPainted(false);
        botonSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonSalir.addActionListener(e -> dispose());
        GridBagConstraints gbcBotonSalir = new GridBagConstraints();
        gbcBotonSalir.gridx = 0; gbcBotonSalir.gridy = 8;
        gbcBotonSalir.fill = GridBagConstraints.HORIZONTAL;
        gbcBotonSalir.insets = new Insets(0, 0, 20, 0);
        panelDeLogin.add(botonSalir, gbcBotonSalir);
        
        configurarAtajoNube();
    }

    private void estilarCampo(JTextField field, String placeholder) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCC, 0xCC, 0xCC), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(320, 38));
        field.setText(placeholder);

        if (field instanceof JPasswordField)
            ((JPasswordField) field).setEchoChar((char) 0);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                boolean isPlaceholder = field instanceof JPasswordField
                    ? new String(((JPasswordField) field).getPassword()).equals(placeholder)
                    : field.getText().equals(placeholder);
                if (isPlaceholder) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField)
                        ((JPasswordField) field).setEchoChar('•');
                }
            }
            public void focusLost(FocusEvent e) {
                boolean empty = field instanceof JPasswordField
                    ? ((JPasswordField) field).getPassword().length == 0
                    : field.getText().isEmpty();
                if (empty) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (field instanceof JPasswordField)
                        ((JPasswordField) field).setEchoChar((char) 0);
                }
            }
        });
    }

    public static boolean mostrarLogin(Frame owner) {
        Login dialog = new Login(owner);
        dialog.setVisible(true);
        return dialog.loginExitoso;
    }

    private void validarLogin() {
        String usuarioIngresado  = campoUsuario.getText().trim();
        String passwordIngresada = new String(campoContrasena.getPassword());

        if (usuarioIngresado.isEmpty() || passwordIngresada.isEmpty()
                || usuarioIngresado.equals("Nombre de usuario")
                || passwordIngresada.equals("••••••")) {
            JOptionPane.showMessageDialog(this,
                    "Complete todos los campos.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Deshabilitar el botón mientras se conecta, para evitar doble click
        botonAcceder.setEnabled(false);
        botonAcceder.setText("Conectando...");

        new SwingWorker<Void, Void>() {
            private boolean encontrado = false;
            private boolean cuentaActiva = true;
            private boolean claveCorrecta = false;
            private String errorMensaje = null;

            @Override
            protected Void doInBackground() {
                Conexion bd = new Conexion();
                Connection cn = null;
                ResultSet rs = null;
                try {
                    cn = bd.conectar();
                    rs = bd.buscarUsuario(cn, usuarioIngresado);

                    if (rs == null || !rs.next()) {
                        encontrado = false;
                        return null;
                    }
                    encontrado = true;

                    String hashBD = rs.getString("contrasena_hash");
                    cuentaActiva = rs.getBoolean("activo");

                    if (cuentaActiva && BCrypt.checkpw(passwordIngresada, hashBD)) {
                        claveCorrecta = true;
                        usuarioId     = rs.getInt("id");
                        usuarioNombre = rs.getString("nombre");
                        usuarioRol    = rs.getString("rol");
                    }
                } catch (SQLException e) {
                    errorMensaje = "Error al conectar con la base de datos.";
                    e.printStackTrace();
                } finally {
                    bd.cerrar(rs, null, cn);
                }
                return null;
            }
            
            @Override
            protected void done() {
                botonAcceder.setEnabled(true);
                botonAcceder.setText("ACCEDER");
                if (isCancelled()) {
                    validarLogin();
                    return;
                }

                if (errorMensaje != null) {
                    JOptionPane.showMessageDialog(Login.this, errorMensaje,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!encontrado) {
                    JOptionPane.showMessageDialog(Login.this,
                            "Usuario no encontrado.\nLe queda(n) " + intentos + " intento(s).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    intentos--;
                    if (intentos < 0) {
                        JOptionPane.showMessageDialog(Login.this,
                                "Ha superado el número de intentos permitidos.",
                                "Bloqueado", JOptionPane.ERROR_MESSAGE);
                        dispose();
                    }
                    return;
                }

                if (!cuentaActiva) {
                    JOptionPane.showMessageDialog(Login.this,
                            "Esta cuenta ha sido suspendida.\nContacte al administrador.",
                            "Cuenta suspendida", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (claveCorrecta) {
                    loginExitoso = true;
                    JOptionPane.showMessageDialog(Login.this,
                            "Bienvenido, " + usuarioNombre + ".",
                            "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Login.this,
                            "Contraseña incorrecta.\nLe queda(n) " + intentos + " intento(s).",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    intentos--;
                    if (intentos < 0) {
                        JOptionPane.showMessageDialog(Login.this,
                                "Ha superado el número de intentos permitidos.",
                                "Bloqueado", JOptionPane.ERROR_MESSAGE);
                        dispose();
                    }
                }
            }
        }.execute();
    }
    
    // FORZAR CONEXION CON LA NUBE
    private void configurarAtajoNube() {
        KeyStroke atajo = KeyStroke.getKeyStroke(KeyEvent.VK_N,
                java.awt.event.InputEvent.CTRL_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(atajo, "forzarNube");
        getRootPane().getActionMap().put("forzarNube", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Conexion.forzarConexionNube();
                JOptionPane.showMessageDialog(Login.this,
                        "Conexión forzada al servidor en la nube.",
                        "Modo nube activado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}