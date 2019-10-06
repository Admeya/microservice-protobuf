package com.guru.admeya.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

public class CalculatorClient extends GrpcClient {
    public static void main(String[] args) {
        GreetingClient main = new GreetingClient();
        ManagedChannel channel = main.run();
        // created a greet service client (blocking - synchronous
        CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient = main.createStubCalc(channel);
        //sumTwoNumbers(channel, greetClient);
        primeDivision(channel, greetClient);
        // doErrorCall(channel);
        System.out.println("Shutting down");
        channel.shutdown();

    }

    private static void primeDivision(ManagedChannel channel,
        CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient) {
        Integer number = 567890;
        greetClient.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder().setNumber(number).build())
            .forEachRemaining(primeNumberDecompositionResponse ->
                System.out.println(primeNumberDecompositionResponse.getPrimeFactor()));
    }

    private static void sumTwoNumbers(ManagedChannel channel,
        CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient) {
        SumRequest request = SumRequest.newBuilder()
            .setFirstNumber(10)
            .setSecondNumber(3)
            .build();
        SumResponse response = greetClient.sum(request);
        System.out.println(request.getFirstNumber() + " + " + request.getSecondNumber()
            + " = " + response.getSumResult());
    }

    private static void doErrorCall(ManagedChannel channel,
        CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient) {
        int number = -1;
        try {
            greetClient.squareRoot(SquareRootRequest.newBuilder()
                .setNumber(number)
                .build());
        } catch (StatusRuntimeException e) {
            System.out.println("Got an exception for square root! ");
            e.printStackTrace();
        }
    }
}
