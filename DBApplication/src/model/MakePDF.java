package model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class MakePDF {

    private static Font rf;
    private static Font bf;

    //kinopya ko yung fonts ha
    static {
        try {
            BaseFont regularf = BaseFont.createFont("RobotoRegular.ttf",  BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont boldf = BaseFont.createFont("RobotoBold.ttf",  BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            bf = new Font(boldf, 18, Font.BOLD);
            rf = new Font(regularf, 18, Font.NORMAL);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exportToPDF(ResultSet rs, String file) {
        try {
            Document document = new Document(PageSize.LETTER); // letter muna linagay ko pero pwede naman siya ibahin
            PdfWriter.getInstance(document, new FileOutputStream("PDFs" + "/" + file));

            document.open();

            // title
            Paragraph title = new Paragraph("Report", bf);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            // table
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            PdfPTable table = new PdfPTable(columnCount);
            table.setWidthPercentage(90);

            // header
            for (int i = 1; i <= columnCount; i++) {
                Font headerFont = new Font(bf.getBaseFont(), 5, Font.BOLD); // adjust as needed
                PdfPCell header = new PdfPCell(new Phrase(meta.getColumnName(i), headerFont));
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setPhrase(new Phrase(meta.getColumnName(i), rf));
                header.setNoWrap(false);
                table.addCell(header);
            }

            // rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    table.addCell(new Phrase(rs.getString(i), rf));
                }
            }

            document.add(table);
            document.close();

            System.out.println("PDF successfully created " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args)
    {
        System.out.println("hi");
    }
}
