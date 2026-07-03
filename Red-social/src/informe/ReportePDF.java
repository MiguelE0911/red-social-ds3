package informe;  


import login_test.Conexion;

import java.io.FileOutputStream;
import java.sql.*;

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

	        String sql = "SELECT usuario, creado_en FROM usuarios";
	        PreparedStatement ps = cn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        String carpetaDestino = System.getProperty("user.home") + java.io.File.separator + "Desktop";
	        java.io.File archivo = new java.io.File(carpetaDestino, "reporte_usuarios.pdf");

	        Document doc = new Document();
	        PdfWriter.getInstance(doc, new FileOutputStream(archivo));
	        doc.open();

	        Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	        Paragraph p = new Paragraph("REPORTE DE USUARIOS", titulo);
	        p.setAlignment(Element.ALIGN_CENTER);
	        doc.add(p);
	        doc.add(new Paragraph(" "));

	        PdfPTable table = new PdfPTable(2);
	        table.setWidthPercentage(100);

	        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	        PdfPCell c1 = new PdfPCell(new Phrase("Usuario", headFont));
	        PdfPCell c2 = new PdfPCell(new Phrase("Fecha de creación", headFont));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	        table.addCell(c2);

	        while (rs.next()) {
	            table.addCell(rs.getString("usuario"));
	            table.addCell(rs.getString("creado_en"));
	        }

	        doc.add(table);
	        doc.add(new Paragraph(" "));
	        doc.add(new Paragraph("Reporte generado automáticamente."));
	        doc.close();

	        return archivo.getAbsolutePath();

	    } finally {
	        bd.cerrar(null, null, cn);
	    }
	}
}