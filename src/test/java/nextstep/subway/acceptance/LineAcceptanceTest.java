package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static nextstep.subway.acceptance.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철노선을 생성한다.")
    void createLineTest() {
        // when
        long upStationId = createStationAndGetId(Map.of(NAME, "남태령역"));
        long downStationId = createStationAndGetId(Map.of(NAME, "사당역"));

        ExtractableResponse<Response> response = createLine("4호선", "bg-blue-300", upStationId, downStationId, 10);
        checkResponseStatus(response, HttpStatus.CREATED);

        // then
        ExtractableResponse<Response> getResponse = getLine(response.jsonPath().getLong("id"));
        checkResponseStatus(getResponse, HttpStatus.OK);

        assertThat(getResponse.jsonPath().getString("name")).isEqualTo("4호선");
        assertThat(getResponse.jsonPath().getList("stations")).hasSize(2);
        assertThat(getResponse.jsonPath().getList("stations.name")).contains("남태령역", "사당역");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선의 목록이 조회된다.")
    void getLinesTest() {
        long upStationId = createStationAndGetId(Map.of(NAME, "남태령역"));
        long downStationId = createStationAndGetId(Map.of(NAME, "사당역"));

        // given
        createLine("4호선", "bg-blue-300", upStationId, downStationId, 10);

        long nextStationId = createStationAndGetId(Map.of(NAME, "총신대입구역"));
        createLine("2호선", "bg-green-300", downStationId, nextStationId, 10);

        // when
        ExtractableResponse<Response> response = getLines();
        checkResponseStatus(response, HttpStatus.OK);

        // then
        assertThat(response.jsonPath().getList(".")).hasSize(2);
        assertThat(response.jsonPath().getList("name", String.class)).contains("4호선", "2호선");

    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선의 상세 정보가 조회된다.")
    void getLineTest() {
        // given
        long id = createLineAndGetId();

        // when
        ExtractableResponse<Response> response = getLine(id);

        // then
        checkResponseStatus(response, HttpStatus.OK);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철노선의 정보가 수정된다.")
    void updateLineTest() {
        // given
        long id = createLineAndGetId();

        // when
        ExtractableResponse<Response> response = updateLine(id, "다른4호선", "bg-skyblue-400");
        checkResponseStatus(response, HttpStatus.OK);

        // then
        ExtractableResponse<Response> updatedResponse = getLine(id);
        assertThat(updatedResponse.jsonPath().getString("name")).isEqualTo("다른4호선");
        assertThat(updatedResponse.jsonPath().getString("color")).isEqualTo("bg-skyblue-400");

    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철노선의 정보가 삭제된다.")
    void deleteLineTest() {
        // given
        long id = createLineAndGetId();

        // when
        ExtractableResponse<Response> response = deleteLine(id);
        checkResponseStatus(response, HttpStatus.NO_CONTENT);

        // then
        ExtractableResponse<Response> getResponse = getLine(id);
        checkResponseStatus(getResponse, HttpStatus.NOT_FOUND);
    }


    private long createLineAndGetId() {
        long upStationId = createStationAndGetId(Map.of(NAME, "남태령역"));
        long downStationId = createStationAndGetId(Map.of(NAME, "사당역"));

        return createLine("4호선", "bg-blue-300", upStationId, downStationId, 10)
                .jsonPath()
                .getLong("id");
    }

    public static ExtractableResponse<Response> createLine(String name, String color, long upStationId, long downStationId, int distance) {
        return RestAssured.given().log().all()
                .body(Map.of(
                        "name", name,
                        "color", color,
                        "upStationId", upStationId,
                        "downStationId", downStationId,
                        "distance", distance
                ))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLine(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(long id, String name, String color) {
        return RestAssured.given().log().all()
                .body(Map.of(
                        "name", name,
                        "color", color
                ))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
