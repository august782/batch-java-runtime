/*******************************************************************************
 * The accompanying source code is made available to you under the terms of 
 * the UT Research License (this "UTRL"). By installing or using the code, 
 * you are consenting to be bound by the UTRL. See LICENSE.html for a 
 * full copy of the license.
 * 
 * Copyright 2009, The University of Texas at Austin. All rights reserved.
 * 
 * UNIVERSITY EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES CONCERNING THIS 
 * SOFTWARE AND DOCUMENTATION, INCLUDING ANY WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR ANY PARTICULAR PURPOSE, NON-INFRINGEMENT AND WARRANTIES 
 * OF PERFORMANCE, AND ANY WARRANTY THAT MIGHT OTHERWISE ARISE FROM COURSE 
 * OF DEALING OR USAGE OF TRADE. NO WARRANTY IS EITHER EXPRESS OR IMPLIED 
 * WITH RESPECT TO THE USE OF THE SOFTWARE OR DOCUMENTATION. Under no circumstances 
 * shall University be liable for incidental, special, indirect, direct 
 * or consequential damages or loss of profits, interruption of business, 
 * or related expenses which may arise from use of Software or Documentation, 
 * including but not limited to those resulting from defects in Software 
 * and/or Documentation, or loss or inaccuracy of data of any kind.
 * 
 * Created by: William R. Cook and Eli Tilevich
 * with: Jose Falcon, Marc Fisher II, Ali Ibrahim, Yang Jiao, Ben Wiedermann
 * University of Texas at Austin and Virginia Tech
 ******************************************************************************/
package batch.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import org.antlr.runtime.RecognitionException;

import batch.Service;
import batch.syntax.BatchScriptParser;
import batch.syntax.Expression;
import batch.util.BatchFactory;
import batch.util.BatchTransport;
import batch.util.Forest;

public class TCPServer<E, T> implements Runnable {

  Service<E, T> handler;
  ServerSocket socket;
  BatchTransport transport;
  BatchFactory<E> factory;
  public boolean debug;

  public TCPServer(Service<E, T> handler, ServerSocket socket,
      BatchTransport transport, BatchFactory<E> factory) throws IOException {
    this.handler = handler;
    this.socket = socket;
    this.transport = transport;
    this.factory = factory;
  }

  public void run() {
    while (true) {
      try {
        handle(socket.accept());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void handle(Socket connectionSocket) {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(
          connectionSocket.getInputStream()));
      //			Expression exp = BatchScriptParser.parse(in);
      String script = in.readLine();
      if (debug)
        System.out.println("Script: " + script);
      Expression exp = BatchScriptParser.parse(script);
      // Forest data = transport.readForest(in);
      // System.out.print("Data: ");
      // System.out.println(data.toString());
      Forest result = handler.execute(exp.run(factory), null);
      if (debug)
        transport.write(result, new OutputStreamWriter(System.out));

      Writer out = new OutputStreamWriter(connectionSocket.getOutputStream());
      // TODO: supercompilation can combine the execute and write
      // phases!
      transport.write(result, out);
    } catch (RecognitionException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    System.out.println("Server starting on " + socket);
    Thread thread = new Thread(this);
    thread.start();
  }
}