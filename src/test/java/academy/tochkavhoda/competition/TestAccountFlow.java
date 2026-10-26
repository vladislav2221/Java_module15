package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.server.*;
import academy.tochkavhoda.competition.util.Json;
import java.util.Map;

/** Dependency-free automated smoke/integration tests, run by scripts/test.sh. */
public final class TestAccountFlow {
    private static int passed;
    public static void main(String[] args){
        Database.getInstance().clear(); Server server=new Server();
        eq(200,server.registerParticipant("{\"fullName\":\"Иван Иванов\",\"companyName\":\"Ромашка\",\"login\":\"ivan\",\"password\":\"Password1\"}").getResponseCode());
        eq(400,server.registerParticipant("{\"fullName\":\"Иван Иванов\",\"companyName\":\"Другая\",\"login\":\"ivan\",\"password\":\"Password1\"}").getResponseCode());
        eq(400,server.registerParticipant("{\"fullName\":\"\",\"companyName\":\"X\",\"login\":\"xx\",\"password\":\"short\"}").getResponseCode());
        String participantToken=login(server,"ivan","Password1");String participantToken2=login(server,"ivan","Password1");check(!participantToken.equals(participantToken2));
        eq(200,server.logout(participantToken).getResponseCode());eq(400,server.logout(participantToken).getResponseCode());
        ServerResponse appResponse=server.addApplication(participantToken2,"{\"title\":\"Научная платформа\",\"description\":\"Прототип платформы\",\"directions\":[\"ИТ\"],\"requestedAmount\":1000}");eq(200,appResponse.getResponseCode());
        String appId=(String)Json.object(appResponse.getResponseData()).get("applicationId");
        eq(400,server.addApplication(participantToken2,"{\"title\":\"Bad\",\"description\":\"X\",\"directions\":[\"ИТ\"],\"requestedAmount\":-1}").getResponseCode());
        eq(200,server.registerExpert("{\"fullName\":\"Анна\",\"directions\":[\"ИТ\",\"Наука\"],\"login\":\"expert\",\"password\":\"Password2\"}").getResponseCode());
        eq(400,server.registerExpert("{\"fullName\":\"Борис\",\"directions\":[],\"login\":\"expert2\",\"password\":\"Password2\"}").getResponseCode());
        String expertToken=login(server,"expert","Password2");
        eq(200,server.getApplications(expertToken,"{}").getResponseCode());
        eq(200,server.rateApplication(expertToken,"{\"applicationId\":\""+appId+"\",\"score\":5}").getResponseCode());
        eq(200,server.rateApplication(expertToken,"{\"applicationId\":\""+appId+"\",\"score\":4}").getResponseCode());
        eq(400,server.rateApplication(expertToken,"{\"applicationId\":\""+appId+"\",\"score\":6}").getResponseCode());
        eq(200,server.getRatedApplications(expertToken).getResponseCode());
        ServerResponse result=server.results(participantToken2,"{\"fund\":2000,\"minAverageScore\":2}");eq(200,result.getResponseCode());check(result.getResponseData().contains("fundedApplications"));
        eq(200,server.deleteRating(expertToken,"{\"applicationId\":\""+appId+"\"}").getResponseCode());
        eq(200,server.cancelApplication(participantToken2,"{\"applicationId\":\""+appId+"\"}").getResponseCode());
        eq(400,server.cancelApplication(participantToken2,"{\"applicationId\":\""+appId+"\"}").getResponseCode());
        eq(200,server.leave(participantToken2).getResponseCode());eq(400,server.logout(participantToken2).getResponseCode());
        System.out.println("OK: "+passed+" checks passed.");
    }
    private static String login(Server server,String login,String password){ServerResponse r=server.login("{\"login\":\""+login+"\",\"password\":\""+password+"\"}");eq(200,r.getResponseCode());String token=(String)Json.object(r.getResponseData()).get("token");check(token!=null&&!token.isBlank());return token;}
    private static void eq(Object expected,Object actual){if(!expected.equals(actual))throw new AssertionError("Expected "+expected+" but got "+actual);passed++;}
    private static void check(boolean condition){if(!condition)throw new AssertionError("Condition was false");passed++;}
}
