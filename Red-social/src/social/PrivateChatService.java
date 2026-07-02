package social;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import login_test.Conexion;

public class PrivateChatService {

    // Modelo de mensaje privado
    public static class MensajePrivado {
        public final int id;
        public final int remitenteId;
        public final String nombreRemitente;
        public final String contenido;
        public final String fechaHora;
        public final boolean esPropio; // true = enviado por el usuario de esta sesión

        public MensajePrivado(int id, int remitenteId, String nombreRemitente, String contenido, String fechaHora, boolean esPropio) {
            this.id = id;
            this.remitenteId = remitenteId;
            this.nombreRemitente = nombreRemitente;
            this.contenido = contenido;
            this.fechaHora = fechaHora;
            this.esPropio = esPropio;
        }
    }

    // Cargar conversación entre dos usuarios
    public static List<MensajePrivado> cargarConversacion(int usuarioActual, int usuarioDestino) {
        List<MensajePrivado> lista = new ArrayList<>();
        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            cn = bd.conectar();
            if (cn == null)
                return lista;
            String sql = "SELECT mp.id, mp.remitente_id, u.nombre, mp.contenido, mp.creado_en "
            	       + "FROM mensajes_privados mp "
            	       + "JOIN usuarios u ON u.id = mp.remitente_id "
            	       + "WHERE (mp.remitente_id = ? AND mp.destinatario_id = ?) "
            	       + "OR (mp.remitente_id = ? AND mp.destinatario_id = ?) "
            	       + "ORDER BY mp.creado_en";

            pstm = cn.prepareStatement(sql);
            pstm.setInt(1, usuarioActual);
            pstm.setInt(2, usuarioDestino);
            pstm.setInt(3, usuarioDestino);
            pstm.setInt(4, usuarioActual);
            rs = pstm.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int remitenteId = rs.getInt("remitente_id");
                String nombre = rs.getString("nombre");
                String contenido = rs.getString("contenido");
                String fecha = rs.getString("creado_en");
                boolean esPropio = (remitenteId == usuarioActual);
                lista.add(new MensajePrivado(id, remitenteId, nombre, contenido, fecha, esPropio));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar conversación privada.");
            e.printStackTrace();
        } finally {
            bd.cerrar(rs, pstm, cn);
        }
        return lista;
    }

    // enviar mensaje privado
    public static int enviarMensajePrivado(int remitenteId, int destinatarioId, String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) return -1;
        String sql = "INSERT INTO mensajes_privados (remitente_id, destinatario_id, contenido, creado_en) VALUES (?, ?, ?, ?)";

        try (Connection cn = new Conexion().conectar();
             PreparedStatement pstm = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setInt(1, remitenteId);
            pstm.setInt(2, destinatarioId);
            pstm.setString(3, contenido.trim());
            pstm.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            pstm.executeUpdate();
            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // validar antes de subir a la bd
    public static String validarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty())
            return "El mensaje no puede estar vacío.";
        String textoLimpio = texto.trim();
        if (textoLimpio.length() > 500)
            return "El mensaje no puede superar los 500 caracteres.";
        String[] patronesSql = {
                "--", ";--", "/*", "*/", "xp_",
                "DROP ", "DELETE ", "INSERT ",
                "UPDATE ", "SELECT ", "UNION ",
                "ALTER ", "CREATE ", "EXEC ",
                "EXECUTE ", "TRUNCATE "
            };

        String textoUpper = textoLimpio.toUpperCase();
        for (String patron : patronesSql) {
            if (textoUpper.contains(patron)) {
                return "El mensaje contiene contenido no permitido.";
            }
        }
        return null;
    }
    
    public static boolean eliminarMensajePrivado(int mensajeId) {
        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstmDelete = null;
        try {
            cn = bd.conectar();
            if (cn == null) return false;

            String sql = "DELETE FROM mensajes_privados WHERE id = ?";
            pstmDelete = cn.prepareStatement(sql);
            pstmDelete.setInt(1, mensajeId);
            pstmDelete.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar mensaje.");
            e.printStackTrace();
            return false;
        } finally {
            bd.cerrar(null, pstmDelete, cn);
        }
    }
}