package social;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import login_test.Conexion;

public class AdminService {
    // Modelo 
    public static class UsuarioInfo {
        public final int id;
        public final String usuario;
        public final String nombre;
        public final String rol;
        public final boolean activo;
        public final String creadoEn;

        public UsuarioInfo(int id, String usuario, String nombre, String rol, boolean activo, String creadoEn) {
            this.id = id;
            this.usuario = usuario;
            this.nombre = nombre;
            this.rol = rol;
            this.activo = activo;
            this.creadoEn = creadoEn;
        }

        @Override
        public String toString() { return nombre + " (@" + usuario + ")"; }
    }

    // Consultas 
    public static List<UsuarioInfo> cargarUsuarios(String usuarioActual) {
        List<UsuarioInfo> lista = new ArrayList<>();
        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            cn = bd.conectar();
            if (cn == null) return lista;

            String sql = "SELECT u.id, u.usuario, u.nombre, r.nombre AS rol, u.activo, u.creado_en "
                       + "FROM usuarios u "
                       + "JOIN roles r ON r.id = u.rol_id "
                       + "WHERE u.usuario != ? "
                       + "ORDER BY u.creado_en ASC";
            pstm = cn.prepareStatement(sql);
            pstm.setString(1, usuarioActual);
            rs   = pstm.executeQuery();

            while (rs.next()) {
                lista.add(new UsuarioInfo(
                    rs.getInt("id"), rs.getString("usuario"), rs.getString("nombre"),
                    rs.getString("rol"), rs.getBoolean("activo"), rs.getString("creado_en")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            bd.cerrar(rs, pstm, cn);
        }
        return lista;
    }

    // Cambia el rol de un usuario (Con límite máximo de 3 administradores)
    public static boolean cambiarRol(int usuarioId, String rolNombre) {
        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstmCheck = null;
        PreparedStatement pstmUpdate = null;
        ResultSet rs = null;

        try {
            cn = bd.conectar();
            if (cn == null) return false;

            // 1. VALIDACIÓN: Si se intenta promover a 'admin', validar el límite
            if ("admin".equalsIgnoreCase(rolNombre)) {
                String sqlCheck = "SELECT COUNT(*) FROM usuarios u "
                                + "JOIN roles r ON u.rol_id = r.id "
                                + "WHERE r.nombre = 'admin'";
                
                pstmCheck = cn.prepareStatement(sqlCheck);
                rs = pstmCheck.executeQuery();
                
                if (rs.next()) {
                    int cantidadAdmins = rs.getInt(1);
                    if (cantidadAdmins >= 3) {
                        System.out.println("No se puede cambiar el rol: Ya existen 3 administradores en el sistema.");
                        return false; 
                    }
                }
            }

            // 2. EJECUCIÓN: Si pasa la validación (o es otro rol), se actualiza
            String sqlUpdate = "UPDATE usuarios SET rol_id = "
                             + "(SELECT id FROM roles WHERE nombre = ?) "
                             + "WHERE id = ?";
            pstmUpdate = cn.prepareStatement(sqlUpdate);
            pstmUpdate.setString(1, rolNombre);
            pstmUpdate.setInt(2, usuarioId);
            
            int filasAfectadas = pstmUpdate.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al cambiar rol.");
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmCheck != null) pstmCheck.close(); } catch (Exception e) {}
            bd.cerrar(null, pstmUpdate, cn);
        }
    }

    // Activa o suspende una cuenta.
    public static boolean cambiarEstado(int usuarioId, boolean activar) {
        Conexion bd = new Conexion();
        Connection cn = null;
        PreparedStatement pstm = null;

        try {
            cn = bd.conectar();
            if (cn == null) return false;

            pstm = cn.prepareStatement("UPDATE usuarios SET activo = ? WHERE id = ?");
            pstm.setBoolean(1, activar);
            pstm.setInt(2, usuarioId);
            pstm.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado.");
            e.printStackTrace();
            return false;
        } finally {
            bd.cerrar(null, pstm, cn);
        }
    }
}