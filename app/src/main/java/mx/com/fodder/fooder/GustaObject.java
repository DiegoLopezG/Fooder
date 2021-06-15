package mx.com.fodder.fooder;

public class GustaObject {
    private String userId, name, ImagenPerfilURL;

    public GustaObject (String userId, String name, String ImagenPerfilURL){
        this.userId = userId;
        this.name = name;
        this.ImagenPerfilURL = ImagenPerfilURL;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagenPerfilURL() {
        return ImagenPerfilURL;
    }

    public void setImagenPerfilURL(String imagenPerfilURL) {
        this.ImagenPerfilURL = imagenPerfilURL;
    }
}
