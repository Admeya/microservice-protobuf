package com.guru.admeya.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.ComputeAverageRequest;
import com.proto.calculator.ComputeAverageResponse;
import com.proto.calculator.FindMaximumRequest;
import com.proto.calculator.FindMaximumResponse;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient extends GrpcClient {
    public static void main(String[] args) {
        CalculatorClient main = new CalculatorClient();
        main.run();
        // created a greet service client (blocking - synchronous
        // CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient = main.createStubCalc(channel);
        //main.sumTwoNumbers(channel, greetClient);
        //main.primeDivision(greetClient);
       // main.doClientStreamingCall();
        main.doBiDiStreamingCall();
        // main.doErrorCall(channel);
        System.out.println("Shutting down");
        channel.shutdown();

    }

    private void doClientStreamingCall() {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> request = asyncClient
            .computeAverage(new StreamObserver<ComputeAverageResponse>() {
                @Override
                public void onNext(ComputeAverageResponse computeAverageResponse) {
                    System.out.println("Received a response from the server");
                    System.out.println(computeAverageResponse.getAverage());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("Server has completed sending data");
                    latch.countDown();
                }
            });

        for (int i = 0; i < 10000; i++) {
            request.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(i)
                .build());
        }

        request.onCompleted();

        try {
            latch.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiStreamingCall() {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println("Got new maximum from Server " + value.getMaximum());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending messages");
            }
        });

        Arrays.asList(3, 5, 17, 9, 8, 30, 12).forEach(
            number -> {
                System.out.println("Sending number " + number);
                requestObserver.onNext(
                    FindMaximumRequest.newBuilder()
                        .setNumber(number)
                        .build()
                );
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        );
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void primeDivision(CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient) {
        Integer number = 567890;
        greetClient.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder().setNumber(number).build())
            .forEachRemaining(primeNumberDecompositionResponse ->
                System.out.println(primeNumberDecompositionResponse.getPrimeFactor()));
    }

    private void sumTwoNumbers(CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient) {
        SumRequest request = SumRequest.newBuilder()
            .setFirstNumber(10)
            .setSecondNumber(3)
            .build();
        SumResponse response = greetClient.sum(request);
        System.out.println(request.getFirstNumber() + " + " + request.getSecondNumber()
            + " = " + response.getSumResult());
    }

    private void doErrorCall(CalculatorServiceGrpc.CalculatorServiceBlockingStub greetClient) {
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
