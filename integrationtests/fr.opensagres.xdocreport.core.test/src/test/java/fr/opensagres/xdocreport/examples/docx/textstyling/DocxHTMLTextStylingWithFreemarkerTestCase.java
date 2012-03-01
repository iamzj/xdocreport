/**
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com> and Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fr.opensagres.xdocreport.examples.docx.textstyling;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.SyntaxKind;
import fr.opensagres.xdocreport.core.io.IOUtils;
import fr.opensagres.xdocreport.core.io.XDocArchive;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.docx.DocxConstants;
import fr.opensagres.xdocreport.document.docx.DocxReport;
import fr.opensagres.xdocreport.document.docx.DocxUtils;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

/**
 * Example with MS Word Docx which contains the content Hello ${name}!. Merge with Freemarker template engine will
 * replace this cell with Hello world!
 */
public class DocxHTMLTextStylingWithFreemarkerTestCase
{

    @Test
    public void testOne()
        throws IOException, XDocReportException
    {

        // 1) Load Docx file by filling Freemarker template engine and cache it
        // to the registry
        IXDocReport report =
            XDocReportRegistry.getRegistry().loadReport( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( "DocxHTMLTextStylingWithFreemarker.docx" ),
                                                         TemplateEngineKind.Freemarker );

        Assert.assertTrue( "This is a Docx file, DocxReport implementation should have been resolved....",
                           report instanceof DocxReport );

        FieldsMetadata metadata = report.createFieldsMetadata();
        metadata.addFieldAsTextStyling( "comments", SyntaxKind.Html );

        StringBuilder html = new StringBuilder();
        html.append( "<h1>Title 1</h1>" );
        html.append( "xxx <a href=\"xxxxxxxx\" >xxx</a> and <strong>b</strong>." );
        html.append( "<h2>Title 1.1</h2>" );
        html.append( "aaaa<em>aaaaa</em>aaaaa <p>iiiiiiiiiiiiiii<b>hhh</b></p>" );
        html.append( "<h3>Title 1.1.1</h3>" );
        html.append( "<ul><li>aaaa</li><li>bbbb</li>" + "<ul><li>aa</li></ul>" + "<ul><li>bb</li></ul>" + "</ul>" );
        html.append( "<ol><li>aaaa</li><li>bbbb</li>" + "<ol><li>aa</li></ol>" + "<ol><li>bb</li></ol>" +

        "</ol>" );
        html.append( "<ol><li>aaaa</li><li>bbbb</li>" + "<ol><li>aa</li></ol>" + "<ol><li>bb</li></ol>" +

        "</ol>" );
        html.append( "<ol><li>aaaa</li><li>bbbb</li></ol>" );
        html.append( "<h1>Title 2</h1>" );

        // 2) Create context Java model
        IContext context = report.createContext();
        context.put( "comments", html.toString() );

        // 3) Merge Java model with the Docx
        File out = new File( "target" );
        out.mkdirs();
        File file = new File( out, "DocxHTMLTextStylingWithFreemarker.docx" );
        report.process( context, new FileOutputStream( file ) );

    }

    @Test
    public void loadNonExistingReport()
    {

        try
        {
            XDocArchive.readZip( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( "not_found" ) );
            fail( "'not_found' does not exists " );
        }
        catch ( IOException e )
        {
            // success
        }
    }

    @Test
    public void loadExistingDocxReport()
    {
        String fileName = "DocxHTMLTextStylingWithFreemarker.docx";
        // 1) Load Docx file by filling Freemarker template engine and cache it
        // to the registry
        IXDocReport report = null;
        try
        {

            report =
                XDocReportRegistry.getRegistry().loadReport( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( fileName ),
                                                             TemplateEngineKind.Freemarker );

        }
        catch ( Exception e )
        {
            fail( "Unable to load " + fileName + " " + e.getMessage() );
        }

        assertThat( "This is a Docx file, DocxReport implementation should have been resolved....", report,
                    instanceOf( DocxReport.class ) );

    }

    @Test
    public void loadReportWithId()
    {
        String fileName = "DocxHTMLTextStylingWithFreemarker.docx";

        IXDocReport report = null;
        try
        {

            report =
                XDocReportRegistry.getRegistry().loadReport( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( fileName ),
                                                             fileName, TemplateEngineKind.Freemarker );

        }
        catch ( Exception e )
        {
            fail( "Unable to load " + fileName + " " + e.getMessage() );
        }

        assertEquals( fileName, report.getId() );
        assertEquals( report, XDocReportRegistry.getRegistry().getReport( fileName ) );
    }

    @Test
    public void cannotRegisterTwoTimeSameId()
    {
        String fileName = "DocxHTMLTextStylingWithFreemarker.docx";

        IXDocReport report = null;
        try
        {

            report =
                XDocReportRegistry.getRegistry().loadReport( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( fileName ),
                                                             "id", TemplateEngineKind.Freemarker );

            XDocReportRegistry.getRegistry().loadReport( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( fileName ),
                                                         "id", TemplateEngineKind.Freemarker );
            fail( "cannot register 2 reports with the same id" );
        }
        catch ( Exception e )
        {
            // success
        }

    }

    @Test
    public void checkXDocArchiveContent()
        throws IOException, XDocReportException
    {
        String fileName = "DocxHTMLTextStylingWithFreemarker.docx";

        IXDocReport report = null;

        report =
            XDocReportRegistry.getRegistry().loadReport( DocxHTMLTextStylingWithFreemarkerTestCase.class.getResourceAsStream( fileName ),
                                                         TemplateEngineKind.Freemarker );

        XDocArchive archive = report.getPreprocessedDocumentArchive();

        assertNotNull( archive );
        assertTrue( archive.hasEntry( DocxConstants.WORD_DOCUMENT_XML_ENTRY ) );
        assertTrue( DocxUtils.isDocx( archive ) );

        Reader reader = archive.getEntryReader( DocxConstants.WORD_DOCUMENT_XML_ENTRY );
        StringWriter writer = new StringWriter();
        IOUtils.copy( reader, writer );

        String contentAsString = writer.toString();
        // System.out.println(contentAsString);
        assertTrue( contentAsString.contains( "${comments}" ) );
    }
}