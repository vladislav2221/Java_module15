package academy.tochkavhoda.competition.model;
public final class Rating {
 private final String applicationId,expertId; private int score;
 public Rating(String applicationId,String expertId,int score){this.applicationId=applicationId;this.expertId=expertId;this.score=score;}
 public String getApplicationId(){return applicationId;} public String getExpertId(){return expertId;} public int getScore(){return score;} public void setScore(int score){this.score=score;}
}
