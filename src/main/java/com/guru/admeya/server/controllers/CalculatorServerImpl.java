package com.guru.admeya.server.controllers;

import com.proto.calculator.CalcResultGrpc;
import com.proto.calculator.Calculator;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalcResultGrpc.CalcResultImplBase {

    @Override
    public void calculator(Calculator.calcRequest request,
        StreamObserver<Calculator.calcResponse> responseObserver) {
        Calculator.calcResponse response = Calculator.calcResponse.newBuilder()
            .setResult(request.getFirstNumber() + request.getSecondNumber()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void squareRoot(Calculator.SquareRootRequest request,
        StreamObserver<Calculator.SquareRootResponse> responseObserver) {
        int number = request.getNumber();

        if (number >= 0) {
            double numberRoot = Math.sqrt(number);
            responseObserver.onNext(
                Calculator.SquareRootResponse.newBuilder()
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
