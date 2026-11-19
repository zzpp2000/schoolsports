package com.yyg.schoolsports.utils;

import android.os.Environment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlUtil {
    public static final String DISPOSECODE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/DisposeCode.xml";

    public static final int ProtocolType = 0;  //0-元谷科技原来协议  1-元谷新的协议

    //获取xml中的设备编号
    public static String getDeviceItem(){
        try{
            //创建文档对象
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            document = documentBuilder.parse(new File(DISPOSECODE));

            //获取包含配置文件的文本节点
            NodeList nl = document.getElementsByTagName("DeviceItem");
            Node classNode = nl.item(0).getFirstChild();
            String DeviceSN = classNode.getNodeValue().trim();
            return DeviceSN;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取xml中的ip地址
    public static String getServerIP(){
        try{
            //创建文档对象
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            document = documentBuilder.parse(new File(DISPOSECODE));

            //获取包含配置文件的文本节点
            NodeList nl = document.getElementsByTagName("ServerIP");
            Node classNode = nl.item(0).getFirstChild();
            String ServerIP = classNode.getNodeValue().trim();
            return ServerIP;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void setDeviceItem(String deviceItem){
        try{
            //创建文档对象
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            document = documentBuilder.parse(new File(DISPOSECODE));

            Element element = (Element)document.getElementsByTagName("DeviceItem").item(0);   //更新DeviceItem
            element.setTextContent(deviceItem);

            //更新结果集
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(DISPOSECODE)));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void setServerIP(String serverip){
        try{
            //创建文档对象
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            document = documentBuilder.parse(new File(DISPOSECODE));

            Element element = (Element)document.getElementsByTagName("ServerIP").item(0);   //更新DeviceItem
            element.setTextContent(serverip);

            //更新结果集
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(DISPOSECODE)));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
