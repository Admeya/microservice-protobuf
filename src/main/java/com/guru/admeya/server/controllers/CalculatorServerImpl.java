package com.guru.admeya.server.controllers;

import com.proto.calculator.CalculatorServiceGrpc;
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
}
