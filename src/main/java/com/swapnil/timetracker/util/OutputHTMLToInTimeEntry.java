package com.swapnil.timetracker.util;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OutputHTMLToInTimeEntry {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		File inFile = new File("./timetracker_out.txt");
		Document doc = builder.parse(inFile);
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "//tr";	        
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
		   doc, XPathConstants.NODESET);
		/*for(int i=0;i<nodeList.getLength();i++) {
			Node node = nodeList.item(i);
			String start = node.getChildNodes().item(3).getTextContent();
			String end = node.getChildNodes().item(4).getTextContent();
			String comment = node.getChildNodes().item(6).getTextContent();
			System.out.println(start + (comment!=null && comment.trim().length()>0?"#" + comment:""));
			System.out.println(end);
		}*/
		
		/*for(int i=0;i<nodeList.getLength();i++) {
			Node node = nodeList.item(i);
			NodeList childs = (NodeList) xPath.compile("./td").evaluate(
					   node, XPathConstants.NODESET);
			if(childs.getLength()==6) {
				for(int j=0;j<childs.getLength();j++) {
					Node child = childs.item(j);
					System.out.print(j + " : "+ child.getTextContent() + "\t\t");
				}
				System.out.println();				
			}
		}*/
		
		List<String> entries = new ArrayList<String>();
		for(int i=0;i<nodeList.getLength();i++) {
			Node node = nodeList.item(i);
			NodeList childs = (NodeList) xPath.compile("./td").evaluate(
					   node, XPathConstants.NODESET);
			if(childs.getLength()==6) {
				String start = childs.item(2).getTextContent();
				String end = childs.item(3).getTextContent();
				String comment = childs.item(5).getTextContent();
				if(!start.endsWith("00:00:00")) {
					entries.add(end);					
				}
				entries.add(start + (comment!=null && comment.trim().length()>0?"#" + comment:""));
			}
		}
		
		if(entries.size()>0) {
			for(int i=entries.size()-1; i>=0;i--) {
				System.out.println(entries.get(i));
			}
		}
	}
}
