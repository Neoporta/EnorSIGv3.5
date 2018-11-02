package enorsul.com.enorsul.models;

public class MessageModelo {

    private String fecha;
    private String mensaje;
    private String usuario;

    public MessageModelo(String fecha, String mensaje, String usuario) {
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
