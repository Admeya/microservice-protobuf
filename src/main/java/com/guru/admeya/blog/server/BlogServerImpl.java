package com.guru.admeya.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;
import com.proto.blog.UpdateBlogRequest;
import com.proto.blog.UpdateBlogResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServerImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        System.out.println("Received Create Blog request");

        Blog blog = request.getBlog();
        Document doc = new Document("author_id", blog.getAuthorId())
            .append("title", blog.getTitle())
            .append("content", blog.getContent())
            .append("new_context", "something context")
            .append("else_1", "else... else");

        System.out.println("Inserting blog ... ");
        // we insert (create) the document in mongoDB
        collection.insertOne(doc);

        // we retrieve the MongoDB generated ID
        String id = doc.getObjectId("_id").toString();
        System.out.println("Inserted blog: " + id);

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
            .setBlog(
                // first way
                //                Blog.newBuilder()
                //                .setAuthorId(blog.getAuthorId())
                //                .setContent(blog.getContent())
                //                .setTitle(blog.getTitle())
                //                .setId(id)
                blog.toBuilder().setId(id).build())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("Received Read Blog request");

        String blogId = request.getBlogId();
        Document result = null;
        try {
            result = getDocument(blogId);
        } catch (Exception e) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException()
            );
        }

        if (result == null) {
            // we don't have a match
            System.out.println("Blog not found");
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("The blog with the corresponding id was not found")
                    .asRuntimeException()
            );
        } else {
            System.out.println("Blog found, sending response");
            Blog blog = documentToBlog(blogId, result);
            responseObserver.onNext(
                ReadBlogResponse.newBuilder().setBlog(blog).build()
            );
            responseObserver.onCompleted();
        }
    }

    private Blog documentToBlog(String blogId, Document result) {
        return Blog.newBuilder().setAuthorId(result.getString("author_id"))
                    .setTitle(result.getString("title"))
                    .setContent(result.getString("content"))
                    .setId(blogId)
                    .build();
    }

    private Document getDocument(String blogId) {
        System.out.println("Searching for a blog");
        return collection.find(eq("_id", new ObjectId(blogId)))
            .first();
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("Received Update Blog request");

        Blog blog = request.getBlog();
        String blogId = blog.getId();
        Document result = null;
        try {
            result = getDocument(blogId);
            System.out.println("so we can update it");
        } catch (Exception e) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException()
            );
        }

        if (result == null) {
            // we don't have a match
            System.out.println("Blog not found");
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("The blog with the corresponding id was not found")
                    .asRuntimeException()
            );
        } else {
            System.out.println("Blog found, update document");

            Document replacementDoc = new Document("author_id", blog.getAuthorId())
                .append("content", blog.getContent())
                .append("title", blog.getTitle());

            System.out.println("Replacing blog in database...");

            collection.replaceOne(eq("_id", result.getObjectId("_id")), replacementDoc);
            Blog resultBlog = documentToBlog(blogId, replacementDoc);

            System.out.println("Replaced! Send the response!");
            responseObserver.onNext(
                UpdateBlogResponse.newBuilder().setBlog(resultBlog).build()
            );
            responseObserver.onCompleted();
        }
    }
}
