package com.guru.admeya.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadLineRequest;
import com.proto.greet.GreetWithDeadLineResponse;
import com.proto.greet.Greeting;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) {
        GreetingClient main = new GreetingClient();
        ManagedChannel channel = main.run();
        main.firstGreeting(channel);
        System.out.println("Shutting down");
        channel.shutdown();
    }

    private void firstGreeting(ManagedChannel channel) {
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        // created a greet service client (blocking - synchronous
        GreetServiceGrpc.GreetServiceBlockingStub greetClient =
            GreetServiceGrpc.newBlockingStub(channel);

        // created a protocol buffer greeting message
        Greeting greeting = Greeting
            .newBuilder()
            .setFirstName("Irina")
            .setLastName("Bykova")
            .build();

        // do the same for a GreetRequest
        GreetRequest greetRequest = GreetRequest
            .newBuilder()
            .setGreeting(greeting)
            .build();

        // call the RPC and get back a GreetResponse (protocol buffers)
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }

    private ManagedChannel run() {
        System.out.println("hello i'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();
        System.out.println("Creating stub");

        //  doUnaryCallWithDeadline(channel, 4000, "");
        //  doUnaryCallWithDeadline(channel, 100, "");

        return channel;
    }

    private void doUnaryCallWithDeadline(ManagedChannel channel, int duration, String text) {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);

        try {
            System.out.println(String.format("Sending a request with a deadline of %s ms", duration));
            GreetWithDeadLineResponse response = blockingStub
                .withDeadline(Deadline.after(duration, TimeUnit.MILLISECONDS))
                .greetWithDeadline(
                    GreetWithDeadLineRequest.newBuilder()
                        .setGreeting(Greeting.newBuilder().setFirstName("Irina").getDefaultInstanceForType())
                        .build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded, we don't want the responce");
            } else {
                e.printStackTrace();
            }
        }
    }
}
