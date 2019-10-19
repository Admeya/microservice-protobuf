package com.guru.admeya.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

public class GrpcClient {

    static ManagedChannel channel;
    static ManagedChannel secureChannel;

    protected static GreetServiceGrpc.GreetServiceBlockingStub createStubGreet(ManagedChannel channel) {
        return GreetServiceGrpc.newBlockingStub(channel);
    }

    protected static CalculatorServiceGrpc.CalculatorServiceBlockingStub createStubCalc(ManagedChannel channel) {
        return CalculatorServiceGrpc.newBlockingStub(channel);
    }

    protected void run() throws SSLException {
        System.out.println("hello i'm a gRPC client");
//        channel = ManagedChannelBuilder
//            .forAddress("localhost", 50051)
//            .usePlaintext()
//            .build();
        secureChannel = NettyChannelBuilder.forAddress(
            "localhost", 50051)
            .sslContext(GrpcSslContexts.forClient().trustManager(
                new File("ssl/ca.crt")).build())
            .build();
        System.out.println("Creating stub");
    }
}
