package com.guru.admeya.server;

import com.guru.admeya.server.controllers.GreetingServerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("hello grpc");

        // plaintext server
        Server serverPlain = new GreetingServer().getPlainTextServer();
        //Server serverSecurity = new GreetingServer().securityServer();
        serverPlain.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Received shutdown request");
            serverPlain.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        serverPlain.awaitTermination();
    }

    private Server getPlainTextServer() {
        return ServerBuilder.forPort(50051)
            .addService(new GreetingServerImpl())
           // .addService(new CalculatorServerImpl())
            .addService(ProtoReflectionService.newInstance())
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
