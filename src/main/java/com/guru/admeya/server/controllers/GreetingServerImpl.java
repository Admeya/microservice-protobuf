package com.guru.admeya.server.controllers;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetManyTimesResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadLineRequest;
import com.proto.greet.GreetWithDeadLineResponse;
import com.proto.greet.Greeting;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        // extract the fields we need
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        // cresate the response
        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
            .setResult(result)
            .build();

        // send the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetWithDeadline(GreetWithDeadLineRequest request,
        StreamObserver<GreetWithDeadLineResponse> responseObserver) {

        Context current = Context.current();

        try {
            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("sleep for 100 ms");
                    Thread.sleep(100);
                } else {
                    return;
                }
            }

            System.out.println("send response");
            responseObserver.onNext(
                GreetWithDeadLineResponse.newBuilder()
                    .setResult("hello " + request.getGreeting().getFirstName())
                    .build()
            );
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request,
        StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number: " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();
                responseObserver.onNext(response);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }

        responseObserver.onCompleted();
    }
}
