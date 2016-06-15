package org.angryautomata.xml;
/**
 * Cecile FU 03/06 . Update 13/06 pour un automate dans chaque XML . Version 3.0   15/06/2016
 */

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Dom4j读写xml
 * @author QIANQIAN fu
 */
public class XMLReader
{
	static class TestDom4j
	{
		public static final String FILENAME_STRING = "./data/essai.xml";

		public static void main(String[] args)
		{
			//Scanner in = new Scanner(System.in);
			//System.out.print("enter the fichier name：");
			read();
		}

		private static void read()
		{
			// TODO Auto-generated method stub
			try
			{
				//String filename = in.next();

				// new saxReader                      创建saxReader对象
				SAXReader reader = new SAXReader();
				// read the Document               通过read方法读取一个文件 转换成Document对象
				Document document = reader.read(new File(FILENAME_STRING));
				//full root                                       获取根节点元素对象
				Element node = document.getRootElement();
				//travers all nodes                      遍历所有的元素节点
				listNodes(node);
			}
			catch(Exception e)
			{
				// TODO: handle exception
			}

		}

		private static void listNodes(Element node)
		{
			// TODO Auto-generated method stub
			System.out.println("Current Node: " + node.getName());
			// all attributes of this node        获取当前节点的所有属性节点
			List<Attribute> list = node.attributes();
			// read all Attributes                      遍历属性节点
			for(Attribute attr : list)
			{
				System.out.println(attr.getText() + "-----" + attr.getName()
						+ "---" + attr.getValue());
			}
			if(!(node.getTextTrim().equals("")))
			{
				System.out.println("Value:" + node.getText());
			}


			

                        Element maxsym=node.element("nb_symbole_max");
    	                int nb_symbole_max = 0;
			if(String.valueOf(maxsym.getStringValue())!=null &&! "".equals(maxsym.getStringValue()) &&!"null".equals(maxsym.getStringValue())&&!"".equals(maxsym.getStringValue().trim()))
			{
				nb_symbole_max = Integer.parseInt(maxsym.getStringValue());
			}
			Element auto =node.element("automate");
			String joueur = auto.element("nom").getStringValue();
			int etat = Integer.valueOf(auto.element("nb_etat").getStringValue());
			int[][] transition = new int[etat * nb_symbole_max][4];
		
			Element tran =auto.element("transitions");
			List<Element> transio = tran.elements("transition");  
			for(int j = 0; j < transio.size(); j++)
			{
				String str[] = transio.get(j).getStringValue().split(",");
				for(int k = 0; k < str.length; k++)
				{
					transition[j][k] = Integer.parseInt(str[k]);
				}

			}
		//System.out.println("依test照:" + transition[3][2]);              //C'est bon tetst!~~~~~
		
			// Iterator sous-node                 当前节点下面子节点迭代器
			Iterator<Element> it = node.elementIterator();
			// Iterator                                       遍历
			while(it.hasNext())
			{
				// certain                                     获取某个子节点对象
				Element e = it.next();
				// Iterator                                   对子节点进行遍历
				listNodes(e);
			}
			/*int[][] transitions = new int[etat * nb_symbole_max][4];
				for(int j = 0; it.hasNext(); j++)
				{
					transitions[j] = transition;
				}*/
		}
	}

	static class MyVistor extends VisitorSupport
	{
		public void visit(Attribute node)
		{
			System.out.println("Attibute: " + node.getName() + "="
					+ node.getValue());
		}

		public void visit(Element node)
		{
			if(node.isTextOnly())
			{
				System.out.println("Element: " + node.getName() + "="
						+ node.getText());
			}
			else
			{
				System.out.println(node.getName());
			}
		}

		@Override
		public void visit(ProcessingInstruction node)
		{
			System.out.println("PI:" + node.getTarget() + " " + node.getText());
		}
	}
}
