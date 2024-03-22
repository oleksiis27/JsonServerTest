import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static java.lang.String.format;

public class JsonServerTest extends Simulation{
    private HttpRequestActionBuilder postJson() {
        return http("Post json file")
                .post("/json")
                .body(RawFileBody("bodies/sample1.json"))
                .header("Content-Type", "application/json")
                .check(status().is(201));
    }

    private HttpRequestActionBuilder getJson() {
        return http("Get json file")
                .get("/json")
                .header("Content-Type", "application/json")
                .check(status().is(200));
    }

    ChainBuilder postAndGetJson =
            ChainBuilder.EMPTY
                    .exec(postJson())
                    .exec(getJson());

    ScenarioBuilder postAndGetScenario = scenario("Execute post and get scenario")
            .exec(postAndGetJson);
    HttpProtocolBuilder httpProtocol = http.baseUrl(format("http://%s:%s", System.getProperty("minikubeIP"), "30333"));

    {
        setUp(
                postAndGetScenario.injectOpen(
                        atOnceUsers(1)
                )

        ).assertions(global().failedRequests().count().is(0L))
                .protocols(httpProtocol);

    }
}
