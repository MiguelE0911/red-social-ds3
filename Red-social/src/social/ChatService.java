package social;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import login_test.Conexion;

public class ChatService {

    // Modelo de mensaje 
	public static class Mensaje {
		public final int     id;
		public final int usuarioId;
	    public final String  nombreUsuario;
	    public final String  contenido;
	    public final String  fechaHora;
	    public final boolean esPropio; // true = enviado por el usuario de esta sesión
	 
	    public Mensaje(int id, int usuarioId, String nombreUsuario, String contenido, String fechaHora, boolean esPropio) {
	    	this.id = id;
	    	this.usuarioId = usuarioId;
	        this.nombreUsuario = nombreUsuario;
	        this.contenido = contenido;
	        this.fechaHora = fechaHora;
	        this.esPropio = esPropio;
	    }
	    
	    public Mensaje(String nombreUsuario, String contenido, String fechaHora, boolean esPropio) {
	    	this(0, 0, nombreUsuario, contenido, fechaHora, esPropio);
	    }
	}

	//  Carga de mensajes desde la bd
    public static List<Mensaje> cargarMensajes(boolean verFecha, String usuarioNombre) {
        List<Mensaje> lista = new ArrayList<>();
        Conexion bd = new Conexion();
        Connection cn   = null;
        PreparedStatement pstm = null;
        ResultSet rs    = null;
        try {
            cn = bd.conectar();
            if (cn == null) return lista;
     
            String sql = "SELECT m.id, u.id AS usuario_id, u.nombre, m.contenido, m.creado_en "
                       + "FROM mensajes m "
                       + "JOIN usuarios u ON u.id = m.usuario_id "
                       + "ORDER BY m.creado_en ASC";
            pstm = cn.prepareStatement(sql);
            rs   = pstm.executeQuery();
     
            while (rs.next()) {
            	int id = rs.getInt("id"); 
            	int usuarioId = rs.getInt("usuario_id");
            	String nombre = rs.getString("nombre");
            	String contenido = rs.getString("contenido");
            	String fecha = verFecha ? rs.getString("creado_en") : null;
            	boolean esPropio = nombre.equals(usuarioNombre);
            	lista.add(new Mensaje(id, usuarioId, nombre, contenido, fecha, esPropio)); 
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar mensajes.");
            e.printStackTrace();
        } finally {
            bd.cerrar(rs, pstm, cn);
        }
        return lista;
    }

    // Envío de mensaje 
    public static boolean enviarMensaje(int usuarioId, String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) return false;

        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstm = null;

        try {
            cn = bd.conectar();
            if (cn == null) return false;

            String sql = "INSERT INTO mensajes (usuario_id, contenido) VALUES (?, ?)";
            pstm = cn.prepareStatement(sql);
            pstm.setInt(1, usuarioId);
            pstm.setString(2, contenido.trim());
            pstm.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al enviar mensaje.");
            e.printStackTrace();
            return false;
        } finally {
            bd.cerrar(null, pstm, cn);
        }
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
    
    public static boolean eliminarMensaje(int mensajeId) {
        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstmDelete = null;
        try {
            cn = bd.conectar();
            if (cn == null) return false;

            String sql = "DELETE FROM mensajes WHERE id = ?";
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