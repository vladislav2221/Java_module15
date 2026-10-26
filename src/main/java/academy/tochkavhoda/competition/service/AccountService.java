package academy.tochkavhoda.competition.service;

import academy.tochkavhoda.competition.database.Database;
import academy.tochkavhoda.competition.model.*;
import academy.tochkavhoda.competition.server.ServerResponse;
import academy.tochkavhoda.competition.util.Json;
import java.util.*;

public final class AccountService {
    private final Database db;
    public AccountService(){this(Database.getInstance());} public AccountService(Database db){this.db=Objects.requireNonNull(db);}
    public ServerResponse registerParticipant(String json){return safely(()->{Map<String,Object> m=Json.object(json);String name=required(m,"fullName"),company=required(m,"companyName"),login=login(m),password=password(m);db.register(new Participant(UUID.randomUUID().toString(),name,company,login,password));return "{}";});}
    public ServerResponse registerExpert(String json){return safely(()->{Map<String,Object> m=Json.object(json);String name=required(m,"fullName"),login=login(m),password=password(m);Object raw=m.get("directions");if(!(raw instanceof List)||((List<?>)raw).isEmpty())throw new IllegalArgumentException("Необходимо указать хотя бы одно направление экспертизы");Set<String> directions=new LinkedHashSet<>();for(Object d:(List<?>)raw){if(!(d instanceof String)||((String)d).trim().isEmpty())throw new IllegalArgumentException("Направления не должны быть пустыми");directions.add(((String)d).trim());}db.register(new Expert(UUID.randomUUID().toString(),name,directions,login,password));return "{}";});}
    public ServerResponse login(String json){return safely(()->{Map<String,Object> m=Json.object(json);String login=required(m,"login"),password=required(m,"password");Account account=db.authenticate(login,password);if(account==null)throw new IllegalArgumentException("Неверный логин или пароль");String token=UUID.randomUUID().toString();db.createSession(token,account);return Json.stringify(Map.of("token",token));});}
    public ServerResponse logout(String token){return safely(()->{if(token==null||!db.logout(token))throw new IllegalArgumentException("Недействительный токен");return "{}";});}
    public ServerResponse leave(String token){return safely(()->{Account account=authorized(token);db.removeAccount(account.getId());return "{}";});}
    public Account authorized(String token){if(token==null||token.isBlank())throw new IllegalArgumentException("Токен обязателен");Account a=db.accountByToken(token);if(a==null)throw new IllegalArgumentException("Недействительный токен");return a;}
    private static String required(Map<String,Object> m,String key){Object v=m.get(key);if(!(v instanceof String)||((String)v).trim().isEmpty())throw new IllegalArgumentException("Поле '"+key+"' обязательно");return ((String)v).trim();}
    private static String login(Map<String,Object> m){String s=required(m,"login");if(s.length()<3)throw new IllegalArgumentException("Логин должен содержать не менее 3 символов");return s;}
    private static String password(Map<String,Object> m){String s=required(m,"password");if(s.length()<8)throw new IllegalArgumentException("Пароль должен содержать не менее 8 символов");return s;}
    private ServerResponse safely(Work work){try{return new ServerResponse(200,work.run());}catch(RuntimeException e){return new ServerResponse(400,Json.stringify(Map.of("error",e.getMessage()==null?"Ошибка запроса":e.getMessage())));}}
    @FunctionalInterface private interface Work{String run();}
}
