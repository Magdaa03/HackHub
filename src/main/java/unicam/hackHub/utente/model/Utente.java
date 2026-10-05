package unicam.hackHub.utente.model;
import unicam.hackHub.team.model.Invito;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Utente {

    @Id
    private String id;

    private String nome;
    private String email;
    private String password;

    //costruttore vuoto per caricare dati nel db
    public Utente() { }

    //costruttore
    public Utente(String id, String nome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public void accettaInvito(Invito invito){
        invito.accetta();
    }

    // --- Getter e Setter ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

