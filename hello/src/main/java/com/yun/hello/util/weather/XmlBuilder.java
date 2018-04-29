package com.yun.hello.util.weather;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;

public class XmlBuilder {

    public static Object xmlStrToObject(Class<?> clazz,String xmlStr) throws Exception{
        Object xmlObject = null;
        Reader reader = null;
        JAXBContext context = JAXBContext.newInstance(clazz);

        reader = new StringReader(xmlStr);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        xmlObject = unmarshaller.unmarshal(reader);

        if(reader != null) {
            reader.close();
        }

        return xmlObject;
    }
}
