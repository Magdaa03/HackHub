package unicam.hackHub.team.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Team {

    @Id
    private String id;

    private String nome;

    //costruttore vuoto per leggere i dati dal db
    public Team(){ }

    //costruttore
    public Team(String id, String nome){
        this.id=id;
        this.nome=nome;
    }


    // --- Getter e Setter ---

    public String getId(){
        return id;
    }

    public String setId(String id){
       return this.id=id;
    }

    public String getNome(){
        return nome;
    }

    public String setNome(String nome){
        return this.nome=nome;
    }
}
