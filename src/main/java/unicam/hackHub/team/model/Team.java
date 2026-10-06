package unicam.hackHub.team.model;

import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import unicam.hackHub.utente.model.Utente;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class Team {

    @Id
    private String id;

    private String nome;

    //Lista membri del Team
    @OneToMany
    private List<Utente> membri = new ArrayList<>();

    //Lista inviti Spediti
    @OneToMany(mappedBy = "mittente")
    private List<Invito> invitiSpediti = new ArrayList<>();

    //costruttore vuoto per leggere i dati dal db
    public Team(){ }

    //costruttore
    public Team(String id, String nome){
        this.id=id;
        this.nome=nome;
    }

    public void aggiungiMembro(Utente utente) {
        this.membri.add(utente);
    }

    public void rimuoviMembro(Utente utente) {
        this.membri.remove(utente);
    }

    public void CreaInvito(Utente utente){
        //Genera id
        String idTemp=java.util.UUID.randomUUID().toString();

        //Crea l'oggetto invito con la data attuale e stato PENDENTE
        Invito nuovoInvito = new Invito(idTemp, new Date(), StatoInvito.PENDENTE, this, utente);

        //Aggiunge l'invito alla lista inviti del team
        this.invitiSpediti.add(nuovoInvito);
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
