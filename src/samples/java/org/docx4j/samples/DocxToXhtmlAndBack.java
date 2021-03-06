/*
 *  Copyright 2007-2008, Plutext Pty Ltd.
 *
 *  This file is part of docx4j.

    docx4j is licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.

    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 */

package org.docx4j.samples;

import java.io.File;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.html.AbstractHtmlExporter;
import org.docx4j.convert.out.html.AbstractHtmlExporter.HtmlSettings;
import org.docx4j.convert.out.html.HTMLExporterXslt;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;

/**
 * docx to xhtml to docx again.
 * 
 * Useful for testing support for specific features.
 *
 */
public class DocxToXhtmlAndBack extends AbstractSample {

	static String dir;
	
	// Config for non-command line version
	static {
	
		dir = System.getProperty("user.dir") + "/sample-docs/docx/";
//		dir = System.getProperty("user.dir") + "/";
    	inputfilepath = "sample-docxv2.docx";
//    	inputfilepath = System.getProperty("user.dir") + "/sample-docs/docx/tables.docx";
//    	inputfilepath = System.getProperty("user.dir") + "/images.docx";
		
	}

    public static void main(String[] args)
            throws Exception {
    	
//    	String baseURL = "file:///C:/Users/jharrop/git/docx4j-ImportXHTML/images";    	

    	Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);
    	
    	
		try {
			getInputFilePath(args);
		} catch (IllegalArgumentException e) {
		}

		System.out.println(inputfilepath);
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(dir+inputfilepath));

		// XHTML export
		AbstractHtmlExporter exporter = new HtmlExporterNG2();
    	HtmlSettings htmlSettings = new HtmlSettings();
    	
    	htmlSettings.setWmlPackage(wordMLPackage);
    	
    	htmlSettings.setImageDirPath(dir + inputfilepath + "_files");
    	htmlSettings.setImageTargetUri(dir + inputfilepath + "_files");
    	
    	String htmlFilePath = dir + "/DocxToXhtmlAndBack.html";
		OutputStream os = new java.io.FileOutputStream(htmlFilePath);

//		javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(os);
//		exporter.html(wordMLPackage, result, htmlSettings);
//		os.flush();
//		os.close();

		
		Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_NONE);
		
		
		// XHTML to docx
        String stringFromFile = FileUtils.readFileToString(new File(htmlFilePath), "UTF-8");
		
        
		WordprocessingMLPackage docxOut = WordprocessingMLPackage.createPackage();
		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
		docxOut.getMainDocumentPart().addTargetPart(ndp);
		ndp.unmarshalDefaultNumbering();	
		
        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(docxOut);
        XHTMLImporter.setHyperlinkStyle("Hyperlink");
					
		docxOut.getMainDocumentPart().getContent().addAll( 
				XHTMLImporter.convert(stringFromFile, null) );
				
		docxOut.save(new java.io.File(dir + "/DocxToXhtmlAndBack.docx") );

    }
}