package com.guru.admeya.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SquareRootResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = run();
        sumTwoNumbers(channel);
        // doErrorCall(channel);
        System.out.println("Shutting down");
        channel.shutdown();

    }

    private static ManagedChannel run() {
        System.out.println("hello i'm a gRPC client");
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        System.out.println("Creating stub");
        return channel;
    }

    private static void sumTwoNumbers(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub
            = CalculatorServiceGrpc.newBlockingStub(channel);
        SumRequest request = SumRequest.newBuilder()
            .setFirstNumber(10)
            .setSecondNumber(3)
            .build();
        SumResponse response = stub.sum(request);
        System.out.println(request.getFirstNumber() + " + " + request.getSecondNumber()
            + " = " + response.getSumResult());
    }

    private static void doErrorCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub
            = CalculatorServiceGrpc.newBlockingStub(channel);
        int number = -1;
        try {
            SquareRootResponse result = blockingStub.squareRoot(SquareRootRequest.newBuilder()
                .setNumber(number)
                .build());
        } catch (StatusRuntimeException e) {
            System.out.println("Got an exception for square root! ");
            e.printStackTrace();
        }
    }
}
