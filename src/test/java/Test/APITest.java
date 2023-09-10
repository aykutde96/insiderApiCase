package Test;

import Base.BaseAssertions;
import Base.BaseMethods;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static Constant.APIConstant.*;

public class APITest{
    private static final Logger logger = LoggerFactory.getLogger(APITest.class);
    private static BaseMethods baseMethods;
    private static BaseAssertions baseAssertions;

    @Before
    public void setup() {
        logger.info("Initialize methods and assertions");
        baseMethods = new BaseMethods(BASEURL);
        baseAssertions = new BaseAssertions(BASEURL);
        logger.info("Test is starting");
    }

    Response response;
    JsonPath responseJson;

    @Test
    public void createPetSuccess() throws IOException {
        response = baseMethods.createPet("PetCreateSuccess");
        responseJson = new JsonPath(response.asString());
        logger.info("create pet request has been sent");
        baseAssertions.checkStatusCode(response,200);
        baseAssertions.nullCheck(response,"id");
        baseAssertions.nullCheck(response,"photoUrls[0]");
        baseAssertions.nullCheck(response,"tags");
        baseAssertions.checkParameterStringValue(response,"name",CREATE_PET_NAME);
        baseAssertions.checkParameterStringValue(response,"status",CREATE_PET_STATUS);
        baseAssertions.checkParameterStringValue(response,"category.name", (String) CREATE_PET_CATEGORY.get("name"));
        Assert.assertEquals(responseJson.getString("category.name"),CREATE_PET_CATEGORY.get("name"));
        logger.info("pet has been created successfully");

    }

    @Test
    public void createPet400() throws IOException{
        response = baseMethods.createPet("PetCreate400");
        logger.info("create pet request has been sent");
        baseAssertions.checkStatusCode(response,400);
        baseAssertions.checkParameterStringValue(response,"message",CREATE_PET_400_MESSAGE);
        baseAssertions.checkParameterIntegerValue(response,"code",400);
        logger.info("bad input message has been received");

    }

    @Test
    public void updatePetSuccess() throws IOException{
        response = baseMethods.updatePet("PetUpdateSuccess");
        responseJson = new JsonPath(response.asString());
        logger.info("update pet request has been sent");
        baseAssertions.checkStatusCode(response,200);
        baseAssertions.nullCheck(response,"id");
        baseAssertions.nullCheck(response,"photoUrls[0]");
        baseAssertions.nullCheck(response,"tags");
        baseAssertions.checkParameterStringValue(response,"name",UPDATE_PET_NAME);
        baseAssertions.checkParameterStringValue(response,"status",UPDATE_PET_STATUS);
        baseAssertions.checkParameterStringValue(response,"category.name", (String) UPDATE_PET_CATEGORY.get("name"));
        Assert.assertEquals(responseJson.get("photoUrls"), UPDATE_PET_PHOTOS);
    }

    @Test
    public void updatePet400() throws IOException {
        response = baseMethods.updatePet("PetUpdate400");
        logger.info("create pet request has been sent");
        baseAssertions.checkStatusCode(response,400);
        baseAssertions.checkParameterIntegerValue(response,"code", 400);
        baseAssertions.checkParameterStringValue(response,"message", UPDATE_PET_400_MESSAGE);
        logger.info("bad input message has been received");
    }

    @Test
    public void updatePet404(){
        System.out.println("Update 404");
    }

    @Test
    public void findPetByStatusAvailableSuccess(){
        response = baseMethods.findPetByStatus(STATUS_AVAILABLE);
        responseJson = new JsonPath(response.asString());
        logger.info("available find pet request has been sent");
        baseAssertions.checkStatusCode(response,200);

        for (int i = 1; i < responseJson.getInt("$.size()"); i++) {
            baseAssertions.checkParameterStringValue(response,"["+i+"].status",STATUS_AVAILABLE);
            baseAssertions.nullCheck(response,"["+i+"].id");
            baseAssertions.nullCheck(response,"["+i+"].name");
        }
        logger.info("available-the pets are found successfully");
    }

    @Test
    public void findPetByStatusPendingSuccess(){
        response = baseMethods.findPetByStatus(STATUS_PENDING);
        responseJson = new JsonPath(response.asString());
        logger.info("pending find pet request has been sent");
        baseAssertions.checkStatusCode(response,200);

        for (int i = 1; i < responseJson.getInt("$.size()"); i++) {
            baseAssertions.nullCheck(response,"["+i+"].id");
            baseAssertions.nullCheck(response,"["+i+"].name");
            baseAssertions.checkParameterStringValue(response,"["+i+"].status",STATUS_PENDING);
        }
        logger.info("pending-the pets are found successfully");
    }

    @Test
    public void findPetByStatusSoldSuccess(){
        response = baseMethods.findPetByStatus(STATUS_SOLD);
        responseJson = new JsonPath(response.asString());
        logger.info("sold-the pet request has been sent");
        baseAssertions.checkStatusCode(response,200);

        for (int i = 1; i < responseJson.getInt("$.size()"); i++) {
            baseAssertions.nullCheck(response,"["+i+"].id");
            baseAssertions.checkParameterStringValue(response,"["+i+"].status",STATUS_SOLD);
        }
        logger.info("sold-the pets are found successfully");
    }

    @Test
    public void findPetById(){
        Response response = baseMethods.petFingById(10);
        logger.info("pet found by id request has been sent");
        baseAssertions.checkStatusCode(response, 200);
        baseAssertions.checkParameterIntegerValue(response,"id",10);
        logger.info("pet found by id request is sent successfully");
    }
    @Test
    public void deletePetById200(){
        Response response = baseMethods.petDeleteById(5);
        logger.info("delete pet request has been sent");
        baseAssertions.checkStatusCode(response, 200);
        baseAssertions.checkParameterStringValue(response,"message","5");
        baseAssertions.checkParameterStringValue(response,"code","200");
        logger.info("pet is deleted successfully");
    }
    @Test
    public void deletePetById404(){
        Response response = baseMethods.petDeleteById(5);
        logger.info("delete pet request has been sent");
        baseAssertions.checkStatusCode(response, 404);
        logger.info("pet not found");
    }
    @Test
    public void deletePetById400(){
        logger.error("Invalid ID supplied");
    }

}
