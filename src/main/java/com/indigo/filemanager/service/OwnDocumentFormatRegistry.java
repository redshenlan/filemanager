package com.indigo.filemanager.service;

import com.artofsolving.jodconverter.BasicDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFamily;
import com.artofsolving.jodconverter.DocumentFormat;

public class OwnDocumentFormatRegistry  extends BasicDocumentFormatRegistry {
    public OwnDocumentFormatRegistry() {
        DocumentFormat pdf = new DocumentFormat("Portable Document Format", "application/pdf", "pdf");
        pdf.setExportFilter(DocumentFamily.DRAWING, "draw_pdf_Export");
        pdf.setExportFilter(DocumentFamily.PRESENTATION, "impress_pdf_Export");
        pdf.setExportFilter(DocumentFamily.SPREADSHEET, "calc_pdf_Export");
        pdf.setExportFilter(DocumentFamily.TEXT, "writer_pdf_Export");
        this.addDocumentFormat(pdf);
        DocumentFormat swf = new DocumentFormat("Macromedia Flash", "application/x-shockwave-flash", "swf");
        swf.setExportFilter(DocumentFamily.DRAWING, "draw_flash_Export");
        swf.setExportFilter(DocumentFamily.PRESENTATION, "impress_flash_Export");
        this.addDocumentFormat(swf);
        DocumentFormat xhtml = new DocumentFormat("XHTML", "application/xhtml+xml", "xhtml");
        xhtml.setExportFilter(DocumentFamily.PRESENTATION, "XHTML Impress File");
        xhtml.setExportFilter(DocumentFamily.SPREADSHEET, "XHTML Calc File");
        xhtml.setExportFilter(DocumentFamily.TEXT, "XHTML Writer File");
        this.addDocumentFormat(xhtml);
        DocumentFormat html = new DocumentFormat("HTML", DocumentFamily.TEXT, "text/html", "html");
        html.setExportFilter(DocumentFamily.PRESENTATION, "impress_html_Export");
        html.setExportFilter(DocumentFamily.SPREADSHEET, "HTML (StarCalc)");
        html.setExportFilter(DocumentFamily.TEXT, "HTML (StarWriter)");
        this.addDocumentFormat(html);
        DocumentFormat odt = new DocumentFormat("OpenDocument Text", DocumentFamily.TEXT, "application/vnd.oasis.opendocument.text", "odt");
        odt.setExportFilter(DocumentFamily.TEXT, "writer8");
        this.addDocumentFormat(odt);
        DocumentFormat sxw = new DocumentFormat("OpenOffice.org 1.0 Text Document", DocumentFamily.TEXT, "application/vnd.sun.xml.writer", "sxw");
        sxw.setExportFilter(DocumentFamily.TEXT, "StarOffice XML (Writer)");
        this.addDocumentFormat(sxw);
        DocumentFormat doc = new DocumentFormat("Microsoft Word", DocumentFamily.TEXT, "application/msword", "doc");
        doc.setExportFilter(DocumentFamily.TEXT, "MS Word 97");
        this.addDocumentFormat(doc);
        DocumentFormat docx = new DocumentFormat("Microsoft Word", DocumentFamily.TEXT, "application/msword", "docx");
        docx.setExportFilter(DocumentFamily.TEXT, "MS Word 2010");
        this.addDocumentFormat(docx);
        DocumentFormat rtf = new DocumentFormat("Rich Text Format", DocumentFamily.TEXT, "text/rtf", "rtf");
        rtf.setExportFilter(DocumentFamily.TEXT, "Rich Text Format");
        this.addDocumentFormat(rtf);
        DocumentFormat wpd = new DocumentFormat("WordPerfect", DocumentFamily.TEXT, "application/wordperfect", "wpd");
        this.addDocumentFormat(wpd);
        DocumentFormat txt = new DocumentFormat("Plain Text", DocumentFamily.TEXT, "text/plain", "txt");
        txt.setImportOption("FilterName", "Text");
        txt.setExportFilter(DocumentFamily.TEXT, "Text");
        this.addDocumentFormat(txt);
        DocumentFormat ods = new DocumentFormat("OpenDocument Spreadsheet", DocumentFamily.SPREADSHEET, "application/vnd.oasis.opendocument.spreadsheet", "ods");
        ods.setExportFilter(DocumentFamily.SPREADSHEET, "calc8");
        this.addDocumentFormat(ods);
        DocumentFormat sxc = new DocumentFormat("OpenOffice.org 1.0 Spreadsheet", DocumentFamily.SPREADSHEET, "application/vnd.sun.xml.calc", "sxc");
        sxc.setExportFilter(DocumentFamily.SPREADSHEET, "StarOffice XML (Calc)");
        this.addDocumentFormat(sxc);
        DocumentFormat xls = new DocumentFormat("Microsoft Excel", DocumentFamily.SPREADSHEET, "application/vnd.ms-excel", "xls");
        xls.setExportFilter(DocumentFamily.SPREADSHEET, "MS Excel 97");
        this.addDocumentFormat(xls);
        DocumentFormat csv = new DocumentFormat("CSV", DocumentFamily.SPREADSHEET, "text/csv", "csv");
        csv.setImportOption("FilterName", "Text - txt - csv (StarCalc)");
        csv.setImportOption("FilterOptions", "44,34,0");
        csv.setExportFilter(DocumentFamily.SPREADSHEET, "Text - txt - csv (StarCalc)");
        csv.setExportOption(DocumentFamily.SPREADSHEET, "FilterOptions", "44,34,0");
        this.addDocumentFormat(csv);
        DocumentFormat tsv = new DocumentFormat("Tab-separated Values", DocumentFamily.SPREADSHEET, "text/tab-separated-values", "tsv");
        tsv.setImportOption("FilterName", "Text - txt - csv (StarCalc)");
        tsv.setImportOption("FilterOptions", "9,34,0");
        tsv.setExportFilter(DocumentFamily.SPREADSHEET, "Text - txt - csv (StarCalc)");
        tsv.setExportOption(DocumentFamily.SPREADSHEET, "FilterOptions", "9,34,0");
        this.addDocumentFormat(tsv);
        DocumentFormat odp = new DocumentFormat("OpenDocument Presentation", DocumentFamily.PRESENTATION, "application/vnd.oasis.opendocument.presentation", "odp");
        odp.setExportFilter(DocumentFamily.PRESENTATION, "impress8");
        this.addDocumentFormat(odp);
        DocumentFormat sxi = new DocumentFormat("OpenOffice.org 1.0 Presentation", DocumentFamily.PRESENTATION, "application/vnd.sun.xml.impress", "sxi");
        sxi.setExportFilter(DocumentFamily.PRESENTATION, "StarOffice XML (Impress)");
        this.addDocumentFormat(sxi);
        DocumentFormat ppt = new DocumentFormat("Microsoft PowerPoint", DocumentFamily.PRESENTATION, "application/vnd.ms-powerpoint", "ppt");
        ppt.setExportFilter(DocumentFamily.PRESENTATION, "MS PowerPoint 97");
        this.addDocumentFormat(ppt);
        DocumentFormat odg = new DocumentFormat("OpenDocument Drawing", DocumentFamily.DRAWING, "application/vnd.oasis.opendocument.graphics", "odg");
        odg.setExportFilter(DocumentFamily.DRAWING, "draw8");
        this.addDocumentFormat(odg);
        DocumentFormat svg = new DocumentFormat("Scalable Vector Graphics", "image/svg+xml", "svg");
        svg.setExportFilter(DocumentFamily.DRAWING, "draw_svg_Export");
        this.addDocumentFormat(svg);
    }
}
