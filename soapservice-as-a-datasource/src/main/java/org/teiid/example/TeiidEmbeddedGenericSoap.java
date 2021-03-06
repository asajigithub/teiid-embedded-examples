/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.teiid.example;

import static org.teiid.example.util.JDBCUtils.execute;

import java.sql.Connection;
import java.util.logging.Level;

import org.teiid.resource.adapter.ws.WSManagedConnectionFactory;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.ws.WSExecutionFactory;

/**
 * This example shows invoking a generic soap service.
 * 
 */
@SuppressWarnings("nls")
public class TeiidEmbeddedGenericSoap {
	
	
	public static void main(String[] args) throws Exception {
		
		EmbeddedHelper.enableLogger(Level.INFO);
		
		EmbeddedServer es = new EmbeddedServer();
		
		WSExecutionFactory ef = new WSExecutionFactory();
		ef.start();
		es.addTranslator("translator-ws", ef);
		
		//add a connection factory
		WSManagedConnectionFactory wsmcf = new WSManagedConnectionFactory();
		es.addConnectionFactory("java:/StateServiceWebSvcSource", wsmcf.createConnectionFactory());
		
		es.start(new EmbeddedConfiguration());
		
		es.deployVDB(TeiidEmbeddedGenericSoap.class.getClassLoader().getResourceAsStream("webservice-vdb.xml"));
		
		Connection c = es.getDriver().connect("jdbc:teiid:StateServiceVDB", null);
		
		//This assume 'stateService' run on localhost
		execute(c, "EXEC GetStateInfo('CA', 'http://localhost:8080/StateService/stateService/StateServiceImpl?WSDL')", false);
		
		execute(c, "EXEC GetAllStateInfo('http://localhost:8080/StateService/stateService/StateServiceImpl?WSDL')", true);
		
		es.stop();
	}

}
