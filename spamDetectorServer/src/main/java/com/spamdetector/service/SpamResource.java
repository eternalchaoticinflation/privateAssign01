package com.spamdetector.service;

import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Path("/spam")
public class SpamResource {

    private final SpamDetector spamDetector;

    public SpamResource() throws IOException {
        System.out.println("Training and testing the model, please wait...");
        this.spamDetector = new SpamDetector();
        // Load data, train, and test. This assumes loadData prepares the SpamDetector.
        this.spamDetector.loadData(); // Assuming this method exists and does what's needed.
    }

    @GET
    @Produces("application/json")
    public Response getSpamResults() throws IOException {
        List<TestFile> testFiles = trainAndTest(); // Ensure this fetches your test data correctly.

        JSONArray jsonData = new JSONArray();
        for (TestFile testFile : testFiles) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("spamProbRounded", String.format("%.5f", testFile.getSpamProbability()));
            jsonObject.put("file", testFile.getFilename());
            jsonObject.put("spamProbability", testFile.getSpamProbability());
            jsonObject.put("actualClass", testFile.getActualClass());
            jsonData.put(jsonObject);
        }

        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Content-Type", "application/json")
                .entity(jsonData.toString())
                .build();
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() throws IOException {
        List<TestFile> testFiles = trainAndTest();
        double precision = calculatePrecision(testFiles);
        JSONObject precisionObject = new JSONObject();
        precisionObject.put("precision", precision);
        return Response.status(200)
                .entity(precisionObject.toString())
                .build();
    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() throws IOException {
        List<TestFile> testFiles = trainAndTest();
        double accuracy = calculateAccuracy(testFiles);
        JSONObject accuracyObject = new JSONObject();
        accuracyObject.put("accuracy", accuracy);
        return Response.status(200)
                .entity(accuracyObject.toString())
                .build();
    }

    private List<TestFile> trainAndTest() throws IOException {
        // Adjust the path as necessary.
        String currentDir = System.getProperty("user.dir");
        String relativePath = "spamDetectorServer/target/classes/data/test";
        File mainDirectory = new File(currentDir, relativePath);
        return spamDetector.trainAndTest(mainDirectory);
    }

    private double calculateAccuracy(List<TestFile> testFiles) {
        int truePositives = 0, trueNegatives = 0;
        for (TestFile file : testFiles) {
            if ("spam".equals(file.getActualClass()) && file.getSpamProbability() >= 0.5)
                truePositives++;
            else if ("ham".equals(file.getActualClass()) && file.getSpamProbability() < 0.5)
                trueNegatives++;
        }
        return (truePositives + trueNegatives) / (double) testFiles.size();
    }

    private double calculatePrecision(List<TestFile> testFiles) {
        int truePositives = 0, falsePositives = 0;
        for (TestFile file : testFiles) {
            if ("spam".equals(file.getActualClass()) && file.getSpamProbability() >= 0.5)
                truePositives++;
            else if ("ham".equals(file.getActualClass()) && file.getSpamProbability() >= 0.5)
                falsePositives++;
        }
        return truePositives / (double) (truePositives + falsePositives);
    }
}