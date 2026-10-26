package academy.tochkavhoda.competition.server;

import academy.tochkavhoda.competition.service.AccountService;
import academy.tochkavhoda.competition.service.CompetitionService;

/** Public entry point; requests and responses are JSON strings as required by the assignment. */
public final class Server {
    private final AccountService accounts; private final CompetitionService competition;
    public Server(){this(new AccountService());} public Server(AccountService accounts){this.accounts=accounts;this.competition=new CompetitionService(academy.tochkavhoda.competition.database.Database.getInstance(),accounts);}
    public ServerResponse registerParticipant(String requestJsonString){return accounts.registerParticipant(requestJsonString);}
    public ServerResponse registerExpert(String requestJsonString){return accounts.registerExpert(requestJsonString);}
    public ServerResponse login(String requestJsonString){return accounts.login(requestJsonString);}
    public ServerResponse logout(String token){return accounts.logout(token);}
    public ServerResponse leave(String token){return accounts.leave(token);}
    public ServerResponse addApplication(String token,String requestJsonString){return competition.addApplication(token,requestJsonString);}
    public ServerResponse cancelApplication(String token,String requestJsonString){return competition.cancelApplication(token,requestJsonString);}
    public ServerResponse rateApplication(String token,String requestJsonString){return competition.rateApplication(token,requestJsonString);}
    public ServerResponse deleteRating(String token,String requestJsonString){return competition.deleteRating(token,requestJsonString);}
    public ServerResponse getApplications(String token,String requestJsonString){return competition.getApplications(token,requestJsonString);}
    public ServerResponse getRatedApplications(String token){return competition.getRatedApplications(token);}
    public ServerResponse results(String token,String requestJsonString){return competition.results(token,requestJsonString);}
}
