package com.guru.admeya.blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.DeleteBlogRequest;
import com.proto.blog.DeleteBlogResponse;
import com.proto.blog.ListBlogRequest;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;
import com.proto.blog.UpdateBlogRequest;
import com.proto.blog.UpdateBlogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
    static ManagedChannel channel;

    public static void main(String[] args) {
        BlogClient main = new BlogClient();
        main.run();
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        CreateBlogResponse createBlogResponse = createDocument(blogClient);

        String blogId = createBlogResponse.getBlog().getId();

        readDocument(blogClient, blogId);
        updateDocument(blogClient, blogId);
        deleteDocument(blogClient, blogId);
        getAllDocuments(blogClient);

        // created a greet service client (blocking - synchronous
        System.out.println("Shutting down");
        channel.shutdown();

    }

    private static void getAllDocuments(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
        // we list the blog in our database
        blogClient.listBlog(ListBlogRequest.newBuilder().build())
            .forEachRemaining(
                listBlogResponse -> System.out.println(listBlogResponse.getBlog().toString())
            );
    }

    private static void deleteDocument(BlogServiceGrpc.BlogServiceBlockingStub blogClient, String blogId) {
        System.out.println("Deleting blog");
        DeleteBlogResponse deleteBlogResponse = blogClient.deleteBlog(
            DeleteBlogRequest.newBuilder().setBlogId(blogId).build()
        );
        System.out.println("Deleted document with id " + blogId);
        System.out.println("Read blog after deleting");
        // this one should return NOT_FOUND
        // readDocument(blogClient, blogId);
    }

    private static CreateBlogResponse createDocument(BlogServiceGrpc.BlogServiceBlockingStub blogClient) {
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
        return createBlogResponse;
    }

    private static void readDocument(BlogServiceGrpc.BlogServiceBlockingStub blogClient,
        String blogId) {

        System.out.println("Try to found blog id " + blogId + " ...");
        ReadBlogResponse response = blogClient.readBlog(ReadBlogRequest.newBuilder()
            .setBlogId(blogId)
            .build());

        System.out.println(response.toString());

        // example of Not found
        //        System.out.println("I do not found message");
        //        ReadBlogResponse notFound = blogClient.readBlog(ReadBlogRequest.newBuilder()
        //            .setBlogId("fakeId")
        //            .build());
    }

    private static void updateDocument(BlogServiceGrpc.BlogServiceBlockingStub blogClient, String blogId) {
        Blog newBlog = Blog.newBuilder()
            .setAuthorId("Changed Author")
            .setTitle("New blog (Updated)!")
            .setContent("(Update blog message)")
            .setId(blogId)
            .build();

        UpdateBlogResponse responseUpdate = blogClient.updateBlog(UpdateBlogRequest.newBuilder()
            .setBlog(newBlog).build());

        System.out.println("Updated blog ");
        System.out.println(responseUpdate.toString());
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
