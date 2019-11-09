package com.guru.admeya.blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
    static ManagedChannel channel;

    public static void main(String[] args) {
        BlogClient main = new BlogClient();
        main.run();
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        Blog blog = Blog.newBuilder()
            .setAuthorId("Irina")
            .setTitle("New blog!")
            .setContent("Hello world this is my first blog!")
            .build();

        CreateBlogResponse createBlogResponse = blogClient.createBlog(
            CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build()
        );

        System.out.println("Receive create blog response");
        System.out.println(createBlogResponse.toString());

        String blogId = createBlogResponse.getBlog().getId();

        System.out.println("I found just created message!");
        ReadBlogResponse response = blogClient.readBlog(ReadBlogRequest.newBuilder()
            .setBlogId(blogId)
            .build());

        System.out.println(response.toString());

        // example of Not found
//        System.out.println("I do not found message");
//        ReadBlogResponse notFound = blogClient.readBlog(ReadBlogRequest.newBuilder()
//            .setBlogId("fakeId")
//            .build());

        // created a greet service client (blocking - synchronous
        System.out.println("Shutting down");
        channel.shutdown();

    }

    protected void run() {
        System.out.println("hello i'm a gRPC client");
                channel = ManagedChannelBuilder
                    .forAddress("localhost", 50051)
                    .usePlaintext()
                    .build();
        System.out.println("Creating stub");
    }
}
