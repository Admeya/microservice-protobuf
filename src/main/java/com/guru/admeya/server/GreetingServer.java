package com.guru.admeya.server;

import com.guru.admeya.server.controllers.CalculatorServerImpl;
import com.guru.admeya.server.controllers.GreetingServerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("hello grpc");

        Server server = ServerBuilder.forPort(50051)
            .addService(new GreetingServerImpl())
            .addService(new CalculatorServerImpl())
            .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Recieved shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
