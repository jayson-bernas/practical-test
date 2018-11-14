package com.oxford;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestDictionary
{

    @DisplayName("Valid Word")
    @Test
    void validWord()
    {
        String word = "happy";

        given().
                baseUri(getProperty("host")).
                headers(getHeaders()).
        when().
                get("/api/v1/entries/en/" + word).
        then().
                statusCode(200);
    }

    @DisplayName("Invalid Word")
    @Test
    void invalidWord()
    {

        String word = "xXxXx";

        given().
                baseUri(getProperty("host")).
                headers(getHeaders()).
        when().
                get("/api/v1/entries/en/" + word).
        then().
                statusCode(404);
    }

    @DisplayName("Check Definition")
    @Test
    void checkDefinition()
    {
        String word = "happy";

        Response response=
                given().
                        baseUri(getProperty("host")).
                        headers(getHeaders()).
                        when().
                        get("/api/v1/entries/en/" + word).
                        then().extract().response();

        String[] definitionList =
                (response.path("results.lexicalEntries.entries.senses.definitions").
                        toString().
                        replaceAll("\\[|]","")).
                        split(",");

        for(String def : definitionList){
            System.out.println(def);
            assertTrue(def.length() != 0);
        }
    }


    String getProperty(String key){
        Properties prop = new Properties();
        InputStream input = null;
        String value = "";

        try {

            input = new FileInputStream("test.properties");
            prop.load(input);

            value = prop.getProperty(key);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return value;

    }

    Headers getHeaders(){
        Header h1 = new Header("app_key",getProperty("app_key"));
        Header h2 = new Header("app_id",getProperty("app_id"));
        Headers headers = new Headers(h1,h2);

        return headers;
    }

}
