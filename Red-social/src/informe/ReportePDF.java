package informe;
import login_test.Conexion;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.JOptionPane;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ReportePDF {
	public static String generarReporte() throws Exception {
	    Conexion bd = new Conexion();
	    Connection cn = bd.conectar();

	    try {
	        if (cn == null) {
	            throw new SQLException("No se pudo obtener conexión a la base de datos.");
	        }

	        // Consulta principal (usuarios)
	        String sql = "SELECT usuario, creado_en FROM usuarios";
	        PreparedStatement ps = cn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        //  Cálculos estadísticos 
	        int totalUsuarios = contar(cn, "SELECT COUNT(*) FROM usuarios");
	        int usuariosActivos = contar(cn, "SELECT COUNT(*) FROM usuarios WHERE activo = 1");
	        int usuariosInactivos = totalUsuarios - usuariosActivos;
	        int totalMensajes = contar(cn, "SELECT COUNT(*) FROM mensajes");
	        int totalMensajesPrivados = contar(cn, "SELECT COUNT(*) FROM mensajes_privados");
	        int mensajesUltimos30Dias = contar(cn,
	                "SELECT COUNT(*) FROM mensajes WHERE creado_en >= (NOW() - INTERVAL 30 DAY)");
	        double promedioMensajesPorUsuario = usuariosActivos > 0
	                ? (double) totalMensajes / usuariosActivos: 0.0;
	        String usuarioMasActivo = "N/A";
	        String sqlTop = "SELECT u.usuario, COUNT(m.id) AS cantidad " +
	                "FROM mensajes m " +
	                "JOIN usuarios u ON u.id = m.usuario_id " +
	                "GROUP BY m.usuario_id " +
	                "ORDER BY cantidad DESC " +
	                "LIMIT 1";
	        try (PreparedStatement psTop = cn.prepareStatement(sqlTop);
	             ResultSet rsTop = psTop.executeQuery()) {
	            if (rsTop.next()) {
	                usuarioMasActivo = rsTop.getString("usuario") + " (" + rsTop.getInt("cantidad") + " mensajes)";
	            }
	        }

	        // Destino
	        String carpetaDestino = System.getProperty("user.home") + java.io.File.separator + "Desktop";
	        java.io.File archivo = new java.io.File(carpetaDestino, "reporte_usuarios.pdf");

	        // Generacion PDF
	        Document doc = new Document();
	        PdfWriter.getInstance(doc, new FileOutputStream(archivo));
	        doc.open();

	        // Titulo
	        Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	        Paragraph p = new Paragraph("REPORTE DE USUARIOS", titulo);
	        p.setAlignment(Element.ALIGN_CENTER);
	        doc.add(p);
	        doc.add(new Paragraph(" "));

	        // Tabla de usuarios
	        PdfPTable table = new PdfPTable(2);
	        table.setWidthPercentage(100);

	        // Encabezados
	        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	        PdfPCell c1 = new PdfPCell(new Phrase("Usuario", headFont));
	        PdfPCell c2 = new PdfPCell(new Phrase("Fecha de creación", headFont));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	        table.addCell(c2);

	        // Datos
	        while (rs.next()) {
	            table.addCell(rs.getString("usuario"));
	            table.addCell(rs.getString("creado_en"));
	        }

	        doc.add(table);
	        doc.add(new Paragraph(" "));

	        // Sección de estadísticas 
	        Font subtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	        Paragraph pEstadisticas = new Paragraph("ESTADÍSTICAS GENERALES", subtitulo);
	        pEstadisticas.setAlignment(Element.ALIGN_CENTER);
	        doc.add(pEstadisticas);
	        doc.add(new Paragraph(" "));

	        PdfPTable statsTable = new PdfPTable(2);
	        statsTable.setWidthPercentage(100);

	        PdfPCell sc1 = new PdfPCell(new Phrase("Estadística", headFont));
	        PdfPCell sc2 = new PdfPCell(new Phrase("Valor", headFont));
	        sc1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        sc2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        statsTable.addCell(sc1);
	        statsTable.addCell(sc2);

	        agregarFila(statsTable, "Total de usuarios registrados", String.valueOf(totalUsuarios));
	        agregarFila(statsTable, "Usuarios activos", String.valueOf(usuariosActivos));
	        agregarFila(statsTable, "Usuarios inactivos", String.valueOf(usuariosInactivos));
	        agregarFila(statsTable, "Total de mensajes (públicos)", String.valueOf(totalMensajes));
	        agregarFila(statsTable, "Total de mensajes privados", String.valueOf(totalMensajesPrivados));
	        agregarFila(statsTable, "Mensajes en los últimos 30 días", String.valueOf(mensajesUltimos30Dias));
	        agregarFila(statsTable, "Promedio de mensajes por usuario", String.format("%.2f", promedioMensajesPorUsuario));
	        agregarFila(statsTable, "Usuario con más mensajes enviados", usuarioMasActivo);

	        doc.add(statsTable);
	        doc.add(new Paragraph(" "));

	        doc.add(new Paragraph("Reporte generado automáticamente."));
	        doc.close();

	        JOptionPane.showMessageDialog(null,
                    "El reporte PDF se generó correctamente.",
                    "Reporte generado",
                    JOptionPane.INFORMATION_MESSAGE
            );

	        return archivo.getAbsolutePath();

	    } finally {
	        bd.cerrar(null, null, cn);
	    }
	}

	// Ejecuta una consulta de tipo COUNT(*) y devuelve el resultado como entero.
	private static int contar(Connection cn, String sql) throws SQLException {
	    try (PreparedStatement ps = cn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	        return 0;
	    }
	}

	//Agrega una fila de dos columnas (etiqueta / valor) a una tabla de estadísticas.
	private static void agregarFila(PdfPTable table, String etiqueta, String valor) {
	    table.addCell(new Phrase(etiqueta));
	    table.addCell(new Phrase(valor));
	}
}