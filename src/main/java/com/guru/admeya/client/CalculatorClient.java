package com.guru.admeya.client;

import com.proto.calculator.CalcResultGrpc;
import com.proto.calculator.Calculator;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("hello i'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        System.out.println("Creating stub");

        sumTwoNumbers(channel);

       // doErrorCall(channel);

        System.out.println("Shutting down");
        channel.shutdown();

    }

    private static void sumTwoNumbers(ManagedChannel channel) {
        CalcResultGrpc.CalcResultBlockingStub stub = CalcResultGrpc.newBlockingStub(channel);
        Calculator.calcRequest request = Calculator.calcRequest.newBuilder().setFirstNumber(10)
            .setSecondNumber(3).build();
        Calculator.calcResponse response = stub.calculator(request);
        System.out.println(response.getResult());
    }

    private static void doErrorCall(ManagedChannel channel) {
        CalcResultGrpc.CalcResultBlockingStub blockingStub = CalcResultGrpc.newBlockingStub(channel);
        int number = -1;
       try {
           Calculator.SquareRootResponse result = blockingStub.squareRoot(Calculator.SquareRootRequest.newBuilder()
               .setNumber(number)
               .build());
       } catch (StatusRuntimeException e) {
           System.out.println("Got an exception for square root! ");
           e.printStackTrace();
       }
    }
}
