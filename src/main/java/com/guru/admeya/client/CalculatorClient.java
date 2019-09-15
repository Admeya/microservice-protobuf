package com.guru.admeya.client;

import com.proto.calculator.CalcResultGrpc;
import com.proto.calculator.Calculator;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("hello i'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        System.out.println("Creating stub");

        CalcResultGrpc.CalcResultBlockingStub stub = CalcResultGrpc.newBlockingStub(channel);
        Calculator.calcRequest request = Calculator.calcRequest.newBuilder().setFirstNumber(10)
        .setSecondNumber(3).build();
        Calculator.calcResponse response = stub.calculator(request);
        System.out.println(response.getResult());

        System.out.println("Shutting down");
        channel.shutdown();

    }
}
