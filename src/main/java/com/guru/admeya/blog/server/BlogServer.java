package com.guru.admeya.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("hello grpc");

        // plaintext server
        Server serverPlain = new BlogServer().getPlainTextServer();
        serverPlain.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Received shutdown request");
            serverPlain.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        serverPlain.awaitTermination();
    }

    //new comment
    private Server getPlainTextServer() {
        return ServerBuilder.forPort(50051)
            .addService(new BlogServerImpl())
            .addService(ProtoReflectionService.newInstance())
            .build();
    }
}
