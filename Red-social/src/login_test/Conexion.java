package login_test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.InputStream;
import java.util.Properties;


public class Conexion {
	private static final String CONDUCTOR = "com.mysql.cj.jdbc.Driver";
    private static final Properties props = new Properties();
    static {
        try (InputStream input = Conexion.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró config.properties en el classpath");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando configuración", e);
        }
    }
	
    // SERVIDOR VPN
    private static final String DIR = props.getProperty("db1.address");
    private static final String USUARIO = props.getProperty("db1.user");
    private static final String CONTRA = props.getProperty("db1.password");

    // SERVIDOR EN LA NUBE
    private static final String DIR2 = props.getProperty("db2.address");
    private static final String USUARIO2 = props.getProperty("db2.user");
    private static final String CONTRA2 = props.getProperty("db2.password");
    
    private static final int TIMEOUT_CONEXION_MS = 15_000; // 15s para intentar conectar
    private static final int SLICE_MS = 3000; // cada intento individual
    private static HikariDataSource poolLocal;
    private static HikariDataSource poolNube;
    private static final AtomicReference<HikariDataSource> poolActivo = new AtomicReference<>();
    private static final AtomicBoolean forzarNube = new AtomicBoolean(false);

    static {
        try {
            Class.forName(CONDUCTOR);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC.", e);
        }
        poolLocal = crearPool("PoolLocal", DIR, USUARIO, CONTRA, SLICE_MS); // rebanadas de 3s
        poolNube  = crearPool("PoolNube", DIR2, USUARIO2, CONTRA2, TIMEOUT_CONEXION_MS); // nube normal, 15s
        seleccionarPoolInicial();
    }

    private static HikariDataSource crearPool(String nombre, String dir, String usuario, String contra, int timeoutConexionMs) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dir);
        config.setUsername(usuario);
        config.setPassword(contra);
        config.setPoolName(nombre);
        config.setConnectionTimeout(timeoutConexionMs);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setInitializationFailTimeout(-1);
        return new HikariDataSource(config);
    }

    private static void seleccionarPoolInicial() {
        if (forzarNube.get()) {
            activarNube();
            return;
        }
        if (probarPool(poolLocal)) {
            poolActivo.set(poolLocal);
            System.out.println("Conectado al servidor local (VPN).");
        } else activarNube(); 
    }

    private static void activarNube() {
        poolActivo.set(poolNube);
        System.out.println("Usando servidor en la nube.");
    }

    private static boolean probarPool(HikariDataSource pool) {
        try (Connection c = pool.getConnection()) {
            return c.isValid(3);
        } catch (SQLException e) {
            System.out.println("No se pudo validar " + pool.getPoolName() + ": " + e.getMessage());
            return false;
        }
    }

    public static void forzarConexionNube() {
        forzarNube.set(true);
        activarNube();
    }
    
    // CONEXION CENTRAL
    public Connection conectar() {
        HikariDataSource pool = poolActivo.get();
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            System.out.println("Error al obtener conexión de " + pool.getPoolName() + ": " + e.getMessage());
            return null;
        }
    }
    
    
    // LOGICA DE SISTEMA
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