package org.angryautomata.xml;
/**
*Cecile FU 03/06 . Update 13/06 pour un automate dans chaque XML . Version 3.0   14/06/2016
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ProcessingInstruction;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.NodeList;

/**
 * Dom4j读写xml
 * @author QIANQIAN fu
 */
public class TestDom4j {
	public static final String FILENAME_STRING = "./data/essai.xml";
    public static void main(String[] args) {
    	//Scanner in = new Scanner(System.in);
        //System.out.print("enter the fichier name：");
        read();
    }

    private static void read() {
		// TODO Auto-generated method stub
    	try {
    		//String filename = in.next();
    		
    		 // new saxReader                      创建saxReader对象  
            SAXReader reader = new SAXReader();  
            // read the Document               通过read方法读取一个文件 转换成Document对象  
            Document document = reader.read(new File(FILENAME_STRING));  
            //full root                                       获取根节点元素对象  
            Element node = document.getRootElement();  
            //travers all nodes                      遍历所有的元素节点  
            listNodes(node); 
            
            
	    } catch (Exception e) {
			// TODO: handle exception
	    }
		
	}

	private static void listNodes(Element node) {
		// TODO Auto-generated method stub
		System.out.println("Current Node: " + node.getName());  
        // all attributes of this node        获取当前节点的所有属性节点  
        List<Attribute> list = node.attributes();  
        // read all Attributes                      遍历属性节点  
        for (Attribute attr : list) {  
            System.out.println(attr.getText() + "-----" + attr.getName()  
                    + "---" + attr.getValue());  
        }    
        if (!(node.getTextTrim().equals(""))) {  
            System.out.println("Value:" + node.getText());  
        }  
  
        
        int nb_symbole_max = 0;
        if(node.getName().equals("nb_symbole_max")){
        	nb_symbole_max=Integer.parseInt(node.getText());
        }
        int etat=0;
        if(node.getName().equals("nb_etat")){
        	etat = Integer.valueOf(node.getText());
        }
        String str[] =null;                ////////////////////////////////////////////   ????!!!!!   ici   ???!!!
    	int[]transition=new int[4];
        if(node.getName().equals("transition")){
        	str = node.getText().split(",");
			for(int k = 0; k < str.length; k++)
			{
				transition[k] = Integer.parseInt(str[k]);
			}
        }
                      
        // Iterator sous-node                 当前节点下面子节点迭代器  
        Iterator<Element> it = node.elementIterator();  
        // Iterator                                       遍历  
        while (it.hasNext()) {  
            // certain                                     获取某个子节点对象  
            Element e = it.next();  
            // Iterator                                   对子节点进行遍历  
            listNodes(e);  
            int[][] transitions = new int[etat * nb_symbole_max][4];
            for(int j = 0;it.hasNext(); j++)
			{
				transitions[j]=transition;
			}
        } 
	}
}

class MyVistor extends VisitorSupport {
    public void visit(Attribute node) {
        System.out.println("Attibute: " + node.getName() + "="
                + node.getValue());
    }

    public void visit(Element node) {
        if (node.isTextOnly()) {
            System.out.println("Element: " + node.getName() + "="
                    + node.getText());
        } else {
            System.out.println(node.getName());
        }
    }

    @Override
    public void visit(ProcessingInstruction node) {
        System.out.println("PI:" + node.getTarget() + " " + node.getText());
    }
}
