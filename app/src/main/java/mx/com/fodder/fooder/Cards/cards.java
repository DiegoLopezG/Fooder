package mx.com.fodder.fooder.Cards;
//LOS CAMBI0S FUERON AQUI:DESCOMENTAR LAS COSAS
//EN EL ARRAYADAPTER CAMBIAR EL INPUT DE LA FOTO
//EN EL MAIN CAMBIAR LA LINEA COMENTADA PARA CREAR LAS CARTAS
public class cards {
    private String userId;
    private  String name;
    private String instagram;
    private String profileImageURL;

    public cards (String userId, String name, String profileImageURL, String instagram){
        this.userId = userId;
        this.name = name;
        this.profileImageURL = profileImageURL;
        this.instagram = instagram;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
