package com.guru.admeya.client;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadLineRequest;
import com.proto.greet.GreetWithDeadLineResponse;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient extends GrpcClient{

    public static void main(String[] args) {
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        GreetingClient main = new GreetingClient();
        main.run();
        // created a greet service client (blocking - synchronous
       // GreetServiceGrpc.GreetServiceBlockingStub greetClient = main.createStubGreet(channel);
        //main.doUnaryCall(channel, greetClient);
        //main.doServerStreamingCall(greetClient);
        //main.doClientStreamingCall();
        main.doBidiStreamingCall(channel);

        System.out.println("Shutting down");
        channel.shutdown();
    }

    private void doBidiStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(
            new StreamObserver<GreetEveryoneResponse>() {
                @Override
                public void onNext(GreetEveryoneResponse value) {
                    System.out.println("Response from server: " + value.getResult());
                }

                @Override
                public void onError(Throwable throwable) {
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("Server is done sending data");
                    latch.countDown();
                }
            });

        Arrays.asList("Stephan", "John", "Marc", "Patricia").forEach(
            name ->
            {
                System.out.println("Sending: " + name);
                requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder()
                        .setFirstName(name))
                    .build());
            }
        );

        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doClientStreamingCall() {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                // we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(longGreetResponse.getResult());
                //onNext will be called only once
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                //the server is done sending us data
                System.out.println("Server has completed sending us something");
                // onCompleted will be called right after onNext
                latch.countDown();
            }
        });

        // streaming message #1
        System.out.println("sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(
                Greeting.newBuilder()
                .setFirstName("Irina")
                .build()
            ).build());

        // streaming message #2
        System.out.println("sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(
                Greeting.newBuilder()
                    .setFirstName("Dmitry")
                    .build()
            ).build());

        // streaming message #3
        System.out.println("sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(
                Greeting.newBuilder()
                    .setFirstName("Nastya")
                    .build()
            ).build());

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doServerStreamingCall(GreetServiceGrpc.GreetServiceBlockingStub greetClient) {
        GreetManyTimesRequest request = GreetManyTimesRequest
            .newBuilder()
            .setGreeting(Greeting.newBuilder().setFirstName("Irina"))
            .build();
        // we stream the responses in a blocking manner
        greetClient.greetManyTimes(request)
            .forEachRemaining(greetManyTimesResponse ->
                System.out.println(greetManyTimesResponse.getResult()));
    }

    // Unary call
    private void doUnaryCall(GreetServiceGrpc.GreetServiceBlockingStub greetClient) {
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

    private void doUnaryCallWithDeadline(int duration, String text) {
        try {
            System.out.println(String.format("Sending a request with a deadline of %s ms", duration));
            GreetWithDeadLineResponse response = createStubGreet(channel)
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
