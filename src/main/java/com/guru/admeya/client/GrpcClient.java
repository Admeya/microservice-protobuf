package com.guru.admeya.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.greet.GreetServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {

    static ManagedChannel channel;

    protected static GreetServiceGrpc.GreetServiceBlockingStub createStubGreet(ManagedChannel channel) {
        return GreetServiceGrpc.newBlockingStub(channel);
    }

    protected static CalculatorServiceGrpc.CalculatorServiceBlockingStub createStubCalc(ManagedChannel channel) {
        return CalculatorServiceGrpc.newBlockingStub(channel);
    }

    protected void run() {
        System.out.println("hello i'm a gRPC client");
        channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        System.out.println("Creating stub");
    }
}
