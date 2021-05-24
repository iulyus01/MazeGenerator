package com.iulyus01.mazegenerator.api;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.iulyus01.mazegenerator.algorithms.*;

public class ApiHandler {

    private String algorithmName;
    private final String widthParam;
    private final String heightParam;

    private String response;

    public ApiHandler(String algorithmName, String widthParam, String heightParam) {
        this.algorithmName = algorithmName;
        this.widthParam = widthParam;
        this.heightParam = heightParam;

        handle();
    }

    private void handle() {

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);


        int width;
        int height;
        try {
            // width and height have to be odd numbers
            width = Integer.parseInt(widthParam) | 1;
            height = Integer.parseInt(heightParam) | 1;
        } catch(NumberFormatException e) {
            handleError(json, 400, "Invalid request", "Width and height have to be numbers.");
            return;
        }

        if(width < 3 || height < 3) {
            handleError(json, 400, "Invalid request", "Width and height have to be higher than 2.");
            return;
        }


        Algorithm algorithm;
        switch(algorithmName) {
            case "recursive_backtracking":
                algorithm = new RecursiveBacktracking(width, height);
                algorithmName = "Recursive Backtracking";
                break;
            case "kruskal":
                algorithm = new Kruskal(width, height);
                algorithmName = "Kruskal";
                break;
            case "prim":
                algorithm = new PrimSimplified(width, height);
                algorithmName = "Prim simplified";
                break;
            case "aldous_broder":
                algorithm = new AldousBroder(width, height);
                algorithmName = "Aldous Broder";
                break;
            case "wilson":
                algorithm = new Wilson(width, height);
                algorithmName = "Wilson";
                break;
            case "hunt_and_kill":
                algorithm = new HuntAndKill(width, height);
                algorithmName = "Hunt-and-Kill";
                break;
            case "growing_tree_random":
                algorithm = new GrowingTreeRandom(width, height);
                algorithmName = "Growing Tree Random";
                break;
            case "growing_tree_oldest":
                algorithm = new GrowingTreeOldest(width, height);
                algorithmName = "Growing Tree Oldest";
                break;
            case "growing_tree_newest":
                algorithm = new GrowingTreeNewest(width, height);
                algorithmName = "Growing Tree Newest";
                break;
            case "growing_tree_custom":
                algorithm = new GrowingTreeCustom(width, height);
                algorithmName = "Growing Tree Custom";
                break;
                // TODO add other algorithms
            default:
                handleError(json, 404, "Not found", "Algorithm '" + algorithmName + "' not found.");
                return;
        }

        algorithm.reset();
        algorithm.create(0, 0, 0);

        ApiJsonObject jsonObject = new ApiJsonObject(algorithmName, width, height);
        jsonObject.algName = algorithmName;
        jsonObject.width = width;
        jsonObject.height = height;
        jsonObject.maze = algorithm.gridToMaze();

        response = json.toJson(jsonObject);
    }

    private void handleError(Json json, int status, String errorString, String message) {
        ApiJsonError error = new ApiJsonError(status, errorString, message);
        response = json.toJson(error);
    }

    public String getResponse() {
        return response;
    }
}
