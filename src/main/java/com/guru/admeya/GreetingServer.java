package com.guru.admeya;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("hello grpc");

        Server server = ServerBuilder.forPort(50051).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Recieved shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
