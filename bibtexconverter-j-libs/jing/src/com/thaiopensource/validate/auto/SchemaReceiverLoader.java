package com.thaiopensource.validate.auto;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.Service;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.auto.SchemaReceiverFactory;
import com.thaiopensource.validate.Option;

import java.util.Enumeration;

public class SchemaReceiverLoader implements SchemaReceiverFactory {
  private final Service service = new Service(SchemaReceiverFactory.class);
  public SchemaReceiver createSchemaReceiver(String namespaceUri,
                                             PropertyMap properties) {
    for (Enumeration eenum = service.getProviders(); eenum.hasMoreElements();) {
      SchemaReceiverFactory srf = (SchemaReceiverFactory)eenum.nextElement();
      SchemaReceiver sr = srf.createSchemaReceiver(namespaceUri, properties);
      if (sr != null)
        return sr;
    }
    return null;
  }

  public Option getOption(String uri) {
    for (Enumeration eenum = service.getProviders(); eenum.hasMoreElements();) {
      SchemaReceiverFactory srf = (SchemaReceiverFactory)eenum.nextElement();
      Option option = srf.getOption(uri);
      if (option != null)
        return option;
    }
    return null;
  }

}
