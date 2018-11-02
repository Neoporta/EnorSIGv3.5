package enorsul.com.enorsul.models;

public class OrdenModelo {

    private String RGI;
    private String NroOrden;
    private String CodeSABESP;
    private String Observaciones;
    private String Estado;
    private String Nombre;
    private String Funcionario;
    private String FechaOrden;
    private String Acao;
    private String Pariedade;
    private String Orden;

    public OrdenModelo(String RGI,
                       String nroOrden,
                       String codeSABESP,
                       String observaciones,
                       String estado,
                       String nombre,
                       String funcionario,
                       String fechaOrden,
                       String acao,
                       String pariedade,
                       String orden) {
        this.RGI = RGI;
        this.NroOrden = nroOrden;
        this.CodeSABESP = codeSABESP;
        this.Observaciones = observaciones;
        this.Estado = estado;
        this.Nombre = nombre;
        this.Funcionario = funcionario;
        this.FechaOrden = fechaOrden;
        this.Acao = acao;
        this.Pariedade = pariedade;
        this.Orden = orden;
    }

    public String getRGI() {
        return RGI;
    }

    public void setRGI(String RGI) {
        this.RGI = RGI;
    }

    public String getNroOrden() {
        return NroOrden;
    }

    public void setNroOrden(String nroOrden) {
        NroOrden = nroOrden;
    }

    public String getCodeSABESP() {
        return CodeSABESP;
    }

    public void setCodeSABESP(String codeSABESP) {
        CodeSABESP = codeSABESP;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getFuncionario() {
        return Funcionario;
    }

    public void setFuncionario(String funcionario) {
        Funcionario = funcionario;
    }

    public String getFechaOrden() {
        return FechaOrden;
    }

    public void setFechaOrden(String fechaOrden) {
        FechaOrden = fechaOrden;
    }

    public String getAcao() {
        return Acao;
    }

    public void setAcao(String acao) {
        Acao = acao;
    }

    public String getPariedade() {
        return Pariedade;
    }

    public void setPariedade(String pariedade) {
        Pariedade = pariedade;
    }

    public String getOrden() {
        return Orden;
    }

    public void setOrden(String orden) {
        Orden = orden;
    }
}
