package com.guru.admeya.server.controllers;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.PrimeNumberDecompositionResponse;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SquareRootResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse response = SumResponse.newBuilder()
            .setSumResult(request.getFirstNumber() + request.getSecondNumber())
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void squareRoot(SquareRootRequest request,
        StreamObserver<SquareRootResponse> responseObserver) {
        int number = request.getNumber();

        if (number >= 0) {
            double numberRoot = Math.sqrt(number);
            responseObserver.onNext(
                SquareRootResponse.newBuilder()
                    .setNumberRoot(numberRoot)
                    .build()
            );
        } else {
            // we construct the exception
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("The number being sent is not positive")
                    .augmentDescription("Number sent: " + number)
                    .asRuntimeException()
            );
        }
    }

    @Override public void primeNumberDecomposition(PrimeNumberDecompositionRequest request,
        StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer divisor = 2;
        while (number > 1) {
            if (number % divisor == 0) {
                number = number /divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                    .setPrimeFactor(divisor)
                    .build());
            } else {
                divisor = divisor + 1;
            }
        }
    }
}
