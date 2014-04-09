package com.example.androidproject.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import android.os.Environment;

import com.OSGiEmbedApp;
import com.example.androidproject.adaptation.bundle.BundleSpec;

public class XMLUtil {


	public static class ConfigBuilder {

		private Document doc = new Document();  
		private Element rootElement;
		private final String rootAppName = "app";

		public ConfigBuilder(String appName) {
			rootElement = new Element(rootAppName);
			rootElement.setAttribute(new Attribute("name", appName));
			addElement("bundles");
		}


		public ConfigBuilder addElement(String element) {
			rootElement.addContent(new Element(element));
			return this;
		}

		public ConfigBuilder addSubElement(String subElement, String name, String rawID) {
			int size = rootElement.getContentSize();
			Attribute nameAttr = new Attribute("name", name);
			Attribute rawIDAttr = new Attribute("rawID", rawID);
			Element lastElement = (Element) rootElement.getChildren().get(size-1);
			lastElement.addContent(new Element(subElement).setAttribute(rawIDAttr).setAttribute(nameAttr));			
			return this;
		}

		public Document getDOM() {
			doc.setRootElement(rootElement);
			return doc;
		}


	}

	public static void writeConfigXML(ConfigBuilder config) {

		Document doc = config.getDOM();

		FileOutputStream out;
		//xml 파일을 떨구기 위한 경로와 파일 이름 지정해 주기
		XMLOutputter serializer = new XMLOutputter();                 

		Format f = serializer.getFormat();                            
		f.setEncoding("UTF-8");
		//encoding 타입을 UTF-8 로 설정
		f.setIndent(" ");                                             
		f.setLineSeparator("\r\n");                                   
		f.setTextMode(Format.TextMode.TRIM);                          
		serializer.setFormat(f);                

		try {
			String getpath = Environment.getExternalStorageDirectory().getPath();

			out = new FileOutputStream(getpath + "/bundle-config.xml");

			
			serializer.output(doc, out);                                  
			out.flush();                                                  
			out.close();    	

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

	public static List<BundleSpec> getBundleList() {

		ArrayList<BundleSpec> bundleList = new ArrayList<BundleSpec>();

		Document doc = null;
		String getpath = Environment.getExternalStorageDirectory().getPath();
		
		
		try {
			doc = new SAXBuilder().build(getpath+ "/bundle-config.xml");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		

		if(doc != null) {

			//root엘리먼트인 요소를 뽑아오기
			Element rss = doc.getRootElement();

			Element channel = rss.getChild("bundles");

			List<Element> itemList = channel.getChildren("bundle");

			for(Element element : itemList) {
				BundleSpec spec = new BundleSpec();

				spec.setName(element.getAttribute("name").getValue());
				try {
					spec.setRawID(element.getAttribute("rawID").getIntValue());
				} catch (DataConversionException e) {
					e.printStackTrace();
				}

				bundleList.add(spec);
			}
		}

		return bundleList;
	}

}
