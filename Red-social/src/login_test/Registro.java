package login_test;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.mindrot.jbcrypt.BCrypt;

public class Registro extends JDialog {

    private static final long serialVersionUID = 1L;
    private JTextField campoNombre;
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JPasswordField campoConfirmar;
    private boolean registroExitoso = false;

    private static final Color BG = new Color(0x1E, 0x1E, 0x1E);
    private static final Color PURPLE = new Color(0x7B, 0x2F, 0xFF);
    private static final Color GRAY_T = new Color(0xAA, 0xAA, 0xAA);

    public Registro(Dialog owner) {
        super(owner, "Crear cuenta", true);

        setSize(420, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            new LineBorder(PURPLE, 3)
        ));
        setContentPane(panel);

        // Título 
        JLabel titulo = new JLabel("Crear cuenta");
        titulo.setFont(new Font("Arial Black", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbcTitulo = new GridBagConstraints();
        gbcTitulo.gridx = 0; gbcTitulo.gridy = 0;
        gbcTitulo.insets = new Insets(20, 25, 15, 25);
        panel.add(titulo, gbcTitulo);

        //  Label Nombre
        JLabel labelNombre = new JLabel("Nombre");
        labelNombre.setFont(new Font("Arial", Font.PLAIN, 13));
        labelNombre.setForeground(GRAY_T);
        GridBagConstraints gbcLabelNombre = new GridBagConstraints();
        gbcLabelNombre.gridx = 0; gbcLabelNombre.gridy = 1;
        gbcLabelNombre.fill = GridBagConstraints.HORIZONTAL;
        gbcLabelNombre.insets = new Insets(4, 25, 0, 25);
        panel.add(labelNombre, gbcLabelNombre);

        // Campo Nombre 
        campoNombre = new JTextField(20);
        estilarCampo(campoNombre, "Nombre Completo");
        GridBagConstraints gbcNombre = new GridBagConstraints();
        gbcNombre.gridx = 0; gbcNombre.gridy = 2;
        gbcNombre.fill = GridBagConstraints.HORIZONTAL;
        gbcNombre.insets = new Insets(0, 25, 8, 25);
        panel.add(campoNombre, gbcNombre);

        // Label Usuario
        JLabel labelUsuario = new JLabel("Usuario");
        labelUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        labelUsuario.setForeground(GRAY_T);
        GridBagConstraints gbcLabelUsuario = new GridBagConstraints();
        gbcLabelUsuario.gridx = 0; gbcLabelUsuario.gridy = 3;
        gbcLabelUsuario.fill = GridBagConstraints.HORIZONTAL;
        gbcLabelUsuario.insets = new Insets(4, 25, 0, 25);
        panel.add(labelUsuario, gbcLabelUsuario);

        // Campo Usuario 
        campoUsuario = new JTextField(20);
        estilarCampo(campoUsuario, "Usuario");
        GridBagConstraints gbcUsuario = new GridBagConstraints();
        gbcUsuario.gridx = 0; gbcUsuario.gridy = 4;
        gbcUsuario.fill = GridBagConstraints.HORIZONTAL;
        gbcUsuario.insets = new Insets(0, 25, 8, 25);
        panel.add(campoUsuario, gbcUsuario);

        // Label Contraseña 
        JLabel labelPass = new JLabel("Contraseña");
        labelPass.setFont(new Font("Arial", Font.PLAIN, 13));
        labelPass.setForeground(GRAY_T);
        GridBagConstraints gbcLabelPass = new GridBagConstraints();
        gbcLabelPass.gridx = 0; gbcLabelPass.gridy = 5;
        gbcLabelPass.fill = GridBagConstraints.HORIZONTAL;
        gbcLabelPass.insets = new Insets(4, 25, 0, 25);
        panel.add(labelPass, gbcLabelPass);

        // Campo Contraseña
        campoContrasena = new JPasswordField(20);
        estilarCampo(campoContrasena, "••••••••••••");
        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.gridx = 0; gbcPass.gridy = 6;
        gbcPass.fill = GridBagConstraints.HORIZONTAL;
        gbcPass.insets = new Insets(0, 25, 8, 25);
        panel.add(campoContrasena, gbcPass);

        // Label Confirmar 
        JLabel labelConfirmar = new JLabel("Confirmar contraseña");
        labelConfirmar.setFont(new Font("Arial", Font.PLAIN, 13));
        labelConfirmar.setForeground(GRAY_T);
        GridBagConstraints gbcLabelConfirmar = new GridBagConstraints();
        gbcLabelConfirmar.gridx = 0; gbcLabelConfirmar.gridy = 7;
        gbcLabelConfirmar.fill = GridBagConstraints.HORIZONTAL;
        gbcLabelConfirmar.insets = new Insets(4, 25, 0, 25);
        panel.add(labelConfirmar, gbcLabelConfirmar);

        // Campo Confirmar 
        campoConfirmar = new JPasswordField(20);
        estilarCampo(campoConfirmar, "••••••••••••");
        GridBagConstraints gbcConfirmar = new GridBagConstraints();
        gbcConfirmar.gridx = 0; gbcConfirmar.gridy = 8;
        gbcConfirmar.fill = GridBagConstraints.HORIZONTAL;
        gbcConfirmar.insets = new Insets(0, 25, 24, 25);
        panel.add(campoConfirmar, gbcConfirmar);

        // Botón Crear
        JButton botonRegistrar = new JButton("Crear");
        botonRegistrar.setFont(new Font("Arial Black", Font.BOLD, 14));
        botonRegistrar.setBackground(PURPLE);
        botonRegistrar.setForeground(Color.WHITE);
        botonRegistrar.setOpaque(true);
        botonRegistrar.setContentAreaFilled(true);
        botonRegistrar.setBorderPainted(false);
        botonRegistrar.setFocusPainted(false);
        botonRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonRegistrar.setPreferredSize(new Dimension(320, 44));
        GridBagConstraints gbcBoton = new GridBagConstraints();
        gbcBoton.gridx = 0; gbcBoton.gridy = 9;
        gbcBoton.fill = GridBagConstraints.HORIZONTAL;
        gbcBoton.insets = new Insets(0, 25, 25, 25);
        panel.add(botonRegistrar, gbcBoton);

        botonRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                intentarRegistro();
            }
        });
    }

    private void estilarCampo(JTextField field, String placeholder) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xCC, 0xCC, 0xCC), 1),
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

  
    private void intentarRegistro() {
        String nombre = campoNombre.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String pass = new String(campoContrasena.getPassword());
        String confirmar = new String(campoConfirmar.getPassword());

        if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (usuario.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de usuario debe tener al menos 3 caracteres.",
                    "Usuario inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 6 caracteres.",
                    "Contraseña débil", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass.equals(confirmar)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            campoContrasena.setText("");
            campoConfirmar.setText("");
            campoContrasena.requestFocus();
            return;
        }

        try {
            String hash = BCrypt.hashpw(confirmar, BCrypt.gensalt());
            Conexion bd = new Conexion();
            Connection cn = bd.conectar();
            if (cn != null) {
                bd.registrarUsuario(cn, nombre, usuario, hash);
                bd.cerrar(null, null, cn);
                registroExitoso = true;
                JOptionPane.showMessageDialog(this,
                        "Cuenta creada correctamente.\nIngrese sus datos en el login.",
                        "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ha ocurrido un error",
                    "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static boolean mostrar(Dialog owner) {
        Registro dialog = new Registro(owner);
        dialog.setVisible(true);
        return dialog.registroExitoso;
    }
}