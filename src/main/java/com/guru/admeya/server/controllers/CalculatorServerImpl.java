package com.guru.admeya.server.controllers;

import com.proto.calculator.CalcResultGrpc;
import com.proto.calculator.Calculator;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalcResultGrpc.CalcResultImplBase {

    @Override
    public void calculator(Calculator.calcRequest request,
        StreamObserver<Calculator.calcResponse> responseObserver) {
        Calculator.calcResponse response = Calculator.calcResponse.newBuilder().setResult(request.getFirstNumber()+request.getSecondNumber()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
