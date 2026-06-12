package login_test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String CONDUCTOR = "com.mysql.jdbc.Driver"; 
    private static final String DIR = "jdbc:mysql://localhost:3307/redsocial_db";
    private static final String USUARIO = "root";
    private static final String CONTRA = "";

   
    public Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName(CONDUCTOR);
            conexion = DriverManager.getConnection(DIR, USUARIO, CONTRA);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: no se encontró el controlador JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: no se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
        return conexion;
    }
    
    public void registrarUsuario(Connection cn, String nombre, String usuario, String passHash) {
        PreparedStatement pstmCheck = null;
        PreparedStatement pstmInsert = null;
        ResultSet rs = null;
        try {
            if (cn == null) {
                System.out.println("No se pudo obtener la conexión.");
                return;
            }

            int rolId = 3; // usuario por defecto
            pstmCheck = cn.prepareStatement("SELECT COUNT(*) FROM usuarios");
            rs = pstmCheck.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                rolId = 1; // admin
            }

            String sql = "INSERT INTO usuarios (usuario, nombre, contrasena_hash, activo, rol_id) "
                       + "VALUES (?, ?, ?, TRUE, ?)";
            pstmInsert = cn.prepareStatement(sql);
            pstmInsert.setString(1, usuario);
            pstmInsert.setString(2, nombre);
            pstmInsert.setString(3, passHash);
            pstmInsert.setInt(4, rolId);
            pstmInsert.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al registrar usuario.");
            e.printStackTrace();
        } finally {
            cerrar(rs, pstmCheck,  null);
            cerrar(null, pstmInsert, null);
        }
    }
    
    public ResultSet buscarUsuario(Connection cn, String usuario) {
        ResultSet rs = null;
        try {
            if (cn == null) {
                System.out.println("No se pudo obtener la conexión.");
                return null;
            }
            String sql = "SELECT u.id, u.usuario, u.nombre, u.contrasena_hash, u.activo, r.nombre AS rol "
                       + "FROM usuarios u "
                       + "JOIN roles r ON r.id = u.rol_id "
                       + "WHERE u.usuario = ?";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, usuario);
            rs = pstm.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario.");
            e.printStackTrace();
        }
        return rs;
    }
    
    public ResultSet buscarMensaje(Connection cn, int mensajeId) {
        ResultSet rs = null;
        try {
            if (cn == null) {
                System.out.println("No se pudo obtener la conexión.");
                return null;
            }
            String sql = "SELECT u.id AS id_usuario, u.usuario, m.contenido "
                       + "FROM usuarios u "
                       + "JOIN mensajes m ON m.usuario_id = u.id "
                       + "WHERE m.id = ?";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setInt(1, mensajeId);
            rs = pstm.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar mensaje.");
            e.printStackTrace();
        }
        return rs;
    }
    
    public void cerrar(ResultSet rs, Statement stm, Connection cn) {
        try { if (rs  != null) rs.close();  } catch (Exception e) { e.printStackTrace(); }
        try { if (stm != null) stm.close(); } catch (Exception e) { e.printStackTrace(); }
        try { if (cn  != null) cn.close();  } catch (Exception e) { e.printStackTrace(); }
    }
}