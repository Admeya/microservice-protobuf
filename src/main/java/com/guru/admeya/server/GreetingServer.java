package com.guru.admeya.server;

import com.guru.admeya.server.controllers.CalculatorServerImpl;
import com.guru.admeya.server.controllers.GreetingServerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("hello grpc");

        // plaintext server
        //Server serverPlain = new GreetingServer().getPlainTextServer();
        Server serverSecurity = new GreetingServer().securityServer();
        serverSecurity.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Received shutdown request");
            serverSecurity.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        serverSecurity.awaitTermination();
    }

    private Server getPlainTextServer() {
        return ServerBuilder.forPort(50051)
            .addService(new GreetingServerImpl())
           // .addService(new CalculatorServerImpl())
            .build();
    }

    private Server securityServer() {
        return ServerBuilder.forPort(50051)
            .addService(new GreetingServerImpl())
            .useTransportSecurity(
                new File("ssl/server.crt"),
                new File("ssl/server.pem")
            )
            .build();
    }
}
