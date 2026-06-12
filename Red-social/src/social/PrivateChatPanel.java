package social;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import social.PrivateChatService.MensajePrivado;

import java.util.Arrays;
import java.util.List;

public class PrivateChatPanel extends JDialog {

    private static final long serialVersionUID = 1L;
    private JTextField campoMensaje;
    private JPanel panelMensajes;
    private JLabel lblUsuario;
    private JScrollPane scrollPane;

    private final int usuarioId;
    private final int destinatarioId;
    private final String usuarioNombre;
    private final String rol;
    private boolean cerroChat = false;
    public boolean isCerroChat() { return cerroChat; }

    private static final Color BG_DARK = new Color(0x3A, 0x3A, 0x3A);
    private static final Color BG_HEADER = new Color(0x2A, 0x2A, 0x2A);
    private static final Color BG_AREA = new Color(0x4A, 0x4A, 0x4A);
    private static final Color BG_INPUT = new Color(0x2A, 0x2A, 0x2A);
    private static final Color PURPLE = new Color(0x7B, 0x2F, 0xFF);
    private static final Color BURBUJA_PROPIO = new Color(0x7B, 0x2F, 0xFF);
    private static final Color BURBUJA_OTRO = new Color(0xCC, 0xCC, 0xCC);
    private static final Color TEXT_WHITE = new Color(0xF0, 0xF0, 0xF0);
    private static final Color TEXT_MUTED = new Color(0xAA, 0xAA, 0xAA);

    public PrivateChatPanel(Frame owner, int usuarioId, String usuarioNombre, String rol, int destinatarioId, String destinatarioNombre) {
        super(owner, "Chat Privado", true);

        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.rol = rol;
        this.destinatarioId = destinatarioId;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        // ── Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(BG_HEADER);
        panelHeader.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitulo = new JLabel("Chat con " + destinatarioNombre);
        lblTitulo.setFont(new Font("Arial Black", Font.BOLD, 16));
        lblTitulo.setForeground(TEXT_WHITE);
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        JPanel panelHeaderEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelHeaderEast.setOpaque(false);

        if (rol.equals("admin")) {
            JButton btnAdmin = new JButton("Panel Admin");
            styleBotonHeader(btnAdmin, false);
            btnAdmin.addActionListener(e -> AdminPanel.mostrar((Dialog) PrivateChatPanel.this, usuarioNombre)); 
            panelHeaderEast.add(btnAdmin);
        }

        JButton btnCerrarChat = new JButton("Cerrar chat");
        styleBotonHeader(btnCerrarChat, true);
        btnCerrarChat.addActionListener(e -> {
            int confirmar = JOptionPane.showConfirmDialog(
                    PrivateChatPanel.this,
                    "¿Estás seguro que deseas cerrar el chat privado?",
                    "Cerrar chat",
                    JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                cerroChat = true;
                dispose();
            }
        });
        panelHeaderEast.add(btnCerrarChat);

        lblUsuario = new JLabel(usuarioNombre + " - " + capitalize(rol));
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        lblUsuario.setForeground(TEXT_MUTED);
        panelHeaderEast.add(lblUsuario);

        panelHeader.add(panelHeaderEast, BorderLayout.EAST);
        getContentPane().add(panelHeader, BorderLayout.NORTH);

        // Área de mensajes 
        panelMensajes = new JPanel();
        panelMensajes.setLayout(new BoxLayout(panelMensajes, BoxLayout.Y_AXIS));
        panelMensajes.setBorder(new EmptyBorder(12, 12, 12, 12));
        panelMensajes.setBackground(BG_AREA);

        scrollPane = new JScrollPane(panelMensajes);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_AREA);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Input 
        JPanel panelInput = new JPanel(new BorderLayout(10, 0));
        panelInput.setBackground(BG_INPUT);
        panelInput.setBorder(new EmptyBorder(12, 16, 12, 16));

        campoMensaje = new JTextField();
        campoMensaje.setFont(new Font("Arial", Font.PLAIN, 14));
        campoMensaje.setBackground(BG_AREA);
        campoMensaje.setForeground(TEXT_WHITE);
        campoMensaje.setCaretColor(TEXT_WHITE);
        campoMensaje.setBorder(new CompoundBorder(
            new LineBorder(PURPLE, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        // Placeholder "Escribe un mensaje"
        campoMensaje.setText("Escribe un mensaje");
        campoMensaje.setForeground(TEXT_MUTED);
        campoMensaje.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campoMensaje.getText().equals("Escribe un mensaje")) {
                    campoMensaje.setText("");
                    campoMensaje.setForeground(TEXT_WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (campoMensaje.getText().isEmpty()) {
                    campoMensaje.setForeground(TEXT_MUTED);
                    campoMensaje.setText("Escribe un mensaje");
                }
            }
        });

        panelInput.add(campoMensaje, BorderLayout.CENTER);

        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.setFont(new Font("Arial Black", Font.BOLD, 13));
        btnEnviar.setBackground(PURPLE);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setOpaque(true);
        btnEnviar.setContentAreaFilled(true);
        btnEnviar.setBorderPainted(false);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEnviar.setPreferredSize(new Dimension(90, 38));
        panelInput.add(btnEnviar, BorderLayout.EAST);

        getContentPane().add(panelInput, BorderLayout.SOUTH);

        btnEnviar.addActionListener(e -> onEnviar());
        campoMensaje.addActionListener(e -> onEnviar());
        cargarConversacion();
    }

    // Helpers de estilo 
    private void styleBotonHeader(JButton btn, boolean filled) {
        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBackground(PURPLE);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 32));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ── Lógica 
    private void onEnviar() {
        String texto = campoMensaje.getText();
        if (texto.equals("Escribe un mensaje")) texto = "";

        String error = PrivateChatService.validarTexto(texto);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idGenerado = PrivateChatService.enviarMensajePrivado(usuarioId, destinatarioId, texto);

        if (idGenerado != -1) {
            agregarBurbuja(
                new MensajePrivado(idGenerado, usuarioId, usuarioNombre, texto.trim(), null, true)
            );

            campoMensaje.setText("");
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo enviar el mensaje. Intente de nuevo.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cargarConversacion() {
        panelMensajes.removeAll();
        List<PrivateChatService.MensajePrivado> mensajes = PrivateChatService.cargarConversacion(usuarioId, destinatarioId);
        for (PrivateChatService.MensajePrivado m : mensajes) {
            agregarBurbuja(m);
        }
        panelMensajes.revalidate();
        panelMensajes.repaint();
    }

    private void agregarBurbuja(PrivateChatService.MensajePrivado m) {
        JPanel burbuja = new JPanel();
        burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));

        Color colorFondo = m.esPropio ? BURBUJA_PROPIO : BURBUJA_OTRO;
        Color colorTexto = m.esPropio ? Color.WHITE : new Color(0x22, 0x22, 0x22);
        Color colorMeta  = m.esPropio ? new Color(0xDD, 0xBB, 0xFF) : new Color(0x66, 0x66, 0x66);

        burbuja.setBackground(colorFondo);
        burbuja.setBorder(new CompoundBorder(
            new LineBorder(colorFondo.darker(), 0),
            new EmptyBorder(10, 14, 10, 14)
        ));
        burbuja.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        burbuja.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Fila meta: nombre | fecha | botón eliminar
        JPanel panelMeta = new JPanel(new BorderLayout(8, 0));
        panelMeta.setOpaque(false);

        JLabel lblNombre = new JLabel(m.nombreRemitente);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        lblNombre.setForeground(colorTexto);
        panelMeta.add(lblNombre, BorderLayout.WEST);

        JPanel panelMetaEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelMetaEast.setOpaque(false);

        if (m.fechaHora != null) {
            JLabel lblFecha = new JLabel(m.fechaHora);
            lblFecha.setFont(new Font("Arial", Font.PLAIN, 11));
            lblFecha.setForeground(colorMeta);
            panelMetaEast.add(lblFecha);
        }
        
        // Botón eliminar 
        if (m.esPropio || rol.equals("admin")) {
            JButton btnEliminar = new JButton("...");
            btnEliminar.setFont(new Font("Arial", Font.BOLD, 11));
            btnEliminar.setBackground(new Color(123, 47, 255));
            btnEliminar.setForeground(Color.WHITE);
            btnEliminar.setOpaque(true);
            btnEliminar.setContentAreaFilled(true);
            btnEliminar.setBorderPainted(true);
            btnEliminar.setFocusPainted(false);
            btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnEliminar.setPreferredSize(new Dimension(40, 20));
            btnEliminar.setToolTipText("Eliminar mensaje");

            final int idMensaje = m.id; // efectivamente final para el lambda
            btnEliminar.addActionListener(e -> {
                int confirmar = JOptionPane.showConfirmDialog(
                        PrivateChatPanel.this,
                        "¿Eliminar este mensaje?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                if (confirmar == JOptionPane.YES_OPTION) {
                    boolean ok = PrivateChatService.eliminarMensajePrivado(idMensaje);
                    if (ok) {
                        // Quitar burbuja del panel sin recargar todo
                        Container parent = burbuja.getParent();
                        if (parent != null) {
                            // Buscar y quitar también el RigidArea que le sigue
                            int idx = Arrays.asList(parent.getComponents()).indexOf(burbuja);
                            if (idx >= 0 && idx + 1 < parent.getComponentCount()) {
                                parent.remove(idx + 1); // el Box.createRigidArea
                            }
                            parent.remove(burbuja);
                            parent.revalidate();
                            parent.repaint();
                        }
                    } else {
                        JOptionPane.showMessageDialog(PrivateChatPanel.this,
                                "No se pudo eliminar el mensaje.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            panelMetaEast.add(btnEliminar);
        }
        panelMeta.add(panelMetaEast, BorderLayout.EAST);

        // Contenido
        JTextArea txtMensaje = new JTextArea(m.contenido);
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setEditable(false);
        txtMensaje.setOpaque(false);
        txtMensaje.setForeground(colorTexto);
        txtMensaje.setBorder(new EmptyBorder(4, 0, 0, 0));

        burbuja.add(panelMeta);
        burbuja.add(txtMensaje);

        panelMensajes.add(burbuja);
        panelMensajes.add(Box.createRigidArea(new Dimension(0, 8)));

        panelMensajes.revalidate();
        panelMensajes.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    public static void mostrarChatPrivado(Frame owner, int usuarioId, String usuarioNombre, String rol, int destinatarioId, String destinatarioNombre) {
        PrivateChatPanel dialog = new PrivateChatPanel(owner, usuarioId, usuarioNombre, rol, destinatarioId, destinatarioNombre);
        dialog.setVisible(true);
    }
}